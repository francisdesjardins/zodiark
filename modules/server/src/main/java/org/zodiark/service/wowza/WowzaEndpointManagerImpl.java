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
package org.zodiark.service.wowza;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Instance of this class will be managed by the {@link org.zodiark.server.ZodiarkObjectFactory} and represent the
 * list of available Wowza remote endpoint.
 */
public class WowzaEndpointManagerImpl implements WowzaEndpointManager {

    private final Queue<WowzaEndpoint> endpoints = new ConcurrentLinkedQueue<WowzaEndpoint>();

    /**
     * Return the list of {@link WowzaEndpoint}
     * @return list of {@link WowzaEndpoint}
     */
    @Override
    public Queue<WowzaEndpoint> endpoints() {
        return endpoints;
    }

    /**
     * Bind/add a {@link WowzaEndpoint}
     * @param endpoint {@link WowzaEndpoint}
     * @return this
     */
    @Override
    public WowzaEndpointManager bind(WowzaEndpoint endpoint) {
        endpoints.offer(endpoint);
        return this;
    }

    /**
     * Unbind/remove a {@link WowzaEndpoint}
     * @param endpoint {@link WowzaEndpoint}
     * @return this
     */
    @Override
    public WowzaEndpointManager unbind(WowzaEndpoint endpoint) {
        endpoints.remove(endpoint);
        return this;
    }

    /**
     * Lookup a {@link WowzaEndpoint} based on a name
     * @param wowzaName the endpoint's name
     * @return {@link WowzaEndpoint}
     */
    @Override
    public WowzaEndpoint lookup(String wowzaName) {
        for (WowzaEndpoint e : endpoints) {
            if (e.uuid().equalsIgnoreCase(wowzaName)) {
                return e;
            }
        }
        return null;
    }
}
