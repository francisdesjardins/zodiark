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

    String CREATE_SESSION = "/REACT/EXECUTION/CREATE_SESSION";

    String LOAD_CONFIG = "/REACT/EXECUTION/LOADCONFIG";

    String CREATE_SHOW = "/REACT/EXECUTION/CREATESHOW";

    String WOWZA_PUBLISHER_RESPONSE_OK = "/REACT/OK";

    String WOWZA_PUBLISHER_RESPONSE_ERROR = "/REACT/ERROR";

}
