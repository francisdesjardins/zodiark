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
package org.zodiark.service.subscriber;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.EventBusListener;
import org.zodiark.service.Service;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;

public interface SubscriberService extends Service {

    public void validateAndExecuteAction(Envelope e, AtmosphereResource r, EventBusListener l);

    public void requestForAction(Envelope e, PublisherEndpoint p, Action action);

    public void acceptAction(Envelope e);

    public void refuseAction(Envelope e);
}
