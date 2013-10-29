/*
 * Copyright 2013 High-Level Technologies
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
package org.zodiark.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.fail;

public class ProtocolTest {

    private ObjectMapper mapper = new ObjectMapper();
    private final static String testEnvelope = "{\n" +
            "    \"path\": \"/REQUEST/Action\",\n" +
            "    \"traceId\": 0,\n" +
            "    \"uuid\": \"234-456-w2dsce3-29sj3\",\n" +
            "    \"to\": \"SUBSCRIBER\",\n" +
            "    \"from\": \"SERVER\",\n" +
            "    \"protocol\": \"zodiark/1.0\",\n" +
            "    \"message\": {\n" +
            "        \"path\": \"/REACT/EXECUTION/CREATE_USER_SESSION\",\n" +
            "        \"data\": \"\\\"{\\\"foo\\\": \\\"bar \\\"}\\\"\"\n" +
            "    }\n" +
            "}";

    private final static String testInvalidEnvelopPath = "{\n" +
            "    \"path\": \"/ZOOO/Action\",\n" +
            "    \"traceId\": \"0\",\n" +
            "    \"uuid\": \"234-456-w2dsce3-29sj3\",\n" +
            "    \"to\": \"SUBSCRIBER\",\n" +
            "    \"from\": \"SERVER\",\n" +
            "    \"protocol\": \"zodiark/1.0\",\n" +
            "    \"message\": {\n" +
            "        \"path\": \"/REACT/EXECUTION/CREATE_USER_SESSION\",\n" +
            "        \"data\": \"\\\"{\\\"foo\\\": \\\"bar \\\"}\\\"\"\n" +
            "    }\n" +
            "}";

    private final static String testInvalidTo = "{\n" +
            "    \"path\": \"/REQUEST/Action\",\n" +
            "    \"traceId\": \"0\",\n" +
            "    \"uuid\": \"234-456-w2dsce3-29sj3\",\n" +
            "    \"to\": \"Yooo\",\n" +
            "    \"from\": \"SERVER\",\n" +
            "    \"protocol\": \"zodiark/1.0\",\n" +
            "    \"message\": {\n" +
            "        \"path\": \"/REACT/EXECUTION/CREATE_USER_SESSION\",\n" +
            "        \"data\": \"\\\"{\\\"foo\\\": \\\"bar \\\"}\\\"\"\n" +
            "    }\n" +
            "}";

    private final static String testInvalidFrom = "{\n" +
            "    \"path\": \"/REQUEST/Action\",\n" +
            "    \"traceId\": \"0\",\n" +
            "    \"uuid\": \"234-456-w2dsce3-29sj3\",\n" +
            "    \"to\": \"SUBSCRIBER\",\n" +
            "    \"from\": \"UYTIU\",\n" +
            "    \"protocol\": \"zodiark/1.0\",\n" +
            "    \"message\": {\n" +
            "        \"path\": \"/REACT/EXECUTION/CREATE_USER_SESSION\",\n" +
            "        \"data\": \"\\\"{\\\"foo\\\": \\\"bar \\\"}\\\"\"\n" +
            "    }\n" +
            "}";

    @Test
    public void testCreateEnvelope() throws IOException {
        System.out.println(testEnvelope);
        Envelope e = mapper.readValue(testEnvelope, Envelope.class);

        assertNotNull(e);
        assertEquals(e.getPath().toString(), "/request/Action");
        assertEquals(e.getTraceId(), 0);
        assertEquals(e.getUuid(), "234-456-w2dsce3-29sj3");

    }

    @Test
    public void testInvalidEnvelop() throws IOException {
        IOException ex = null;
        try {
            Envelope e = mapper.readValue(testInvalidEnvelopPath, Envelope.class);

            assertNotNull(e);

            e.getPath().toString();
            fail();
        } catch (com.fasterxml.jackson.databind.JsonMappingException jme) {
            ex = jme;
        }
        assertEquals(com.fasterxml.jackson.databind.JsonMappingException.class, ex.getClass());
    }


}
