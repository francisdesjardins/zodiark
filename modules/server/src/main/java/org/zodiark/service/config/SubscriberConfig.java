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
 * The data associated with a {@link org.zodiark.service.subscriber.SubscriberEndpoint}
 */
public interface SubscriberConfig extends DBResult {
    /**
     * The current {@link org.zodiark.service.session.StreamingSession#type()}
     *
     * @return the {@link org.zodiark.service.session.StreamingSession#type()}
     */
    StreamingSession.TYPE sessionType();

    /**
     * Set the current {@link org.zodiark.service.session.StreamingSession#type()}
     *
     * @param streamingSessionType {@link org.zodiark.service.session.StreamingSession#type()}
     * @return this
     */
    SubscriberConfig sessionType(StreamingSession.TYPE streamingSessionType);

    /**
     * Make sure the {@link org.zodiark.service.subscriber.SubscriberEndpoint} has the proper
     * state for executing {@link org.zodiark.service.action.Action}, joining session/chat et.c
     *
     * @return true is valid
     */
    boolean isStateValid();
}
