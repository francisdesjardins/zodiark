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
import org.zodiark.service.util.RestService;
import org.zodiark.service.util.mock.OKRestService;

import java.util.UUID;

import static org.testng.Assert.assertNotNull;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SESSION_CREATE;

public class OKRestServiceTest {


    @Test
    public void createPublisherService() throws IllegalAccessException, InstantiationException {
        RestService restService = new ZodiarkObjectFactory().newClassInstance(null, RestService.class, OKRestService.class);
        AuthConfig config = restService.post(DB_PUBLISHER_SESSION_CREATE.replace("@uuid", UUID.randomUUID().toString()),
                "{\"username\": \"foo\", \"password\":\"12345\", \"ip\": \"127.0.0.1\", \"referrer\":\"zzzz\"}", AuthConfig.class);

        assertNotNull(config);
    }


}
