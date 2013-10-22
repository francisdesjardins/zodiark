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
package org.zodiark.server.impl;

import org.atmosphere.annotation.Processor;
import org.atmosphere.config.AtmosphereAnnotation;
import org.atmosphere.cpr.AtmosphereFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.EventBus;
import org.zodiark.server.EventBusFactory;
import org.zodiark.server.Service;
import org.zodiark.server.annotation.On;

@AtmosphereAnnotation(On.class)
public class EventBusAnnotationProcessor implements Processor {

    private final EventBus eventBus;
    private Logger logger = LoggerFactory.getLogger(EventBusAnnotationProcessor.class);

    public EventBusAnnotationProcessor() {
        eventBus = EventBusFactory.getDefault().eventBus();
    }


    @Override
    public void handle(AtmosphereFramework framework, Class<?> annotatedClass) {

        On s = annotatedClass.getAnnotation(On.class);
        try {
            logger.info("Registering @EventBusListener {}", annotatedClass.getName());
            eventBus.on(s.value(), (Service) framework.newClassInstance(annotatedClass));
        } catch (Exception e) {
            logger.error("Unable to register {}", annotatedClass, e);
        }
    }
}
