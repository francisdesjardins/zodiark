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
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.OnMessage;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.service.ServiceLocator;
import org.zodiark.server.service.ServiceLocatorFactory;

import java.io.IOException;

@AtmosphereHandlerService(
        path = "/",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class,
                BroadcastOnPostAtmosphereInterceptor.class,
                HeartbeatInterceptor.class,
                TrackMessageSizeInterceptor.class}
)
public class ZodiarkDispatcher extends OnMessage<String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ZodiarkDispatcher.class);
    private final ServiceLocator serviceLocator = ServiceLocatorFactory.getDefault().locator();

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        try {
            logger.trace("{}", message);
            Envelope e = mapper.readValue(message, Envelope.class);
            serviceLocator.dispatch(response.resource(), e);
        } catch (Exception ex) {
            logger.error("", ex);
            response.resource().close();
        }
    }

}
