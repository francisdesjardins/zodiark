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
import org.zodiark.service.db.result.Result;
import org.zodiark.service.rest.AHCBlockingRestClient;
import org.zodiark.service.rest.InMemoryRestClient;
import org.zodiark.service.rest.RestClient;
import org.zodiark.service.rest.RestService;
import org.zodiark.service.rest.RestServiceImpl;
import org.zodiark.service.session.StreamingRequest;
import org.zodiark.service.state.AuthConfig;
import org.zodiark.service.state.EndpointState;
import org.zodiark.service.util.StreamingRequestImpl;
import org.zodiark.service.util.mock.OKAuthConfig;
import org.zodiark.service.wowza.WowzaEndpointManager;
import org.zodiark.service.wowza.WowzaEndpointManagerImpl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An {@link AtmosphereObjectFactory} that handles the injection of Zodiark's object. Field injection are discovered via the
 * {@link javax.inject.Inject} annotation. Injection uses {@link Injectable} and {@link org.zodiark.server.ZodiarkObjectFactory.Implementable} implementation to discover what to inject and which
 * default implementation class to use. Those default can be overridden by calling the {@link #implementatble(Class, org.zodiark.server.ZodiarkObjectFactory.Implementable)}
 * and {@link #injectable(Class, org.zodiark.server.ZodiarkObjectFactory.Injectable)}
 * <p/>
 * Injectable instances are
 * {@link EventBus}, {@link ObjectMapper}, {@link WowzaEndpointManager}, {@link StreamingRequest}
 * <p/>
 * Extendable classes are
 * {@link AuthConfig}, {@link org.zodiark.service.rest.RestService}
 * <p/>
 * Injectable and Extendable can be replaced.
 * <p/>
 * This class contains all default instanced used by the {@link org.zodiark.service.Service} and object used by this framework.
 * Override this class to replace the default object injection.
 */
public class ZodiarkObjectFactory implements AtmosphereObjectFactory {
    private final Logger logger = LoggerFactory.getLogger(ZodiarkObjectFactory.class);
    public final static String DB_URL = "zodiark.db.url";

    private final ConcurrentHashMap<Class<?>, Injectable> injectRepository = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Implementable> implementationRepository = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends Object>, Object> instanceRepository = new ConcurrentHashMap<>();

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

        injectable(URI.class, new Injectable<URI>() {
            @Override
            public URI construct(Class<URI> t) {
                return URI.create(System.getProperty(DB_URL, "0.0.0.0"));
            }
        });

        // Install the default extensable
        implementatble(AuthConfig.class, new Implementable<AuthConfig>() {
            @Override
            public Class<OKAuthConfig> extend(Class<AuthConfig> t) {
                return OKAuthConfig.class;
            }
        });

        if (System.getProperty(DB_URL, "").isEmpty()) {
            implementatble(RestClient.class, new Implementable<RestClient>() {
                @Override
                public Class<InMemoryRestClient> extend(Class<RestClient> t) {
                    return InMemoryRestClient.class;
                }
            });
        } else {
            implementatble(RestClient.class, new Implementable<RestClient>() {
                @Override
                public Class<AHCBlockingRestClient> extend(Class<RestClient> t) {
                    return AHCBlockingRestClient.class;
                }
            });
        }

        implementatble(RestService.class, new Implementable<RestService>() {
            @Override
            public Class<RestServiceImpl> extend(Class<RestService> t) {
                return RestServiceImpl.class;
            }
        });

    }

    @Override
    public <T, U extends T> T newClassInstance(final AtmosphereFramework framework, Class<T> classType, Class<U> tClass) throws InstantiationException, IllegalAccessException {
        logger.debug("About to create {}", tClass.getName());
        if (!added.getAndSet(true) && framework != null) {
            framework.getAtmosphereConfig().shutdownHook(new AtmosphereConfig.ShutdownHook() {
                @Override
                public void shutdown() {
                    timer.shutdown();
                }
            });
            framework.getAtmosphereConfig().startupHook(new AtmosphereConfig.StartupHook() {

                @Override
                public void started(AtmosphereFramework framework) {
                    injectRepository.clear();
                    implementationRepository.clear();
                    instanceRepository.clear();
                }
            });

        }

        Class<? extends T> impl = implement(classType);

        boolean needsPostConstruct = (impl == null || instanceRepository.get(impl) == null);
        T instance = impl != null ? newInstance(impl) : tClass.newInstance();

        Field[] fields = tClass.equals(instance.getClass()) ? tClass.getDeclaredFields() : impl.getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                if (field.getType().isAssignableFrom(ObjectMapper.class)) {
                    field.set(instance, inject(ObjectMapper.class));
                } else if (field.getType().isAssignableFrom(EventBus.class)) {
                    field.set(instance, inject(EventBus.class));
                } else if (field.getType().isAssignableFrom(RestService.class)) {
                    field.set(instance, newClassInstance(framework, RestService.class, RestService.class));
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
                    field.set(instance, newClassInstance(framework, Result.class, AuthConfig.class));
                } else if (field.getType().isAssignableFrom(EndpointState.class)) {
                    field.set(instance, newClassInstance(framework, EndpointState.class, EndpointState.class));
                } else if (field.getType().isAssignableFrom(StreamingRequest.class)) {
                    field.set(instance, inject(StreamingRequest.class));
                } else if (field.getType().isAssignableFrom(ScheduledExecutorService.class)) {
                    field.set(instance, timer);
                } else if (field.getType().isAssignableFrom(RestClient.class)) {
                    field.set(instance, newClassInstance(framework, RestClient.class, RestClient.class));
                } else if (field.getType().isAssignableFrom(URI.class)) {
                    field.set(instance, inject(URI.class));
                }
            }
        }

        if (needsPostConstruct) {
            Method[] methods = tClass.equals(instance.getClass()) ? tClass.getMethods() : instance.getClass().getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        m.invoke(instance);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }

    private <T> T newInstance(Class<? extends T> impl) throws IllegalAccessException, InstantiationException {
        T instance = (T) instanceRepository.get(impl);
        if (instance == null) {
            instance = inject(impl);
            if (instance == null) {
                instance = impl.newInstance();
            }
        }
        instanceRepository.put(impl, instance);
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
        Implementable<T> t = implementationRepository.get(clazz);
        if (t != null) {
            return t.extend(clazz);
        }
        return null;
    }

    /**
     * Register an {@link Injectable} that will be responsible for creation class T
     *
     * @param clazz      a class
     * @param injectable its associated {@link Injectable}
     * @return this
     */
    public <T> ZodiarkObjectFactory injectable(Class<T> clazz, Injectable<T> injectable) {
        injectRepository.put(clazz, injectable);
        return this;
    }

    /**
     * Register an {@link org.zodiark.server.ZodiarkObjectFactory.Implementable} that will return the associated implementation of class T
     *
     * @param clazz         a class
     * @param implementable its associated {@link Injectable}
     * @return this
     */
    public <T> ZodiarkObjectFactory implementatble(Class<T> clazz, Implementable<T> implementable) {
        implementationRepository.put(clazz, implementable);
        return this;
    }

    /**
     * Handle creation of object of type T
     *
     * @param <T> A class of type T
     */
    public static interface Injectable<T> {
        /**
         * Create an instance of T
         *
         * @param t a class to be created
         * @return an instance of T
         */
        T construct(Class<T> t);
    }

    /**
     * Handle implementation of class of type T
     *
     * @param <T>
     */
    public static interface Implementable<T> {
        /**
         * Return the class' extending of T
         *
         * @param t A class extending T
         * @return a class extending T
         */
        Class<? extends T> extend(Class<T> t);
    }
}
