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
package org.zodiark.service;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.service.state.EndpointState;

/**
 * Base class for {@link Endpoint}
 * @param <T>
 */
public class EndpointAdapter<T> implements Endpoint {
    protected String uuid;
    protected EndpointState state;
    protected AtmosphereResource resource;
    protected String wowzaServer;

    public EndpointAdapter() {
    }

    public String uuid() {
        return uuid;
    }

    public T state(EndpointState state) {
        this.state = state;
        return (T) this;
    }

    public EndpointState state() {
        return state;
    }

    public T uuid(String uuid) {
        this.uuid = uuid;
        return (T) this;
    }

    @Override
    public TYPE type() {
        return TYPE.SUPER_USER;
    }

    @Override
    public void terminate() {
    }

    public AtmosphereResource resource() {
        return resource;
    }

    public String wowzaServerUUID() {
        return wowzaServer;
    }

    public T wowzaServerUUID(String wowzaServer) {
        this.wowzaServer = wowzaServer;
        return (T) this;
    }

    public T resource(AtmosphereResource resource) {
        this.resource = resource;
        return (T) this;
    }
}
