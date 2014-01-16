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
package org.zodiark.service.util.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.Context;
import org.zodiark.server.annotation.Inject;
import org.zodiark.service.db.Result;
import org.zodiark.service.util.RestService;

/**
 * Mock class for testing purpose only. This class does nothing.
 */
public class OKRestService implements RestService {

    private final Logger logger = LoggerFactory.getLogger(OKRestService.class);

    @Inject
    public Context context;

    @Inject
    public ObjectMapper mapper;

    protected final LocalDatabase db = new LocalDatabase();

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

            String restResponse = db.serve(uri, o.toString(), LocalDatabase.RESULT.PASS);
                        // Normally, we would use AHC to call the REST client

            if (restResponse != null) {
                Result r = mapper.readerForUpdating(context.newInstance(Result.class)).readValue(restResponse);
                return r.transform(result);
            }

            return null;
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T delete(String uri, Object o, Class<T> result) {
        return post(uri, "", result);
    }
}
