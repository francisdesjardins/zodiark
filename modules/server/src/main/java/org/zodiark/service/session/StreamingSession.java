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
package org.zodiark.service.session;

import org.zodiark.server.Reply;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A Streaming Session representation.
 */
public interface StreamingSession {
    /**
     * The type of Session.
     */
    enum TYPE { PUBLIC, PRIVATE, PROTECTED, SHARED_PRIVATE, VIEW}

    /**
     * Execute an {@link Action}
     * @param action  {@link Action}
     * @return this
     */
    StreamingSession executeAction(Action action);

    /**
     * Return the current pending action.
     * @return
     */
    Action pendingAction();

    /**
     * Set the current streaming {@link Action}
     * @param action {@link Action}
     * @return this
     */
    StreamingSession pendingAction(Action action);

    /**
     * Invoked when an Action has been completed an Wowza deobfuscated the stream
     * @param completedAction {@link Action}
     * @return this
     */
    StreamingSession completeAction(Action completedAction);

    /**
     * Validate the {@link SubscriberEndpoint} and then join the {@link Action}
     * @param subscriberEndpoint  {@link SubscriberEndpoint}
     * @param reply a {@link Reply}
     * @return this
     */
    StreamingSession validateAndJoin(SubscriberEndpoint subscriberEndpoint, Reply<SubscriberEndpoint> reply);

    /**
     * Return the {@link PublisherEndpoint} associated with this session.
     * @return {@link PublisherEndpoint}
     */
    PublisherEndpoint publisher();

    /**
     * Set the {@link PublisherEndpoint}
     * @param publisherEndpoint {@link PublisherEndpoint}
     * @return this
     */
    StreamingSession publisher(PublisherEndpoint publisherEndpoint);

    /**
     * Return the list of {@link SubscriberEndpoint} associated with this session
     * @return ConcurrentLinkedQueue<SubscriberEndpoint>
     */
    ConcurrentLinkedQueue<SubscriberEndpoint> subscribers();

    /**
     * The {@link StreamingSession.TYPE}
     * @return {@link StreamingSession.TYPE}
     */
    TYPE type();

    /**
     * Terminate this session
     */
    void terminate();

    /**
     * Initialize and prepare the streaming session.
     * @return this
     */
    StreamingSession initAndAct();

}
