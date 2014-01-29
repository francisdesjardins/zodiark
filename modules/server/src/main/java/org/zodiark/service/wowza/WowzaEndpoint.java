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
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.service.Endpoint;
import org.zodiark.service.session.StreamingSession;
import org.zodiark.service.subscriber.SubscriberEndpoint;
import org.zodiark.service.util.UUID;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.zodiark.protocol.Paths.*;

/**
 * Represent a Wowza endpoint.
 */
public class WowzaEndpoint implements Endpoint {

    private final Logger logger = LoggerFactory.getLogger(WowzaEndpoint.class);

    @Inject
    public ObjectMapper mapper;

    private String uuid;
    private final List<Endpoint> supportedEndpoints = new LinkedList<Endpoint>();
    private AtmosphereResource resource;
    private Message message;

    public WowzaEndpoint() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TYPE type() {
        return TYPE.WOWZA;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        // TODO: Not Implemented
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String uuid() {
        return uuid;
    }

    /**
     * TODO: For later use
     * @return list of Endpoint
     */
    public List<Endpoint> supportedEndpoints() {
        return supportedEndpoints;
    }

    /**
     * Send a {@link Paths#SERVER_VALIDATE_OK} to Wowza and listen for the response. The response will be delivered
     * to the {@link org.zodiark.service.publisher.PublisherService}
     * <p/>
     * {@link Paths#START_PUBLISHER_STREAMING_SESSION} if the streaming session is accepted.
     * </p>
     * {@link Paths#FAILED_PUBLISHER_STREAMING_SESSION} if the streaming session is not accepted, for whatever reason.
     *
     * @param p
     * @param l
     */
    public void isEndpointConnected(Endpoint p, Reply l) {
        Message m = new Message();
        m.setPath(SERVER_VALIDATE_OK);
        try {
            m.setData(mapper.writeValueAsString(new UUID(p.uuid())));

            Envelope e = Envelope.newServerRequest(REQUEST_ACTION, uuid, m);
            resource.write(mapper.writeValueAsString(e));
        } catch (Exception e) {
            logger.error("", e);
            l.fail(p);
        }

    }

    /**
     * Set the Wowza UUID
     *
     * @param uuid Wowza UUID
     * @return this
     */
    public WowzaEndpoint uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Set a {@link Message} who most probably will contains some information like geo localisation.
     *
     * @param m a {@link Message}
     * @return this.
     */
    public WowzaEndpoint message(Message m) {
        this.message = m;
        return this;
    }

    /**
     * Set the {@link AtmosphereResource} representing the underlying connection.
     *
     * @param r {@link AtmosphereResource}
     * @return this
     */
    public WowzaEndpoint resource(AtmosphereResource r) {
        this.resource = r;
        return this;
    }

    /**
     * Obfuscate {@link SubscriberEndpoint} that are not allowed to participate to a streaming session. This
     * method will send an {@link Envelope} to the targeted remote Wowza endpoint with a list of {@link SubscriberEndpoint}
     * 's uuid that must be obfuscated.
     *
     * @param session the current {@link StreamingSession}
     * @param reply   a {@link Reply} in case a failure occurs. If the operation works, an I/O events will be delivered
     *                and no {@link Reply#ok(Object)} will be called.
     */
    public void obfuscate(StreamingSession session, Reply reply) {
        String executorUuid = session.pendingAction().getSubscriberUUID();
        List<String> uuids = new ArrayList<>();

        for (SubscriberEndpoint s : session.subscribers()) {
            if (!s.uuid().equalsIgnoreCase(executorUuid)) {
                uuids.add(s.uuid());
            }
        }

        WowzaMessage w = new WowzaMessage(uuids);
        w.setPublisherUUID(session.publisher().uuid());
        Message m = new Message();

        m.setPath(WOWZA_OBFUSCATE);
        try {
            m.setData(mapper.writeValueAsString(w));

            Envelope e = Envelope.newServerRequest(REQUEST_ACTION, uuid, m);
            resource.write(mapper.writeValueAsString(e));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
            reply.fail(session.pendingAction());
        }

    }

    /**
     * The {@link org.zodiark.service.action.Action} completed, send a message to the remote Wowza with a list
     * of {@link SubscriberEndpoint} that can now be re-added to a streaming session. If the operation works, an I/O events will be delivered
     * and no {@link Reply#ok(Object)} will be called.
     *
     * @param session {@link StreamingSession}
     * @param reply   a {@link Reply} in case a failure occurs.
     */
    public void deobfuscate(StreamingSession session, Reply reply) {
        String executorUuid = session.pendingAction().getSubscriberUUID();
        List<String> uuids = new ArrayList<>();

        for (SubscriberEndpoint s : session.subscribers()) {
            if (!s.uuid().equalsIgnoreCase(executorUuid)) {
                uuids.add(s.uuid());
            }
        }

        WowzaMessage w = new WowzaMessage(uuids);
        w.setPublisherUUID(session.publisher().uuid());
        Message m = new Message();

        m.setPath(WOWZA_DEOBFUSCATE);
        try {
            m.setData(mapper.writeValueAsString(w));

            Envelope e = Envelope.newServerRequest(MESSAGE_ACTION, uuid, m);
            resource.write(mapper.writeValueAsString(e));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
            reply.fail(session.pendingAction());
        }

    }
}
