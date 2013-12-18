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
package org.zodiark.service.monitoring;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;

import static org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter.OnDisconnect;
import static org.zodiark.protocol.Paths.DISCONNECTED_RESOURCE;
import static org.zodiark.protocol.Paths.MONITOR_RESOURCE;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;

/**
 * Monitor {@link org.zodiark.service.Endpoint} disconnect.
 */
@On("/monitor")
public class MonitoringServiceImpl implements MonitoringService {

    private final Logger logger = LoggerFactory.getLogger(MonitoringServiceImpl.class);

    @Inject
    public EventBus eventBus;

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
    }

    @Override
    public void reactTo(String path, Object message, Reply reply) {
        switch (path) {
            case MONITOR_RESOURCE:
                final AtmosphereResource r = AtmosphereResource.class.cast(message);
                r.addEventListener(new OnDisconnect() {
                    @Override
                    public void onDisconnect(AtmosphereResourceEvent event) {
                        logger.trace("{} disconnected with {}", r, event);

                        // TODO: This message goes into the void right now
                        eventBus.message(RETRIEVE_PUBLISHER, r.uuid(), new Reply<EndpointAdapter>() {
                            @Override
                            public void ok(EndpointAdapter p) {
                                // TODO: This message goes into the void right now
                                eventBus.message(DISCONNECTED_RESOURCE, p);
                            }

                            @Override
                            public void fail(EndpointAdapter p) {
                                logger.error("", p);
                            }
                        });
                    }
                });
                break;
        }
    }
}
