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
package org.zodiark.service.db.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.Reply;
import org.zodiark.service.db.DBError;
import org.zodiark.service.db.Passthrough;

public class PassthroughReply extends ReplyAdapter<String, DBError> {
    private final Logger logger = LoggerFactory.getLogger(StatusReply.class);

    public final String ERROR = "error";

    public PassthroughReply(Reply reply) {
        super(reply);
    }

    @Override
    public void success(String passthrough) {
        logger.trace("Response {}", passthrough);
        reply.ok(new Passthrough().response(passthrough));
    }

    @Override
    public void failure(DBError failure) {
        logger.trace("Error {}", failure);
        reply.fail(new Passthrough().exception(failure.exception()));
    }
}