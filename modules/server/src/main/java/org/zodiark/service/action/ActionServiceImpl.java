/*
 * Copyright 2013-2014 High-Level Technologies
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
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.On;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.publisher.PublisherResults;
import org.zodiark.service.subscriber.SubscriberEndpoint;
import org.zodiark.service.subscriber.SubscriberResults;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.zodiark.protocol.Paths.ACTION_ACCEPT_REFUSED;
import static org.zodiark.protocol.Paths.ACTION_COMPLETED;
import static org.zodiark.protocol.Paths.ACTION_START_OK;
import static org.zodiark.protocol.Paths.ACTION_TIMER;
import static org.zodiark.protocol.Paths.MESSAGE_ACTION_VALIDATE;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_VALIDATE_STATE;
import static org.zodiark.protocol.Paths.PUBLISHER_ACTION_ACCEPT;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;
import static org.zodiark.protocol.Paths.RETRIEVE_SUBSCRIBER;
import static org.zodiark.protocol.Paths.SERVICE_ACTION;
import static org.zodiark.protocol.Paths.STREAMING_COMPLETE_ACTION;
import static org.zodiark.protocol.Paths.STREAMING_EXECUTE_ACTION;
import static org.zodiark.protocol.Paths.ZODIARK_ACTION_ACCEPTED;

@On(SERVICE_ACTION)
public class ActionServiceImpl implements ActionService {

    private final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);
    // TODO: Timer to clear action that were never accepted.
    private final ConcurrentHashMap<String, Reply> handshakingActions = new ConcurrentHashMap<>();

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    public ScheduledExecutorService timer;

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
        switch (e.getMessage().getPath()) {
            case ZODIARK_ACTION_ACCEPTED:
                actionAccepted(e);
                break;
            case ACTION_ACCEPT_REFUSED:
                actionRefused(e);
                break;
            case ACTION_START_OK:
                actionStarted(e);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void actionStarted(Envelope e) {
        try {
            final PublisherResults results = mapper.readValue(e.getMessage().getData(), PublisherResults.class);
            eventBus.message(RETRIEVE_PUBLISHER, results.getUuid(), new Reply<PublisherEndpoint>() {
                @Override
                public void ok(final PublisherEndpoint p) {

                    final AtomicInteger time = new AtomicInteger(p.action().time());
                    final AtmosphereResource publisher = p.resource();
                    final AtmosphereResource subscriber = p.action().subscriber().resource();

                    final Future<?> timerFuture = timer.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            if (time.get() == 0) return;

                            Message m = new Message();
                            m.setPath(ACTION_TIMER);
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
                            m.setPath(ACTION_COMPLETED);
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
                                eventBus.message(STREAMING_COMPLETE_ACTION, p);
                            }
                        }
                    }, p.action().time(), TimeUnit.SECONDS);
                }

                @Override
                public void fail(PublisherEndpoint p) {
                    logger.error("Unable to retrieve Publishere for {}", results.getUuid());
                }
            });


        } catch (IOException e1) {
            logger.error("", e1);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(String path, Object message, Reply reply) {
        switch (path) {
            case MESSAGE_ACTION_VALIDATE:
                if (Action.class.isAssignableFrom(message.getClass())) {
                    Action action = Action.class.cast(message);
                    validateAction(action, reply);
                }
                break;

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAction(final Action action, final Reply reply) {
        final SubscriberEndpoint s = action.subscriber();
        final PublisherEndpoint p = s.publisherEndpoint();

        if (p.actionInProgress()) {
            reply.fail(s);
            return;
        }
        p.action(action);

        eventBus.message(DB_SUBSCRIBER_VALIDATE_STATE, s, new Reply<SubscriberEndpoint>() {
            @Override
            public void ok(SubscriberEndpoint s) {
                logger.trace("Action {} succeeded. Sending request to publisher {}", action, s);

                // No need to have a listener here since the response will be dispatched to EnvelopeDigester
                action.setPath(PUBLISHER_ACTION_ACCEPT);
                handshakingActions.put(s.uuid(), reply);
                requestForAction(p, action);
            }

            @Override
            public void fail(SubscriberEndpoint s) {
                reply.fail(s);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionAccepted(Envelope e) {
        try {
            final Action action = mapper.readValue(e.getMessage().getData(), Action.class);

            eventBus.message(RETRIEVE_SUBSCRIBER, action.getSubscriberUUID(), new Reply<SubscriberEndpoint>() {
                @Override
                public void ok(SubscriberEndpoint s) {
                    action.subscriber(s);
                }

                @Override
                public void fail(SubscriberEndpoint s) {
                    logger.error("No Endpoint");
                }
            });

            Reply l = handshakingActions.remove(action.getSubscriberUUID());
            if (l == null) {
                throw new IllegalStateException("Invalid state");
            }

            // Send OK
            l.ok(action);

            eventBus.message(STREAMING_EXECUTE_ACTION, action, new Reply<Action>() {
                @Override
                public void ok(Action action) {

                }

                @Override
                public void fail(Action action) {

                }
            });
        } catch (IOException e1) {
            logger.error("", e1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionRefused(Envelope e) {
        try {
            Action action = mapper.readValue(e.getMessage().getData(), Action.class);
            Reply l = handshakingActions.remove(action.getSubscriberUUID());
            if (l == null) {
                throw new IllegalStateException("Invalid state");
            }

            l.fail(action);
        } catch (IOException e1) {
            logger.error("", e1);
        }
    }

}
