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

    String LOAD_CONFIG = "/REACT/EXECUTION/LOADCONFIG";

    String VALIDATE_PUBLISHER_STREAMING_SESSION = "/publisher/validateStreamingSession";

    String VALIDATE_SUBSCRIBER_STREAMING_SESSION = "/subscriber/validateStreamingSession";

    String WOWZA_ERROR_STREAMING_SESSION = "/publisher/errorStreamingSession";

    String WOWZA_ERROR_SUBSCRIBER_STREAMING_SESSION = "/subscriber/errorStreamingSession";

    String ERROR_STREAMING_SESSION = "/error/errorStreamingSession";

    String BEGIN_STREAMING_SESSION = "/streaming/begin";

    String WOWZA_CONNECT = "/wowza/connect";

    String DB_CONFIG = "/db/config";

    String DB_INIT = "/db/init";

    String TERMINATE_STREAMING_SESSSION = "/publisher/disconnect";

    String TERMINATE_SUBSCRIBER_STREAMING_SESSSION = "/subscriber/disconnect";

    String SERVER_VALIDATE_OK = "/wowza/validate";

    String REQUEST_ACTION = "/request/action";

    String RETRIEVE_PUBLISHER = "/publisher/retrieve";

    String SUBSCRIBER_ACTION = "/subscriber/action";

    String ACTION_VALIDATE = "/action/validate";

    String PUBLISHER_ACTION_ACCEPT = "/action/accept";

    String PUBLISHER_ACTION_ACCEPT_OK = "/publisher/actionAccepted";

    String PUBLISHER_ACTION_ACCEPT_REFUSED = "/publisher/actionRefused";


}
