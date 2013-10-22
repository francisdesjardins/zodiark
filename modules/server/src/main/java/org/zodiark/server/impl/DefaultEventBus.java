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
package org.zodiark.server.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEventBus implements EventBus {

    private final static Logger logger = LoggerFactory.getLogger(DefaultEventBus.class);
    private final ConcurrentHashMap<String, EventBusListener> services = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public EventBus fire(AtmosphereResource r, Envelope e) {
        logger.debug("Dispatching Envelop {} to {}", e, r.uuid());

        if (e.getUuid().isEmpty()) {
            e.setUuid(r.uuid());
        }

        EventBusListener eventBusListener = services.get(e.getMessage().getPath().toString());
        if (eventBusListener != null) {
            Envelope response  = eventBusListener.on(r, e);
            if (response != null) {
                try {
                    r.getResponse().write(mapper.writeValueAsString(response));
                } catch (IOException e1) {
                   logger.error("{}", e);
                }
            }
        }
        return this;
    }

    @Override
    public EventBus on(String path, EventBusListener e) {
        logger.debug("{} => {}", path, e);
        services.put(path, e);
        return this;
    }
}
