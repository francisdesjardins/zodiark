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
package org.zodiark.service.action;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.Service;
import org.zodiark.service.publisher.PublisherEndpoint;

/**
 * @author Jeanfrancois Arcand
 */
public interface ActionService extends Service {

    @Override
    public void serve(Envelope e, AtmosphereResource r);

    @Override
    public void serve(String event, Object message, EventBusListener l);

    public void validateAction(Action action, EventBusListener l);

    public void actionAccepted(Envelope e);

    public void actionRefused(Envelope e);

    public void requestForAction(PublisherEndpoint p, Action action);

    public void actionStarted(Envelope e);
}
