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
import org.zodiark.service.util.RestService;

import java.lang.reflect.Method;

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
    public void get(String uri, Reply r) {
        post(uri, null, r);
    }

    @Override
    public void put(String uri, Object o, Reply r) {
        post(uri, o, r);
    }

    @Override
    public void post(String uri, Object o, Reply r) {
        try {
            Class<?> success = null;
            Class<?> failure = null;
            for (Method m : r.getClass().getMethods()) {
                switch(m.getName()) {
                    case "success" :
                        if (!((Class)m.getGenericParameterTypes()[0]).getName().equals(Object.class.getName())) {
                            success = (Class) m.getGenericParameterTypes()[0];
                        }
                        break;
                    case "failure" :
                        if (!((Class)m.getGenericParameterTypes()[0]).getName().equals(Object.class.getName())) {
                            failure = (Class) m.getGenericParameterTypes()[0];
                        }
                        break;
                    case "exception" :
                        break;
                }
            }

            String restResponse = db.serve(uri, mapper.writeValueAsString(o), LocalDatabase.RESULT.PASS);

            try {
                r.success(mapper.readerForUpdating(context.newInstance(success)).readValue(restResponse));
            } catch (Exception ex) {
                ex.printStackTrace();
                r.failure(mapper.readerForUpdating(context.newInstance(failure)).readValue(restResponse));
            }
        } catch (Exception e) {
            r.exception(e);
        }
    }

    @Override
    public void delete(String uri, Object o, Reply r) {
        post(uri, o, r);
    }
}
