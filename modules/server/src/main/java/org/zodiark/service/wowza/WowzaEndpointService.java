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
package org.zodiark.service.wowza;

import java.util.List;

/**
 * A Service for managing the lifecycle of {@link WowzaEndpoint}
 */
public interface WowzaEndpointService {

    /**
     * Return the list of connected and active {@link WowzaEndpoint}
     *
     * @return a list of connected endpoint
     */
    public List<WowzaEndpoint> endpoints();

    /**
     * Register a connected
     *
     * @param endpoint {@link WowzaEndpoint}
     * @return this
     */
    public WowzaEndpointService connected(WowzaEndpoint endpoint);

    /**
     * Unregister a {@link WowzaEndpoint}
     *
     * @param endpoint {@link WowzaEndpoint}
     * @return this
     */
    public WowzaEndpointService disconnected(WowzaEndpoint endpoint);
}
