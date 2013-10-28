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

import org.zodiark.server.Endpoint;

public class SubscriberEndpoint implements Endpoint {

    private String uuid;


    public SubscriberEndpoint uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public Endpoint.TYPE type() {
        return TYPE.SUBSCRIBER;
    }

    @Override
    public void terminate() {

    }

    @Override
    public String uuid() {
        return uuid;
    }
}
