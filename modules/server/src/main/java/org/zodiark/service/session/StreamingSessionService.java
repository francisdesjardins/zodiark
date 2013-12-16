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

import org.zodiark.server.EventBusListener;
import org.zodiark.service.Service;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

/**
 * @author Jeanfrancois Arcand
 */
public interface StreamingSessionService extends Service {

    void terminate(PublisherEndpoint p, EventBusListener l);

    void initiate(PublisherEndpoint p, EventBusListener l);

    void join(SubscriberEndpoint s, EventBusListener l);

    void executeAction(final Action a, final EventBusListener l);

    void completeAction(PublisherEndpoint p);

}
