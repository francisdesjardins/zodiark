/*
 * Copyright 2013 High-Level Technologies
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
package org.zodiark.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.Context;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Session;
import org.zodiark.service.util.UUID;
import org.zodiark.service.wowza.WowzaUUID;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter.OnDisconnect;
import static org.zodiark.protocol.Paths.BEGIN_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.BROADCASTER_CREATE;
import static org.zodiark.protocol.Paths.CREATE_PUBLISHER_SESSION;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_CONFIG;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.ERROR_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.FAILED_PUBLISHER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.LOAD_PUBLISHER_CONFIG;
import static org.zodiark.protocol.Paths.PUBLISHER_ABOUT_READY;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;
import static org.zodiark.protocol.Paths.START_PUBLISHER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.TERMINATE_STREAMING_SESSSION;
import static org.zodiark.protocol.Paths.VALIDATE_PUBLISHER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.WOWZA_CONNECT;

/**
 * The Publisher's application logic for validating, creating and starting a {@link org.zodiark.service.session.StreamingSession}
 */
@On(Paths.SERVICE_PUBLISHER)
public class PublisherServiceImpl implements PublisherService, Session<PublisherEndpoint> {

    private final ConcurrentHashMap<String, PublisherEndpoint> endpoints = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    public Context context;

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
        logger.trace("Handling Publisher Envelop {} to Service {}", e, r.uuid());
        switch (e.getMessage().getPath()) {
            case LOAD_PUBLISHER_CONFIG:
            case CREATE_PUBLISHER_SESSION:
                createSession(e, r);
                break;
            case VALIDATE_PUBLISHER_STREAMING_SESSION:
                createOrJoinStreamingSession(e);
                break;
            case START_PUBLISHER_STREAMING_SESSION:
                startStreamingSession(e);
                break;
            case FAILED_PUBLISHER_STREAMING_SESSION:
                errorStreamingSession(e);
                break;
            case TERMINATE_STREAMING_SESSSION:
                String uuid = e.getMessage().getUUID();
                PublisherEndpoint p = endpoints.get(uuid);
                terminateStreamingSession(p, r);
                break;
            default:
                throw new IllegalStateException("Invalid Message Path" + e.getMessage().getPath());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retrieveEndpoint(Object publisherEndpointUuid, Reply reply) {
        if (String.class.isAssignableFrom(publisherEndpointUuid.getClass())) {
            reply.ok(endpoints.get(publisherEndpointUuid.toString()));
        } else {
            reply.fail(new Exception("No publisher associated"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void errorStreamingSession(Envelope e) {
        try {
            PublisherResults result = mapper.readValue(e.getMessage().getData(), PublisherResults.class);
            PublisherEndpoint p = endpoints.get(result.getUuid());

            Message m = new Message();
            m.setPath(ERROR_STREAMING_SESSION);
            try {
                m.setData(mapper.writeValueAsString(new PublisherResults("ERROR")));
            } catch (JsonProcessingException e1) {
                //
            }
            error(e, p, m);
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateStreamingSession(PublisherEndpoint endpoint, AtmosphereResource r) {
        // TODO:
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createOrJoinStreamingSession(final Envelope e) {
        String uuid = e.getUuid();
        PublisherEndpoint p = retrieve(uuid);

        try {
            p.wowzaServerUUID(mapper.readValue(e.getMessage().getData(), WowzaUUID.class).getUuid());
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }

        // TODO: Callback is not called at the moment as the dispatching to Wowza is asynchronous
        eventBus.message(WOWZA_CONNECT, p, new Reply<PublisherEndpoint>() {
            @Override
            public void ok(PublisherEndpoint p) {
                // TODO: Proper Message
                Message m = new Message();
                response(e, p, m);
            }

            @Override
            public void fail(PublisherEndpoint p) {
                error(e, p, constructMessage(WOWZA_CONNECT, "error"));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startStreamingSession(final Envelope e) {
        UUID uuid = null;
        try {
            uuid = mapper.readValue(e.getMessage().getData(), UUID.class);
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }

        PublisherEndpoint p = retrieve(uuid.getUuid());
        eventBus.message(BEGIN_STREAMING_SESSION, p, new Reply<PublisherEndpoint>() {
            @Override
            public void ok(PublisherEndpoint p) {
                response(e, p, constructMessage(BEGIN_STREAMING_SESSION, "OK"));
            }

            @Override
            public void fail(PublisherEndpoint p) {
                error(e, p, constructMessage(BEGIN_STREAMING_SESSION, "error"));
            }
        }).message(BROADCASTER_CREATE, p);
    }

    Message constructMessage(String path, String status) {
        Message m = new Message();
        m.setPath(path);
        try {
            m.setData(mapper.writeValueAsString(new PublisherResults(status)));
        } catch (JsonProcessingException e1) {
            logger.warn("{}", e1);
        }
        return m;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void response(Envelope e, PublisherEndpoint endpoint, Message m) {
        AtmosphereResource r = endpoint.resource();
        Envelope newResponse = Envelope.newServerReply(e, m);
        try {
            r.write(mapper.writeValueAsString(newResponse));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {} {}", endpoint, m);
        }
    }

    PublisherEndpoint retrieve(String uuid) {
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            throw new IllegalStateException("No Publisher associated with " + uuid);
        }
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(String path, Object message, Reply reply) {
        switch (path) {
            case RETRIEVE_PUBLISHER:
                retrieveEndpoint(message, reply);
                break;
            case PUBLISHER_ABOUT_READY:
                resetEndpoint(message, reply);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetEndpoint(Object publisherEndpointUuid, Reply reply) {
        try {
            PublisherEndpoint p = endpoints.get(publisherEndpointUuid.toString());
            AtmosphereResource r = p.resource();
            Message m = new Message();
            m.setPath(PUBLISHER_ABOUT_READY);
            m.setData(mapper.writeValueAsString(new PublisherResults("READY")));

            Envelope e = Envelope.newPublisherRequest(p.uuid(), m);
            r.write(mapper.writeValueAsString(e));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {} {}", publisherEndpointUuid);
            reply.fail(publisherEndpointUuid);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherEndpoint createSession(final Envelope e, final AtmosphereResource resource) {
        final String uuid = e.getUuid();
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            p = context.newInstance(PublisherEndpoint.class);
            p.uuid(uuid).message(e.getMessage()).resource(resource);

            endpoints.put(uuid, p);
            eventBus.message(DB_POST_PUBLISHER_SESSION_CREATE, p, new Reply<PublisherEndpoint>() {
                @Override
                public void ok(PublisherEndpoint p) {
                    // TODO: Wrong
                   lookupConfig(e, p);
                }

                @Override
                public void fail(PublisherEndpoint p) {
                    error(e, p, constructMessage(VALIDATE_PUBLISHER_STREAMING_SESSION, "error"));
                }
            });
        }

        resource.addEventListener(new OnDisconnect() {
            @Override
            public void onDisconnect(AtmosphereResourceEvent event) {
                logger.debug("Publisher {} disconnected", uuid);
                endpoints.remove(uuid);
            }
        });
        return p;
    }

    protected void annonceSession(final Envelope e, PublisherEndpoint p) {
        eventBus.message(DB_POST_PUBLISHER_SHOW_START, p, new Reply<PublisherEndpoint>() {
            @Override
            public void ok(PublisherEndpoint p) {
                // Nothing to do, the endpoint has been configured
                logger.trace("Publisher ready {}", p);
            }

            @Override
            public void fail(PublisherEndpoint p) {
                // TODO: Wrong error message
                error(e, p, constructMessage(VALIDATE_PUBLISHER_STREAMING_SESSION, "error"));
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Envelope e, PublisherEndpoint endpoint, Message m) {
        AtmosphereResource r = endpoint.resource();
        Envelope error = Envelope.newServerReply(e, m);
        eventBus.ioEvent(error, r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherEndpoint config(Envelope e) {
        PublisherEndpoint p = endpoints.get(e.getUuid());
        lookupConfig(e, p);
        return p;
    }

    private void lookupConfig(final Envelope e, PublisherEndpoint p) {
        eventBus.message(DB_PUBLISHER_CONFIG, p, new Reply<PublisherEndpoint>() {
            @Override
            public void ok(PublisherEndpoint p) {
                response(e, p, constructMessage(CREATE_PUBLISHER_SESSION, "OK"));
                annonceSession(e, p);
            }

            @Override
            public void fail(PublisherEndpoint p) {
                error(e, p, constructMessage(VALIDATE_PUBLISHER_STREAMING_SESSION, "error"));
            }
        });
    }
}
