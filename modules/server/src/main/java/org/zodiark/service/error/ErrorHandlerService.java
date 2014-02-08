/*
 * Copyright 2013-2014 High-Level Technologies
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
package org.zodiark.service.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.Reply;
import javax.inject.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Service;

/**
 * A Service for handling unexpected error.
 */
@On("/error")
public class ErrorHandlerService implements Service {

    private final Logger logger = LoggerFactory.getLogger(ErrorHandlerService.class);

    @Inject
    public ObjectMapper mapper;

    @Override
    public void reactTo(Envelope error, AtmosphereResource r, Reply reply) {
        try {
            r.write(mapper.writeValueAsString(error));
        } catch (JsonProcessingException e1) {
            logger.error("", e1);
        }
    }

    @Override
    public void reactTo(String path, Object message, Reply reply) {

    }
}
