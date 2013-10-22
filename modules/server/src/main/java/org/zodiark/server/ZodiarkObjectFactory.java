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

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.zodiark.server.annotation.Inject;

import java.lang.reflect.Field;

public class ZodiarkObjectFactory implements AtmosphereObjectFactory{

    private final EventBus evenBus = EventBusFactory.getDefault().eventBus();

    @Override
    public <T> T newClassInstance(AtmosphereFramework atmosphereFramework, Class<T> tClass) throws InstantiationException, IllegalAccessException {

        T instance = tClass.newInstance();

        Field[] fields = tClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.set(instance, evenBus);
            }
        }
        return instance;
    }
}
