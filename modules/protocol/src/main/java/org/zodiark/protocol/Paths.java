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
package org.zodiark.protocol;

/**
 * @author Jeanfrancois Arcand
 */
public interface Paths {

    String START_STREAMING_SESSION = "/publisher/startStreamingSession";

    String JOIN_STREAMING_SESSION = "/subscriber/joinStreamingSession";

    String CREATE_PUBLISHER_SESSION = "/publisher/createUserSession";

    String CREATE_SUBSCRIBER_SESSION = "/subscriber/createUserSession";

    String LOAD_CONFIG = "/publisher/loadConfig";

    String VALIDATE_PUBLISHER_STREAMING_SESSION = "/publisher/validateStreamingSession";

    String VALIDATE_SUBSCRIBER_STREAMING_SESSION = "/subscriber/validateStreamingSession";

    String WOWZA_ERROR_STREAMING_SESSION = "/publisher/errorStreamingSession";

    String WOWZA_ERROR_SUBSCRIBER_STREAMING_SESSION = "/subscriber/errorStreamingSession";

    String ERROR_STREAMING_SESSION = "/error/errorStreamingSession";

    String BEGIN_STREAMING_SESSION = "/streaming/begin/publisher";

    String WOWZA_CONNECT = "/wowza/connect";

    String WOWZA_OBFUSCATE = "/wowza/obfuscate";

    String WOWZA_DEOBFUSCATE = "/wowza/deobfuscate";

    String WOWZA_OBFUSCATE_OK = "/wowza/obfuscate/ok";

    String WOWZA_DEOBFUSCATE_OK = "/wowza/deobfuscate/ok";

    String DB_CONFIG = "/db/config";

    String DB_INIT = "/db/init";

    String DB_WORD = "/db/word";

    String SUBSCRIBER_VALIDATE_STATE = "/db/validate";

    String TERMINATE_STREAMING_SESSSION = "/publisher/disconnect";

    String TERMINATE_SUBSCRIBER_STREAMING_SESSSION = "/subscriber/disconnect";

    String SERVER_VALIDATE_OK = "/wowza/validate";

    String REQUEST_ACTION = "/request/action";

    String MESSAGE_ACTION = "/message/action";

    String RETRIEVE_PUBLISHER = "/publisher/retrieve";

    String RETRIEVE_SUBSCRIBER = "/subscriber/retrieve";

    String SUBSCRIBER_ACTION = "/subscriber/action";

    String ACTION_VALIDATE = "/action/validate";

    String ACTION_ACCEPT = "/action/accept";

    String ACTION_ACCEPT_OK = "/action/actionAccepted";

    String ACTION_START = "/action/start";

    String ACTION_ACCEPT_REFUSED = "/action/actionRefused";

    String BEGIN_SUBSCRIBER_STREAMING_SESSION = "/streaming/join";

    String STREAMING_EXECUTE_ACTION = "/streaming/executeAction";

    String STREAMING_COMPLETE_ACTION = "/streaming/completeAction";

    String ACTION_START_OK = "/action/start/ok";

    String ACTION_TIMER = "/action/timer";

    String ACTION_COMPLETED = "/action/completed";

    String PUBLISHER_ACTION_COMPLETED = "/publisher/actionCompleted";

    String PUBLISHER_ABOUT_READY = "/publisher/ready";

    String SUBSCRIBER_BROWSER_HANDSHAKE = "/subscriber/handshake";

    String SUBSCRIBER_BROWSER_HANDSHAKE_OK = "/subscriber/handshake/OK";

    String BROADCASTER_CREATE = "/broadcaster/create";

    String BROADCASTER_TRACK = "/broadcaster/track";

    String BROADCASTER_DISPATCH = "/broadcaster/dispatch";
}