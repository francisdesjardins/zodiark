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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class StreamingSessionBase implements StreamingSession {

    private final Logger logger = LoggerFactory.getLogger(StreamingSessionBase.class);
    private PublisherEndpoint endpoint;
    private final ConcurrentLinkedQueue<SubscriberEndpoint> subscribers = new ConcurrentLinkedQueue<>();
    private Action pendingAction;

    @Override
    public StreamingSession owner(PublisherEndpoint p) {
        endpoint = p;
        return this;
    }

    @Override
    public PublisherEndpoint owner() {
        return endpoint;
    }

    @Override
    public List<SubscriberEndpoint> susbcribers() {
        return susbcribers();
    }

    @Override
    public StreamingSession validateAndJoin(SubscriberEndpoint s, EventBusListener<SubscriberEndpoint> e) {
        if (!validateSession(s)) {
            e.failed(s);
        } else {
            logger.debug("Subscriber {} joined Publisher {}", s, endpoint);
            subscribers.add(s);
            e.completed(s);
        }
        return this;
    }

    @Override
    public void terminate() {
        endpoint.terminate();
        for (SubscriberEndpoint s : subscribers) {
            s.terminate();
        }
    }

    private boolean validateSession(SubscriberEndpoint s) {
        // TODO: Validate the Publisher.
        // Can it be added to the session ?
        return true;
    }

    @Override
    public StreamingSession initAndAct() {
        // TODO: DB CALL
        return this;
    }

    @Override
    public void executeAction(Action action) {

    }

    public Action pendingAction() {
        return pendingAction;
    }

    public StreamingSession pendingAction(Action action) {
        this.pendingAction = action;
        return this;
    }
}
