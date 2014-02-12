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
import org.zodiark.service.util.RestService;

import javax.inject.Inject;

import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_ONDEMAND_START;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_PUBLIC_MODE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHARED_PRIVATE_START_POST;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_BLOCK;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_EJECT;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_EXTRA;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_FAVORITES_START;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_JOIN_ACTION;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_REQUEST_ACTION;

@Retrieve({DB_SUBSCRIBER_REQUEST_ACTION,
        DB_SUBSCRIBER_FAVORITES_START,
        DB_POST_PUBLISHER_SESSION_CREATE,
        DB_POST_PUBLISHER_ONDEMAND_START,
        DB_PUBLISHER_SHARED_PRIVATE_START_POST,
        DB_SUBSCRIBER_BLOCK,
        DB_SUBSCRIBER_EJECT,
        DB_PUBLISHER_PUBLIC_MODE,
        DB_POST_SUBSCRIBER_SESSION_CREATE,
        DB_SUBSCRIBER_JOIN_ACTION,
        DB_SUBSCRIBER_EXTRA,
        DB_PUBLISHER_SHOW_START})
public class PostService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        final RetrieveMessage p = RetrieveMessage.class.cast(message);
        restService.post(path.replace("{guid}", p.uuid()), p.message(), reply);
    }
}
