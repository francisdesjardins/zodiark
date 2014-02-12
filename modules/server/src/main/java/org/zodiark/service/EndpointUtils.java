/*
 * Copyright 2013 Jeanfrancois Arcand
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
package org.zodiark.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.ReplyException;
import org.zodiark.service.db.Status;

import java.util.concurrent.ConcurrentHashMap;

public class EndpointUtils<T extends EndpointAdapter> {

    private final Logger logger = LoggerFactory.getLogger(EndpointUtils.class);

    private final EventBus eventBus;
    private final ObjectMapper mapper;
    private final ConcurrentHashMap<String, T> endpoints;

    public EndpointUtils(EventBus eventBus, ObjectMapper mapper, ConcurrentHashMap<String, T> endpoints) {
        this.eventBus = eventBus;
        this.mapper = mapper;
        this.endpoints = endpoints;
    }


    public void statusEvent(final String path, final Envelope e) {
        String uuid = e.getUuid();
        final T p = endpoints.get(uuid);
        statusEvent(path, e, p);
    }

    public void statusEvent(final String path, final Envelope e, final T p) {

        if (!validateAll(p, e)) ;


        statusEvent(path, e, p, new Reply<Status, String>() {
            @Override
            public void ok(Status status) {
                logger.trace("Status {}", status);
                response(e, p, constructMessage(path, writeAsString(status)));
            }

            @Override
            public void fail(ReplyException replyException) {
                error(e, p, constructMessage(path, writeAsString(new Error().error("Unauthorized"))));
            }
        });
    }

    public void statusEvent(final String path, final Envelope e, final T p, Reply<Status, String> r) {
        eventBus.message(path, new RetrieveMessage(p.uuid(), e.getMessage()), r);
    }

    public void passthroughEvent(final String path, final Envelope e) {
        passthroughEvent(path, e, retrieve(e.getUuid()));
    }

    public void passthroughEvent(final String path, final Envelope e, final T p) {

        if (!validate(p, e)) return;

        if (!validateShowId(p, e)) return;

        eventBus.message(path, new RetrieveMessage(p.uuid(), e.getMessage()), new Reply<String, String>() {
            @Override
            public void ok(String passthrough) {
                succesPassThrough(e, p, path, passthrough);
            }

            @Override
            public void fail(ReplyException replyException) {
                failPassThrough(e, p, replyException);
            }
        });
    }


    public boolean validate(T p, Envelope e) {
        if (p == null) {
            error(e, p, constructMessage("/error", writeAsString(new Error().error("Unauthorized"))));
            return false;
        }
        return true;
    }

    public boolean validateShowId(T p, Envelope e) {
        if (p == null) {
            error(e, p, constructMessage("/error", writeAsString(new Error().error("Unauthorized"))));
            return false;
        }
        return true;
    }

    public boolean validateAll(T p, Envelope e) {
        if (!validate(p, e)) return false;

        if (!validateShowId(p, e)) return false;

        return true;
    }

    public void succesPassThrough(Envelope e, T p, String path, String passthrough) {
        logger.trace("Passthrough succeed {}", passthrough);
        response(e, p, constructMessage(path, passthrough));
    }

    public void failPassThrough(Envelope e, T p, ReplyException passthrough) {
        logger.trace("Passthrough failed {}", passthrough);
        error(e, p, constructMessage(e.getMessage().getPath(), passthrough.throwable().getMessage()));
    }

    public void error(Envelope e, T endpoint, Message m) {
        AtmosphereResource r = endpoint.resource();

        // TODO: Validate
        endpoints.remove(endpoint);
        error(e, r, m);
    }

    public void error(Envelope e, AtmosphereResource r, Message m) {
        Envelope error = Envelope.newServerReply(e, m);
        eventBus.ioEvent(error, r);
    }

    public String writeAsString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }


    public Message constructMessage(String path, String status) {
        Message m = new Message();
        m.setPath(path);
        m.setData(status);
        return m;
    }

    /**
     * {@inheritDoc}
     */
    public void response(Envelope e, T endpoint, Message m) {
        AtmosphereResource r = endpoint.resource();
        Envelope newResponse = Envelope.newServerReply(e, m);
        try {
            r.write(mapper.writeValueAsString(newResponse));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {} {}", endpoint, m);
        }
    }

    public T retrieve(String uuid) {
        T p = endpoints.get(uuid);
        return p;
    }

}
