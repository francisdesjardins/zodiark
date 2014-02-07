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
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EnvelopeDigester;
import org.zodiark.server.ZodiarkObjectFactory;
import org.zodiark.service.util.mock.InMemoryDB;
import org.zodiark.service.util.mock.OKRestService;

import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class InternalObjectTest {


    @Test
    public void basicPass() {
        InMemoryDB localDb = new InMemoryDB();

        String s = localDb.serve(OKRestService.METHOD.POST, "/v1/publisher/{guid}/session/create", "{\"username\": \"foo\", \"password\":\"12345\", \"ip\": \"127.0.0.1\", \"referrer\":\"zzzz\"}",
                InMemoryDB.RESULT.PASS);

        assertNotNull(s);

    }

    @Test
    public void envelopeTest() throws IllegalAccessException, InstantiationException, IOException {
        EnvelopeDigester digester = new ZodiarkObjectFactory().newClassInstance(null, EnvelopeDigester.class, EnvelopeDigester.class);

        HashMap<String,Object> map = new HashMap<String,Object>();

        map.put("path", "/a/b");
        map.put("to", "/me");
        map.put("from", "/yo");
        map.put("uuid", "123456");
        map.put("traceId", 0);
        map.put("path", "/a/b");
        map.put("protocol", "Zodiark/1.0");


        HashMap<String,Object> message = new HashMap<String,Object>();

        message.put("path", "/1/2/3");
        message.put("data", "{\"grrr\":\"foooo\"}");
        message.put("uuid", "1234");
        map.put("message", message);

        Envelope e = digester.newEnvelope(map);

        assertNotNull(e);
        assertEquals(e.getMessage().getPath(),"/1/2/3");
        assertEquals("{\"grrr\":\"foooo\"}", e.getMessage().getData());

    }
}
