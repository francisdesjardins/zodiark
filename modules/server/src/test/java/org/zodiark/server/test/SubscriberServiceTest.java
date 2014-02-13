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

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusFactory;
import org.zodiark.server.ZodiarkServer;
import org.zodiark.service.util.mock.InMemoryDB;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_EXTRA;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_FAVORITES_END;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_FAVORITES_START;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_JOIN_ACTION;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_REQUEST_ACTION;

public class SubscriberServiceTest {


    public final static String UUID = java.util.UUID.randomUUID().toString();
    public final static AtmosphereFramework framework = new AtmosphereFramework().init();
    public final static AtmosphereResource RESOURCE = AtmosphereResourceFactory.getDefault().create(framework.getAtmosphereConfig(), UUID);
    private ZodiarkServer server;

    public final static String UC1 = "";

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

    private String escape(String s) {
        return s.replaceAll("\n","").replaceAll("\\s","");
    }

    @Test
    public void uc24Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC29
        Envelope em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC24
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_AVAILABLE_ACTIONS, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 2);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(escape(InMemoryDB.ACTIONS), writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc25Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC29
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC24
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_AVAILABLE_ACTIONS, ""));
        eventBus.ioEvent(em, RESOURCE);

        // UC25
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_REQUEST_ACTION, RestServiceTest.ACTION));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 3);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(escape(InMemoryDB.ACTIONS), writer.e.poll().getMessage().getData());
        assertEquals(escape(InMemoryDB.ACTION_REQUEST), writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc26Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC29
        Envelope em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC24
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_AVAILABLE_ACTIONS, ""));
        eventBus.ioEvent(em, RESOURCE);

        // UC25
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_REQUEST_ACTION, RestServiceTest.ACTION));
        eventBus.ioEvent(em, RESOURCE);

        // UC26
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_JOIN_ACTION, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(escape(InMemoryDB.ACTIONS), writer.e.poll().getMessage().getData());
        assertEquals(escape(InMemoryDB.ACTION_REQUEST), writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.TRANSACTION_ID, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc27Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC29
        Envelope em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC27
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_FAVORITES_END, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 2);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc28Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC29
        Envelope em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC27
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_FAVORITES_START, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 2);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.FAVORITE_ID, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc29Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1

        Envelope em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, "{\"guid\":\"1234\"}"));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 1);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc30Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC29
        Envelope em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_POST_SUBSCRIBER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC30
        em = Envelope.newSubscriberrToServerRequest(UUID, message(DB_SUBSCRIBER_EXTRA, RestServiceTest.AMOUNT_TOKEN));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 2);
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.TRANSACTION_ID, writer.e.poll().getMessage().getData());
    }
}
