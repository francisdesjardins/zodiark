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
package org.zodiark.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.FrameworkConfig;
import org.atmosphere.cpr.Serializer;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.annotation.Inject;

import java.io.IOException;
import java.io.OutputStream;

import static org.atmosphere.cpr.ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID;

@AtmosphereHandlerService(
        path = "/",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class,
                HeartbeatInterceptor.class,
                TrackMessageSizeInterceptor.class}
)
public class EnvelopeDigester extends AtmosphereHandlerAdapter {

    private final ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(EnvelopeDigester.class);

    @Inject
    public EventBus eventBus;

    public void onRequest(final AtmosphereResource r) throws IOException {
        String message = IOUtils.readEntirely(r).toString();
        if (!message.isEmpty()) {
            try {
                logger.debug("\n\n{}\n\n", message);
                final Envelope e = mapper.readValue(message, Envelope.class);

                if (e.getUuid().isEmpty()) {
                    e.setUuid(r.uuid());
                }

                // TODO: Dangerous
                if (!e.getMessage().getPath().startsWith("/chat")) {
                    eventBus.dispatch(e, r);
                } else {
                    final AtmosphereRequest request = r.getRequest();
                    final AtmosphereResponse response = r.getResponse();

                    // Prevent Atmosphere from ignoring multi level Atmosphere dispatch
                    request.setAttribute(SUSPENDED_ATMOSPHERE_RESOURCE_UUID, null);

                    // We redispatch the request to Atmosphere, we create a virtual Broadcaster
                    final Broadcaster b = r.getAtmosphereConfig().getBroadcasterFactory().lookup(e.getMessage().getPath(), true);
                    b.addAtmosphereResource(r);

                    r.setBroadcaster(b);
                    request.pathInfo(e.getMessage().getPath())
                        .body(e.getMessage().getData()).attributes().put(FrameworkConfig.INJECTED_ATMOSPHERE_RESOURCE, r);

                    r.setSerializer(new Serializer() {
                        @Override
                        public void write(OutputStream os, Object o) throws IOException {
                            Message m = new Message();
                            m.setPath(e.getMessage().getPath());
                            m.setData(o.toString());
                            byte[] message = mapper.writeValueAsBytes(Envelope.newServerToSubscriberResponse(m));
                            try {
                                os.write(message);
                            } finally {
                                b.removeAtmosphereResource(r);
                                request.destroy(true);
                                response.destroy(true);
                            }
                        }
                    });

                    r.getAtmosphereConfig().framework().doCometSupport(request.destroyable(false), response.destroyable(false));
                }
            } catch (Exception ex) {
                logger.error("", ex);
                Envelope e = Envelope.newError(r.uuid());
                r.write(mapper.writeValueAsString(e)).close();
            }
        } else {
            logger.debug("Empty envelope for {}", r);
        }
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        logger.trace("onRequest {}", event.getResource().uuid());
    }

}
