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
package org.zodiark.service.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionState {
    private int transactionId;
    private boolean clear;
    private int joinDurationInSeconds;
    private int minimumDurationInSeconds;
    private int maximumDurationsInSeconds;
    private int cooldownDurationInSeconds;

    public ActionState() {
    }

    @JsonProperty("transactionId")
    public ActionState transactionId(int transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @JsonProperty("joinDurationInSeconds")
    public ActionState joinDurationInSeconds(int joinDurationInSeconds) {
        this.joinDurationInSeconds = joinDurationInSeconds;
        return this;
    }

    @JsonProperty("minimumDurationInSeconds")
    public ActionState minimumDurationInSeconds(int minimumDurationInSeconds) {
        this.minimumDurationInSeconds = minimumDurationInSeconds;
        return this;
    }

    @JsonProperty("maximumDurationsInSeconds")
    public ActionState maximumDurationsInSeconds(int maximumDurationsInSeconds) {
        this.maximumDurationsInSeconds = maximumDurationsInSeconds;
        return this;
    }

    @JsonProperty("cooldownDurationInSeconds")
    public ActionState cooldownDurationInSeconds(int cooldownDurationInSeconds) {
        this.cooldownDurationInSeconds = cooldownDurationInSeconds;
        return this;
    }

    @JsonProperty("clear")
    public ActionState clear(boolean clear) {
        this.clear = clear;
        return this;
    }
}
