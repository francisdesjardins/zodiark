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
package org.zodiark.wowza;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.Service;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;

@On("/echo")
public class EchoService implements Service {

    @Inject
    public ObjectMapper mapper;

    @Override
    public void serve(Envelope e, AtmosphereResource r, EventBusListener l) {
        try {
            AtmosphereResource.class.cast(r).write(mapper.writeValueAsString(Envelope.newServerReply(e, e.getMessage())));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void serve(String event, Object r, EventBusListener l) {
    }
}
