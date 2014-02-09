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
import org.zodiark.service.subscriber.SubscriberEndpoint;
import org.zodiark.service.util.RestService;

import javax.inject.Inject;

import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_JOIN_ACTION;

@Retrieve(DB_SUBSCRIBER_JOIN_ACTION)
public class PostActionTransactionService extends DBServiceAdapter {
    private final Logger logger = LoggerFactory.getLogger(PostPassthoughService.class);

    @Inject
    public RestService restService;

    @Override
    public void reactTo(String path, Object message, final Reply reply) {
        logger.trace("Servicing {}", path);
        final SubscriberEndpoint s = SubscriberEndpoint.class.cast(message);
        restService.post(path.replace("@guid", s.uuid()), s.message(), new RestService.Reply<TransactionId, DBError>() {
            @Override
            public void success(TransactionId success) {
                s.transactionId(success);
                reply.ok(s);
            }

            @Override
            public void failure(DBError failure) {
                logger.trace("Error {}", failure);
                reply.fail(new TransactionId().transactionId(-1));
            }

            @Override
            public void exception(Exception exception) {
                logger.trace("Error {}", exception);
                reply.fail(new TransactionId().transactionId(-1));
            }
        });
    }
}
