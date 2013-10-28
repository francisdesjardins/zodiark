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
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Service;
import org.zodiark.service.publisher.PublisherEndpoint;

@On("/register/{endpoint}/")
public class WowzaService implements Service {

    @Inject
    public EventBus evenBus;

    @Inject
    public WowzaEndpointService wowzaService;


    @Override
    public void on(Envelope e, AtmosphereResource r, EventBusListener l) {

        // TODO We are getting the response from Wowza. We need to dispatch
        // (1) we are getting called when wowza client connect
        // (2) we are getting called when the Publisher is getting accepted
        // (3) Notify the LiveSession service we are ready.
        // (4) The Liveshow will creates call back the Publisher so it can connect.


    }

    @Override
    public void on(Object message, EventBusListener l) {
        if (PublisherEndpoint.class.isAssignableFrom(message.getClass())) {
            PublisherEndpoint p = PublisherEndpoint.class.cast(message);
            WowzaEndpoint w = wowzaService.lookup(p.wowzaServer());
            w.isReady(p, l);
        }
    }

}
