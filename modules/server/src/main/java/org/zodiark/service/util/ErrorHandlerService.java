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
package org.zodiark.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Service;

@On("/error")
public class ErrorHandlerService implements Service {

    private final Logger logger = LoggerFactory.getLogger(ErrorHandlerService.class);

    @Inject
    public ObjectMapper mapper;

    @Override
    public void serve(Envelope error, AtmosphereResource r, EventBusListener l) {
        try {
            r.write(mapper.writeValueAsString(error));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
        }
    }

    @Override
    public void serve(String event, Object r, EventBusListener l) {

    }
}