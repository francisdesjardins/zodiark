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
package org.zodiark.service;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    /**
     * The path that maps the {@link org.zodiark.server.service.ServiceHandler} to the request
     * @return the path, or '/' if not defined
     */
    String path() default "/";

    /**
     * If a class annotated with this annotation set the nativeService to true, the service will not be registered with
     * the {@link org.zodiark.server.service.ServiceLocator}. This is useful when a Service is fully handled by
     * Atmosphere like a chat or any service that doesn't requires special handling.
     * @return false by default
     */
    boolean nativeService() default false;

}
