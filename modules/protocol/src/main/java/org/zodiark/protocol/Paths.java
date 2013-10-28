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

    String CREATE_USER_SESSION = "/REACT/EXECUTION/CREATE_USER_SESSION";

    String LOAD_CONFIG = "/REACT/EXECUTION/LOADCONFIG";

    String CREATE_STREAMING_SESSION = "/REACT/EXECUTION/CREATESHOW";

    String WOWZA_STREAMING_SESSION_OK = "/REACT/OK";

    String WOWZA_STREAMING_SESSION_ERROR = "/REACT/ERROR";

    String START_STREAMINGSESSION = "/streamingsession/start";

    String WOWZA_CONNECT = "/wowza/connect";

    String DB_CONFIG = "/db/config";

    String DB_INIT = "/db/init";

    String TERMINATE_STREAMING_SESSSION = "/publishere/disconnect";
}
