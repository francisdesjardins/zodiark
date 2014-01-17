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
package org.zodiark.service.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.config.SubscriberConfig;
import org.zodiark.service.util.RESTService;

import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_VALIDATE_STATE;

@On(DB_SUBSCRIBER_VALIDATE_STATE)
public class ValidateService extends DBServiceAdapter {
    private final Logger logger = LoggerFactory.getLogger(ValidateService.class);

    @Inject
    public RESTService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        if (EndpointAdapter.class.isAssignableFrom(message.getClass())) {
            final EndpointAdapter p = EndpointAdapter.class.cast(message);
            restService.post(DB_SUBSCRIBER_VALIDATE_STATE.replace("@uuid", p.uuid()), p.message(), new RESTService.Reply<SubscriberConfig, DBError>() {

                @Override
                public void success(SubscriberConfig config) {
                    if (config.isStateValid()) {
                        reply.ok(p);
                    } else {
                        reply.fail(p);
                    }
                }

                @Override
                public void failure(DBError failure) {
                    reply.fail(p);
                }

                @Override
                public void exception(Exception exception) {
                    logger.error("", exception);
                }
            });
        }
    }
}
