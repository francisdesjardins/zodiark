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
package org.zodiark.server.impl;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.util.DefaultEndpointMapper;
import org.atmosphere.util.EndpointMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.service.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Synchronous EventBus implementation.
 */
public class DefaultEventBus implements EventBus {
    private final Logger logger = LoggerFactory.getLogger(DefaultEventBus.class);

    private final EndpointMapper<Service> mapper = new DefaultEndpointMapper<>();
    private final ConcurrentHashMap<String, Service> ioServices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Service> services = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<PathTransformer> transformers = new ConcurrentLinkedQueue<>();
    private final Reply l = new Reply() {
        @Override
        public void ok(Object response) {
            logger.trace("completed", response);
        }

        @Override
        public void fail(Object response) {
            logger.trace("failed", response);
        }
    };

    public DefaultEventBus() {
        transformers.add(new PathTransformer() {
            @Override
            public String transform(String path) {
                if (path.startsWith("_")) {
                    return path.substring(path.indexOf("/"));
                }
                return path;
            }
        }) ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus ioEvent(Envelope e, AtmosphereResource o) {
        return ioEvent(e, o, l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus ioEvent(Envelope e, AtmosphereResource r, Reply reply) {
        try {
            Service s = mapper.map(e.getMessage().getPath(), ioServices);
            logger.debug("Dispatching Envelop {} to Service {}", e, s);

            if (e.getUuid().isEmpty() || e.getUuid().equals("0")) {
                e.setUuid(r.uuid());
            }

            if (s != null) {
                s.reactTo(e, r, reply);
            } else {
                logger.error("No Service available for {}", e);
                sendError(e, r);
            }
        } catch (Throwable t) {
            logger.error("Error on {}", e, t);
            sendError(e, r);
        }
        return this;
    }

    private void sendError(Envelope e, AtmosphereResource r) {
        Message m = new Message();
        m.setPath("/error");
        m.setUUID(e.getMessage().getUUID());
        Envelope error = Envelope.newServerReply(e, m);
        ioEvent(error, r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus message(String path, Object message, Reply reply) {
        try {
            Service s = mapper.map(path, services);
            if (s == null) {
                s = mapper.map(path, ioServices);
            }
            logger.debug("Dispatching Message {} to {}", path, s);

            if (s != null) {
                s.reactTo(transform(path), message, reply);
            } else {
                logger.error("No Service available for {}", path);
            }
        } catch (Throwable t) {
            logger.error("Error on {}", path, t);
        }
        return this;
    }

    private String transform(String path) {
        for (PathTransformer t : transformers) {
            try {
                path = t.transform(path);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus message(String path, Object message) {
        return message(path, message, l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus onIOEvent(String path, Service service) {
        logger.debug("{} => {}", path, service);
        ioServices.put(path, service);
        return this;
    }

    @Override
    public EventBus on(String path, Service service) {
        logger.debug("{} => {}", path, service);
        services.put(path, service);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventBus off(String path) {
        services.remove(path);
        return this;
    }

    @Override
    public EventBus pathTransformer(PathTransformer transformer) {
        transformers.add(transformer);
        return this;
    }

    public Service service(Class<? extends Service> clazz) {
        Collection<Service> i = ioServices.values();
        for (Service s : i) {
            if (clazz.isAssignableFrom(s.getClass())) {
                return s;
            }
        }
        return null;
    }
}
