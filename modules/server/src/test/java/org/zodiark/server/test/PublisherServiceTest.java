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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AsyncIOWriter;
import org.atmosphere.cpr.AsyncIOWriterAdapter;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.HeaderConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusFactory;
import org.zodiark.server.ZodiarkServer;
import org.zodiark.service.util.mock.InMemoryDB;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.zodiark.protocol.Paths.DB_GET_WORD_PASSSTHROUGH;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_ONDEMAND_END;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_ONDEMAND_START;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_CONFIG_SHOW_AVAILABLE_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_ERROR_REPORT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SAVE_CONFIG;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SAVE_CONFIG_SHOW;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHARED_PRIVATE_START;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE;

public class PublisherServiceTest {

    public final static String UUID = java.util.UUID.randomUUID().toString();
    public final static AtmosphereFramework framework = new AtmosphereFramework().init();
    public final static AtmosphereResource RESOURCE = AtmosphereResourceFactory.getDefault().create(framework.getAtmosphereConfig(), UUID);
    private ZodiarkServer server;

    public final static String UC1 = "";


    public Message message(String path, String message) {
        return new Message().setPath(path).setData(message);
    }

    private final static class Writer extends AsyncIOWriterAdapter {
        final ConcurrentLinkedQueue<Envelope> e = new ConcurrentLinkedQueue<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final ObjectMapper mapper = new ObjectMapper();

        @Override
        public AsyncIOWriter writeError(AtmosphereResponse r, int errorCode, String message) throws IOException {
            error.set(new IOException(message));
            return this;
        }

        @Override
        public AsyncIOWriter write(AtmosphereResponse r, String data) throws IOException {
            e.add(mapper.readValue(data, Envelope.class));
            return this;
        }

        @Override
        public AsyncIOWriter write(AtmosphereResponse r, byte[] data) throws IOException {
            e.add(mapper.readValue(data, Envelope.class));
            return this;
        }

        @Override
        public AsyncIOWriter write(AtmosphereResponse r, byte[] data, int offset, int length) throws IOException {
            return this;
        }
    }

    @BeforeMethod
    public void before() {
        server = new ZodiarkServer().on();
    }

    @AfterMethod
    public void after() {
        server.off();
    }

    @Test
    public void uc1Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 3);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc2Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getRequest().header(HeaderConfig.X_ATMOSPHERE_TRACKING_ID, UUID);
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // (UC2
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SHOW_START, RestServiceTest.SESSION_CREATE));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.SHOWID, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc3Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getRequest().header(HeaderConfig.X_ATMOSPHERE_TRACKING_ID, UUID);
        RESOURCE.getResponse().asyncIOWriter(writer);

        // uc1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // (uc2
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SHOW_START, RestServiceTest.SESSION_CREATE));
        eventBus.ioEvent(em, RESOURCE);

        // (uc3
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SHOW_END, RestServiceTest.SESSION_CREATE));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 5);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.SHOWID, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc5Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC2
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_ERROR_REPORT, RestServiceTest.PUBLISHER_ERROR));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc6Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getRequest().header(HeaderConfig.X_ATMOSPHERE_TRACKING_ID, UUID);
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC2
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SHOW_START, RestServiceTest.SESSION_CREATE));
        eventBus.ioEvent(em, RESOURCE);

        // UC6
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SAVE_CONFIG_SHOW, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 5);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.SHOWID, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc7Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC7
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_CONFIG_SHOW_AVAILABLE_PASSTHROUGHT, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc8Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC8
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SAVE_CONFIG, RestServiceTest.CONFIG));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc9Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC9
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_ONDEMAND_START, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc10Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC9
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_ONDEMAND_START, ""));
        eventBus.ioEvent(em, RESOURCE);

        // UC10
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_ONDEMAND_END, "{\"actionId\":1}"));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 5);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc14Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC14
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_GET_WORD_PASSSTHROUGH, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.MOTD, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc15Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC15
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SUBSCRIBER_PROFILE, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
    }

    @Test
    public void uc16Test() throws Exception {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();

        Writer writer = new Writer();
        RESOURCE.getResponse().asyncIOWriter(writer);

        // UC1
        Envelope em = Envelope.newPublisherToServerRequest(UUID, message(DB_POST_PUBLISHER_SESSION_CREATE, RestServiceTest.AUTHTOKEN));
        eventBus.ioEvent(em, RESOURCE);

        // UC16
        em = Envelope.newPublisherToServerRequest(UUID, message(DB_PUBLISHER_SHARED_PRIVATE_START, ""));
        eventBus.ioEvent(em, RESOURCE);

        assertNull(writer.error.get());
        assertEquals(writer.e.size(), 4);
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.PASSTHROUGH, writer.e.poll().getMessage().getData());
        assertEquals(InMemoryDB.STATUS_OK, writer.e.poll().getMessage().getData());
    }
}
