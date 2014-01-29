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
package org.zodiark.service.session;

/**
 * A simple object used to represent a request from a Subscriber to join an existing Streaming Session
 */
public interface StreamingRequest {

    /**
     * The Publisher' UUID, which will be used to retrieve the streaming session.
     *
     * @return Publisher' UUID
     */
    public String getPublisherUUID();

    /**
     * Return the Wowza's UUID, which will be used to for the streaming session.
     *
     * @return Wowza's UUID
     */
    public String getWowzaUUID();
}
