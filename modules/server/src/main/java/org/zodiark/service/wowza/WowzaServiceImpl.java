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
package org.zodiark.service.wowza;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.Context;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.publisher.PublisherEndpoint;

@On("/wowza/{action}")
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
    public void serve(Envelope e, AtmosphereResource r, EventBusListener l) {
        String uuid = e.getUuid();
        WowzaEndpoint endpoint = wowzaManager.lookup(uuid);
        Message m = e.getMessage();
        if (endpoint == null) {
            connected(e, r);
        }

    }

    // Will be called when the Publisher is ready to start a streaming show
    @Override
    public void serve(String event, Object message, EventBusListener l) {
        if (PublisherEndpoint.class.isAssignableFrom(message.getClass())) {
            PublisherEndpoint p = PublisherEndpoint.class.cast(message);
            WowzaEndpoint w = wowzaManager.lookup(p.wowzaServerUUID());
            w.isReady(p, l);
        }
    }

    @Override
    public void connected(Envelope e, AtmosphereResource r) {

        String uuid = e.getUuid();
        // Message contains the geo-localisation of the client.
        Message m = e.getMessage();

        wowzaManager.bind(context.newInstance(WowzaEndpoint.class).uuid(uuid).message(m).resource(r));
        Message responseMessage = new Message();
        // TODO: m.setPath
        m.setData("OK");
        response(e, r, responseMessage);
    }

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
