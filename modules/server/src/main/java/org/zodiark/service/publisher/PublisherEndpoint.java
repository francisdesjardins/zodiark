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
import org.zodiark.protocol.Message;
import org.zodiark.server.Endpoint;

public class PublisherEndpoint implements Endpoint {

    private final String uuid;
    private PublisherConfig config;
    private final Message message;
    private final AtmosphereResource resource;

    public PublisherEndpoint(String uuid, Message message, AtmosphereResource resource) {
        this.uuid = uuid;
        this.message = message;
        this.resource = resource;
    }

    public String uuid() {
        return uuid;
    }

    public PublisherEndpoint config(PublisherConfig config) {
        this.config = config;
        return this;
    }

    public PublisherConfig config() {
        return config;
    }

    @Override
    public TYPE type() {
        return TYPE.PUBLISHER;
    }

    public Message message() {
        return message;
    }

    public AtmosphereResource resource() {
        return resource;
    }
}
