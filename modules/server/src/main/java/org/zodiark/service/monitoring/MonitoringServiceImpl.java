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
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Paths;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;

@On("/monitor")
public class MonitoringServiceImpl implements MonitoringService {

    private final Logger logger = LoggerFactory.getLogger(MonitoringServiceImpl.class);

    @Inject
    public EventBus eventBus;

    @Override
    public void serve(Envelope e, AtmosphereResource r) {

    }

    @Override
    public void serve(String event, Object message, EventBusListener l) {
        switch (event) {
            case Paths.MONITOR_RESOURCE:
                final AtmosphereResource r = AtmosphereResource.class.cast(message);
                r.addEventListener(new AtmosphereResourceEventListenerAdapter() {
                    @Override
                    public void onDisconnect(AtmosphereResourceEvent event) {
                        logger.trace("{} disconnected with {}", r, event);

                        eventBus.dispatch(Paths.RETRIEVE_PUBLISHER, r.uuid(), new EventBusListener<EndpointAdapter>() {
                            @Override
                            public void completed(EndpointAdapter p) {
                                eventBus.dispatch(Paths.DISCONNECTED_RESOURCE, p);
                            }

                            @Override
                            public void failed(EndpointAdapter p) {
                                logger.error("", p);
                            }
                        });
                    }
                });
                break;
        }
    }
}
