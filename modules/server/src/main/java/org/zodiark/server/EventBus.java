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

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.service.Service;

/**
 * A distributed lightweight event bus which can encompass multiple Service instances. The event bus implements publish / subscribe and point to point messaging.
 * Messages sent over the event bus can be of any type. For publish / subscribe, messages can be published to a {@link Service} annotated with the {@link org.zodiark.server.annotation.On#value()}
 * using one of the {@link #ioEvent(org.zodiark.protocol.Envelope, org.atmosphere.cpr.AtmosphereResource)} and {@link #message(String, Object)}.
 * {@link #ioEvent(org.zodiark.protocol.Envelope, org.atmosphere.cpr.AtmosphereResource)} and
 * {@link #ioEvent(org.zodiark.protocol.Envelope, org.atmosphere.cpr.AtmosphereResource, Reply)} are used to publish I/O events to {@link Service}
 * tha require the manipulation of {@link AtmosphereResource}. {@link #message(String, Object)} and {@link #message(String, Object, Reply)} are used to publish message to other
 * {@link Service}
 * <p/>
 * {@link Service} are registered against a path, defined using the {@link org.zodiark.server.annotation.On}. There can be multiple Service registered against each path,
 * and a particular Service can be registered against multiple paths. The event bus will route a sent message to all Service which are registered against that path.
 * All messages sent over the bus are transient. On event of failure of all or part of the event bus messages may be lost.
 * Applications should be coded to cope with lost messages, e.g. by resending them, and making application services idempotent.
 * When sending a message, a {@link Reply} can be provided. If so, it will be called when the reply from the receiver has been completed.
 * Instances of EventBus are thread-safe.
 */
public interface EventBus {
    /**
     * Deliver an I/O event to the {@link Service} that match the {@link org.zodiark.protocol.Envelope#getMessage()}'s path value.
     *
     * @param e an {@link Envelope}
     * @param r an {@link AtmosphereResource}
     * @return this
     */
    EventBus ioEvent(Envelope e, AtmosphereResource r);

    /**
     * Deliver an I/O events to the {@link Service} that match the {@link org.zodiark.protocol.Envelope#getMessage()}'s path value.
     *
     * @param e     an {@link Envelope}
     * @param r     an {@link AtmosphereResource}
     * @param reply a {@link Reply} for handling the {@link Service}'s success of failure
     * @return this
     */
    EventBus ioEvent(Envelope e, AtmosphereResource r, Reply reply);

    /**
     * Deliver a message to the {@link Service} that match the path value.
     *
     * @param path    an {@link Envelope}
     * @param message an {@link AtmosphereResource}
     * @return this
     */
    EventBus message(String path, Object message);

    /**
     * Deliver a message to the {@link Service} that match the path value.
     *
     * @param path    an {@link Envelope}
     * @param message an {@link AtmosphereResource}
     * @param reply   a {@link Reply} for handling the {@link Service}'s success of failure
     * @return this
     */
    EventBus message(String path, Object message, Reply reply);

    /**
     * Register a path and a {@link org.zodiark.service.Service}. This is the same as doing the {@link org.zodiark.server.annotation.On}
     *
     * @param path    the path that will be used to map {@link Service}
     * @param service an instance of {@link Service}
     * @return this
     */
    EventBus onIOEvent(String path, Service service);


    /**
     * Register a path and a {@link org.zodiark.service.Service}. This is the same as doing the {@link org.zodiark.server.annotation.On}
     *
     * @param path    the path that will be used to map {@link Service}
     * @param service an instance of {@link Service}
     * @return this
     */
    EventBus on(String path, Service service);

    /**
     * Unregister {@link Service}
     *
     * @param path
     * @return this
     */
    EventBus off(String path);

}
