/*
 * Copyright 2013 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zodiark.service.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.publisher.PublisherResults;
import org.zodiark.service.subscriber.SubscriberEndpoint;
import org.zodiark.service.subscriber.SubscriberResults;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@On("/action")
public class ActionServiceImpl implements ActionService {

    private final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);
    // TODO: Timer to clear action that were never accepted.
    private final ConcurrentHashMap<String, EventBusListener> handshakingActions = new ConcurrentHashMap<>();

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    public ScheduledExecutorService timer;

    @Override
    public void serve(Envelope e, AtmosphereResource r) {
        switch (e.getMessage().getPath()) {
            case Paths.ACTION_ACCEPT_OK:
                actionAccepted(e);
                break;
            case Paths.ACTION_ACCEPT_REFUSED:
                actionRefused(e);
                break;
            case Paths.ACTION_START_OK:
                actionStarted(e);
                break;
        }
    }

    public void actionStarted(Envelope e) {
        try {
            final PublisherResults results = mapper.readValue(e.getMessage().getData(), PublisherResults.class);
            eventBus.dispatch(Paths.RETRIEVE_PUBLISHER, results.getUuid(), new EventBusListener<PublisherEndpoint>() {
                @Override
                public void completed(final PublisherEndpoint p) {

                    final AtomicInteger time = new AtomicInteger(p.action().time());
                    final AtmosphereResource publisher = p.resource();
                    final AtmosphereResource subscriber = p.action().subscriber().resource();

                    final Future<?> timerFuture = timer.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            if (time.get() == 0) return;

                            Message m = new Message();
                            m.setPath(Paths.ACTION_TIMER);
                            try {
                                m.setData(mapper.writeValueAsString(time.getAndDecrement()));

                                Envelope e = Envelope.newPublisherMessage(p.uuid(), m);
                                String w = mapper.writeValueAsString(e);
                                publisher.write(w);

                                e = Envelope.newSubscriberMessage(p.uuid(), m);
                                w = mapper.writeValueAsString(e);
                                subscriber.write(w);
                            } catch (JsonProcessingException e1) {
                                logger.error("", e1);
                            }
                        }
                    }, 1, 1, TimeUnit.SECONDS);

                    timer.schedule(new Runnable() {
                        @Override
                        public void run() {
                            timerFuture.cancel(false);

                            Message m = new Message();
                            m.setPath(Paths.ACTION_COMPLETED);
                            try {
                                m.setData(mapper.writeValueAsString(new PublisherResults("OK")));

                                Envelope e = Envelope.newPublisherMessage(p.uuid(), m);
                                String w = mapper.writeValueAsString(e);
                                publisher.write(w);

                                m.setData(mapper.writeValueAsString(new SubscriberResults("OK")));
                                e = Envelope.newSubscriberMessage(p.uuid(), m);
                                w = mapper.writeValueAsString(e);
                                subscriber.write(w);
                            } catch (JsonProcessingException e1) {
                                logger.error("", e1);
                            } finally {
                                eventBus.dispatch(Paths.STREAMING_COMPLETE_ACTION, p);
                            }
                        }
                    }, p.action().time(), TimeUnit.SECONDS);
                }

                @Override
                public void failed(PublisherEndpoint p) {
                    logger.error("Unable to retrieve Publishere for {}", results.getUuid());
                }
            });


        } catch (IOException e1) {
            logger.error("", e1);
        }

    }

    @Override
    public void serve(String event, Object message, EventBusListener l) {
        switch (event) {
            case Paths.ACTION_VALIDATE:
                if (Action.class.isAssignableFrom(message.getClass())) {
                    Action action = Action.class.cast(message);
                    validateAction(action, l);
                }
                break;

        }
    }

    @Override
    public void validateAction(final Action action, final EventBusListener l) {
        final SubscriberEndpoint s = action.subscriber();
        final PublisherEndpoint p = s.publisherEndpoint();

        if (p.actionInProgress()) {
            l.failed(s);
            return;
        }
        p.action(action);

        eventBus.dispatch(Paths.SUBSCRIBER_VALIDATE_STATE, s, new EventBusListener<SubscriberEndpoint>() {
            @Override
            public void completed(SubscriberEndpoint s) {
                logger.trace("Action {} succeeded. Sending request to publisher {}", action, s);

                // No need to have a listener here since the response will be dispatched to EnvelopeDigester
                action.setPath(Paths.ACTION_ACCEPT);
                handshakingActions.put(s.uuid(), l);
                requestForAction(p, action);
            }

            @Override
            public void failed(SubscriberEndpoint s) {
                l.failed(s);
            }
        });
    }

    @Override
    public void requestForAction(PublisherEndpoint p, Action action) {
        Message m = constructMessage(action);
        AtmosphereResource r = p.resource();
        Envelope newResponse = Envelope.newPublisherRequest(p.uuid(), m);
        try {
            r.write(mapper.writeValueAsString(newResponse));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
        }
    }


    Message constructMessage(Action action) {
        Message m = new Message();
        m.setPath(action.getPath());
        try {
            m.setData(mapper.writeValueAsString(action));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
        }
        return m;
    }


    @Override
    public void actionAccepted(Envelope e) {
        try {
            final Action action = mapper.readValue(e.getMessage().getData(), Action.class);

            eventBus.dispatch(Paths.RETRIEVE_SUBSCRIBER, action.getSubscriberUUID(), new EventBusListener<SubscriberEndpoint>() {
                @Override
                public void completed(SubscriberEndpoint s) {
                    action.subscriber(s);
                }

                @Override
                public void failed(SubscriberEndpoint s) {
                    logger.error("No Endpoint");
                }
            });

            EventBusListener l = handshakingActions.remove(action.getSubscriberUUID());
            if (l == null) {
                throw new IllegalStateException("Invalid state");
            }

            // Send OK
            l.completed(action);

            eventBus.dispatch(Paths.STREAMING_EXECUTE_ACTION, action, new EventBusListener<Action>() {
                @Override
                public void completed(Action action) {

                }

                @Override
                public void failed(Action action) {

                }
            });
        } catch (IOException e1) {
            logger.error("", e1);
        }
    }

    @Override
    public void actionRefused(Envelope e) {
        try {
            Action action = mapper.readValue(e.getMessage().getData(), Action.class);
            EventBusListener l = handshakingActions.remove(action.getSubscriberUUID());
            if (l == null) {
                throw new IllegalStateException("Invalid state");
            }

            l.failed(action);
        } catch (IOException e1) {
            logger.error("", e1);
        }
    }

}
