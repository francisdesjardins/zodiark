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
package org.zodiark.service;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.Reply;

/**
 * A Service is a target of an {@link org.zodiark.server.EventBus}. A Service can react to I/O events and messages dispatched
 * via the {@link org.zodiark.server.EventBus}. A Service implementation must be annotated with the {@link org.zodiark.server.annotation.On}
 * annotated in order to be discovered at runtime and made available to an EventBus.
 * <p/>
 */
public interface Service {
    /**
     * React to I/O events produced by a remote {@link Endpoint} and received via an {@link AtmosphereResource} connection.
     * @param e an {@link org.zodiark.protocol.Envelope}
     * @param r a {@link org.atmosphere.cpr.AtmosphereResource}, representing the connection from the remote {@link org.zodiark.service.Endpoint}
     * @param reply a {@link Reply}
     */
    void reactTo(Envelope e, AtmosphereResource r, Reply reply);

    /**
     * React to message produced by other {@link Service}
     * @param path a {@link org.zodiark.protocol.Paths}
     * @param message a message
     * @param reply a {@link Reply}
     */
    void reactTo(String path, Object message, Reply reply);

}
