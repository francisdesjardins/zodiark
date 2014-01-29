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

import org.zodiark.server.Reply;
import org.zodiark.service.Service;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

/**
 * The Service responsible for creating and manipulating streaming session workflow.
 */
public interface StreamingSessionService extends Service {
    /**
     * Terminate the session
     *
     * @param publisherEndpoint {@link PublisherEndpoint}
     * @param reply             {@link Reply}
     */
    void terminate(PublisherEndpoint publisherEndpoint, Reply reply);

    /**
     * Initialize the {@link StreamingSession} by broadcasting a message to all other chat session.
     *
     * @param publisherEndpoint {@link PublisherEndpoint}
     * @param reply             {@link Reply}
     */
    void initiate(PublisherEndpoint publisherEndpoint, Reply reply);

    /**
     * Join an already started session by retriving the session associated with the {@link PublisherEndpoint}
     *
     * @param subscriberEndpoint  {@link SubscriberEndpoint}
     * @param reply              {@link Reply}
     */
    void join(SubscriberEndpoint subscriberEndpoint, Reply reply);

    /**
     * Execute an {@link Action}
     *
     * @param a     an {@link Action}
     * @param reply {@link Reply}
     */
    void executeAction(final Action a, final Reply reply);

    /**
     * Complete an {@link Action}. This method is responsible for invoking Wowza server in order to de-obfuscate
     * the {@link org.zodiark.service.Endpoint} not included by this {@link Action}
     * @param publisherEndpoint {@link PublisherEndpoint}
     */
    void completeAction(PublisherEndpoint publisherEndpoint);

}
