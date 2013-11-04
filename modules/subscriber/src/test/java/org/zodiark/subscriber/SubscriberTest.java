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
package org.zodiark.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.zodiark.protocol.ActorValue;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.From;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Path;
import org.zodiark.protocol.Paths;
import org.zodiark.server.ZodiarkServer;
import org.zodiark.service.publisher.PublisherResults;
import org.zodiark.service.subscriber.SubscriberResults;
import org.zodiark.service.wowza.WowzaUUID;
import org.zodiark.wowza.OnEnvelopHandler;
import org.zodiark.wowza.ZodiarkClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.AssertJUnit.assertEquals;

public class SubscriberTest {

    private final ObjectMapper mapper = new ObjectMapper();

    public final static int findFreePort()  {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(0);

            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 8080;
    }

    private ZodiarkServer server;
    private int port = 8080;

    @BeforeClass
    public void startZodiark() {
        server = new ZodiarkServer().listen(URI.create("http://127.0.0.1:" + port)).on();
    }

    @AfterClass
    public void stopZodiark() {
        if (server != null) server.off();
    }

    @Test
    public void createSessionTest() throws IOException, InterruptedException {
        final AtomicReference<SubscriberResults> answer = new AtomicReference<>();
        final ZodiarkClient publisherClient = new ZodiarkClient.Builder().path("http://127.0.0.1:" + port).build();
        final CountDownLatch latch = new CountDownLatch(1);

        publisherClient.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                answer.set(mapper.readValue(e.getMessage().getData(), SubscriberResults.class));
                latch.countDown();
                return true;
            }
        }).open();

        Envelope createSessionMessage = Envelope.newClientToServerRequest(
                new Message(new Path(Paths.CREATE_SUBSCRIBER_SESSION), mapper.writeValueAsString(new UserPassword("foo", "bar"))));
        createSessionMessage.setFrom(new From(ActorValue.SUBSCRIBER));
        publisherClient.send(createSessionMessage);
        latch.await();
        assertEquals("OK", answer.get().getResults());
    }

    @Test
    public void joinStreamingSession() throws IOException, InterruptedException {

        final ZodiarkClient wowzaClient = new ZodiarkClient.Builder().path("http://127.0.0.1:" + port).build();
        final CountDownLatch connected = new CountDownLatch(1);
        final AtomicReference<String> uuid = new AtomicReference<>();
        final AtomicReference<String> paths = new AtomicReference<>();

        // =============== Wowza

        paths.set(Paths.START_STREAMING_SESSION);
        wowzaClient.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {

                Message m = e.getMessage();
                switch(m.getPath()) {
                    case Paths.WOWZA_CONNECT:
                        // Connected. Listen
                        uuid.set(e.getUuid());
                        break;
                    case Paths.SERVER_VALIDATE_OK:
                        Envelope publisherOk = Envelope.newClientToServerRequest(
                                new Message(new Path(paths.get()), e.getMessage().getData()));
                        wowzaClient.send(publisherOk);
                        break;
                    default:
                        // ERROR
                }

                connected.countDown();
                return false;
            }
        }).open();

        Envelope wowzaConnect = Envelope.newClientToServerRequest(
                new Message(new Path(Paths.WOWZA_CONNECT), mapper.writeValueAsString(new UserPassword("wowza", "bar"))));
        wowzaClient.send(wowzaConnect);
        connected.await();

        // ================ Publisher

        final AtomicReference<PublisherResults> answer = new AtomicReference<>();
        final ZodiarkClient publisherClient = new ZodiarkClient.Builder().path("http://127.0.0.1:" + port).build();
        final CountDownLatch latch = new CountDownLatch(1);

        publisherClient.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                answer.set(mapper.readValue(e.getMessage().getData(), PublisherResults.class));
                latch.countDown();
                return true;
            }
        }).open();

        Envelope createSessionMessage = Envelope.newClientToServerRequest(
                new Message(new Path(Paths.CREATE_PUBLISHER_SESSION), mapper.writeValueAsString(new UserPassword("publisherex", "bar"))));
        createSessionMessage.setFrom(new From(ActorValue.PUBLISHER));
        publisherClient.send(createSessionMessage);
        latch.await();
        assertEquals("OK", answer.get().getResults());
        answer.set(null);

        final CountDownLatch tlatch = new CountDownLatch(1);
        publisherClient.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                answer.set(mapper.readValue(e.getMessage().getData(), PublisherResults.class));
                tlatch.countDown();
                return true;
            }
        });

        Envelope startStreamingSession = Envelope.newClientToServerRequest(
                new Message(new Path(Paths.VALIDATE_PUBLISHER_STREAMING_SESSION), mapper.writeValueAsString(new WowzaUUID(uuid.get()))));
        createSessionMessage.setFrom(new From(ActorValue.PUBLISHER));
        publisherClient.send(startStreamingSession);

        tlatch.await();

        assertEquals("OK", answer.get().getResults());

        // ================ Subscriber

        paths.set(Paths.JOIN_STREAMING_SESSION);
        final AtomicReference<SubscriberResults> sanswer = new AtomicReference<>();
        final ZodiarkClient subscriberClient = new ZodiarkClient.Builder().path("http://127.0.0.1:" + port).build();
        final CountDownLatch platch = new CountDownLatch(1);

        subscriberClient.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                sanswer.set(mapper.readValue(e.getMessage().getData(), SubscriberResults.class));
                platch.countDown();
                return true;
            }
        }).open();

        createSessionMessage = Envelope.newClientToServerRequest(
                new Message(new Path(Paths.CREATE_SUBSCRIBER_SESSION), mapper.writeValueAsString(new UserPassword("123456", "bar"))));
        createSessionMessage.setFrom(new From(ActorValue.SUBSCRIBER));
        subscriberClient.send(createSessionMessage);
        platch.await();
        assertEquals("OK", sanswer.get().getResults());
        sanswer.set(null);

        final CountDownLatch elatch = new CountDownLatch(1);
        subscriberClient.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                sanswer.set(mapper.readValue(e.getMessage().getData(), SubscriberResults.class));
                elatch.countDown();
                return true;
            }
        });

        startStreamingSession = Envelope.newClientToServerRequest(
                new Message(new Path(Paths.VALIDATE_SUBSCRIBER_STREAMING_SESSION), mapper.writeValueAsString(new WowzaUUID(uuid.get()))));
        createSessionMessage.setFrom(new From(ActorValue.SUBSCRIBER));
        subscriberClient.send(startStreamingSession);

        elatch.await();

        assertEquals("OK", answer.get().getResults());

    }

}
