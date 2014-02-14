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
package org.zodiark.service.rest;

import org.atmosphere.util.DefaultEndpointMapper;
import org.atmosphere.util.EndpointMapper;
import org.zodiark.protocol.Paths;

import java.util.HashMap;
import java.util.Map;

import static org.zodiark.protocol.Paths.DB_ENDPOINT_STATE;
import static org.zodiark.protocol.Paths.DB_GET_WORD_PASSTHROUGH;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_END;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_CHARGE_START;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_JOIN_SESSION;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;

public class InMemoryRestClient implements RestClient {

    public enum RESULT {PASS, FAIL}

    public final static String PASSTHROUGH = "{\"no_need_to_parse\":\"_something_\"}";
    public final static String STATUS_OK = "{\"result\":\"OK\"}";
    public final static String TRANSACTION_ID = "{\"transactionId\":1234}";
    public final static String FAVORITE_ID = "{\"favoriteId\":1234}";
    public final static String SHOWID ="{\"showId\":123234}";
    public final static String MOTD = "{\"motds\":[{\"motdId\":1,\"title\":\"foo\",\"message\":\"blabla\",\"createdOn\":\"20140125\",\"expiresOn\":\"20140125\",\"expired\":true}]}";
    public final static String STATE = "{\"username\":\"123\",\"language\":\"fr\",\"showId\":123,\"watchId\":123,\"modeId\":123,\"type\":\"0\",\"administrator\": true,\"guid\":\"123456\"}";
    public final static String ACTIONS = "{\n" +
            "    \"actions\": [\n" +
            "        {\n" +
            "            \"actionId\": 123,\n" +
            "            \"title\": \"foo\",\n" +
            "            \"cost\": 123,\n" +
            "            \"groupDiscount\": 123,\n" +
            "            \"minimumDurationInSeconds\": 30,\n" +
            "            \"scramble\": true,\n" +
            "            \"forced\": true,\n" +
            "            \"createdOn\": \"1502000\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    public final static String ACTION_REQUEST="{\n" +
            "    \"transactionId\": 1234,\n" +
            "    \"scramble\": true,\n" +
            "    \"joinDurationInSeconds\": 30,\n" +
            "    \"minimumDurationInSeconds\": 30,\n" +
            "    \"maximumDurationsInSeconds\": 30,\n" +
            "    \"cooldownDurationInSeconds\": 30\n" +
            "}";

    EndpointMapper<String> mapper = new DefaultEndpointMapper<>();

    private final Map<String, String> get = new HashMap<>();
    private final Map<String, String> post = new HashMap<>();
    private final Map<String, String> put = new HashMap<>();
    private final Map<String, String> delete = new HashMap<>();

    private final Map<String, String> fakeFailDatabase = new HashMap<>();

    public InMemoryRestClient() {
        post.put(DB_POST_PUBLISHER_SESSION_CREATE, STATUS_OK);
        get.put(DB_ENDPOINT_STATE, STATE);

        post.put(DB_PUBLISHER_SHOW_START, SHOWID);
        delete.put(DB_PUBLISHER_SHOW_END, STATUS_OK);

        post.put(DB_POST_SUBSCRIBER_JOIN_SESSION, " {\"watchId\": \"123234\"}");

        get.put(DB_GET_WORD_PASSTHROUGH,MOTD);

        post.put(DB_POST_SUBSCRIBER_CHARGE_START, STATUS_OK);
        delete.put(DB_POST_SUBSCRIBER_CHARGE_END, STATUS_OK);

        post.put(Paths.DB_POST_PUBLISHER_ONDEMAND_START, STATUS_OK);
        post.put(Paths.DB_POST_PUBLISHER_ONDEMAND_KEEPALIVE, STATUS_OK);
        delete.put(Paths.DB_POST_PUBLISHER_ONDEMAND_END, STATUS_OK);

        get.put(Paths.DB_GET_SUBSCRIBER_STATUS_TO_PUBLISHER_PASSTHROUGHT, PASSTHROUGH);


        post.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_START, STATUS_OK);
        delete.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_START, STATUS_OK);

        put.put(Paths.DB_PUBLISHER_SHARED_PRIVATE_END, STATUS_OK);

        get.put(Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS, ACTIONS);
        get.put(Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT, PASSTHROUGH);
        put.put(Paths.DB_PUBLISHER_ACTIONS, STATUS_OK);

        post.put(Paths.DB_SUBSCRIBER_REQUEST_ACTION, ACTION_REQUEST);

        post.put(Paths.DB_SUBSCRIBER_JOIN_ACTION, TRANSACTION_ID);

        post.put(Paths.DB_SUBSCRIBER_CHARGE_ACTION, STATUS_OK);
        post.put(Paths.DB_SUBSCRIBER_BLOCK, STATUS_OK);
        post.put(Paths.DB_SUBSCRIBER_EJECT, STATUS_OK);
        delete.put(Paths.DB_SUBSCRIBER_FAVORITES_END, STATUS_OK);
        get.put(Paths.DB_PUBLISHER_LOAD_CONFIG_PASSTHROUGHT, PASSTHROUGH);
        put.put(Paths.DB_PUBLISHER_SAVE_CONFIG, STATUS_OK);

        get.put(Paths.DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT, PASSTHROUGH);
        get.put(Paths.DB_PUBLISHER_LOAD_CONFIG, PASSTHROUGH);

        get.put(Paths.DB_PUBLISHER_SETTINGS_SHOW, PASSTHROUGH);
        put.put(Paths.DB_PUBLISHER_SETTINGS_SHOW, STATUS_OK);

        post.put(Paths.DB_PUBLISHER_PUBLIC_MODE, STATUS_OK);

        delete.put(Paths.DB_PUBLISHER_PUBLIC_MODE_END, STATUS_OK);
        post.put(Paths.DB_PUBLISHER_ERROR_REPORT, STATUS_OK);

        put.put(Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE_PUT, STATUS_OK);
        get.put(Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE_GET_PASSTHROUGH, PASSTHROUGH);


        post.put(Paths.DB_SUBSCRIBER_EXTRA, TRANSACTION_ID);

        post.put(Paths.DB_POST_SUBSCRIBER_SESSION_CREATE, STATUS_OK);

        put.put(Paths.DB_SUBSCRIBER_CONFIG_PASSTHROUGHT, PASSTHROUGH);

        post.put(Paths.DB_SUBSCRIBER_FAVORITES_START, FAVORITE_ID);
    }

    @Override
    public String serve(RestServiceImpl.METHOD m, String url, String body) {
        return serve(m, url, body, RESULT.PASS);
    }

    public String serve(RestService.METHOD m, String url, String body, RESULT passOrFail) {
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
