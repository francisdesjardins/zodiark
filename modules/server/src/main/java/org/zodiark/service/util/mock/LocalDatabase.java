/*
 * Copyright 2014 Jeanfrancois Arcand
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
package org.zodiark.service.util.mock;

import org.atmosphere.util.DefaultEndpointMapper;
import org.atmosphere.util.EndpointMapper;
import org.zodiark.protocol.Paths;

import java.util.HashMap;
import java.util.Map;

import static org.zodiark.protocol.Paths.DB_PUBLISHER_ANNOUNCE_SESSION;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_CONFIG;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_JOIN_SESSION;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_VALIDATE_STATE;

public class LocalDatabase {

    public enum RESULT {PASS, FAIL}

    EndpointMapper<String> mapper = new DefaultEndpointMapper<>();

    private final Map<String, String> fakePassDatabase = new HashMap<>();
    private final Map<String, String> fakeFailDatabase = new HashMap<>();


    public LocalDatabase() {
        fakePassDatabase.put(DB_PUBLISHER_SESSION_CREATE.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(DB_PUBLISHER_CONFIG.replace("@uuid","{guid}"), " {\"configuration\": \"bla bla bla\"}");
        fakePassDatabase.put(DB_SUBSCRIBER_VALIDATE_STATE.replace("@uuid","{guid}"), " {\"configuration\": \"null\"}");
        fakePassDatabase.put(DB_PUBLISHER_ANNOUNCE_SESSION.replace("@uuid","{guid}"), " {\"showId\": \"123234\"}");
        fakePassDatabase.put(DB_SUBSCRIBER_JOIN_SESSION.replace("@uuid","{guid}"), " {\"watchId\": \"123234\"}");


        fakeFailDatabase.put("/v1/publisher/{guid}/session/create", " {\"result\": \"ko\", \"data\":{\"status\":500, \"content\": \"null\"}}");
    }

    public String serve(String url, String body, RESULT passOrFail) {
        String bdResult;
        if (RESULT.PASS.equals(passOrFail)) {
            bdResult = mapper.map(url, fakePassDatabase);
        } else {
            bdResult = mapper.map(url, fakeFailDatabase);
        }
        return bdResult;

    }

}
