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

import static org.zodiark.protocol.Paths.DB_GET_WORD;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SHOW_END;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_END;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_JOIN_SESSION;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_CONFIG;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_VALIDATE_STATE;

public class LocalDatabase {

    public enum RESULT {PASS, FAIL}

    EndpointMapper<String> mapper = new DefaultEndpointMapper<>();

    private final Map<String, String> fakePassDatabase = new HashMap<>();
    private final Map<String, String> fakeFailDatabase = new HashMap<>();


    public LocalDatabase() {
        fakePassDatabase.put(DB_POST_PUBLISHER_SESSION_CREATE.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(DB_PUBLISHER_CONFIG.replace("@uuid","{guid}"), " {\"configuration\": \"bla bla bla\"}");
        fakePassDatabase.put(DB_SUBSCRIBER_VALIDATE_STATE.replace("@uuid","{guid}"), " {\"configuration\": \"null\"}");
        fakePassDatabase.put(DB_POST_PUBLISHER_SHOW_START.replace("@uuid","{guid}"), " {\"showId\": \"123234\"}");
        fakePassDatabase.put(DB_POST_PUBLISHER_SHOW_END.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");

        fakePassDatabase.put(DB_POST_SUBSCRIBER_JOIN_SESSION.replace("@uuid","{guid}"), " {\"watchId\": \"123234\"}");

        fakePassDatabase.put(DB_GET_WORD.replace("@uuid","{guid}"),"{\"motds\": [{\"motdId\": 1, \"title\": \"foo\", \"message\": \"blabla\", \"createdOn\":\"20140125\", \"expiresOn\":\"20140125\", " +
                "\"expired\": true}]}");

        fakePassDatabase.put(DB_POST_SUBSCRIBER_CHARGE_START.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(DB_POST_SUBSCRIBER_CHARGE_END.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");

        fakePassDatabase.put(Paths.DB_POST_PUBLISHER_ONDEMAND_START.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(Paths.DB_POST_PUBLISHER_ONDEMAND_KEEPALIVE.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(Paths.DB_POST_PUBLISHER_ONDEMAND_END.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");

        fakePassDatabase.put(Paths.DB_GET_SUBSCRIBER_STATUS_TO_PUBLISHER_PASSTHROUGHT.replace("@uuid","{guid}"), " {\"no_need_to_parse\": \"_something_\"}");


        fakePassDatabase.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_START.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_END.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");

        fakePassDatabase.put(Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("@uuid","{guid}"), " {\"no_need_to_parse\": \"_something_\"}");
        fakePassDatabase.put(Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("@uuid","{guid}"), " {\"no_need_to_parse\": \"_something_\"}");


        fakePassDatabase.put(Paths.DB_SUBSCRIBER_REQUEST_ACTION.replace("@uuid","{guid}"), "{\"transactionId\": \"1\",\"clear\":\"true\"," +
                "\"joinDurationInSeconds\":30," +
                "\"minimumDurationInSeconds\":30," +
                "\"maximumDurationsInSeconds\":30," +
                "\"cooldownDurationInSeconds\":30}");

        fakePassDatabase.put(Paths.DB_SUBSCRIBER_JOIN_ACTION.replace("@uuid","{guid}"), "{\"transactionId\":1234}");

        fakePassDatabase.put(Paths.DB_SUBSCRIBER_CHARGE_ACTION.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(Paths.DB_SUBSCRIBER_BLOCK.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");
        fakePassDatabase.put(Paths.DB_SUBSCRIBER_EJECT.replace("@uuid","{guid}"), " {\"status\": \"OK\"}");


        fakeFailDatabase.put("/v1/publisher/{guid}/session/create", " {\"result\": \"ko\", \"data\":{\"status\":500, \"content\": \"null\"}}");
    }

    public String serve(OKRestService.METHOD m, String url, String body, RESULT passOrFail) {
        String bdResult;
        if (RESULT.PASS.equals(passOrFail)) {
            bdResult = mapper.map(url, fakePassDatabase);
        } else {
            bdResult = mapper.map(url, fakeFailDatabase);
        }
        return bdResult;

    }

}
