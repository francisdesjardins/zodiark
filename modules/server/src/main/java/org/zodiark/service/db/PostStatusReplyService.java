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
package org.zodiark.service.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.Retrieve;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.db.util.StatusReply;
import org.zodiark.service.util.RestService;

import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;

/**
 * Initialize a remove session in a Database/Web Service for an {@link org.zodiark.service.Endpoint}. This class
 * use the injected {@link org.zodiark.service.util.RestService} to communicate with the remote endpoint.
 */
@Retrieve(DB_POST_PUBLISHER_SESSION_CREATE)
public class PostStatusReplyService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(PostStatusReplyService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        final EndpointAdapter p = EndpointAdapter.class.cast(message);
        restService.post(path.replace("@guid", p.uuid()), p.message(), new StatusReply<EndpointAdapter>(reply, p));
    }
}
