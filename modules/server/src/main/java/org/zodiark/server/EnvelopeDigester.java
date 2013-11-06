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
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.annotation.Inject;

import java.io.IOException;

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

    public void onRequest(AtmosphereResource r) throws IOException {
        String message = IOUtils.readEntirely(r).toString();
        if (!message.isEmpty()) {
            try {
                logger.debug("\n\n{}\n\n", message);
                Envelope e = mapper.readValue(message, Envelope.class);
                eventBus.dispatch(e, r);
            } catch (Exception ex) {
                logger.error("", ex);
                Envelope e = Envelope.newError(r.uuid());
                r.write(mapper.writeValueAsString(e)).close();
            }
        }
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        logger.trace("onRequest {}", event.getResource().uuid());
    }

}
