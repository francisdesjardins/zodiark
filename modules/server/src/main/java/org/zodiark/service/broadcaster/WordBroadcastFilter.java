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
package org.zodiark.service.broadcaster;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.PerRequestBroadcastFilter;

public class WordBroadcastFilter implements PerRequestBroadcastFilter {

    private final BroadcasterDBResult broadcasterDBResult;

    public WordBroadcastFilter(BroadcasterDBResult broadcasterDBResult) {
        this.broadcasterDBResult = broadcasterDBResult;
    }

    @Override
    public BroadcastAction filter(AtmosphereResource r, Object originalMessage, Object message) {
        if (broadcasterDBResult.deny(message)) {
            return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
        }
        return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
    }

    @Override
    public BroadcastAction filter(Object originalMessage, Object message) {
         // TODO
        return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
    }
}
