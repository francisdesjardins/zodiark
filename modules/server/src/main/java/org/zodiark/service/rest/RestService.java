/*
 * Copyright 2013-2014 High-Level Technologies
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

import org.zodiark.server.Reply;

/**
 * The REST API to use for interacting with a remote database/web service. Implementation must follow REST principle.
 * Implementation of this class must have an empty, default constructor so they can be injected by the {@link org.zodiark.server.ZodiarkObjectFactory}
 */
public interface RestService {

    public enum METHOD {GET, PUT, POST, DELETE}

    void get(String uri, Reply r);

    void put(String uri, Object o, Reply r);

    void post(String uri, Object o, Reply r);

    void delete(String uri, Object o, Reply r);

}