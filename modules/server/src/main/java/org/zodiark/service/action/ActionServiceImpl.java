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
package org.zodiark.service.action;

import org.atmosphere.cpr.AtmosphereResource;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Paths;
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.On;
import org.zodiark.service.subscriber.SubscriberEndpoint;

@On("/action")
public class ActionServiceImpl implements ActionService {

    @Override
    public void serve(Envelope e, AtmosphereResource r, EventBusListener l) {
    }

    @Override
    public void serve(String event, Object message, EventBusListener l) {
        switch (event) {
            case Paths.ACTION_VALIDATE:
                if (Action.class.isAssignableFrom(message.getClass())) {
                    Action action = Action.class.cast(message);
                    if (validate(action.endpoint())) {
                        l.completed(action);
                    } else {
                        l.failed(action);
                    }
                }
        }
    }

    public boolean validate(SubscriberEndpoint s) {
        // TODO: Validate The Subscriber State
        return true;
    }

}
