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
import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.annotation.Inject;

import java.io.IOException;

@AtmosphereInterceptorService
public class EnvelopeDigester implements AtmosphereInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(EnvelopeDigester.class);

    @Inject
    public EventBus eventBus;

    @Override
    public void configure(AtmosphereConfig config) {
    }

    @Override
    public Action inspect(AtmosphereResource r) {
        if (r.getRequest().getAttribute("dispatched") == null) {
            String message = IOUtils.readEntirely(r).toString();
            if (!message.isEmpty()) {
                try {
                    final Envelope e = mapper.readValue(message, Envelope.class);

                    if (e.getUuid() == null || e.getUuid().isEmpty()) {
                        e.setUuid(r.uuid());
                    }

                    logger.debug("\n\n{}\n\n", message);
                    eventBus.dispatch(e, r);
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
            r.getRequest().removeAttribute("dispatched");
        }
        return Action.CONTINUE;
    }

    @Override
    public void postInspect(AtmosphereResource r) {

    }
}
