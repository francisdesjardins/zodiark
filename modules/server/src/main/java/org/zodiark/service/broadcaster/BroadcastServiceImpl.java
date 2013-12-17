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
package org.zodiark.service.broadcaster;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.MetaBroadcaster;
import org.atmosphere.cpr.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.EnvelopeDigester;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.io.IOException;
import java.io.OutputStream;

import static org.atmosphere.cpr.ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID;
import static org.zodiark.protocol.Paths.BROADCASTER_CREATE;
import static org.zodiark.protocol.Paths.BROADCASTER_TRACK;
import static org.zodiark.protocol.Paths.BROADCAST_TO_ALL;
import static org.zodiark.protocol.Paths.DB_WORD;

/**
 * Create {@link Broadcaster}, or channel, used by {@link PublisherEndpoint} and {@link SubscriberEndpoint} to communicate.
 * <p/>
 * A call to the database is always executed in order to retrieve the session information like banned words, etc.
 */
@On({"/chat", "/broadcaster"})
public class BroadcastServiceImpl implements BroadcasterService {
    private Logger logger = LoggerFactory.getLogger(BroadcastServiceImpl.class);

    @Inject
    public ObjectMapper mapper;

    @Inject
    public EventBus eventBus;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(Envelope e, AtmosphereResource r) {
        dispatchMessage(e, r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(String path, Object message, Reply reply) {
        switch (path) {
            case BROADCASTER_CREATE:
                createBroadcaster(PublisherEndpoint.class.cast(message));
                break;
            case BROADCASTER_TRACK:
                associatedSubscriber(SubscriberEndpoint.class.cast(message));
                break;
            case BROADCAST_TO_ALL: {
                broadcastToAll(message);
                break;
            }
            default:
                logger.error("Unhandled message {}", message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void broadcastToAll(Object message) {
        if (PublisherEndpoint.class.isAssignableFrom(message.getClass())) {
            PublisherEndpoint p = PublisherEndpoint.class.cast(message);

            // TODO: Service for retrieving the proper message.
            String m = p + " is about to start a new streaming session";

            logger.debug("About to broadcast to all connected Endpoint {}", m);
            MetaBroadcaster.getDefault().broadcastTo("/*", m);
        }
    }

    public void dispatchMessage(final Envelope e, final AtmosphereResource r) {
        final AtmosphereRequest request = r.getRequest();
        final AtmosphereResponse response = r.getResponse();

        logger.debug("Dispatch {}", e.getMessage().getPath());

        request.pathInfo(e.getMessage().getPath()).body(e.getMessage().getData());
        r.getRequest().setAttribute(EnvelopeDigester.REQUEST_REDISPATCHED, Boolean.TRUE);

        try {
            r.getAtmosphereConfig().framework().doCometSupport(request, response);
        } catch (Exception e1) {
            eventBus.message("/error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createBroadcaster(final PublisherEndpoint p) {
        endpointBroadcaster(p.resource(), p);
    }

    private void endpointBroadcaster(final AtmosphereResource r, final EndpointAdapter p) {
        final String uuid = p == null ? r.uuid() : p.uuid();

        AtmosphereFramework f = r.getAtmosphereConfig().framework();
        final Broadcaster b = f.getBroadcasterFactory().lookup("/chat/" + uuid, true);

        eventBus.message(DB_WORD, p, new Reply<BroadcasterDBResult>() {

            @Override
            public void ok(BroadcasterDBResult result) {
                b.getBroadcasterConfig().addFilter(new WordBroadcastFilter(result));
            }

            @Override
            public void fail(BroadcasterDBResult result) {
                logger.error("{}", result);
            }
        });
        r.setBroadcaster(b);

        r.getRequest().setAttribute(SUSPENDED_ATMOSPHERE_RESOURCE_UUID, null);

        b.addAtmosphereResource(r);

        logger.debug("Created {}", b.getID());

        r.setSerializer(new Serializer() {
            @Override
            public void write(OutputStream os, Object o) throws IOException {
                if (Envelope.class.isAssignableFrom(o.getClass())) {
                    os.write(mapper.writeValueAsBytes(o));
                } else {
                    r.getRequest().removeAttribute(EnvelopeDigester.REQUEST_REDISPATCHED);
                    b.removeAtmosphereResource(r);
                    Message m = new Message();
                    m.setPath("/chat/" + uuid);
                    m.setData(o.toString());
                    byte[] message = mapper.writeValueAsBytes(Envelope.newServerToSubscriberResponse(uuid, m));
                    os.write(message);
                }
            }
        });
        logger.debug("Endpoint {} created Broadcaster {}", p, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void associatedSubscriber(SubscriberEndpoint s) {
        endpointBroadcaster(s.resource(), s.publisherEndpoint());
    }
}
