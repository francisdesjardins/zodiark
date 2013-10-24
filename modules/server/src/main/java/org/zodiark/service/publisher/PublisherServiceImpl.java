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
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;

import java.util.concurrent.ConcurrentHashMap;

@On("/publisher")
public class PublisherServiceImpl implements PublisherService {

    private final ConcurrentHashMap<String, PublisherEndpoint> endpoints = new ConcurrentHashMap<String, PublisherEndpoint>();
    private final Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Override
    public void on(Envelope e, Object r, EventBusListener l) {
        switch (e.getMessage().getPath()) {
            case Paths.LOAD_CONFIG:
            case Paths.CREATE_SESSION:
                init(e, AtmosphereResource.class.cast(r));
                break;
            case Paths.CREATE_SHOW:
                createShow(e,  AtmosphereResource.class.cast(r));
                break;
            default:
                throw new IllegalStateException("Invalid Message Path" + e.getMessage().getPath());
        }
    }

    public void createShow(final Envelope e, AtmosphereResource cast) {
        String uuid = e.getUuid();
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            throw new IllegalStateException("No Publisher associated with " + uuid);
        }

        eventBus.fire("/wowza/connect", p, new EventBusListener() {
            @Override
            public void completed(Object response) {
                if (PublisherEndpoint.class.isAssignableFrom(response.getClass())) {
                    // TODO: Send OK to the Publisher
                }
            }

            @Override
            public void failed(Object response) {
                if (PublisherEndpoint.class.isAssignableFrom(response.getClass())) {
                    error(e, PublisherEndpoint.class.cast(response));
                }
            }
        });
    }

    @Override
    public void on(Object r, EventBusListener l) {
    }

    @Override
    public PublisherEndpoint init(final Envelope e, AtmosphereResource resource) {
        String uuid = e.getUuid();
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            p = new PublisherEndpoint(uuid, e.getMessage(), resource);
            endpoints.put(uuid, p);
            eventBus.fire("/db/init", p, new EventBusListener() {
                @Override
                public void completed(Object response) {
                    if (PublisherEndpoint.class.isAssignableFrom(response.getClass())) {
                        lookupConfig(e, PublisherEndpoint.class.cast(response));
                    }
                }

                @Override
                public void failed(Object response) {
                    if (PublisherEndpoint.class.isAssignableFrom(response.getClass())) {
                        error(e, PublisherEndpoint.class.cast(response));
                    }
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
        eventBus.fire("/db/config", p, new EventBusListener() {
            @Override
            public void completed(Object response) {
                if (PublisherEndpoint.class.isAssignableFrom(response.getClass())) {
                    // TODO: OK, we are ready to return the response.
                }
            }

            @Override
            public void failed(Object response) {
                if (PublisherEndpoint.class.isAssignableFrom(response.getClass())) {
                    error(e, PublisherEndpoint.class.cast(response));
                }
            }
        });
    }
}
