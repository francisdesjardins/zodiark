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
package org.zodiark.service.broadcaster;

import org.zodiark.service.Service;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

/**
 * Handle the life cycle of Atmosphere's {@link BroadcasterService}
 */
public interface BroadcasterService extends Service {
    /**
     * Create a {@link org.atmosphere.cpr.Broadcaster} based on a {@link PublisherEndpoint}
     * @param p a {@link PublisherEndpoint}
     */
    void createBroadcaster(PublisherEndpoint p);

    /**
     * Associate a {@link SubscriberEndpoint} to a {@link BroadcasterService}
     * @param s {@link SubscriberEndpoint}
     */
    void associatedSubscriber(SubscriberEndpoint s);

    /**
     * Broadcast a message to all connected {@link SubscriberEndpoint}
     * @param message
     */
    void broadcastToAll(Object message);

}
