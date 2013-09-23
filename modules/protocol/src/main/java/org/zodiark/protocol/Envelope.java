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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Envelope {

    private Path path;
    private TraceId traceId;
    private To to;
    private From from;
    private Message message;
    private Protocol protocol;
    private String uuid;

    public Path getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(Path path) {
        this.path = path;
    }

    public TraceId getTraceId() {
        return traceId;
    }

    @JsonProperty("traceId")
    public void setTraceId(TraceId traceId) {
        this.traceId = traceId;
    }

    public To getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(To to) {
        this.to = to;
    }

    public From getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(From from) {
        this.from = from;
    }

    public Message getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Message message) {
        this.message = message;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    @JsonProperty("protocol")
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
