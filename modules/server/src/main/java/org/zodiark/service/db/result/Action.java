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
package org.zodiark.service.db.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Action {
    public int actionId;
    public String title;
    public int cost;
    public int groupDiscount;
    public int minimumDurationInSeconds;
    public boolean scramble;
    public boolean forced;
    public String createdOn;

    public Action(){
    }

    public int getActionId() {
        return actionId;
    }

    @JsonProperty("actionId")
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public int getCost() {
        return cost;
    }

    @JsonProperty("cost")
    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getGroupDiscount() {
        return groupDiscount;
    }

    @JsonProperty("groupDiscount")
    public void setGroupDiscount(int groupDiscount) {
        this.groupDiscount = groupDiscount;
    }

    public int getMinimumDurationInSeconds() {
        return minimumDurationInSeconds;
    }

    @JsonProperty("minimumDurationInSeconds")
    public void setMinimumDurationInSeconds(int minimumDurationInSeconds) {
        this.minimumDurationInSeconds = minimumDurationInSeconds;
    }

    public boolean isScramble() {
        return scramble;
    }

    @JsonProperty("scramble")
    public void setScramble(boolean scramble) {
        this.scramble = scramble;
    }

    public boolean isForced() {
        return forced;
    }

    @JsonProperty("forced")
    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
