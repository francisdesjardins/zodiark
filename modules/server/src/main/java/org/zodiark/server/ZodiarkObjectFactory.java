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
package org.zodiark.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.zodiark.server.annotation.Inject;
import org.zodiark.service.AuthConfig;
import org.zodiark.service.PublisherConfig;
import org.zodiark.service.publisher.PublisherConfigImpl;
import org.zodiark.service.util.RESTService;
import org.zodiark.service.util.mock.OKAuthConfig;
import org.zodiark.service.util.mock.OKRestService;
import org.zodiark.service.wowza.WowzaEndpointManager;
import org.zodiark.service.wowza.WowzaEndpointManagerImpl;

import java.lang.reflect.Field;

public class ZodiarkObjectFactory implements AtmosphereObjectFactory {

    private final EventBus evenBus = EventBusFactory.getDefault().eventBus();
    private final ObjectMapper mapper = new ObjectMapper();
    private final WowzaEndpointManager wowzaService = new WowzaEndpointManagerImpl();
    private final Class<? extends AuthConfig> authConfig = OKAuthConfig.class;
    private final Class<? extends PublisherConfig> publisherConfig = PublisherConfigImpl.class;
    private final Class<? extends RESTService> restService = OKRestService.class;

    @Override
    public <T> T newClassInstance(final AtmosphereFramework framework, Class<T> tClass) throws InstantiationException, IllegalAccessException {

        if (tClass.isAssignableFrom(AuthConfig.class)) {
            return (T) newClassInstance(framework, authConfig);
        } else if (tClass.isAssignableFrom(PublisherConfig.class)) {
            return (T) newClassInstance(framework, publisherConfig);
        }

        T instance = tClass.newInstance();

        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                if (field.getType().isAssignableFrom(ObjectMapper.class)) {
                    field.set(instance, mapper);
                } else if (field.getType().isAssignableFrom(EventBus.class)) {
                    field.set(instance, evenBus);
                } else if (field.getType().isAssignableFrom(RESTService.class)) {
                    field.set(instance, newClassInstance(framework, restService));
                } else if (field.getType().isAssignableFrom(WowzaEndpointManager.class)) {
                    field.set(instance, wowzaService);
                } else if (field.getType().isAssignableFrom(Context.class)) {
                    field.set(instance, new Context() {

                        @Override
                        public <T> T newInstance(Class<T> t) {
                            try {
                                return newClassInstance(framework, t);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else if (field.getType().isAssignableFrom(AuthConfig.class)) {
                    field.set(instance, newClassInstance(framework, authConfig));
                } else if (field.getType().isAssignableFrom(PublisherConfig.class)) {
                    field.set(instance, newClassInstance(framework, publisherConfig));
                }
            }
        }
        return instance;
    }
}
