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
package org.zodiark.wowza;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Path;
import org.zodiark.server.ZodiarkServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ZodiarkClientTest {

    public final static String TEST = "This is a test";

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
    public void echoTest() throws IOException, InterruptedException {
        server.service(EchoService.class);

        final AtomicReference<Envelope> answer = new AtomicReference<>();
        final ZodiarkClient c = new ZodiarkClient.Builder().path("http://127.0.0.1:" + port).build();
        final CountDownLatch latch = new CountDownLatch(1);
        c.handle(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                answer.set(e);
                latch.countDown();
                return true;
            }
        }).open().send(Envelope.newClientToServerRequest(new Message(new Path("/echo"), TEST)));

        latch.await(5, TimeUnit.SECONDS);
        assertNotNull(answer.get());
        assertEquals(TEST, answer.get().getMessage().getData());
        assertEquals(1, answer.get().getTraceId());
        assertEquals("/request/action", answer.get().getPath());

    }

    @Test
    public void echoPingPongTest() throws IOException, InterruptedException {
        server.service(EchoService.class);

        final AtomicReference<Envelope> answer = new AtomicReference<>();
        final ZodiarkClient c = new ZodiarkClient.Builder().path("http://127.0.0.1:" + port).build();
        final CountDownLatch latch = new CountDownLatch(1);
        c.handle(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                c.handle(new OnEnvelopHandler() {
                    @Override
                    public boolean onEnvelop(Envelope e) throws IOException {
                        answer.set(e);
                        latch.countDown();
                        return false;
                    }
                }).send(Envelope.newClientReply(e, e.getMessage()));
                return true;
            }
        }).open().send(Envelope.newClientToServerRequest(new Message(new Path("/echo"), TEST)));

        latch.await(5, TimeUnit.SECONDS);
        assertNotNull(answer.get());
        assertEquals(TEST, answer.get().getMessage().getData());
        assertEquals(3, answer.get().getTraceId());
        assertEquals("/request/action", answer.get().getPath());
    }
}
