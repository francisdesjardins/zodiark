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
import org.zodiark.service.util.mock.InMemoryDB;
import org.zodiark.service.util.mock.OKRestService;

import static org.testng.Assert.assertNotNull;

public class LocalDBTest {


    @Test
    public void basicPass() {
        InMemoryDB localDb = new InMemoryDB();

        String s = localDb.serve(OKRestService.METHOD.POST, "/v1/publisher/{guid}/session/create", "{\"username\": \"foo\", \"password\":\"12345\", \"ip\": \"127.0.0.1\", \"referrer\":\"zzzz\"}",
                InMemoryDB.RESULT.PASS);

        assertNotNull(s);

    }


}
