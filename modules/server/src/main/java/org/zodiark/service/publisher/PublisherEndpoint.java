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
package org.zodiark.service.publisher;

import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.action.Action;
import org.zodiark.service.db.ShowId;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link PublisherEndpoint} is the coordinator of all operation. It's the representation of a remote publisher and
 * can be associated with one or more {@link org.zodiark.service.subscriber.SubscriberEndpoint}. A {@link PublisherEndpoint}
 * can execute {@link Action} requested by {@link org.zodiark.service.subscriber.SubscriberEndpoint}
 */
public class PublisherEndpoint extends EndpointAdapter<PublisherEndpoint> {

    private final AtomicBoolean actionInProgress = new AtomicBoolean();
    private Action action;
    private ShowId showId = new ShowId().showId(-1);

    public PublisherEndpoint() {
        super();
    }

    // TODO: Should we allow this?
    public boolean actionInProgress() {
        return actionInProgress.get();
    }

    public PublisherEndpoint actionInProgress(boolean b) {
        actionInProgress.set(b);
        return this;
    }

    public Action action() {
        return action;
    }

    public PublisherEndpoint action(Action action) {
        this.action = action;
        return this;
    }

    @Override
    public TYPE type() {
        return TYPE.PUBLISHER;
    }

    public PublisherEndpoint showId(ShowId showId) {
        this.showId = showId;
        return this;
    }

    public int showId() {
        return showId.showId();
    }
}
