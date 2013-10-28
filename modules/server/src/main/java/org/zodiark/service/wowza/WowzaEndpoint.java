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

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Message;
import org.zodiark.server.Endpoint;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.publisher.PublisherEndpoint;

import java.util.LinkedList;
import java.util.List;

/**
 * Represent a Wowza endpoint.
 */
public class WowzaEndpoint implements Endpoint {

    private String uuid;
    private String uri;
    private final List<Endpoint> supportedEndpoints = new LinkedList<Endpoint>();
    private AtmosphereResource resource;
    private Message message;

    public WowzaEndpoint(){}

    public String uri() {
        return uri;
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
        return null;
    }

    public List<Endpoint> supportedEndpoints() {
        return supportedEndpoints;
    }

    public void isReady(PublisherEndpoint p, EventBusListener l) {

        // TODO: Send a message to wowza.
        //resource.write();
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
