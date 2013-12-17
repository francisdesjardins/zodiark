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
package org.zodiark.service.wowza;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.service.Service;

/**
 * A Service for handling the interaction with remote Wowza {@link org.zodiark.service.Endpoint}
 */
public interface WowzaService extends Service {

    /**
     * Process a Wowza remote endpoint connection.
     *
     * @param e the {@link Envelope} sent by the remote endpoint
     * @param r {@link AtmosphereResource} the underlying connection.
     */
    void connected(Envelope e, AtmosphereResource r);

    /**
     * Prepare and send the {@link Envelope} back to remote Wowza endpoint
     *
     * @param e the {@link Envelope} to send to the remote endpoint
     * @param r {@link AtmosphereResource} the underlying connection.
     * @param m a {@link Message}
     */
    void response(Envelope e, AtmosphereResource r, Message m);
}
