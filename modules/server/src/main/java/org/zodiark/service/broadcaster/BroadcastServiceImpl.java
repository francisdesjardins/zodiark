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
package org.zodiark.service.broadcaster;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.io.IOException;
import java.io.OutputStream;

import static org.atmosphere.cpr.ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID;

@On({"/chat", "/broadcaster"})
public class BroadcastServiceImpl implements BroadcasterService {
    private Logger logger = LoggerFactory.getLogger(BroadcastServiceImpl.class);

    @Inject
    public ObjectMapper mapper;

    @Inject
    public EventBus eventBus;

    @Override
    public void serve(Envelope e, AtmosphereResource r) {
        dispatchMessage(e, r);
    }

    @Override
    public void serve(String event, Object message, EventBusListener l) {
        switch (event) {
            case Paths.BROADCASTER_CREATE:
                createBroadcaster(PublisherEndpoint.class.cast(message));
                break;
            case Paths.BROADCASTER_TRACK:
                associatedSubscriber(SubscriberEndpoint.class.cast(message));
                break;
            default:
                logger.error("Unhandled message {}", message);
        }
    }

    public void dispatchMessage(final Envelope e, final AtmosphereResource r) {
        final AtmosphereRequest request = r.getRequest();
        final AtmosphereResponse response = r.getResponse();

        logger.debug("Dispatch {}", e.getMessage().getPath());

        request.pathInfo(e.getMessage().getPath()).body(e.getMessage().getData());
        r.getRequest().setAttribute("dispatched", Boolean.TRUE);

        try {
            r.getAtmosphereConfig().framework().doCometSupport(request, response);
        } catch (Exception e1) {
            eventBus.dispatch("/error", e);
        }
    }

    @Override
    public void createBroadcaster(final PublisherEndpoint p) {
        endpointBroadcaster(p.resource(), p);
    }

    private void endpointBroadcaster(final AtmosphereResource r, final EndpointAdapter p) {
        final String uuid = p == null ? r.uuid() : p.uuid();

        AtmosphereFramework f = r.getAtmosphereConfig().framework();
        final Broadcaster b = f.getBroadcasterFactory().lookup("/chat/" + uuid, true);

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
                    r.getRequest().removeAttribute("dispatched");
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

    @Override
    public void associatedSubscriber(SubscriberEndpoint s) {
        endpointBroadcaster(s.resource(), s.publisherEndpoint());
    }
}
