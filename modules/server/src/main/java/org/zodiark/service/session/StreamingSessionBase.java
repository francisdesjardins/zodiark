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

import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class StreamingSessionBase implements StreamingSession {

    private PublisherEndpoint endpoint;
    private final ConcurrentLinkedQueue<SubscriberEndpoint> subscribers = new ConcurrentLinkedQueue<>();

    @Override
    public StreamingSession publisher(PublisherEndpoint p) {
        endpoint = p;
        return this;
    }

    @Override
    public PublisherEndpoint publisher() {
        return endpoint;
    }

    @Override
    public List<SubscriberEndpoint> susbcribers() {
        return susbcribers();
    }

    @Override
    public StreamingSession susbcriber(SubscriberEndpoint s) {
        subscribers.add(s);
        return this;
    }

    @Override
    public void terminate() {
        endpoint.terminate();
        for (SubscriberEndpoint s : subscribers) {
            s.terminate();
        }
    }

    @Override
    public StreamingSession initAndAct() {

        return this;
    }

}
