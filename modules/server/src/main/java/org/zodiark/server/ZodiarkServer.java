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
package org.zodiark.server;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Nettosphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.annotation.On;
import org.zodiark.server.impl.OnAnnotationProcessor;
import org.zodiark.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Boostrap classes that configure Atmosphere and NettoSphere.
 */
public class ZodiarkServer {

    private final static Logger logger = LoggerFactory.getLogger(ZodiarkServer.class);
    private final Config.Builder builder = new Config.Builder();
    private Nettosphere server;
    private final List<String> packages = new ArrayList<>();
    private URI uri;
    private String factory = ZodiarkObjectFactory.class.getName();

    public ZodiarkServer() {
        builder.resource(EnvelopeDigester.class)
                .initParam(ApplicationConfig.CUSTOM_ANNOTATION_PACKAGE, OnAnnotationProcessor.class.getPackage().getName())
                .initParam(ApplicationConfig.OBJECT_FACTORY, factory);
    }

    /**
     * The uri to listen to.
     * @param uri the uri to listen to.
     * @return this
     */
    public ZodiarkServer listen(URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Add a path used by Atmosphere to scan for static resource or dynamic resource.
     * @param path used by Atmosphere to scan for static resource or dynamic resource.
     * @return this
     */
    public ZodiarkServer serve(String path) {
        builder.resource(path);
        return this;
    }

    /**
     * Start the {@link ZodiarkServer}
     * @return this
     */
    public ZodiarkServer on() {
        if (packages.isEmpty()) {
            packages.add(Service.class.getPackage().getName());
        }
        builder.initParam(ApplicationConfig.ANNOTATION_PACKAGE, toList());

        if (uri == null) {
            uri = URI.create("http://0.0.0.0:8080");
        }

        builder.port(uri.getPort()).host(uri.getHost()).build();
        server = new Nettosphere.Builder().config(builder.build()).build();
        server.start();
        return this;
    }

    /**
     * Add a {@link Service} which will become candidate for the {@link EventBus}
     * @param annotatedClass a class annotated with {@link On} and implementing the {@link Service}
     * @return this
     */
    public ZodiarkServer service(Class<? extends Service> annotatedClass) {
        if (server == null || !server.isStarted()) {
            packages.add(annotatedClass.getName());
        } else {
            On s = annotatedClass.getAnnotation(On.class);
            String[] values = s.value();
            for (String v : values) {
                try {
                    EventBusFactory.getDefault().eventBus().onIOEvent(v, server.framework().newClassInstance(Service.class, annotatedClass));
                } catch (Exception e) {
                    logger.error("Unable to create Service {}", annotatedClass, e);
                }
            }
        }
        return this;
    }

    /**
     * Replace the default {@link ZodiarkObjectFactory}
     * @param factory an implementation of {@link org.atmosphere.cpr.AtmosphereObjectFactory}
     * @return this
     */
    public ZodiarkServer factory(String factory) {
        this.factory = factory;
        return this;
    }

    /**
     * The current {@link org.atmosphere.cpr.AtmosphereObjectFactory}
     * @return the class's name of {@link org.atmosphere.cpr.AtmosphereObjectFactory}
     */
    public String factory() {
        return factory;
    }

    private String toList() {
        StringBuilder b = new StringBuilder();
        for (String p : packages) {
            b.append(p).append(",");
        }
        return b.toString();
    }

    /**
     * Stop the {@link ZodiarkServer}
     * @return this
     */
    public ZodiarkServer off() {
        if (server != null) server.stop();
        return this;
    }

    /**
     * A default implementation for starting the {@link ZodiarkServer}
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String staticPath = "./modules/server/src/main/resources";
        if (args != null && args.length > 0) {
            staticPath = args[0];
        }

        ZodiarkServer z = new ZodiarkServer().serve(staticPath).on();
        String a = "";
        logger.info("Server started on port {}", 8080);
        logger.info("Type quit to stop the server");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!(a.equals("quit"))) {
            a = br.readLine();
        }
        z.off();
        System.exit(-1);
    }


}
