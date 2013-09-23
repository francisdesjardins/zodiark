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
package org.zodiark.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;

public class From {

    private ActorValue from;

    public From() {
        this.from = ActorValue.SERVER;
    }

    @JsonCreator
    public From(ActorValue from) {
        this.from = from;
    }

    public ActorValue getFrom() {
        return from;
    }

    public void setFrom(ActorValue from) {
        this.from = from;
    }

}
