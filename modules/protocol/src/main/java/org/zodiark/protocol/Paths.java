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

    String CREATE_USER_SESSION = "/publisher/createUserSession";

    String LOAD_CONFIG = "/REACT/EXECUTION/LOADCONFIG";

    String VALIDATE_STREAMING_SESSION = "/publisher/validateStreamingSession";

    String WOWZA_STREAMING_SESSION_ERROR = "/REACT/ERROR";

    String START_STREAMINGSESSION = "/streaming/start";

    String WOWZA_CONNECT = "/wowza/connect";

    String DB_CONFIG = "/db/config";

    String DB_INIT = "/db/init";

    String TERMINATE_STREAMING_SESSSION = "/publisher/disconnect";

    String SERVER_VALIDATE_PUBLISHER_OK = "/wowza/validate";

    String REQUEST_ACTION = "/request/action";
}
