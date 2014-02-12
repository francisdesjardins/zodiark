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
import org.zodiark.server.ReplyException;
import org.zodiark.server.annotation.Retrieve;
import org.zodiark.service.RetrieveMessage;
import org.zodiark.service.util.RestService;

import javax.inject.Inject;

import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;

@Retrieve(DB_PUBLISHER_SHOW_START)
public class ShowStartService extends DBServiceAdapter {

    private final Logger logger = LoggerFactory.getLogger(ShowStartService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        final RetrieveMessage p = RetrieveMessage.class.cast(message);
        restService.post(DB_PUBLISHER_SHOW_START.replace("{guid}", p.uuid()),
                p.message(), new RestService.Reply<ShowId, DBError>() {
            @Override
            public void success(ShowId success) {
                reply.ok(success);
            }

            @Override
            public void failure(DBError failure) {
                reply.fail(ReplyException.DEFAULT);
            }

            @Override
            public void exception(Exception exception) {
                logger.trace("", exception);
                reply.fail(ReplyException.DEFAULT);
            }
        });

    }
}