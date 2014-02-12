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
package org.zodiark.service.publisher;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.Reply;
import org.zodiark.service.Service;

/**
 * A base class for Publisher's Service implementation.
 */
public interface PublisherService extends Service {
    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply);

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(String path, Object message, Reply reply);

    /**
     * Retrieve a {@link PublisherEndpoint} based on a String. It is recommended to pass the {@link PublisherEndpoint#uuid}
     * as a key for retrieving the Endpoint. The {@link Reply#ok(Object)} will be invoked if an {@link PublisherEndpoint}  \
     * is found, or {@link Reply#fail(org.zodiark.server.ReplyException)} if no {@link PublisherEndpoint} is associated with the key.
     *
     * @param publisherEndpointUuid The {@link PublisherEndpoint#uuid}
     * @param reply a {@link Reply}
     */
    public void retrieveEndpoint(Object publisherEndpointUuid, Reply reply);

    /**
     * When an {@link org.zodiark.service.action.Action} is ready to be executed, send a ready message back to the client
     * and wait for its response. The publisherEndpointUuid can be retrieved from an {@link Envelope}
     * @param publisherEndpointUuid The {@link PublisherEndpoint#uuid}
     * @param reply a {@link Reply}
     */
    public void resetEndpoint(Object publisherEndpointUuid, Reply reply);
}

