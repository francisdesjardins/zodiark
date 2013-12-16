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

import org.zodiark.server.impl.DefaultEventBus;

/**
 * A factory for retrieving an instance of {@link EventBus}. It is not recommended to use this class directly from a Service.
 * Instead, use the Inject annotation to install EventBus.
 */
public class EventBusFactory {

    private static EventBusFactory factory;
    private final EventBus eventBus;

    private EventBusFactory() {
        eventBus = new DefaultEventBus();
    }
    /**
     * Return this factory
     * @return a {@link EventBusFactory}
     */
    public final synchronized static EventBusFactory getDefault() {
        if (factory == null) {
            factory = new EventBusFactory();
        }
        return factory;
    }
    /**
     * Return the default {@link EventBus}
     * @return {@link EventBus}
     */
    public EventBus eventBus() {
        return eventBus;
    }

}
