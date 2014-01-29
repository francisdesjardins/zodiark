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
package org.zodiark.service;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;

/**
 * A {@link Endpoint} Session for a Publisher, Susbcriber or Wowza remote client.
 * @param <T>
 */
public interface Session<T extends Endpoint> {
    /**
     * Create the {@link org.zodiark.service.publisher.PublisherEndpoint} Streaming Session. The validation phase will imply
     * calling the remote database/web service to load in memory the publisher/subscriber data. The Endpoint will be
     * called upon success or failure of creating the session.
     *
     * @param e        an {@link Envelope}
     * @param resource a {@link AtmosphereResource}
     * @return An implementation of {@link Endpoint}
     */
    public T createSession(Envelope e, AtmosphereResource resource);

    /**
     * Invoke the remote database/web service and retrieve the data associated with the {@link Endpoint}
     *
     * @param e an {@link Envelope} received from a remote {@link Endpoint}
     * @return the {@link Endpoint}
     */
    public T config(Envelope e);

    /**
     * Write an error response back to the remote {@link Endpoint}
     *
     * @param e        an {@link Envelope}
     * @param endpoint a {@link Endpoint}
     * @param m        a {@link Message}
     */
    public void error(Envelope e, T endpoint, Message m);

    /**
     * Create or join a Streaming Session. The Session will be created if the {@link Endpoint} is a Publisher, and joined if the
     * endpoint is a Subscriber. Implementation of this method must make sure the Wowza Server will allow the Endpoint to
     * create or Join the session.
     *
     * @param e an {@link Envelope} from the {@link Endpoint}
     */
    public void createOrJoinStreamingSession(Envelope e);

    /**
     * Start the streaming session. This method will be called by the remote endpoint upon successful completion of
     * {@link #createSession(org.zodiark.protocol.Envelope, org.atmosphere.cpr.AtmosphereResource)}
     * and {@link #createOrJoinStreamingSession(org.zodiark.protocol.Envelope)}
     *
     * @param e an {@link Envelope} from a remote {@link Endpoint}
     */
    public void startStreamingSession(Envelope e);

    /**
     * Write a response back to the remote {@link Endpoint}
     *
     * @param e        an {@link Envelope}
     * @param endpoint a {@link Endpoint}
     * @param m        a {@link Message}
     */
    public void response(Envelope e, T endpoint, Message m);

    /**
     * Terminate the streaming session.
     *
     * @param endpoint The {@link Endpoint}
     * @param r        The associated {@link AtmosphereResource}
     */
    public void terminateStreamingSession(T endpoint, AtmosphereResource r);

    /**
     * Handle a failed request for opening a streaming session
     *
     * @param e An {@link Envelope} containing the failed response from Wowza
     */
    public void errorStreamingSession(Envelope e);
}
