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
package org.zodiark.service;

/**
 * Representation of a remote endpoint: can be a subscriber, a publisher, a super user or a wowza instance.
 */
public interface Endpoint {

    public enum TYPE { PUBLISHER, SUBSCRIBER, WOWZA, SUPER_USER, MONITOR}

    /**
     * The {@link TYPE}
     * @return
     */
    TYPE type();

    /**
     * Terminate the Endpoint
     */
    public void terminate();

    /**
     * A unique UUID for an Endpoint
     * @return unique UUID for an Endpoint
     */
    public String uuid();

}
