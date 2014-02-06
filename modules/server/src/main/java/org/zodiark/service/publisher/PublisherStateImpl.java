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

import org.zodiark.service.config.PublisherState;
import org.zodiark.service.db.ShowId;
import org.zodiark.service.session.StreamingSession;

/**
 * A {@link PublisherEndpoint} data representation. Instance of this class are populated from a database/web service.
 */
public class PublisherStateImpl implements PublisherState {
    private StreamingSession.TYPE streamingSessionType;
    private ShowId showId = new ShowId(-1);

    public PublisherStateImpl() {
        sessionType(StreamingSession.TYPE.PRIVATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherState sessionType(StreamingSession.TYPE streamingSessionType) {
        this.streamingSessionType = streamingSessionType;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession.TYPE sessionType() {
        return streamingSessionType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherState showId(ShowId showId) {
        this.showId = showId;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShowId showId() {
        return showId;
    }



}
