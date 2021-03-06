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
package org.zodiark.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;

import javax.inject.Inject;
import java.io.IOException;

/**
 * An {@link AtmosphereInterceptor} that intercept requests and dispatch them to the {@link EventBus} when the request's body
 * can be deserialized as a {@link Envelope}, or dispatched to Atmosphere for normal request like decoded chat.
 */
@AtmosphereInterceptorService
public class EnvelopeDigester extends AtmosphereInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(EnvelopeDigester.class);
    public final static String REQUEST_REDISPATCHED = EnvelopeDigester.class.getName() + ".dispatched";

    @Inject
    public ObjectMapper mapper;

    @Inject
    public EventBus eventBus;

    @Override
    public Action inspect(AtmosphereResource r) {
        // Service may redispatch the request to Atmosphere, hence we must make sure we aren't processing
        // dispatched request and let them delivered to Atmosphere components directly.
        if (r.getRequest().getAttribute(REQUEST_REDISPATCHED) == null) {
            String message = IOUtils.readEntirely(r).toString();
            if (!message.isEmpty()) {
                try {
                    logger.debug("Original Message {}", message);
                    final Envelope e = Envelope.newEnvelope(message, mapper);

                    if (e.getUuid() == null || e.getUuid().isEmpty()) {
                        e.setUuid(r.uuid());
                    }

                    logger.debug("\n\n{}\n\n", message);
                    eventBus.ioEvent(e, r);
                } catch (Exception ex) {
                    logger.error("", ex);
                    Envelope e = Envelope.newError(r.uuid());
                    try {
                        r.write(mapper.writeValueAsString(e)).close();
                    } catch (IOException e1) {
                        logger.error("{}", e1);
                    }
                }
                return Action.SKIP_ATMOSPHEREHANDLER;
            }
        } else {
            logger.debug("Redispatching {}", r);
            r.getRequest().removeAttribute(REQUEST_REDISPATCHED);
        }
        return Action.CONTINUE;
    }
}
