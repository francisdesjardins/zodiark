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

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.util.DefaultEndpointMapper;
import org.atmosphere.util.EndpointMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Synchronous EventBus implementation.
 */
public class DefaultEventBus implements EventBus {

    private final EndpointMapper<Service> mapper = new DefaultEndpointMapper<>();
    private final static Logger logger = LoggerFactory.getLogger(DefaultEventBus.class);
    private final ConcurrentHashMap<String, Service> services = new ConcurrentHashMap<>();
    private final EventBusListener l = new EventBusListener() {
        @Override
        public void completed(Object response) {
            logger.trace("completed", response);
        }

        @Override
        public void failed(Object response) {
            logger.trace("failed", response);
        }
    };

    @Override
    public EventBus fire(Envelope e, AtmosphereResource o) {
        return fire(e, o, l);
    }

    @Override
    public EventBus fire(Envelope e, AtmosphereResource o, EventBusListener l) {
        Service s = mapper.map(e.getMessage().getPath(), services); //services.get(e.getMessage().getPath().toString());

        if (AtmosphereResource.class.isAssignableFrom(o.getClass())) {
            AtmosphereResource r = AtmosphereResource.class.cast(o);
            logger.debug("Dispatching Envelop {} to {}", e, r.uuid());

            if (e.getUuid().isEmpty()) {
                e.setUuid(r.uuid());
            }

        }

        if (s != null) {
            s.serve(e, o, l);
        } else {
            // error();
            logger.debug(("Unmapped Service {}"), e);
        }
        return this;
    }

    @Override
    public EventBus fire(String e, Object r, EventBusListener l) {
        return fire(e, r, l);
    }

    @Override
    public EventBus fire(String message, Object o) {
        Service s = mapper.map(message, services); //services.get(message);
        s.serve(message, o, l);
        return this;
    }

    @Override
    public EventBus on(String eventName, Service e) {
        logger.debug("{} => {}", eventName, e);
        services.put(eventName, e);
        return this;
    }

    @Override
    public EventBus off(String eventName) {
        return null;
    }

    @Override
    public Service service(Class<? extends Service> clazz) {
        return null;
    }
}
