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
package org.zodiark.server;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Nettosphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.service.ServiceHandler;
import org.zodiark.server.service.ServiceLocatorFactory;
import org.zodiark.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class ZodiarkServer {

    private final static Logger logger = LoggerFactory.getLogger(ZodiarkServer.class);
    private final Config.Builder builder = new Config.Builder();
    private Nettosphere server;

    public ZodiarkServer() {
        builder.resource(ZodiarkDispatcher.class)
                .initParam(ApplicationConfig.CUSTOM_ANNOTATION_PACKAGE, Service.class.getPackage().getName());
    }

    public ZodiarkServer listen(URI uri) {
        builder.port(uri.getPort()).host(uri.getHost()).build();
        server = new Nettosphere.Builder().config(builder.build()).build();

        return this;
    }

    public ZodiarkServer on() {
        if (server == null) {
            listen(URI.create("http://127.0.0.1:8080"));
        }
        server.start();
        return this;
    }

    public ZodiarkServer service(Class<? extends ServiceHandler> annotatedClass) {
        Service s = annotatedClass.getAnnotation(Service.class);
        if (s == null) throw new IllegalStateException(annotatedClass.getName() + " must be annotated with @Service");

        try {
            ServiceLocatorFactory.getDefault().locator().register(s.path(), ServiceHandler.class.cast(annotatedClass.newInstance()));
        } catch (Exception e) {
            logger.error("Unable to create Service {}", annotatedClass, e);
        }
        return this;
    }

    public ZodiarkServer off() {
        if (server != null) server.stop();
        return this;
    }

    public static void main(String[] args) throws IOException {
        ZodiarkServer z = new ZodiarkServer();
        z.on();
        String a = "";
        logger.info("Server started on port {}", 8080);
        logger.info("Type quit to stop the server");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!(a.equals("quit"))) {
            a = br.readLine();
        }
        System.exit(-1);
    }


}
