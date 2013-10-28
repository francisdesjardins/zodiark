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
package org.zodiark.service.publisher;

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

import java.util.concurrent.ConcurrentHashMap;

@On("/publisher")
public class PublisherServiceImpl implements PublisherService {

    private final ConcurrentHashMap<String, PublisherEndpoint> endpoints = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    Context context;

    @Override
    public void serve(Envelope e, AtmosphereResource r, EventBusListener l) {
        switch (e.getMessage().getPath()) {
            case Paths.LOAD_CONFIG:
            case Paths.CREATE_USER_SESSION:
                createPublisherSession(e, r);
                break;
            case Paths.CREATE_STREAMING_SESSION:
                createStreamingSession(e);
            case Paths.WOWZA_STREAMING_SESSION_OK:
                startStreamingSession(e);
                break;
            case Paths.WOWZA_STREAMING_SESSION_ERROR:
                String uuid = e.getMessage().getUUID();
                PublisherEndpoint p = endpoints.get(uuid);
                error(e, p);
                break;
            case Paths.TERMINATE_STREAMING_SESSSION:
                uuid = e.getMessage().getUUID();
                p = endpoints.get(uuid);
                terminateStreamingSession(p, r);
                break;
            default:
                throw new IllegalStateException("Invalid Message Path" + e.getMessage().getPath());
        }
    }

    public void terminateStreamingSession(PublisherEndpoint p, AtmosphereResource r) {
        // TODO:
    }

    public void createStreamingSession(final Envelope e) {
        String uuid = e.getMessage().getUUID();
        PublisherEndpoint p = retrieve(uuid);

        eventBus.fire(Paths.WOWZA_CONNECT, p, new EventBusListener<PublisherEndpoint>() {
            @Override
            public void completed(PublisherEndpoint p) {
                // TODO: Proper Message
                Message m = new Message();
                response(e, p, m);
            }

            @Override
            public void failed(PublisherEndpoint p) {
                error(e, p);
            }
        });
    }

    @Override
    public void startStreamingSession(final Envelope e) {
        String uuid = e.getMessage().getUUID();
        PublisherEndpoint p = retrieve(uuid);
        eventBus.fire(Paths.START_STREAMINGSESSION, p, new EventBusListener<PublisherEndpoint>() {
            @Override
            public void completed(PublisherEndpoint p) {
                Message m = new Message();
                response(e, p, m);
            }

            @Override
            public void failed(PublisherEndpoint p) {
                error(e, p);
            }
        });
    }

    @Override
    public void response(Envelope e, PublisherEndpoint p, Message m) {
        AtmosphereResource r = p.resource();
        Envelope newResponse = Envelope.newServerReply(e, m);
        try {
            r.write(mapper.writeValueAsString(newResponse));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {} {}", p, m);
        }
    }

    PublisherEndpoint retrieve(String uuid) {
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            throw new IllegalStateException("No Publisher associated with " + uuid);
        }
        return p;
    }

    @Override
    public void serve(String event, Object r, EventBusListener l) {
    }

    @Override
    public PublisherEndpoint createPublisherSession(final Envelope e, AtmosphereResource resource) {
        String uuid = e.getUuid();
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            p = context.newInstance(PublisherEndpoint.class);
            p.uuid(uuid).message(e.getMessage()).resource(resource);

            endpoints.put(uuid, p);
            eventBus.fire(Paths.DB_INIT, p, new EventBusListener<PublisherEndpoint>() {
                @Override
                public void completed(PublisherEndpoint p) {
                    lookupConfig(e, p);
                }

                @Override
                public void failed(PublisherEndpoint p) {
                    error(e, p);
                }
            });
        }
        return p;
    }

    public void error(Envelope e, PublisherEndpoint p) {
        AtmosphereResource r = p.resource();
        // TODO: Define error.
        Envelope error = Envelope.newServerReply(e, new Message());
        try {
            r.write(mapper.writeValueAsString(error));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
        }
    }

    @Override
    public PublisherEndpoint config(Envelope e) {
        PublisherEndpoint p = endpoints.get(e.getUuid());
        lookupConfig(e, p);
        return p;
    }

    private void lookupConfig(final Envelope e, PublisherEndpoint p) {
        eventBus.fire(Paths.DB_CONFIG , p, new EventBusListener<PublisherEndpoint>() {
            @Override
            public void completed(PublisherEndpoint p) {
                lookupConfig(e, p);
            }

            @Override
            public void failed(PublisherEndpoint p) {
                error(e, p);
            }
        });
    }
}
