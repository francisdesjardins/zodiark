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
package org.zodiark.service.subscriber;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.Reply;
import org.zodiark.service.Service;

/**
 * Base class for Service responsible for managing Subscriber.
 */
public interface SubscriberService extends Service {

    /**
     * Retrieve the {@link SubscriberEndpoint} associated with a
     * @param subscriberUuid
     * @param reply
     */
    public void retrieveEndpoint(Object subscriberUuid, Reply reply);

    /**
     * Create a {@link SubscriberEndpoint} from an {@link Envelope} received from the remote endpoint
     * @param e an {@link Envelope}
     * @param r an {@link AtmosphereResource}
     */
    public void connectEndpoint(Envelope e, AtmosphereResource r);
}
