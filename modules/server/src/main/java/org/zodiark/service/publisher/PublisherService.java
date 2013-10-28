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
package org.zodiark.service.publisher;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.Service;

public interface PublisherService extends Service {

    @Override
    public void on(Envelope e, AtmosphereResource r, EventBusListener l) ;

    @Override
    public void on(Object r, EventBusListener l);

    public PublisherEndpoint createPublisherSession(Envelope e, AtmosphereResource resource);

    public PublisherEndpoint config(Envelope e);

    public void error(Envelope e, PublisherEndpoint p);

    public void createStreamingSession(Envelope e);

    public void startStreamingSession(Envelope e);

    public void response(Envelope e, PublisherEndpoint p, Message m);

    public void terminateStreamingSession(PublisherEndpoint p, AtmosphereResource r);

}

