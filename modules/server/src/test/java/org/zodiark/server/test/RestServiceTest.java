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
package org.zodiark.server.test;

import org.testng.annotations.Test;
import org.zodiark.protocol.Paths;
import org.zodiark.server.ZodiarkObjectFactory;
import org.zodiark.service.config.AuthConfig;
import org.zodiark.service.db.ActionState;
import org.zodiark.service.db.DBError;
import org.zodiark.service.db.Motds;
import org.zodiark.service.db.ShowId;
import org.zodiark.service.db.Status;
import org.zodiark.service.db.TransactionId;
import org.zodiark.service.db.WatchId;
import org.zodiark.service.util.RestService;
import org.zodiark.service.util.mock.OKRestService;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.zodiark.protocol.Paths.DB_GET_WORD;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_END;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_JOIN_SESSION;

public class RestServiceTest {

    @Test
    public void createPublisherService() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<AuthConfig> config = new AtomicReference<>();
        restService.post(DB_POST_PUBLISHER_SESSION_CREATE.replace("@uuid", UUID.randomUUID().toString()),
                "{\"username\": \"foo\", \"password\":\"12345\", \"ip\": \"127.0.0.1\", \"referrer\":\"zzzz\"}", new RestService.Reply<AuthConfig, DBError>() {
            @Override
            public void success(AuthConfig success) {
                config.set(success);
            }

            @Override
            public void failure(DBError failure) {

            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });

        assertNotNull(config.get());
    }

    @Test
    public void showIdTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<ShowId> showId = new AtomicReference<>();

        restService.post(DB_POST_PUBLISHER_SHOW_START.replace("@uuid", UUID.randomUUID().toString()),
                "{\"cameraWidth\": \"1\", \"cameraWidth\": \"1\", \"cameraFPS\": \"1\", \"cameraQuality\": \"1\", \"bandwidthOut\": \"1\", \"bandwidthIn\": \"1\"}" +
                        "", new RestService.Reply<ShowId, DBError>() {
            @Override
            public void success(ShowId success) {
                showId.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(showId.get());

    }

    @Test
    public void watchIdService() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<WatchId> watchId = new AtomicReference<>();

        restService.post(DB_POST_SUBSCRIBER_JOIN_SESSION.replace("@uuid", UUID.randomUUID().toString()),
                "{\"modeId\": \"12345\"}" +
                        "", new RestService.Reply<WatchId, DBError>() {
            @Override
            public void success(WatchId success) {
                watchId.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(watchId.get());

    }

    @Test
    public void motdTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Motds> motds = new AtomicReference<>();

        restService.post(DB_GET_WORD.replace("@uuid", UUID.randomUUID().toString()),
                "{\"modeId\": \"12345\"}" +
                        "", new RestService.Reply<Motds, DBError>() {
            @Override
            public void success(Motds success) {
                motds.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(motds.get());
        assertEquals(1, motds.get().motds().size());

    }

    @Test
    public void chargeStartTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(DB_POST_SUBSCRIBER_CHARGE_START.replace("@uuid", UUID.randomUUID().toString()),
                "{\"second\": \"12345\"}" +
                        "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void chargeEndTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<ShowId> showId = new AtomicReference<>();

        restService.post(DB_POST_PUBLISHER_SHOW_START.replace("@uuid", UUID.randomUUID().toString()),
                "{\"cameraWidth\": \"1\", \"cameraWidth\": \"1\", \"cameraFPS\": \"1\", \"cameraQuality\": \"1\", \"bandwidthOut\": \"1\", \"bandwidthIn\": \"1\"}" +
                        "", new RestService.Reply<ShowId, DBError>() {
            @Override
            public void success(ShowId success) {
                showId.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(showId.get());

        final AtomicReference<Status> result = new AtomicReference<>();
        restService.delete(DB_POST_SUBSCRIBER_CHARGE_END.replace("@uuid", UUID.randomUUID().toString()).replace("@showId", "" + showId.get().showId()), null, new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void onDemandStartTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_POST_PUBLISHER_ONDEMAND_START.replace("@uuid", UUID.randomUUID().toString()),
                "{\"second\": \"12345\"}" +
                        "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void onDemandKeepAlive() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_POST_PUBLISHER_ONDEMAND_KEEPALIVE.replace("@uuid", UUID.randomUUID().toString()),
                "{\"second\": \"12345\"}" +
                        "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void onDemandEndTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.delete(Paths.DB_POST_PUBLISHER_ONDEMAND_END.replace("@uuid", UUID.randomUUID().toString()),
                "{\"second\": \"12345\"}" +
                        "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void onSubscriberProfileTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.get(Paths.DB_GET_SUBSCRIBER_STATUS_TO_PUBLISHER_PASSTHROUGHT.replace("@uuid", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
            @Override
            public void success(String success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void sharedPrivateStartTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_START.replace("@uuid", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void sharedPrivateEndTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_END.replace("@uuid", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void availableSubscriberActions() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.get(Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("@uuid", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
            @Override
            public void success(String success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void availablePublisherActions() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.get(Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("@uuid", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
            @Override
            public void success(String success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void subscriberRequestAction() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<ActionState> result = new AtomicReference<>();

        restService.post(Paths.DB_SUBSCRIBER_REQUEST_ACTION.replace("@uuid", UUID.randomUUID().toString()), "{\"actionId\":\123\"}", new RestService.Reply<ActionState, DBError>() {
            @Override
            public void success(ActionState success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void subscriberJoinAction() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<TransactionId> result = new AtomicReference<>();

        restService.put(Paths.DB_SUBSCRIBER_JOIN_ACTION.replace("@uuid", UUID.randomUUID().toString()), "", new RestService.Reply<TransactionId, DBError>() {
            @Override
            public void success(TransactionId success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void chargeSubscriberAction() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_SUBSCRIBER_CHARGE_ACTION.replace("@uuid", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void blockSubscriberFromPublisher() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_SUBSCRIBER_BLOCK.replace("@uuid", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }

    @Test
    public void ejectSubscriberFromPublisher() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_SUBSCRIBER_EJECT.replace("@uuid", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
            @Override
            public void success(Status success) {
                result.set(success);
            }

            @Override
            public void failure(DBError failure) {
            }

            @Override
            public void exception(Exception exception) {
                exception.printStackTrace();
            }
        });
        assertNotNull(result.get());

    }
}