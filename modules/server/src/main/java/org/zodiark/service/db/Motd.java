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
package org.zodiark.service.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Motd {
    private int motdId;
    private String title;
    private String message;
    private String createdOn;
    private String expiresOn;
    private boolean expired;

    public Motd(){};

    @JsonProperty("motdId")
    public Motd motdId(int motdId) {
        this.motdId = motdId;
        return this;
    }

    @JsonProperty("title")
    public Motd title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("message")
    public Motd message(String message) {
        this.message = message;
        return this;
    }

    @JsonProperty("createdOn")
    public Motd createdOn(String createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @JsonProperty("expiresOn")
    public Motd expiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
        return this;
    }

    @JsonProperty("expired")
    public Motd expired(boolean expired) {
        this.expired = expired;
        return this;
    }
}
