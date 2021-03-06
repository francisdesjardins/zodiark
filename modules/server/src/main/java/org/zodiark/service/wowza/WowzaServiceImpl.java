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
package org.zodiark.service.wowza;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.Context;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import javax.inject.Inject;

import org.zodiark.server.ReplyException;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.publisher.PublisherResults;
import org.zodiark.service.session.StreamingSession;

import java.io.IOException;

import static org.zodiark.protocol.Paths.ACTION_START;
import static org.zodiark.protocol.Paths.PUBLISHER_ABOUT_READY;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;
import static org.zodiark.protocol.Paths.WOWZA_CONNECT;
import static org.zodiark.protocol.Paths.WOWZA_DEOBFUSCATE;
import static org.zodiark.protocol.Paths.WOWZA_DEOBFUSCATE_OK;
import static org.zodiark.protocol.Paths.WOWZA_OBFUSCATE;
import static org.zodiark.protocol.Paths.WOWZA_OBFUSCATE_OK;

/**
 * A Service responsible for reacting to Wowza's messages and I/O Events.
 */
@On(Paths.SERVICE_WOWZA)
public class WowzaServiceImpl implements WowzaService {
    private final Logger logger = LoggerFactory.getLogger(WowzaServiceImpl.class);

    @Inject
    public EventBus evenBus;

    @Inject
    public WowzaEndpointManager wowzaManager;

    @Inject
    public Context context;

    @Inject
    public ObjectMapper mapper;

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
        String uuid = e.getUuid();
        switch (e.getMessage().getPath()) {
            case WOWZA_OBFUSCATE_OK:
                logger.debug("Obfuscation executed");
                try {
                    final WowzaMessage w = mapper.readValue(e.getMessage().getData(), WowzaMessage.class);
                    evenBus.message(RETRIEVE_PUBLISHER, w.getPublisherUUID(), new Reply<PublisherEndpoint, String>() {
                        @Override
                        public void ok(PublisherEndpoint p) {
                            try {
                                AtmosphereResource r = p.resource();
                                Message m = new Message();
                                m.setPath(ACTION_START);
                                m.setData(mapper.writeValueAsString(new PublisherResults("OK", w.getPublisherUUID())));
                                Envelope newResponse = Envelope.newPublisherRequest(p.uuid(), m);
                                r.write(mapper.writeValueAsString(newResponse));
                            } catch (JsonProcessingException e) {
                                logger.debug("Unable to write {}", e);
                            }
                        }

                        @Override
                        public void fail(ReplyException replyException) {
                            logger.error("No Publisher found", w.getPublisherUUID());
                        }
                    });
                } catch (IOException e1) {
                    logger.error("{}", e1);
                }
                break;
            case WOWZA_DEOBFUSCATE_OK:
                try {
                    final WowzaMessage w = mapper.readValue(e.getMessage().getData(), WowzaMessage.class);
                    evenBus.message(PUBLISHER_ABOUT_READY, w.getPublisherUUID());
                } catch (IOException e1) {
                    logger.error("{}", e1);
                }
                break;
            case WOWZA_CONNECT:
                WowzaEndpoint endpoint = wowzaManager.lookup(uuid);
                if (endpoint == null) {
                    connected(e, r);
                }
        }
    }


    @Override
    public void reactTo(String path, Object message, Reply reply) {

        switch (path) {
            case WOWZA_OBFUSCATE:
                StreamingSession session = StreamingSession.class.cast(message);
                WowzaEndpoint w = wowzaManager.lookup(session.publisher().wowzaServerUUID());
                if (w != null) {
                    w.obfuscate(session, reply);
                } else {
                    reply.fail(ReplyException.DEFAULT);
                }
                break;
            case WOWZA_DEOBFUSCATE:
                session = StreamingSession.class.cast(message);
                w = wowzaManager.lookup(session.publisher().wowzaServerUUID());
                if (w != null) {
                    w.deobfuscate(session, reply);
                } else {
                    reply.fail(ReplyException.DEFAULT);
                }
                break;
            case WOWZA_CONNECT:
                if (EndpointAdapter.class.isAssignableFrom(message.getClass())) {
                    EndpointAdapter p = EndpointAdapter.class.cast(message);
                    w = wowzaManager.lookup(p.wowzaServerUUID());
                    if (w != null) {
                        w.isEndpointConnected(p, reply);
                    } else {
                        reply.fail(ReplyException.DEFAULT);
                    }
                }
                break;
            default:
                logger.error("Unhandled event {}", path);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(Envelope e, AtmosphereResource r) {

        String uuid = e.getUuid();
        // Message contains the geo-localisation of the client.
        Message m = e.getMessage();

        wowzaManager.bind(context.newInstance(WowzaEndpoint.class).uuid(uuid).message(m).resource(r));
        Message responseMessage = new Message();
        responseMessage.setPath(WOWZA_CONNECT);
        try {
            responseMessage.setData(mapper.writeValueAsString(new WowzaResults("OK")));
        } catch (JsonProcessingException e1) {
            ;
        }
        response(e, r, responseMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void response(Envelope e, AtmosphereResource r, Message m) {
        Envelope newResponse = Envelope.newServerReply(e, m);
        try {
            r.write(mapper.writeValueAsString(newResponse));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {}", e);
        }
    }
}
