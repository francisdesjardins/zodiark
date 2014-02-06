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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.server.annotation.Inject;
import org.zodiark.service.config.AuthConfig;
import org.zodiark.service.config.PublisherState;
import org.zodiark.service.config.SubscriberConfig;
import org.zodiark.service.publisher.PublisherStateImpl;
import org.zodiark.service.session.StreamingRequest;
import org.zodiark.service.subscriber.SubscriberConfigImpl;
import org.zodiark.service.util.RestService;
import org.zodiark.service.util.StreamingRequestImpl;
import org.zodiark.service.util.mock.OKAuthConfig;
import org.zodiark.service.util.mock.OKRestService;
import org.zodiark.service.wowza.WowzaEndpointManager;
import org.zodiark.service.wowza.WowzaEndpointManagerImpl;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An {@link AtmosphereObjectFactory} that handles the injection of Zodiark's object. Field injection are discovered via the
 * {@link Inject} annotation. Injection uses {@link Injectable} and {@link Extendable} implementation to discover what to inject and which
 * default implementation class to use. Those default can be overridden by calling the {@link #extendable(Class, org.zodiark.server.ZodiarkObjectFactory.Extendable)}
 * and {@link #injectable(Class, org.zodiark.server.ZodiarkObjectFactory.Injectable)}
 * <p/>
 * Injectable instances are
 *  {@link EventBus}, {@link ObjectMapper}, {@link WowzaEndpointManager}, {@link StreamingRequest}
 * <p/>
 * Extendable classes are
 *  {@link AuthConfig}, {@link org.zodiark.service.config.PublisherState}, {@link SubscriberConfig}, {@link org.zodiark.service.util.RestService}
 * <p/>
 * Injectable and Extendable can be replaced.
 * <p/>
 * This class contains all default instanced used by the {@link org.zodiark.service.Service} and object used by this framework.
 * Override this class to replace the default object injection.
 */
public class ZodiarkObjectFactory implements AtmosphereObjectFactory {
    private final Logger logger = LoggerFactory.getLogger(ZodiarkObjectFactory.class);

    private final ConcurrentHashMap<Class<?>, Injectable> injectRepository = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Extendable> implementationRepository = new ConcurrentHashMap<>();
    private final AtomicBoolean added = new AtomicBoolean();
    private ScheduledExecutorService timer = Executors.newScheduledThreadPool(200);

    public ZodiarkObjectFactory() {
        // Install the default injectable
        injectable(EventBus.class, new Injectable<EventBus>() {

            @Override
            public EventBus construct(Class<EventBus> t) {
                return EventBusFactory.getDefault().eventBus();
            }
        });
        injectable(ObjectMapper.class, new Injectable<ObjectMapper>() {

            private final ObjectMapper mapper = new ObjectMapper();

            @Override
            public ObjectMapper construct(Class<ObjectMapper> t) {
                return mapper;
            }
        });
        injectable(WowzaEndpointManager.class, new Injectable<WowzaEndpointManager>() {

            private final WowzaEndpointManager wowzaService = new WowzaEndpointManagerImpl();

            @Override
            public WowzaEndpointManager construct(Class<WowzaEndpointManager> t) {
                return wowzaService;
            }
        });
        injectable(StreamingRequest.class, new Injectable<StreamingRequest>() {

            private final StreamingRequest streamingRequest = new StreamingRequestImpl();

            @Override
            public StreamingRequest construct(Class<StreamingRequest> t) {
                return streamingRequest;
            }
        });

        // Install the default extensable
        extendable(AuthConfig.class, new Extendable<AuthConfig>() {
            @Override
            public Class<OKAuthConfig> extend(Class<AuthConfig> t) {
                return OKAuthConfig.class;
            }
        });
        extendable(PublisherState.class, new Extendable<PublisherState>() {
            @Override
            public Class<PublisherStateImpl> extend(Class<PublisherState> t) {
                return PublisherStateImpl.class;
            }
        });
        extendable(SubscriberConfig.class, new Extendable<SubscriberConfig>() {
            @Override
            public Class<SubscriberConfigImpl> extend(Class<SubscriberConfig> t) {
                return SubscriberConfigImpl.class;
            }
        });

        extendable(RestService.class, new Extendable<RestService>() {
            @Override
            public Class<OKRestService> extend(Class<RestService> t) {
                return OKRestService.class;
            }
        });
    }

    @Override
    public <T, U extends T> T newClassInstance(final AtmosphereFramework framework, Class<T> classType, Class<U> tClass) throws InstantiationException, IllegalAccessException {
        logger.debug("About to create {}", tClass.getName());
        if (!added.getAndSet(true) && framework !=null) {
            framework.getAtmosphereConfig().shutdownHook(new AtmosphereConfig.ShutdownHook() {
                @Override
                public void shutdown() {
                    timer.shutdown();
                }
            });
        }

        Class<? extends T> impl = implement(classType);

        T instance = impl != null ? impl.newInstance() : tClass.newInstance();

        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                if (field.getType().isAssignableFrom(ObjectMapper.class)) {
                    field.set(instance, inject(ObjectMapper.class));
                } else if (field.getType().isAssignableFrom(EventBus.class)) {
                    field.set(instance, inject(EventBus.class));
                } else if (field.getType().isAssignableFrom(RestService.class)) {
                    field.set(instance, newClassInstance(framework, RestService.class, implement(RestService.class)));
                } else if (field.getType().isAssignableFrom(WowzaEndpointManager.class)) {
                    field.set(instance, inject(WowzaEndpointManager.class));
                } else if (field.getType().isAssignableFrom(Context.class)) {
                    field.set(instance, new Context() {

                        @Override
                        public <T> T newInstance(Class<T> t) {
                            try {
                                return newClassInstance(framework, t, t);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else if (field.getType().isAssignableFrom(AuthConfig.class)) {
                    field.set(instance, newClassInstance(framework, AuthConfig.class, implement(AuthConfig.class)));
                } else if (field.getType().isAssignableFrom(PublisherState.class)) {
                    field.set(instance, newClassInstance(framework, PublisherState.class, implement(PublisherState.class)));
                } else if (field.getType().isAssignableFrom(SubscriberConfig.class)) {
                    field.set(instance, newClassInstance(framework, SubscriberConfig.class, implement(SubscriberConfig.class)));
                } else if (field.getType().isAssignableFrom(StreamingRequest.class)) {
                    field.set(instance, inject(StreamingRequest.class));
                } else if (field.getType().isAssignableFrom(ScheduledExecutorService.class)) {
                    field.set(instance, timer);
                }
            }
        }
        return instance;
    }

    private <T> T inject(Class<T> clazz) {
        Injectable<T> t = injectRepository.get(clazz);
        if (t != null) {
            return t.construct(clazz);
        }
        return null;
    }

    private <T> Class<? extends T> implement(Class<T> clazz) {
        Extendable<T> t = implementationRepository.get(clazz);
        if (t != null) {
            return t.extend(clazz);
        }
        return null;
    }

    /**
     * Register an {@link Injectable} that will be responsible for creation class T
     * @param clazz a class
     * @param injectable its associated {@link Injectable}
     * @return this
     */
    public <T> ZodiarkObjectFactory injectable(Class<T> clazz, Injectable<T> injectable) {
        injectRepository.put(clazz, injectable);
        return this;
    }
    /**
     * Register an {@link Extendable} that will return the associated implementation of class T
     * @param clazz a class
     * @param extendable its associated {@link Injectable}
     * @return this
     */
    public <T> ZodiarkObjectFactory extendable(Class<T> clazz, Extendable<T> extendable) {
        implementationRepository.put(clazz, extendable);
        return this;
    }

    /**
     * Handle creation of object of type T
     * @param <T> A class of type T
     */
    public static interface Injectable<T>  {
        /**
         * Create an instance of T
         * @param t a class to be created
         * @return an instance of T
         */
        T construct(Class<T> t);
    }

    /**
     * Handle implementation of class of type T
     * @param <T>
     */
    public static interface Extendable<T>  {
        /**
         * Return the class' extending of T
         * @param t A class extending T
         * @return a class extending T
         */
        Class<? extends T> extend(Class<T> t);
    }
}
