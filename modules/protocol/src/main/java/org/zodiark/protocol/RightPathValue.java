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

public enum RightPathValue {

    ACTION("/action"), COMMAND("/command"), SYSTEMS("/systems"), EXECUTION("/execution"), CUSTOM("/custom");

    private String value;

    RightPathValue(String value) {
        this.value = value;
    }

    public static RightPathValue deserialize(String v) {
        String[] s = v.split("/");
        if (s.length > 2) {
            String value = s[2].toLowerCase();
            switch (value) {
                case "action":
                    return ACTION;
                case "command":
                    return COMMAND;
                case "execution":
                    return EXECUTION;
                case "systems":
                    return SYSTEMS;
                default:
                    return CUSTOM.value(value);
            }
        } else {
            return CUSTOM.value("");
        }
    }

    final String value() {
        return this.value.toLowerCase();
    }

    final RightPathValue value(String value) {
        this.value = value;
        return this;
    }

}
