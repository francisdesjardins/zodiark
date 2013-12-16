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
package org.zodiark.service.publisher;

import org.zodiark.service.EndpointAdapter;
import org.zodiark.service.action.Action;

import java.util.concurrent.atomic.AtomicBoolean;

public class PublisherEndpoint extends EndpointAdapter<PublisherEndpoint> {

    private final AtomicBoolean actionInProgress = new AtomicBoolean();
    private Action action;

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
}
