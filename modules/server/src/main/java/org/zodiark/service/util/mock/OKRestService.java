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
package org.zodiark.service.util.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.Context;
import org.zodiark.service.util.RestService;

import javax.inject.Inject;
import java.lang.reflect.Method;

import static org.zodiark.service.util.mock.OKRestService.METHOD.DELETE;
import static org.zodiark.service.util.mock.OKRestService.METHOD.GET;
import static org.zodiark.service.util.mock.OKRestService.METHOD.POST;
import static org.zodiark.service.util.mock.OKRestService.METHOD.PUT;

/**
 * Mock class for testing purpose only. This class does nothing.
 */
public class OKRestService implements RestService {

    private final Logger logger = LoggerFactory.getLogger(OKRestService.class);

    public enum METHOD {GET, PUT, POST, DELETE}

    @Inject
    public Context context;

    @Inject
    public ObjectMapper mapper;

    protected final InMemoryDB db = new InMemoryDB();

    @Override
    public void get(String uri, Reply r) {
        send(GET, uri, null, r);
    }

    @Override
    public void put(String uri, Object o, Reply r) {
        send(PUT, uri, o, r);
    }

    @Override
    public void post(String uri, Object o, Reply r) {
        send(POST, uri, o, r);
    }

    protected void send(METHOD method, String uri, Object o, Reply r) {
        try {
            Class<?> success = null;
            Class<?> failure = null;
            for (Method m : r.getClass().getMethods()) {
                switch (m.getName()) {
                    case "success":
                        if (!((Class) m.getGenericParameterTypes()[0]).getName().equals(Object.class.getName())) {
                            success = (Class) m.getGenericParameterTypes()[0];
                        }
                        break;
                    case "failure":
                        if (!((Class) m.getGenericParameterTypes()[0]).getName().equals(Object.class.getName())) {
                            failure = (Class) m.getGenericParameterTypes()[0];
                        }
                        break;
                    case "exception":
                        break;
                }
            }

            String restResponse = db.serve(method, uri, mapper.writeValueAsString(o), InMemoryDB.RESULT.PASS);

            try {
                Object object = context.newInstance(success);
                r.success(String.class.isAssignableFrom(success) ? restResponse : mapper.readerForUpdating(object).readValue(restResponse));
            } catch (Exception ex) {
                logger.error("",ex);
                Object object = context.newInstance(failure);
                r.failure(String.class.isAssignableFrom(failure) ? restResponse : mapper.readerForUpdating(object).readValue(restResponse));
            }
        } catch (Exception e) {
            logger.error("", e);
            r.exception(e);
        }
    }

    @Override
    public void delete(String uri, Object o, Reply r) {
        send(DELETE, uri, o, r);
    }
}
