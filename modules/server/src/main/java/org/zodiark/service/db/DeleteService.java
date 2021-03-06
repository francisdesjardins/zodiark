/*
 * Copyright 2014 Jeanfrancois Arcand
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

import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_ONDEMAND_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_ERROR_REPORT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_PUBLIC_MODE_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHARED_PRIVATE_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_END;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_FAVORITES_END;

@Retrieve({
        DB_PUBLISHER_ERROR_REPORT,
        DB_POST_PUBLISHER_ONDEMAND_END,
        DB_PUBLISHER_SHARED_PRIVATE_END,
        DB_PUBLISHER_PUBLIC_MODE_END,
        DB_SUBSCRIBER_FAVORITES_END,
        DB_PUBLISHER_SHOW_END
})
public class DeleteService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(DeleteService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        final RetrieveMessage p = RetrieveMessage.class.cast(message);
        restService.delete(DB_PUBLISHER_SHOW_END.replace("{guid}", p.uuid()),
                p.message().getData(), reply);

    }
}
