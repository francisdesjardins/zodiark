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
package org.zodiark.server.service;

import org.atmosphere.annotation.Processor;
import org.atmosphere.config.AtmosphereAnnotation;
import org.atmosphere.cpr.AtmosphereFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.service.Service;

@AtmosphereAnnotation(Service.class)
public class ServiceProcessor implements Processor {

    private final ServiceLocator serviceLocator;
    private Logger logger = LoggerFactory.getLogger(ServiceProcessor.class);

    public ServiceProcessor(){
        serviceLocator = ServiceLocatorFactory.getDefault().locator();
    }


    @Override
    public void handle(AtmosphereFramework framework, Class<?> annotatedClass) {

        Service s = annotatedClass.getAnnotation(Service.class);
        try {
            serviceLocator.register(s.path(), ServiceHandler.class.cast(annotatedClass.newInstance()));
        } catch (Exception e) {
            logger.error("Unable to register {}", annotatedClass, e);
        }
    }
}
