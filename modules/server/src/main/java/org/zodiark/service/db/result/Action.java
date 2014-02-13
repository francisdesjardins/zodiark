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

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getGroupDiscount() {
        return groupDiscount;
    }

    public void setGroupDiscount(int groupDiscount) {
        this.groupDiscount = groupDiscount;
    }

    public int getMinimumDurationInSeconds() {
        return minimumDurationInSeconds;
    }

    public void setMinimumDurationInSeconds(int minimumDurationInSeconds) {
        this.minimumDurationInSeconds = minimumDurationInSeconds;
    }

    public boolean isScramble() {
        return scramble;
    }

    public void setScramble(boolean scramble) {
        this.scramble = scramble;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (actionId != action.actionId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return actionId;
    }
}
