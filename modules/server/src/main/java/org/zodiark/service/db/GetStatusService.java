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
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.Retrieve;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.db.util.StatusReply;
import org.zodiark.service.util.RestService;

@Retrieve()
public class GetStatusService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(PostStatusService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        if (EndpointAdapter.class.isAssignableFrom(message.getClass())) {
            final EndpointAdapter p = EndpointAdapter.class.cast(message);
            restService.post(path.replace("@guid", p.uuid()), p.message(), new StatusReply<EndpointAdapter>(reply, p));
        }
    }
}