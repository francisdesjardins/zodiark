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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An Envelope is the vehicule for exchanging data between Zodiark's Endpoint. All communication must be serialized using an Envelope.
 */
public class Envelope {

    private String path;
    private int traceId;
    private String to;
    private String from;
    private Message message;
    private String protocol;
    private String uuid;

    private Envelope(Builder b) {
        this.path = b.path.getPath();
        this.traceId = b.traceId.getId();
        this.to = b.to.getTo();
        this.from = b.from.getFrom();
        this.message = b.message;
        this.protocol = b.protocol.toString();
        this.uuid = b.uuid;
    }

    protected Envelope() {
    }

    public String getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(Path path) {
        this.path = path.getPath();
    }

    public int getTraceId() {
        return traceId;
    }

    @JsonProperty("traceId")
    public void setTraceId(TraceId traceId) {
        this.traceId = traceId.getId();
    }

    public String getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(To to) {
        this.to = to.getTo();
    }

    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(From from) {
        this.from = from.getFrom();
    }

    public Message getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Message message) {
        this.message = message;
    }

    public String getProtocol() {
        return protocol.toString();
    }

    @JsonProperty("protocol")
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol.toString();
    }

    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "{" +
                "path:'" + path + '\'' +
                ", traceId:" + traceId +
                ", to:'" + to + '\'' +
                ", from:'" + from + '\'' +
                ", message:" + message +
                ", protocol:'" + protocol + '\'' +
                ", uuid:'" + uuid + '\'' +
                '}';
    }

    public final static class Builder {
        private Path path = new Path(LeftPathValue.REQUEST, RightPathValue.ACTION);
        private TraceId traceId = new TraceId();
        private To to = new To(ActorValue.SERVER.name());
        private From from = new From(ActorValue.STREAM_SERVER);
        private Message message;
        private Protocol protocol = new Protocol();
        private String uuid = "";

        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        public Builder traceId(TraceId traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder to(To to) {
            this.to = to;
            return this;
        }

        public Builder from(From from) {
            this.from = from;
            return this;
        }

        public Builder message(Message message) {
            this.message = message;
            return this;
        }

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Envelope build() {
            if (message == null) {
                throw new NullPointerException("message is null");
            }
            return new Envelope(this);
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }
    }

    public final static Envelope newServerReply(Envelope envelope, Message message) {
        return new Envelope.Builder()
                .path(new Path(replyValue(envelope.getPath())))
                .to(new To(envelope.getFrom()))
                .from(new From(ActorValue.SERVER.value()))
                .traceId(new TraceId(envelope.getTraceId() + 1))
                .message(message)
                .uuid(envelope.getUuid())
                .build();
    }

    public final static Envelope newStreamToServerRequest(String path, String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path(path))
                .to(new To(ActorValue.STREAM_SERVER.value()))
                .from(new From(ActorValue.SERVER.value()))
                .traceId(new TraceId())
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newPublisherToServerRequest(String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path("/request/action"))
                .to(new To(ActorValue.PUBLISHER.value()))
                .from(new From(ActorValue.SERVER.value()))
                .traceId(new TraceId())
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newSubscriberrToServerRequest(String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path("/request/action"))
                .to(new To(ActorValue.SUBSCRIBER.value()))
                .from(new From(ActorValue.SERVER.value()))
                .traceId(new TraceId())
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newPublisherRequest(String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path("/request/action"))
                .to(new To(ActorValue.PUBLISHER.value()))
                .from(new From(ActorValue.SERVER.value()))
                .traceId(new TraceId())
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newPublisherMessage(String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path("/message"))
                .to(new To(ActorValue.PUBLISHER.value()))
                .from(new From(ActorValue.SERVER.value()))
                .traceId(new TraceId())
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newSubscriberMessage(String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path("/message"))
                .to(new To(ActorValue.SUBSCRIBER.value()))
                .from(new From(ActorValue.SUBSCRIBER.value()))
                .traceId(new TraceId())
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newError(String uuid) {
        return new Envelope.Builder()
                .path(new Path("/error"))
                .to(new To(ActorValue.SUBSCRIBER.value()))
                .from(new From(ActorValue.SUBSCRIBER.value()))
                .traceId(new TraceId())
                .uuid(uuid)
                .build();
    }

    private final static String replyValue(String path) {
        Path p = new Path(path);
        String left = p.left().startsWith("/") ? p.left() : "/" + p.left();
        switch (left) {
            case "/request":
                return LeftPathValue.RESPONSE.name() + p.right();
            case "/response":
                return LeftPathValue.REQUEST.name() + p.right();
            case "/message":
                return LeftPathValue.MESSAGE.name() + p.right();
            case "/react":
                return LeftPathValue.OK.name() + p.right();
            default:
                return path;
        }
    }

    public final static Envelope newClientToServerRequest(Message message) {
        return newClientToServerRequest("0", message);
    }

    public final static Envelope newClientToServerRequest(String uuid, Message message) {
        return new Builder()
                .path(new Path(LeftPathValue.REQUEST, RightPathValue.ACTION))
                .to(new To(ActorValue.SERVER.value()))
                .from(new From(ActorValue.STREAM_SERVER.value()))
                .message(message)
                .uuid(uuid)
                .build();
    }

    public final static Envelope newServerToSubscriberResponse(Message message) {
        return newServerToSubscriberResponse("0", message);
    }

    public final static Envelope newServerToSubscriberResponse(String uuid, Message message) {
        return new Envelope.Builder()
                .path(new Path(LeftPathValue.REQUEST, RightPathValue.ACTION))
                .to(new To(ActorValue.SUBSCRIBER.value()))
                .from(new From(ActorValue.SERVER.value()))
                .uuid(uuid)
                .message(message)
                .build();
    }

    public final static Envelope newClientReply(Envelope envelope, Message message) {
        return new Envelope.Builder()
                .path(new Path(replyValue(envelope.getPath())))
                .to(new To(envelope.getFrom()))
                .from(new From(ActorValue.STREAM_SERVER.value()))
                .traceId(new TraceId(envelope.getTraceId() + 1))
                .message(message)
                .uuid(envelope.getUuid())
                .build();
    }
}
