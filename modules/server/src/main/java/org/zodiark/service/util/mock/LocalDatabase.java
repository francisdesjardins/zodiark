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
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_END;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_JOIN_SESSION;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_CONFIG;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_VALIDATE_STATE;

public class LocalDatabase {

    public enum RESULT {PASS, FAIL}

    public final static String PASSTHROUGH = "{\"no_need_to_parse\": \"_something_\"}\"";
    public final static String STATUS_OK = "{\"result\":\"OK\"}";
    public final static String TRANSACTION_ID = "{\"transactionId\":1234}";
    public final static String SHOWID ="{\"showId\":123234}";

    EndpointMapper<String> mapper = new DefaultEndpointMapper<>();

    private final Map<String, String> get = new HashMap<>();
    private final Map<String, String> post = new HashMap<>();
    private final Map<String, String> put = new HashMap<>();
    private final Map<String, String> delete = new HashMap<>();

    private final Map<String, String> fakeFailDatabase = new HashMap<>();


    public LocalDatabase() {
        post.put(DB_POST_PUBLISHER_SESSION_CREATE.replace("@guid","{guid}"), STATUS_OK);
        get.put(DB_PUBLISHER_CONFIG.replace("@guid","{guid}"), " {\"configuration\": \"bla bla bla\"}");
        get.put(DB_SUBSCRIBER_VALIDATE_STATE.replace("@guid","{guid}"), " {\"configuration\": \"null\"}");
        post.put(DB_PUBLISHER_SHOW_START.replace("@guid","{guid}"), SHOWID);
        delete.put(DB_PUBLISHER_SHOW_END.replace("@guid","{guid}"), STATUS_OK);

        post.put(DB_POST_SUBSCRIBER_JOIN_SESSION.replace("@guid","{guid}"), " {\"watchId\": \"123234\"}");

        post.put(DB_GET_WORD.replace("@guid","{guid}"),"{\"motds\": [{\"motdId\": 1, \"title\": \"foo\", \"message\": \"blabla\", \"createdOn\":\"20140125\", \"expiresOn\":\"20140125\", " +
                "\"expired\": true}]}");

        post.put(DB_POST_SUBSCRIBER_CHARGE_START.replace("@guid","{guid}"), STATUS_OK);
        delete.put(DB_POST_SUBSCRIBER_CHARGE_END.replace("@guid","{guid}"), STATUS_OK);

        post.put(Paths.DB_POST_PUBLISHER_ONDEMAND_START.replace("@guid","{guid}"), STATUS_OK);
        post.put(Paths.DB_POST_PUBLISHER_ONDEMAND_KEEPALIVE.replace("@guid","{guid}"), STATUS_OK);
        delete.put(Paths.DB_POST_PUBLISHER_ONDEMAND_END.replace("@guid","{guid}"), STATUS_OK);

        get.put(Paths.DB_GET_SUBSCRIBER_STATUS_TO_PUBLISHER_PASSTHROUGHT.replace("@guid","{guid}"), PASSTHROUGH);


        put.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_START.replace("@guid","{guid}"), STATUS_OK);
        put.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_END.replace("@guid","{guid}"), STATUS_OK);

        get.put(Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("@guid","{guid}"), PASSTHROUGH);
        get.put(Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT.replace("@guid","{guid}"), PASSTHROUGH);


        post.put(Paths.DB_SUBSCRIBER_REQUEST_ACTION.replace("@guid","{guid}"), "{\"transactionId\": \"1\",\"clear\":\"true\"," +
                "\"joinDurationInSeconds\":30," +
                "\"minimumDurationInSeconds\":30," +
                "\"maximumDurationsInSeconds\":30," +
                "\"cooldownDurationInSeconds\":30}");

        put.put(Paths.DB_SUBSCRIBER_JOIN_ACTION.replace("@guid","{guid}"), TRANSACTION_ID);

        post.put(Paths.DB_SUBSCRIBER_CHARGE_ACTION.replace("@guid","{guid}"), STATUS_OK);
        post.put(Paths.DB_SUBSCRIBER_BLOCK.replace("@guid","{guid}"), STATUS_OK);
        post.put(Paths.DB_SUBSCRIBER_EJECT.replace("@guid","{guid}"), STATUS_OK);
        post.put(Paths.DB_SUBSCRIBER_END.replace("@guid","{guid}"), STATUS_OK);
        get.put(Paths.DB_PUBLISHER_LOAD_CONFIG_PASSTHROUGHT.replace("@guid","{guid}"), PASSTHROUGH);
        put.put(Paths.DB_PUBLISHER_SAVE_CONFIG.replace("@guid", "{guid}"), STATUS_OK);

        get.put(Paths.DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT.replace("@guid", "{guid}"), PASSTHROUGH);

        get.put(Paths.DB_PUBLISHER_CONFIG_SHOW_AVAILABLE_PASSTHROUGHT.replace("@guid", "{guid}"), PASSTHROUGH);

        put.put(Paths.DB_PUBLISHER_SAVE_CONFIG_SHOW.replace("@guid", "{guid}"), STATUS_OK);

        put.put(Paths.DB_PUBLISHER_PUBLIC_MODE.replace("@guid", "{guid}"), STATUS_OK);

        delete.put(Paths.DB_PUBLISHER_PUBLIC_MODE_END.replace("@guid", "{guid}"), STATUS_OK);
        post.put(Paths.DB_PUBLISHER_ERROR_REPORT.replace("@guid", "{guid}"), STATUS_OK);

        put.put(Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE.replace("@guid", "{guid}"), STATUS_OK);

        put.put(Paths.DB_SUBSCRIBER_EXTRA.replace("@guid", "{guid}"), TRANSACTION_ID);

        put.put(Paths.DB_ENDPOINT_STATE.replace("@guid", "{guid}"), TRANSACTION_ID);

        put.put(Paths.DB_SUBSCRIBER_CONFIG_PASSTHROUGHT.replace("@guid", "{guid}"), PASSTHROUGH);
    }

    public String serve(OKRestService.METHOD m, String url, String body, RESULT passOrFail) {
        String bdResult;
        if (RESULT.PASS.equals(passOrFail)) {
            switch(m) {
                case POST:
                    bdResult = mapper.map(url, post);
                    break;
                case DELETE:
                    bdResult = mapper.map(url, delete);
                    break;
                case GET:
                    bdResult = mapper.map(url, get);
                    break;
                case PUT:
                    bdResult = mapper.map(url, put);
                    break;
                default:
                    throw new IllegalStateException();
            }
        } else {
            bdResult = mapper.map(url, fakeFailDatabase);
        }
        return bdResult;

    }

}
