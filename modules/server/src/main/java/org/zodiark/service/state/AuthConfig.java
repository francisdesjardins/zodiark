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
package org.zodiark.service.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zodiark.service.db.result.Result;

/**
 * A class for representing the publisher/subscriber authorization token.
 */
public interface AuthConfig extends Result {
    /**
     * Is the {@link org.zodiark.service.publisher.PublisherEndpoint
     * @return true if authenticated
     */
    boolean isAuthenticated();

    @JsonProperty("status")
    void status(String status);

    String status();
}
