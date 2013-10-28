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
package org.zodiark.service.session;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.Context;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.publisher.PublisherConfig;
import org.zodiark.service.publisher.PublisherEndpoint;

import java.util.concurrent.ConcurrentHashMap;

@On("/create/{endpoint}/")
public class StreamingSessionServiceImpl implements StreamingSessionService {

    @Inject
    Context context;

    private ConcurrentHashMap<String, StreamingSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void on(Envelope e, AtmosphereResource r, EventBusListener l) {
    }

    @Override
    public void on(Object message, EventBusListener l) {
        if (PublisherEndpoint.class.isAssignableFrom(message.getClass())) {
            PublisherEndpoint p = PublisherEndpoint.class.cast(message);

            boolean hasStreamingSession = hasStreamingSession(p);
            if (hasStreamingSession) {
                terminate(p, l);
            } else {
                initiate(p, l);
            }
        }
    }

    public void terminate(PublisherEndpoint p, EventBusListener l) {
        StreamingSession s = sessions.remove(p.uuid());
        s.terminate();
    }

    public void initiate(PublisherEndpoint p, EventBusListener l) {
        StreamingSession s = sessionType(p);
        sessions.put(p.uuid(), s);

        s.initAndAct();

        l.completed(p);
    }

    private boolean hasStreamingSession(PublisherEndpoint p) {
        return sessions.get(p.uuid()) == null ? false : true;
    }

    private StreamingSession sessionType(PublisherEndpoint p) {
        PublisherConfig config = p.config();

        switch (config.sessionType()) {
            case PUBLIC:
                return context.newInstance(PublicStreamingSession.class);
            case PRIVATE:
                return context.newInstance(PrivateStreamingSession.class);
            case SHARED_PRIVATE:
                return context.newInstance(SharedPrivateStreamingSession.class);
            case VIEW:
                return context.newInstance(ViewStreamingSession.class);
            case PROTECTED:
                return context.newInstance(ProtectedStreamingSession.class);
            default:
                throw new IllegalStateException("Unsupported Session " + config.sessionType());
        }
    }

}
