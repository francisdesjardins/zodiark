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
import org.zodiark.service.db.ActionState;
import org.zodiark.service.db.DBError;
import org.zodiark.service.db.ShowId;
import org.zodiark.service.db.Status;
import org.zodiark.service.db.TransactionId;
import org.zodiark.service.db.WatchId;
import org.zodiark.service.db.util.ReplyAdapter;
import org.zodiark.service.state.EndpointState;
import org.zodiark.service.util.RestService;
import org.zodiark.service.util.mock.OKRestService;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertNotNull;
import static org.zodiark.protocol.Paths.DB_GET_WORD_PASSTHROUGH;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_END;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_JOIN_SESSION;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;

public class RestServiceTest {

    public static final String SESSION_CREATE =  "{\"cameraWidth\": \"1\", \"cameraWidth\": \"1\", \"cameraFPS\": \"1\", \"cameraQuality\": \"1\", \"bandwidthOut\": \"1\", \"bandwidthIn\": \"1\"}";
    public static final String AUTHTOKEN = "{\"username\": \"foo\", \"password\":\"12345\", \"ip\": \"\", \"referrer\":\"zzzz\"}";
    public static final String PUBLISHER_ERROR = "{\"source\":\"xxx\",\"error\":\"xxx\",\"info\":\"xxx\"}";
    public static final String CONFIG = "{\"colorPerformer\":\"xxx\",\"colorClient\":\"xxx\",\"colorAdmin\":\"xxx\",\"colorSystem\":\"xxx\",\"colorVip\":\"xxx\",\"fontSizeChat\":\"xxx\",\"fontSizeMenu\":\"xxx\",\"shortcut1\":\"xxx\",\"shortcut2\":\"xxx\",\"shortcut3\":\"xxx\",\"shortcut4\":\"xxx\",\"shortcut5\":\"xxx\",\"shortcut6\":\"xxx\",\"shortcut7\":\"xxx\",\"shortcut8\":\"xxx\",\"shortcut9\":\"xxx\",\"shortcut10\":\"xxx\",\"welcomeMessage\":\"xxx\"}\n";
    public static final String PROFILE = "{\"profileFullname\":\"xxx\",\"profileAge\":\"xxx\",\"profileGender\":\"xxx\",\"profileNote\":\"xxx\"}";
    public static final String ACTION = "\"{\"actionId\":123\"}";
    public static final String AMOUNT_TOKEN = "{\"amountTokens\":234}";
    public static final String SHOWTYPE_ID = "{\"showTypeId\":1234}";

    @Test
    public void createPublisherService() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> config = new AtomicReference<>();
        restService.post(DB_POST_PUBLISHER_SESSION_CREATE.replace("{guid}", UUID.randomUUID().toString()),
                AUTHTOKEN, new ReplyAdapter<Status, DBError>(null) {
            @Override
            public void success(Status success) {
                config.set(success);
            }

            @Override
            public void failure(DBError failure) {

            }
        });

        assertNotNull(config.get());
    }

    @Test
    public void showIdTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<ShowId> showId = new AtomicReference<>();

        restService.post(DB_PUBLISHER_SHOW_START.replace("{guid}", UUID.randomUUID().toString()),
                SESSION_CREATE, new RestService.Reply<ShowId, DBError>() {
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

        restService.post(DB_POST_SUBSCRIBER_JOIN_SESSION.replace("{guid}", UUID.randomUUID().toString()),
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
        final AtomicReference<String> motds = new AtomicReference<>();

        restService.get(DB_GET_WORD_PASSTHROUGH.replace("{guid}", UUID.randomUUID().toString())
                        , new RestService.Reply<String, DBError>() {
            @Override
            public void success(String success) {
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
    }

    @Test
    public void chargeStartTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(DB_POST_SUBSCRIBER_CHARGE_START.replace("{guid}", UUID.randomUUID().toString()),
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

        restService.post(DB_PUBLISHER_SHOW_START.replace("{guid}", UUID.randomUUID().toString()),
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
        restService.delete(DB_POST_SUBSCRIBER_CHARGE_END.replace("{guid}", UUID.randomUUID().toString()).replace("@showId", "" + showId.get().showId()), null, new RestService.Reply<Status, DBError>() {
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

        restService.post(Paths.DB_POST_PUBLISHER_ONDEMAND_START.replace("{guid}", UUID.randomUUID().toString()),
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

        restService.post(Paths.DB_POST_PUBLISHER_ONDEMAND_KEEPALIVE.replace("{guid}", UUID.randomUUID().toString()),
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

        restService.delete(Paths.DB_POST_PUBLISHER_ONDEMAND_END.replace("{guid}", UUID.randomUUID().toString()),
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

        restService.get(Paths.DB_GET_SUBSCRIBER_STATUS_TO_PUBLISHER_PASSTHROUGHT.replace("{guid}", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
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

        restService.post(Paths.DB_PUBLISHER_SHARED_PRIVATE_START.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
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

        restService.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_END.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
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

        restService.get(Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("{guid}", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
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

        restService.get(Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("{guid}", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
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

        restService.post(Paths.DB_SUBSCRIBER_REQUEST_ACTION.replace("{guid}", UUID.randomUUID().toString()), ACTION, new RestService.Reply<ActionState, DBError>() {
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

        restService.post(Paths.DB_SUBSCRIBER_JOIN_ACTION.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<TransactionId, DBError>() {
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

        restService.post(Paths.DB_SUBSCRIBER_CHARGE_ACTION.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
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

        restService.post(Paths.DB_SUBSCRIBER_BLOCK.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
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

        restService.post(Paths.DB_SUBSCRIBER_EJECT.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
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
    public void endSubscriberFromPublisher() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.delete(Paths.DB_SUBSCRIBER_FAVORITES_END.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<Status, DBError>() {
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
    public void loadPublisherConfig() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.get(Paths.DB_PUBLISHER_LOAD_CONFIG_PASSTHROUGHT.replace("{guid}", UUID.randomUUID().toString()), new RestService.Reply<String, DBError>() {
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
    public void putPublisherConfig() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.put(Paths.DB_PUBLISHER_SAVE_CONFIG.replace("{guid}", UUID.randomUUID().toString()),
                CONFIG,
                new RestService.Reply<Status, DBError>() {
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
    public void errorPublisherConfig() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.get(Paths.DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT.replace("{guid}", UUID.randomUUID().toString()),
                new RestService.Reply<String, DBError>() {
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
    public void showPublisherConfig() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.get(Paths.DB_PUBLISHER_SETTINGS_SHOW.replace("{guid}", UUID.randomUUID().toString()),
                new RestService.Reply<String, DBError>() {
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
    public void putPublisherShow() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.put(Paths.DB_PUBLISHER_SETTINGS_SHOW.replace("{guid}", UUID.randomUUID().toString()), SHOWTYPE_ID,
                new RestService.Reply<Status, DBError>() {
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
    public void postPublisherPublicShow() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_PUBLISHER_PUBLIC_MODE.replace("{guid}", UUID.randomUUID().toString()), "",
                new RestService.Reply<Status, DBError>() {
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
    public void deletePublisherPublicShow() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.delete(Paths.DB_PUBLISHER_PUBLIC_MODE_END.replace("{guid}", UUID.randomUUID().toString()), "",
                new RestService.Reply<Status, DBError>() {
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
    public void reportPublishErrorTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.post(Paths.DB_PUBLISHER_ERROR_REPORT.replace("{guid}", UUID.randomUUID().toString()), PUBLISHER_ERROR,
                new RestService.Reply<Status, DBError>() {
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
    public void publisherSubscriberProfile() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<Status> result = new AtomicReference<>();

        restService.put(Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE.replace("{guid}", UUID.randomUUID().toString()), PROFILE,
                new RestService.Reply<Status, DBError>() {
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
    public void subscriberExtra() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<TransactionId> result = new AtomicReference<>();

        restService.post(Paths.DB_SUBSCRIBER_EXTRA.replace("{guid}", UUID.randomUUID().toString()), AMOUNT_TOKEN, new RestService.Reply<TransactionId, DBError>() {
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
    public void subscriberConfig() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<String> result = new AtomicReference<>();

        restService.put(Paths.DB_SUBSCRIBER_CONFIG_PASSTHROUGHT.replace("{guid}", UUID.randomUUID().toString()), "", new RestService.Reply<String, DBError>() {
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
    public void stateTest() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<EndpointState> result = new AtomicReference<>();

        restService.get(Paths.DB_ENDPOINT_STATE.replace("{guid}", UUID.randomUUID().toString()), new RestService.Reply<EndpointState, DBError>() {
            @Override
            public void success(EndpointState success) {
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


