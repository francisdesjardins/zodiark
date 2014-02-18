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
package org.zodiark.protocol;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.UUID;

public class Message {

    private static final String START_JSON = "{";
    private static final String END_JSON = "}";
    private static final String QUOTE = "\"";

    private String path;
    private String data = "";
    private String uuid = UUID.randomUUID().toString();

    public Message(){
    }

    public Message(Path path, String message) {
        this.path = path.toString();
        this.data = message;
    }

    public String getPath() {
        return path;
    }

    public Message setPath(String path) {
        this.path = path;
        return this;
    }

    @JsonRawValue
    public String getData() {
        return encodeIntoJSON();
    }

    public Message setData(String data) {
        this.data = data;
        return this;
    }

    public String getUUID() {
        return uuid;
    }

    public Message setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public boolean hasData() {
        return data != null && !data.isEmpty() && !data.equalsIgnoreCase("null");
    }

    private String encodeIntoJSON(){
        if (data.startsWith(START_JSON) || data.startsWith(QUOTE)) {
            return data;
        } else {
            return QUOTE + data + QUOTE;
        }
    }

    @Override
    public String toString() {
        return START_JSON +
                "path:'" + path + '\'' +
                ", uuid:'" + uuid + '\'' +
                ", data:'" + encodeIntoJSON() + '\'' +
                END_JSON;
    }
}
