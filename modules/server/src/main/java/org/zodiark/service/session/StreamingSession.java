/*
 * Copyright 2013 Jeanfrancois Arcand
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
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.util.List;

public interface StreamingSession {

    public enum TYPE { PUBLIC, PRIVATE, PROTECTED, SHARED_PRIVATE, VIEW}

    public StreamingSession validateAndJoin(SubscriberEndpoint s, EventBusListener<SubscriberEndpoint> e);

    public PublisherEndpoint owner();

    public StreamingSession owner(PublisherEndpoint p);

    public List<SubscriberEndpoint> susbcribers();

    public TYPE type();

    public void terminate();

    public StreamingSession initAndAct();

}
