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
package org.zodiark.service.subscriber;

import org.zodiark.service.config.SubscriberConfig;
import org.zodiark.service.session.StreamingSession;

public class SubscriberConfigImpl implements SubscriberConfig {

    private StreamingSession.TYPE sessionType;

    @Override
    public StreamingSession.TYPE sessionType() {
        return sessionType;
    }

    @Override
    public SubscriberConfig sessionType(StreamingSession.TYPE sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    @Override
    public boolean isStateValid() {
        return true;
    }

    public void populate(String state) {
        // TODO: Receive the state from the database.
    }
}
