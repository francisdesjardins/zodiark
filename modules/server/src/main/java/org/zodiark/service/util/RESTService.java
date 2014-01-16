/*
 * Copyright 2013 High-Level Technologies
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
package org.zodiark.service.util;

/**
 * The REST API to use for interacting with a remote database/web service. Implementation must follow REST principle.
 * Implementation of this class must have an empty, default constructor so they can be injected by the {@link org.zodiark.server.ZodiarkObjectFactory}
 */
public interface RestService {

    <T> T get(String uri, Class<T> c);

    <T> T put(String uri, Object o, Class<T> result);

    <T> T post(String uri, Object o, Class<T> result);

    <T> T delete(String uri, Object o, Class<T> result);

}

