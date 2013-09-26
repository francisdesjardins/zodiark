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
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActorValue {

    SUBCRIBER("/subscriber"), PUBLISHER("/publisher"), STREAM_SERVER("/stream_server"), SERVER("/server"), MONITOR("/monitor"), CLIENT("client");

    private final String value;

    ActorValue(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActorValue deserialize(String v) {
        String value = v.toLowerCase();
        switch (value) {
            case "subscriber":
                return SUBCRIBER;
            case "publisher":
                return PUBLISHER;
            case "stream_server":
                return STREAM_SERVER;
            case "server":
                return SERVER;
            case "monitor":
                return MONITOR;
            default:
                throw new IllegalStateException("Invalid value " + value);
        }
    }

    @JsonValue
    final String value() {
        return this.value.toLowerCase();
    }
}
