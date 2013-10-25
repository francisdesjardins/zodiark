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
package org.zodiark.server;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.service.Service;

public interface EventBus {

    EventBus fire(Envelope e, AtmosphereResource r);

    EventBus fire(Envelope e, AtmosphereResource r, EventBusListener l);

    EventBus fire(String e, Object r, EventBusListener l);

    EventBus fire(String e, Object r);

    /**
     * Register a {@link org.zodiark.service.Service}
     *
     * @param eventName
     * @param e
     * @return this
     */
    EventBus on(String eventName, Service e);

    /**
     * Unregister {@link Service} associated with an event.
     * @param eventName
     * @return this
     */
    EventBus off(String eventName);


    /**
     * Return the {@link Service} associated with the clazz
     *
     * @param clazz
     * @return
     */
    Service service(Class<? extends Service> clazz);

}
