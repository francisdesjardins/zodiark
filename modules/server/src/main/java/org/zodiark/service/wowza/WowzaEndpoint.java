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
import org.zodiark.server.Endpoint;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.publisher.PublisherEndpoint;

import java.util.List;

/**
 * Represent a Wowza endpoint.
 */
public class WowzaEndpoint implements Endpoint {

    private final String uri;
    private final List<Endpoint> supportedEndpoints;
    private final AtmosphereResource resource;

    public WowzaEndpoint(AtmosphereResource resource, String uri, List<Endpoint> supportedEndpoints) {
        this.uri = uri;
        this.supportedEndpoints = supportedEndpoints;
        this.resource = resource;
    }

    public String uri() {
        return uri;
    }

    @Override
    public TYPE type() {
        return TYPE.WOOZA;
    }

    public List<Endpoint> supportedEndpoints() {
        return supportedEndpoints;
    }

    public void isConnected(PublisherEndpoint p, EventBusListener l) {

        // TODO: Send a message to wowza.
        //resource.write();
    }
}
