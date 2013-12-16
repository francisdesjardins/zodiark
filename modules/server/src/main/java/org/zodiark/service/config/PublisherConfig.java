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
package org.zodiark.service.config;

import org.zodiark.service.db.DBResult;
import org.zodiark.service.session.StreamingSession;

/**
 * The data associated with a {@link org.zodiark.service.publisher.PublisherEndpoint}
 */
public interface PublisherConfig extends DBResult {
    /**
     * The current {@link StreamingSession.TYPE}
     *
     * @return the {@link StreamingSession.TYPE}
     */
    StreamingSession.TYPE sessionType();

    /**
     * Set the current {@link StreamingSession.TYPE}
     *
     * @param streamingSessionType {@link StreamingSession.TYPE}
     * @return this
     */
    PublisherConfig sessionType(StreamingSession.TYPE streamingSessionType);
}
