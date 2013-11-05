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
package org.zodiark.service.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.Context;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Session;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.session.StreamingRequest;
import org.zodiark.service.util.UUID;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@On("/subscriber")
public class SubscriberServiceImpl implements SubscriberService, Session<SubscriberEndpoint> {

    private final ConcurrentHashMap<String, SubscriberEndpoint> endpoints = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(SubscriberServiceImpl.class);

    @Inject
    public Context context;

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    public StreamingRequest requestClass;

    @Override
    public void serve(Envelope e, AtmosphereResource r) {
        logger.trace("Handling Subscriber Envelop {} to Service {}", e, r.uuid());
        switch (e.getMessage().getPath()) {
            case Paths.LOAD_CONFIG:
            case Paths.CREATE_SUBSCRIBER_SESSION:
                createSession(e, r);
                break;
            case Paths.VALIDATE_SUBSCRIBER_STREAMING_SESSION:
                createOrJoinStreamingSession(e);
                break;
            case Paths.JOIN_STREAMING_SESSION:
                startStreamingSession(e);
                break;
            case Paths.WOWZA_ERROR_SUBSCRIBER_STREAMING_SESSION:
                errorStreamingSession(e);
                break;
            case Paths.TERMINATE_SUBSCRIBER_STREAMING_SESSSION:
                String uuid = e.getMessage().getUUID();
                SubscriberEndpoint p = endpoints.get(uuid);
                terminateStreamingSession(p, r);
                break;
            case Paths.SUBSCRIBER_ACTION:
                requestForAction(e, r);
                break;
            default:
                throw new IllegalStateException("Invalid Message Path" + e.getMessage().getPath());
        }
    }

    @Override
    public void requestForAction(final Envelope e, AtmosphereResource r) {
        Message m = e.getMessage();
        final SubscriberEndpoint s = endpoints.get(e.getUuid());

        if (s == null) {
            throw new IllegalStateException("No Subscriber associated with " + e.getUuid());
        }

        try {
            Action a = mapper.readValue(m.getData(), Action.class);
            a.endpoint(s);
            eventBus.dispatch(Paths.ACTION_VALIDATE, a, new EventBusListener<Action>() {
                @Override
                public void completed(Action action) {
                    logger.debug("Action Accepted for {}", action.getSubscriberUUID());
                    response(e, endpoints.get(action.getSubscriberUUID()), constructMessage(Paths.ACTION_VALIDATE, "OK"));
                }

                @Override
                public void failed(Action action) {
                    error(e, s, constructMessage(Paths.ACTION_VALIDATE, "error"));
                }
            });


        } catch (IOException e1) {
            logger.warn("{}", e1);
        }
    }

    @Override
    public void errorStreamingSession(Envelope e) {
        try {
            SubscriberResults result = mapper.readValue(e.getMessage().getData(), SubscriberResults.class);
            SubscriberEndpoint p = endpoints.get(result.getUuid());
            error(e, p, constructMessage(Paths.ERROR_STREAMING_SESSION, "error"));
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }
    }

    @Override
    public void terminateStreamingSession(SubscriberEndpoint p, AtmosphereResource r) {
        // TODO:
    }

    @Override
    public void createOrJoinStreamingSession(final Envelope e) {
        String uuid = e.getUuid();
        try {
            final SubscriberEndpoint s = retrieve(uuid);
            final StreamingRequest request = mapper.readValue(e.getMessage().getData(), requestClass.getClass());

            eventBus.dispatch(Paths.RETRIEVE_PUBLISHER, request.getPublisherUUID(), new EventBusListener<PublisherEndpoint>() {
                @Override
                public void completed(PublisherEndpoint p) {
                    s.wowzaServerUUID(request.getWowzaUUID()).publisherEndpoint(p);

                    // TODO: Callback is not called at the moment as the dispatching to Wowza is asynchronous
                    // TODO: Unit test
                    eventBus.dispatch(Paths.WOWZA_CONNECT, s);
                }

                @Override
                public void failed(PublisherEndpoint p) {
                    error(e, s, constructMessage(Paths.VALIDATE_SUBSCRIBER_STREAMING_SESSION, "error"));
                }
            });
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }

    }

    @Override
    public void retrieveEndpoint(Object s, EventBusListener l) {
        if (String.class.isAssignableFrom(s.getClass())) {
            l.completed(endpoints.get(s.toString()));
        } else {
            l.failed(new Exception("No Sunscriber associated"));
        }
    }

    @Override
    public void startStreamingSession(final Envelope e) {
        UUID uuid = null;
        try {
            uuid = mapper.readValue(e.getMessage().getData(), UUID.class);
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }

        SubscriberEndpoint s = retrieve(uuid.getUuid());
        eventBus.dispatch(Paths.BEGIN_SUBSCRIBER_STREAMING_SESSION, s, new EventBusListener<SubscriberEndpoint>() {
            @Override
            public void completed(SubscriberEndpoint s) {
                response(e, s, constructMessage(Paths.BEGIN_SUBSCRIBER_STREAMING_SESSION, "OK"));
            }

            @Override
            public void failed(SubscriberEndpoint s) {
                error(e, s, constructMessage(Paths.BEGIN_SUBSCRIBER_STREAMING_SESSION, "error"));
            }
        });
    }

    @Override
    public void response(Envelope e, SubscriberEndpoint s, Message m) {
        AtmosphereResource r = s.resource();
        Envelope newResponse = Envelope.newServerReply(e, m);
        try {
            r.write(mapper.writeValueAsString(newResponse));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {} {}", s, m);
        }
    }

    SubscriberEndpoint retrieve(String uuid) {
        SubscriberEndpoint p = endpoints.get(uuid);
        if (p == null) {
            throw new IllegalStateException("No Subscriber associated with " + uuid);
        }
        return p;
    }

    @Override
    public void serve(String event, Object r, EventBusListener l) {
        switch (event) {
            case Paths.RETRIEVE_SUBSCRIBER:
                retrieveEndpoint(r, l);
                break;
        }
    }

    @Override
    public SubscriberEndpoint createSession(final Envelope e, AtmosphereResource resource) {
        String uuid = e.getUuid();
        SubscriberEndpoint s = endpoints.get(uuid);
        if (s == null) {
            s = context.newInstance(SubscriberEndpoint.class);
            s.uuid(uuid).message(e.getMessage()).resource(resource);

            endpoints.put(uuid, s);
            eventBus.dispatch(Paths.DB_INIT, s, new EventBusListener<SubscriberEndpoint>() {
                @Override
                public void completed(SubscriberEndpoint p) {
                    lookupConfig(e, p);
                }

                @Override
                public void failed(SubscriberEndpoint p) {
                    error(e, p, constructMessage(Paths.VALIDATE_SUBSCRIBER_STREAMING_SESSION, "error"));
                }
            });
        }
        return s;
    }

    @Override
    public void error(Envelope e, SubscriberEndpoint p, Message m) {
        AtmosphereResource r = p.resource();
        Envelope error = Envelope.newServerReply(e, m);
        eventBus.dispatch(error, r);
    }

    @Override
    public SubscriberEndpoint config(Envelope e) {
        SubscriberEndpoint p = endpoints.get(e.getUuid());
        lookupConfig(e, p);
        return p;
    }

    private void lookupConfig(final Envelope e, SubscriberEndpoint p) {
        eventBus.dispatch(Paths.DB_CONFIG, p, new EventBusListener<SubscriberEndpoint>() {
            @Override
            public void completed(SubscriberEndpoint p) {
                response(e, p, constructMessage(Paths.CREATE_SUBSCRIBER_SESSION, "OK"));
            }

            @Override
            public void failed(SubscriberEndpoint p) {
                error(e, p, constructMessage(Paths.VALIDATE_SUBSCRIBER_STREAMING_SESSION, "error"));
            }
        });
    }

    Message constructMessage(String path, String status) {
        Message m = new Message();
        m.setPath(path);
        try {
            m.setData(mapper.writeValueAsString(new SubscriberResults(status)));
        } catch (JsonProcessingException e1) {
            logger.warn("{}", e1);
        }
        return m;
    }

}
