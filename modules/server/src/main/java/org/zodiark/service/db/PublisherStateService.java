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
import javax.inject.Inject;
import org.zodiark.server.annotation.Retrieve;
import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.config.PublisherState;
import org.zodiark.service.util.RestService;

import static org.zodiark.protocol.Paths.DB_PUBLISHER_CONFIG;

/**
 * Construct the {@link org.zodiark.service.config.PublisherState} based on {@link org.zodiark.service.publisher.PublisherEndpoint#uuid} from the
 * remote database/web service.
 */
@Retrieve(DB_PUBLISHER_CONFIG)
public class PublisherStateService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(PublisherStateService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message,final  Reply reply) {
        logger.trace("Servicing {}", path);

        if (EndpointAdapter.class.isAssignableFrom(message.getClass())) {
            final EndpointAdapter p = EndpointAdapter.class.cast(message);
            restService.get(DB_PUBLISHER_CONFIG.replace("@guid", p.uuid()), new RestService.Reply<PublisherState, DBError>() {
                @Override
                public void success(PublisherState config) {  p.config(config);
                    reply.ok(p);
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
