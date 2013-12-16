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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.zodiark.service.subscriber.SubscriberEndpoint;

/**
 * Represent an Action that can be submitted by a {@link SubscriberEndpoint}
 */
public class Action {

    private String path;
    private String data;
    private String subscriberUUID;
    @JsonIgnore
    private SubscriberEndpoint endpoint;
    @JsonIgnore
    private int time = 5;

    public Action(){
    }

    public Action(String path, String data) {
        this.path = path;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Action subscriber(SubscriberEndpoint endpoint) {
        this.endpoint = endpoint;
        subscriberUUID = endpoint.uuid();
        return this;
    }

    public SubscriberEndpoint subscriber() {
        return endpoint;
    }

    public String getSubscriberUUID() {
        return subscriberUUID;
    }

    public void setSubscriberUUID(String subscriberUUID) {
        this.subscriberUUID = subscriberUUID;
    }

    public int time() {
        return time;
    }

    public Action time(int time) {
        this.time = time;
        return this;
    }
}
