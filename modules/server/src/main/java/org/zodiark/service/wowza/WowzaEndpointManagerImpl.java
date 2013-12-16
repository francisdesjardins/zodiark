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
package org.zodiark.service.wowza;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: Allow Injection of this guy
public class WowzaEndpointManagerImpl implements WowzaEndpointManager {

    private final Queue<WowzaEndpoint> endpoints = new ConcurrentLinkedQueue<WowzaEndpoint>();

    @Override
    public Queue<WowzaEndpoint> endpoints() {
        return endpoints;
    }

    @Override
    public WowzaEndpointManager bind(WowzaEndpoint endpoint) {
        endpoints.offer(endpoint);
        return this;
    }

    @Override
    public WowzaEndpointManager unbind(WowzaEndpoint endpoint) {
        endpoints.remove(endpoint);
        return this;
    }

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
