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
package org.zodiark.service.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zodiark.service.db.result.ModeId;
import org.zodiark.service.db.result.ShowId;
import org.zodiark.service.db.result.WatchId;

public class EndpointState {

    public final static String VOID = "-1";

    private ShowId showId;
    private String username;
    private WatchId watchId;
    private String language;
    private ModeId modeId;
    private String type;
    private boolean administrator;
    private String publisherUUID = VOID;

    public EndpointState(){}

    public ShowId showId() {
        return showId;
    }

    @JsonProperty("showId")
    public EndpointState showId(ShowId showId) {
        this.showId = showId;
        return this;
    }

    @JsonProperty("username")
    public EndpointState userName(String username) {
        this.username = username;
        return this;
    }

    @JsonProperty("language")
    public EndpointState language(String language) {
        this.language = language;
        return this;
    }

    public WatchId watchId() {
        return watchId;
    }

    @JsonProperty("watchId")
    public EndpointState watchId(WatchId watchId) {
        this.watchId = watchId;
        return this;
    }

    public ModeId modeId() {
        return modeId;
    }

    @JsonProperty("modeId")
    public EndpointState modeId(ModeId modeId) {
        this.modeId = modeId;
        return this;
    }

    public String userName() {
        return username;
    }

    public String type() {
        return type;
    }

    @JsonProperty("type")
    public EndpointState type(String type) {
        this.type = type;
        return this;
    }

    public boolean administrator() {
        return administrator;
    }

    @JsonProperty("administrator")
    public EndpointState administrator(boolean administrator) {
        this.administrator = administrator;
        return this;
    }


    public String publisherUUID() {
        return publisherUUID;
    }

    @JsonProperty("guid")
    public EndpointState publisherUUID(String publisherUUID) {
        this.publisherUUID = publisherUUID;
        return this;
    }
}
