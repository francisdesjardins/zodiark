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
package org.zodiark.wowza;

import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Encoder;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.impl.AtmosphereClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Path;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZodiarkClient {

    private final static Logger logger = LoggerFactory.getLogger(ZodiarkClient.class);

    private final Builder b;
    private Socket socket = null;
    private final ConcurrentLinkedQueue<EnvelopeHandler> events = new ConcurrentLinkedQueue<>();

    protected ZodiarkClient(Builder b) {
        this.b = b;
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }

    public ZodiarkClient handler(EnvelopeHandler e) {
        events.add(e);
        return this;
    }

    public ZodiarkClient open() throws IOException {

        AtmosphereClient client = ClientFactory.getDefault().newClient(AtmosphereClient.class);

        RequestBuilder request = client.newRequestBuilder()
                .method(Request.METHOD.GET)
                .uri(b.servicePath)
                .trackMessageLength(true)
                .encoder(b.encoder)
                .decoder(b.decoder)
                .transport(Request.TRANSPORT.WEBSOCKET)
                .transport(Request.TRANSPORT.STREAMING)
                .transport(Request.TRANSPORT.LONG_POLLING);


        final CountDownLatch connected = new CountDownLatch(1);
        socket = client.create();
        socket.on(new Function<Integer>() {
            @Override
            public void on(Integer statusCode) {
                connected.countDown();
            }
        }).on("message", new Function<Envelope>() {

            @Override
            public void on(Envelope e) {
                for (EnvelopeHandler event : events) {
                    try {
                        if (event.onEnvelop(e)) events.remove(event);
                    } catch (Exception ex) {
                        logger.error("", ex);
                    }
                }
            }
        }).on(new Function<Throwable>() {

            @Override
            public void on(Throwable t) {
                for (EnvelopeHandler event : events) {
                    try {
                        if (event.onError(t)) events.remove(event);
                    } catch (Exception ex) {
                        logger.error("", ex);
                    }
                }
            }
        }).on(Event.CLOSE.name(), new Function<String>() {
            @Override
            public void on(String t) {
                for (EnvelopeHandler event : events) {
                    try {
                        if (event.onClose()) events.remove(event);
                    } catch (Exception ex) {
                        logger.error("", ex);
                    }
                }
            }
        }).open(request.build());

        try {
            connected.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("{}", e);
        }
        return this;
    }

    public ZodiarkClient send(String s) throws IOException {
        if (socket != null) {
            socket.fire(s);
        }
        return this;
    }

    public ZodiarkClient send(Envelope e) throws IOException {
        if (socket != null) {
            socket.fire(e);
        }
        return this;
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            args = new String[]{"http://127.0.0.1:8080"};
        }

        final ZodiarkClient c = new Builder().path(args[0]).build();
        c.handler(new OnEnvelopHandler() {
            @Override
            public boolean onEnvelop(Envelope e) throws IOException {
                logger.info("Received {}", e);
                c.handler(new OnEnvelopHandler() {
                    @Override
                    public boolean onEnvelop(Envelope e) throws IOException {
                        logger.info("Final {}", e);
                        return false;
                    }
                }).send(Envelope.newClientReply(e, e.getMessage()));
                return true;
            }
        }).open().send(Envelope.newClientToServerRequest(new Message(new Path("/echo"), "This is a test")));
    }

    public static class Builder {

        private String servicePath;
        private Encoder<Envelope, String> encoder = new ZodiarkEncoder();
        private Decoder<String, Envelope> decoder = new ZodiarkDecoder();

        public Builder path(String servicePath) {
            this.servicePath = servicePath;
            return this;
        }

        public Builder encoder(Encoder<Envelope, String> encoder) {
            this.encoder = encoder;
            return this;
        }

        public Builder decode(Decoder<String, Envelope> decoder) {
            this.decoder = decoder;
            return this;
        }

        public ZodiarkClient build() {
            return new ZodiarkClient(this);
        }

    }

}
