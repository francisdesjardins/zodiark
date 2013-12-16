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
package org.zodiark.service;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;

public interface Session<T extends Endpoint> {

    public T createSession(Envelope e, AtmosphereResource resource);

    public T config(Envelope e);

    public void error(Envelope e, T p, Message message);

    public void createOrJoinStreamingSession(Envelope e);

    public void startStreamingSession(Envelope e);

    public void response(Envelope e, T p, Message m);

    public void terminateStreamingSession(T p, AtmosphereResource r);

    public void errorStreamingSession(Envelope e);
}
