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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Retrieve;
import org.zodiark.service.RetrieveMessage;
import org.zodiark.service.rest.RestService;

import javax.inject.Inject;

import static org.zodiark.protocol.Paths.DB_ENDPOINT_STATE;
import static org.zodiark.protocol.Paths.DB_GET_WORD_PASSTHROUGH;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_LOAD_CONFIG_GET;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SETTINGS_SHOW_GET_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE_GET_PASSTHROUGH;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS;

@Retrieve({DB_ENDPOINT_STATE,
        DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT,
        DB_PUBLISHER_LOAD_CONFIG_GET,
        DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT,
        DB_PUBLISHER_SETTINGS_SHOW_GET_PASSTHROUGHT,
        DB_GET_WORD_PASSTHROUGH,
        DB_PUBLISHER_SUBSCRIBER_PROFILE_GET_PASSTHROUGH,
        DB_SUBSCRIBER_AVAILABLE_ACTIONS})
public class GetService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(GetService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        final RetrieveMessage p = RetrieveMessage.class.cast(message);
        restService.get(path.replace("{guid}", p.uuid()), reply);
    }

}


