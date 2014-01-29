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
package org.zodiark.server;

/**
 * A {@link org.zodiark.service.Service} response to an {@link EventBus#ioEvent(org.zodiark.protocol.Envelope, org.atmosphere.cpr.AtmosphereResource, Reply)}
 * event.
 * @param <T>
 */
public interface Reply<T> {
    /**
     * The targeted {@link org.zodiark.service.Service} successfully processed the event delivered by the {@link EventBus}
     * @param response the {@link org.zodiark.service.Service}'s success response
     */
    void ok(T response);
    /**
     * The targeted {@link org.zodiark.service.Service} failed to process the event delivered by the {@link EventBus}
     * @param response the {@link org.zodiark.service.Service}'s failure response
     */
    void fail(T response);
}
