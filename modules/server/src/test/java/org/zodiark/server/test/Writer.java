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
import org.atmosphere.cpr.AtmosphereResponse;
import org.zodiark.protocol.Envelope;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Writer extends AsyncIOWriterAdapter {
    public final ConcurrentLinkedQueue<Envelope> e = new ConcurrentLinkedQueue<>();
    public AtomicReference<Throwable> error = new AtomicReference<>();
    public ObjectMapper mapper = new ObjectMapper();

    @Override
    public AsyncIOWriter writeError(AtmosphereResponse r, int errorCode, String message) throws IOException {
        error.set(new IOException(message));
        return this;
    }

    @Override
    public AsyncIOWriter write(AtmosphereResponse r, String data) throws IOException {
        e.add(Envelope.newEnvelope(data, mapper));
        return this;
    }

    @Override
    public AsyncIOWriter write(AtmosphereResponse r, byte[] data) throws IOException {
        e.add(Envelope.newEnvelope(new String(data, "UTF-8"), mapper));
        return this;
    }

    @Override
    public AsyncIOWriter write(AtmosphereResponse r, byte[] data, int offset, int length) throws IOException {
        e.add(Envelope.newEnvelope(new String(data, offset, length, "UTF-8"), mapper));
        return this;
    }
}
