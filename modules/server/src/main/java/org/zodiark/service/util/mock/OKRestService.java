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
package org.zodiark.service.util.mock;

import org.zodiark.server.Context;
import org.zodiark.server.annotation.Inject;
import org.zodiark.service.util.RESTService;

public class OKRestService implements RESTService {

    @Inject
    public Context context;

    @Override
    public <T> T get(String uri, Class<T> c) {
        return post(uri, "", c);
    }

    @Override
    public <T> T put(String uri, Object o, Class<T> result) {
        return post(uri, "", result);
    }

    @Override
    public <T> T post(String uri, Object o, Class<T> result) {
        try {
            return context.newInstance(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T delete(String uri, Object o, Class<T> result) {
        return post(uri, "", result);
    }
}
