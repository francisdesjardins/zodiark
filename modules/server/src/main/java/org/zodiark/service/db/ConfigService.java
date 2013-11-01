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
package org.zodiark.service.db;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.PublisherConfig;
import org.zodiark.service.util.RESTService;

@On("/db/config")
public class ConfigService implements DBService {

    @Inject
    public RESTService restService;

    @Override
    public void serve(Envelope e, AtmosphereResource r, EventBusListener l) {
    }

    @Override
    public void serve(String event, Object message, EventBusListener l) {
        if (EndpointAdapter.class.isAssignableFrom(message.getClass())) {
            EndpointAdapter p = EndpointAdapter.class.cast(message);
            PublisherConfig config = restService.get("/config/" + p.uuid(), PublisherConfig.class);
            p.config(config);
            l.completed(p);
        }
    }
}
