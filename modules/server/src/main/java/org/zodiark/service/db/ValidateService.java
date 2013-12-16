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

@On("/db/validate")
public class ValidateService extends DBServiceAdapter {
    private final Logger logger = LoggerFactory.getLogger(ValidateService.class);

    @Inject
    public RESTService restService;

    @Override
    public void serve(String event, Object message, Reply l) {
        logger.trace("Servicing {}", event);
        if (EndpointAdapter.class.isAssignableFrom(message.getClass())) {
            EndpointAdapter p = EndpointAdapter.class.cast(message);
            SubscriberConfig config = restService.post("/validate/" + p.uuid(), p.message(), SubscriberConfig.class);

            if (config.isStateValid()) {
                l.ok(p);
            } else {
                l.fail(p);
            }
        }
    }
}
