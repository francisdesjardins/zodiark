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
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Service;
import org.zodiark.service.publisher.PublisherEndpoint;

import java.util.concurrent.ConcurrentLinkedQueue;

@On("/create/{endpoint}/")
public class LiveSession implements Service {

    private ConcurrentLinkedQueue<PublisherSession> sessions = new ConcurrentLinkedQueue<>();

    @Override
    public void on(Envelope e, AtmosphereResource r, EventBusListener l) {
    }

    @Override
    public void on(Object message, EventBusListener l) {

        if (PublisherEndpoint.class.isAssignableFrom(message.getClass())) {
            PublisherEndpoint p = PublisherEndpoint.class.cast(message);
            // TODO: The Implementation Class should be injected and read from config.
            PublisherSession s = new PublisherSessionImpl(p);

            sessions.offer(s);

            // TODO: Add session's logic
            l.completed(p);
        }

    }

}
