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
package org.zodiark.service.subscriber;

import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.db.Actions;
import org.zodiark.service.db.TransactionId;
import org.zodiark.service.publisher.PublisherEndpoint;

/**
 * Represent the remote endpoint Subscriber.
 */
public class SubscriberEndpoint extends EndpointAdapter<SubscriberEndpoint> {

    private PublisherEndpoint publisherEndpoint;
    private boolean hasSession;
    private boolean actionRequested;
    private TransactionId transactionId;
    private Actions actionsAvailable;

    public SubscriberEndpoint() {
        super();
        hasSession = false;
    }

    public SubscriberEndpoint publisherEndpoint(PublisherEndpoint publisherEndpoint) {
        this.publisherEndpoint = publisherEndpoint;
        return this;
    }

    public PublisherEndpoint publisherEndpoint() {
        return publisherEndpoint;
    }

    public boolean hasSession() {
        return hasSession;
    }

    public SubscriberEndpoint hasSession(boolean hasSession) {
        this.hasSession = hasSession;
        return this;
    }

    public Actions actionsAvailable() {
        return actionsAvailable;
    }

    public SubscriberEndpoint actionsAvailable(Actions actionsAvailable) {
        this.actionsAvailable = actionsAvailable;
        return this;
    }

    public boolean actionRequested() {
        return actionRequested;
    }

    public SubscriberEndpoint actionRequested(boolean actionRequested) {
        this.actionRequested = actionRequested;
        return this;
    }

    public TransactionId transactionId() {
        return transactionId;
    }

    public SubscriberEndpoint transactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @Override
    public TYPE type() {
        return TYPE.SUBSCRIBER;
    }
}
