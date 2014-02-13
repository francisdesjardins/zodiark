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

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.zodiark.protocol.From;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Path;
import org.zodiark.protocol.Protocol;
import org.zodiark.protocol.To;
import org.zodiark.protocol.TraceId;
import javax.inject.Inject;

import java.io.IOException;
import java.util.HashMap;

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
                    final Envelope e = newEnvelope(message);

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

    public final Envelope newEnvelope(String m) throws IOException {
        HashMap<String, Object> envelope = (HashMap<String, Object>) mapper.readValue(m, HashMap.class);
        return newEnvelope(envelope);
    }

    public final Envelope newEnvelope(HashMap<String, Object> envelope) throws IOException {
        HashMap<String, Object> message = (HashMap<String, Object>) envelope.get("message");
        return new Envelope.Builder()
                .path(new Path(notNull(envelope.get("path"))))
                .to(new To(notNull(envelope.get("to"))))

                .from(new From(notNull(envelope.get("from"))))
                .traceId(new TraceId((int) envelope.get("traceId")))
                .message(new Message().setPath(notNull(message.get("path")))
                        .setData(notNull(encodeJSON(message.get("data"))))
                        .setUUID(notNull(message.get("uuid"))))
                .protocol(new Protocol(notNull(envelope.get("protocol"))))
                .uuid(notNull(envelope.get("uuid")))
                .build();
    }

    private String encodeJSON(Object data) throws JsonProcessingException {
        if (String.class.isAssignableFrom(data.getClass())) {
            return data.toString();
        }

        return mapper.writeValueAsString(data);
    }

    private String notNull(Object o) {
        return o == null ? "" : o.toString();
    }
}
