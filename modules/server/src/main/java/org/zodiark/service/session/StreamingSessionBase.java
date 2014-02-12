/*
 * Copyright 2013-2014 High-Level Technologies
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
import org.zodiark.server.Reply;
import org.zodiark.server.ReplyException;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Default implementation of a Streaming Session.
 */
public abstract class StreamingSessionBase implements StreamingSession {

    private final Logger logger = LoggerFactory.getLogger(StreamingSessionBase.class);
    private PublisherEndpoint endpoint;
    private final ConcurrentLinkedQueue<SubscriberEndpoint> subscribers = new ConcurrentLinkedQueue<>();
    private Action pendingAction;

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession publisher(PublisherEndpoint publisherEndpoint) {
        endpoint = publisherEndpoint;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherEndpoint publisher() {
        return endpoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConcurrentLinkedQueue<SubscriberEndpoint> subscribers() {
        return subscribers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession validateAndJoin(SubscriberEndpoint subscriberEndpoint, Reply<SubscriberEndpoint, String> reply) {
        if (!validateSession(subscriberEndpoint)) {
            reply.fail(ReplyException.DEFAULT);
        } else {
            logger.debug("Subscriber {} joined Publisher {}", subscriberEndpoint, endpoint);
            subscribers.add(subscriberEndpoint);
            reply.ok(subscriberEndpoint);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        endpoint.terminate();
        for (SubscriberEndpoint s : subscribers) {
            s.terminate();
        }
    }

    /**
     * {@inheritDoc}
     */
    private boolean validateSession(SubscriberEndpoint s) {
        // TODO: Validate the Publisher.
        // Can it be added to the session ?
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession initAndAct() {
        // TODO: DB CALL
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession executeAction(Action action) {
        // TODO:
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession completeAction(Action completedAction) {
        // TODO:
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action pendingAction() {
        return pendingAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession pendingAction(Action action) {
        this.pendingAction = action;
        return this;
    }
}
