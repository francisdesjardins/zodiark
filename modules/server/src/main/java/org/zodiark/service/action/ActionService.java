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
import org.zodiark.server.Reply;
import org.zodiark.service.Service;
import org.zodiark.service.publisher.PublisherEndpoint;

/**
 * A {@link Service} for handling {@link Action}
 */
public interface ActionService extends Service {
    /**
     * {@inheritDoc}
     */
    @Override
    public void serve(Envelope e, AtmosphereResource r);

    /**
     * {@inheritDoc}
     */
    @Override
    public void serve(String event, Object message, Reply l);

    /**
     * Validate an {@link Action}
     *
     * @param action {@link Action}
     * @param reply  a {@link Reply} for answering the {@link Service} that asked for an {@link Action}
     */
    public void validateAction(Action action, Reply reply);

    /**
     * Accept an {@link Action}
     *
     * @param e {@link Envelope}
     */
    public void actionAccepted(Envelope e);

    /**
     * Refuse an {@link Action}
     *
     * @param e {@link Envelope}
     */
    public void actionRefused(Envelope e);

    /**
     * Start processing the validation of an {@link Action}
     *
     * @param p      a {@link PublisherEndpoint}
     * @param action an {@link Action}
     */
    public void requestForAction(PublisherEndpoint p, Action action);

    /**
     * Action just started.
     *
     * @param e an {@link Envelope}
     */
    public void actionStarted(Envelope e);
}
