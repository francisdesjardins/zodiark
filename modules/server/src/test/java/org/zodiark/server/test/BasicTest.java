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
package org.zodiark.server.test;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.Reply;
import org.zodiark.server.ReplyException;
import org.zodiark.server.ZodiarkObjectFactory;
import org.zodiark.server.ZodiarkServer;
import org.zodiark.service.db.result.Status;
import org.zodiark.service.rest.RestService;
import org.zodiark.service.rest.RestServiceImpl;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class BasicTest {


    private ZodiarkServer server;

    public Message message(String path, String message) {
        return new Message().setPath(path).setData(message);
    }

    @BeforeMethod
    public void before() {
        server = new ZodiarkServer().serve("http://0.0.0.0:" + Utils.findFreePort()).on();
    }

    @AfterMethod
    public void after() {
        server.off();
    }

    // Issue #2
    @Test
    public void sanityCheckTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, RestServiceImpl.class);
        final AtomicReference<Status> first = new AtomicReference<>();
        final AtomicReference<Status> second = new AtomicReference<>();
        final AtomicReference<ReplyException<String>> exception = new AtomicReference<>();

        String uuid = UUID.randomUUID().toString();
        restService.post(Paths.DB_POST_PUBLISHER_SESSION_CREATE.replace("{guid}", uuid),
                RestServiceTest.AUTHTOKEN, new Reply<Status, String>() {
            @Override
            public void ok(Status response) {
                first.set(response);
            }

            @Override
            public void fail(ReplyException<String> replyException) {
                exception.set(replyException);

            }
        });

        restService.post(Paths.DB_POST_PUBLISHER_SESSION_CREATE.replace("{guid}", uuid),
                RestServiceTest.AUTHTOKEN, new Reply<Status, String>() {
            @Override
            public void ok(Status response) {
                second.set(response);
            }

            @Override
            public void fail(ReplyException<String> replyException) {
                exception.set(replyException);

            }
        });

        assertNull(exception.get());
        assertNotNull(first.get());
        assertNotNull(second.get());
    }
}
