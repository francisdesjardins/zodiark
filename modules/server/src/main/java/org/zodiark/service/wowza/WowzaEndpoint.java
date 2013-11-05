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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.service.Endpoint;
import org.zodiark.service.util.UUID;

import java.util.LinkedList;
import java.util.List;

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

    @Override
    public TYPE type() {
        return TYPE.WOOZA;
    }

    @Override
    public void terminate() {

    }

    @Override
    public String uuid() {
        return uuid;
    }

    public List<Endpoint> supportedEndpoints() {
        return supportedEndpoints;
    }

    public void isEndpointConnected(Endpoint p, EventBusListener l) {
        Message m = new Message();
        m.setPath(Paths.SERVER_VALIDATE_OK);
        try {
            m.setData(mapper.writeValueAsString(new UUID(p.uuid())));

            Envelope e = Envelope.newServerRequest(Paths.REQUEST_ACTION, uuid, m);
            resource.write(mapper.writeValueAsString(e));
        } catch (Exception e) {
            logger.error("", e);
            l.failed(p);
        }

    }

    public WowzaEndpoint uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public WowzaEndpoint message(Message m) {
        this.message = m;
        return this;
    }

    public WowzaEndpoint resource(AtmosphereResource r) {
        this.resource = r;
        return this;
    }
}
