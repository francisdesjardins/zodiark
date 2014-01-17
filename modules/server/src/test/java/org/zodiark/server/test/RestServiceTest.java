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
import org.zodiark.server.ZodiarkObjectFactory;
import org.zodiark.service.config.AuthConfig;
import org.zodiark.service.db.DBError;
import org.zodiark.service.db.ShowId;
import org.zodiark.service.util.RestService;
import org.zodiark.service.util.mock.OKRestService;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertNotNull;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_ANNOUNCE_SESSION;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SESSION_CREATE;

public class RestServiceTest {

    @Test
    public void createPublisherService() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<AuthConfig> config = new AtomicReference<>();
        restService.post(DB_PUBLISHER_SESSION_CREATE.replace("@uuid", UUID.randomUUID().toString()),
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
    public void watchIdService() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        final AtomicReference<ShowId> showId = new AtomicReference<>();

        restService.post(DB_PUBLISHER_ANNOUNCE_SESSION.replace("@uuid", UUID.randomUUID().toString()),
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


}
