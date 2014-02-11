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
package org.zodiark.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.protocol.Paths;
import org.zodiark.server.Context;
import org.zodiark.service.EndpointUtils;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Error;
import org.zodiark.service.Session;
import org.zodiark.service.db.Passthrough;
import org.zodiark.service.db.Status;
import org.zodiark.service.session.StreamingSession;
import org.zodiark.service.subscriber.SubscriberEndpoint;
import org.zodiark.service.wowza.WowzaUUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter.OnDisconnect;
import static org.zodiark.protocol.Paths.BROADCASTER_CREATE;
import static org.zodiark.protocol.Paths.DB_GET_WORD_PASSTHROUGH;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_ONDEMAND_END;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_ONDEMAND_START;
import static org.zodiark.protocol.Paths.DB_POST_PUBLISHER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_ACTIONS;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SETTINGS_SHOW;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SETTINGS_SHOW_GET_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_ERROR_REPORT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_LOAD_CONFIG;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_LOAD_CONFIG_GET;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_PUBLIC_MODE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_PUBLIC_MODE_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SAVE_CONFIG;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SAVE_CONFIG_PUT;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SETTINGS_SHOW_SAVE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHARED_PRIVATE_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHARED_PRIVATE_START;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHARED_PRIVATE_START_POST;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_END;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SHOW_START;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE_GET;
import static org.zodiark.protocol.Paths.DB_PUBLISHER_SUBSCRIBER_PROFILE_PUT;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_BLOCK;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_EJECT;
import static org.zodiark.protocol.Paths.ERROR_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.FAILED_PUBLISHER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.PUBLISHER_ABOUT_READY;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;
import static org.zodiark.protocol.Paths.RETRIEVE_SUBSCRIBER;
import static org.zodiark.protocol.Paths.VALIDATE_PUBLISHER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.WOWZA_CONNECT;

/**
 * The Publisher's application logic for validating, creating and starting a {@link org.zodiark.service.session.StreamingSession}
 */
@On(Paths.SERVICE_PUBLISHER)
public class PublisherServiceImpl implements PublisherService, Session<PublisherEndpoint> {

    private final ConcurrentHashMap<String, PublisherEndpoint> endpoints = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    public Context context;

    private EndpointUtils<PublisherEndpoint> utils;

    @PostConstruct
    public void init() {
        utils = new EndpointUtils(eventBus, mapper, endpoints);
    }

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
        logger.trace("Handling Publisher Envelop {} to Service {}", e, r.uuid());
        String path = e.getMessage().getPath();
        switch (path) {
            case DB_POST_PUBLISHER_SESSION_CREATE:
                createSession(e, r);
                break;
            case VALIDATE_PUBLISHER_STREAMING_SESSION:
                createOrJoinStreamingSession(e, r);
                break;
            case DB_PUBLISHER_SHOW_START:
                startStreamingSession(e, r);
                break;
            case DB_PUBLISHER_ERROR_REPORT:
                reportError(e, r);
                break;
            case FAILED_PUBLISHER_STREAMING_SESSION:
                errorStreamingSession(e);
                break;
            case DB_PUBLISHER_SHOW_END:
                terminateStreamingSession(e, r);
                break;
            case DB_PUBLISHER_SAVE_CONFIG:
                saveConfig(e, r);
                break;
            case DB_PUBLISHER_SETTINGS_SHOW:
                loadOrSaveShow(e, r);
                break;
            case DB_POST_PUBLISHER_ONDEMAND_START:
                onDemandStart(e, r);
                break;
            case DB_POST_PUBLISHER_ONDEMAND_END:
                onDemandEnd(e, r);
                break;
            case DB_GET_WORD_PASSTHROUGH:
                getMotd(e, r);
                break;
            case DB_PUBLISHER_SUBSCRIBER_PROFILE:
                getOrUpdateSubscriberProfile(path, e);
                break;
            case DB_PUBLISHER_SHARED_PRIVATE_START:
                sharedPrivateSession(e, r);
                break;
            case DB_PUBLISHER_SHARED_PRIVATE_END:
                endSharedPrivateSession(e, r);
                break;
            case DB_SUBSCRIBER_EJECT:
            case DB_SUBSCRIBER_BLOCK:
                validateAndStatusEvent(path, e);
                break;
            case DB_PUBLISHER_PUBLIC_MODE:
            case DB_PUBLISHER_PUBLIC_MODE_END:
                publisherPublicMode(path, e);
                break;
            case DB_PUBLISHER_ACTIONS:
                savesAction(path, e);
                break;
            default:
                throw new IllegalStateException("Invalid Message Path " + path);
        }
    }

    private void savesAction(String path, Envelope e) {
        utils.statusEvent(path, e);
    }

    private void publisherPublicMode(final String path, final Envelope e) {

        final PublisherEndpoint p = utils.retrieve(e.getUuid());
        if (!utils.validateAll(p, e)) ;

        utils.statusEvent(path, e, p, new Reply<Status>() {
            @Override
            public void ok(Status status) {
                logger.trace("Status {}", status);

                String[] pathSegments = path.split("/");
                if (pathSegments[6].endsWith("start")) {
                    p.state().sessionType(StreamingSession.TYPE.PUBLIC);
                } else {
                    p.state().sessionType(StreamingSession.TYPE.OFF);
                }
                response(e, p, utils.constructMessage(path, utils.writeAsString(status)));
            }

            @Override
            public void fail(Status status) {
                error(e, p, utils.constructMessage(path, utils.writeAsString(new org.zodiark.service.Error().error("Unauthorized"))));
            }
        });
    }

    private void validateAndStatusEvent(String path, Envelope e) {

        final PublisherEndpoint p = utils.retrieve(e.getUuid());
        if (!utils.validate(p, e)) return;

        String[] paths = e.getMessage().getPath().split("/");

        boolean subscriberOk = validateSubscriberState(paths[5], p);

        // TODO: utils.validate Subscriber
//        if (!isValid.get()) {
//            error(e, p, utils.constructMessage("/error", "No Subscriber for " + paths[5]));
//        }

        utils.statusEvent(path, e);
    }

    private boolean validateSubscriberState(String subscriberId, final PublisherEndpoint p) {
        final AtomicBoolean isValid = new AtomicBoolean();
        // DAangerous if the path change.
        eventBus.message(RETRIEVE_SUBSCRIBER, subscriberId, new Reply<SubscriberEndpoint>() {
            @Override
            public void ok(SubscriberEndpoint s) {
                isValid.set(s.publisherEndpoint().equals(p));
            }

            @Override
            public void fail(SubscriberEndpoint s) {
                logger.error("No Endpoint");
            }
        });
        return isValid.get();
    }

    private void endSharedPrivateSession(Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_PUBLISHER_SHARED_PRIVATE_END, e);
    }

    private void sharedPrivateSession(Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_PUBLISHER_SHARED_PRIVATE_START_POST, e);
    }

    private void getOrUpdateSubscriberProfile(String path, Envelope e) {
        Message m = e.getMessage();
        if (!m.hasData()) {
            // TODO: UC15
            utils.passthroughEvent(DB_PUBLISHER_SUBSCRIBER_PROFILE_GET, e);
        } else {
            String[] paths = path.split("/");

            PublisherEndpoint p = utils.retrieve(e.getUuid());
            if (!utils.validate(p, e)) return;

            // TODO: utils.validate Subscriber
            boolean subscriberOk = validateSubscriberState(paths[5], p);

            utils.statusEvent(DB_PUBLISHER_SUBSCRIBER_PROFILE_PUT, e, p);
        }

    }

    private void getMotd(Envelope e, AtmosphereResource r) {
        utils.passthroughEvent(DB_GET_WORD_PASSTHROUGH, e);
    }

    private void onDemandEnd(Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_POST_PUBLISHER_ONDEMAND_END, e);
    }

    private void onDemandStart(Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_POST_PUBLISHER_ONDEMAND_START, e);
    }

    public void saveConfig(final Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_PUBLISHER_SAVE_CONFIG_PUT, e);
    }

    public void loadOrSaveShow(final Envelope e, AtmosphereResource r) {
        Message m = e.getMessage();
        if (!m.hasData()) {
            utils.passthroughEvent(DB_PUBLISHER_SETTINGS_SHOW_GET_PASSTHROUGHT, e);
        } else {
            utils.statusEvent(DB_PUBLISHER_SETTINGS_SHOW_SAVE, e);
        }

    }

    public void reportError(final Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_PUBLISHER_ERROR_REPORT, e);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void retrieveEndpoint(Object publisherEndpointUuid, Reply reply) {
        if (String.class.isAssignableFrom(publisherEndpointUuid.getClass())) {
            reply.ok(endpoints.get(publisherEndpointUuid.toString()));
        } else {
            reply.fail(new Exception("No publisher associated"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void errorStreamingSession(Envelope e) {
        try {
            PublisherResults result = mapper.readValue(e.getMessage().getData(), PublisherResults.class);
            PublisherEndpoint p = endpoints.get(result.getUuid());

            Message m = new Message();
            m.setPath(ERROR_STREAMING_SESSION);
            try {
                m.setData(mapper.writeValueAsString(new PublisherResults("ERROR")));
            } catch (JsonProcessingException e1) {
                //
            }
            error(e, p, m);
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateStreamingSession(final Envelope e, AtmosphereResource r) {
        utils.statusEvent(DB_PUBLISHER_SHOW_END, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createOrJoinStreamingSession(final Envelope e, AtmosphereResource r) {
        PublisherEndpoint p = utils.retrieve(e.getUuid());

        if (!utils.validate(p, e)) return;

        try {
            p.wowzaServerUUID(mapper.readValue(e.getMessage().getData(), WowzaUUID.class).getUuid());
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }

        // TODO: Callback is not called at the moment as the dispatching to Wowza is asynchronous
        eventBus.message(WOWZA_CONNECT, p, new Reply<PublisherEndpoint>() {
            @Override
            public void ok(PublisherEndpoint p) {
                // TODO: Proper Message
                Message m = new Message();
                response(e, p, m);
            }

            @Override
            public void fail(PublisherEndpoint p) {
                error(e, p, utils.constructMessage(WOWZA_CONNECT, "error"));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startStreamingSession(final Envelope e, AtmosphereResource r) {
        String uuid = e.getUuid();

        PublisherEndpoint p = utils.retrieve(uuid);
        if (!utils.validate(p, e)) return;

        eventBus.message(DB_PUBLISHER_SHOW_START, p, new Reply<PublisherEndpoint>() {
            @Override
            public void ok(PublisherEndpoint p) {
                logger.trace("Publisher ready {}", p);
                response(e, p, utils.constructMessage(DB_PUBLISHER_SHOW_START, utils.writeAsString(p.showId())));
            }

            @Override
            public void fail(PublisherEndpoint p) {
                // TODO: Wrong error message
                error(e, p, utils.constructMessage(DB_PUBLISHER_SHOW_START, utils.writeAsString(new Error().error("Unauthorized"))));
            }
        }).message(BROADCASTER_CREATE, p);
    }

    @Override
    public void response(Envelope e, PublisherEndpoint endpoint, Message m) {
        utils.response(e, endpoint, m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactTo(String path, Object message, Reply reply) {
        switch (path) {
            case RETRIEVE_PUBLISHER:
                retrieveEndpoint(message, reply);
                break;
            case PUBLISHER_ABOUT_READY:
                resetEndpoint(message, reply);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetEndpoint(Object publisherEndpointUuid, Reply reply) {
        try {
            PublisherEndpoint p = endpoints.get(publisherEndpointUuid.toString());
            AtmosphereResource r = p.resource();
            Message m = new Message();
            m.setPath(PUBLISHER_ABOUT_READY);
            m.setData(mapper.writeValueAsString(new PublisherResults("READY")));

            Envelope e = Envelope.newPublisherRequest(p.uuid(), m);
            r.write(mapper.writeValueAsString(e));
        } catch (JsonProcessingException e1) {
            logger.debug("Unable to write {} {}", publisherEndpointUuid);
            reply.fail(publisherEndpointUuid);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherEndpoint createSession(final Envelope e, final AtmosphereResource r) {

        if (!e.getMessage().hasData()) {
            error(e, r, utils.constructMessage(DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT, "error"));
            return null;
        }

        final String uuid = e.getUuid();
        PublisherEndpoint p = endpoints.get(uuid);
        if (p == null) {
            p = context.newInstance(PublisherEndpoint.class);
            p.uuid(uuid).message(e.getMessage()).resource(r);

            endpoints.put(uuid, p);

            String data = e.getMessage().getData();

            e.getMessage().setData(injectIp(r.getRequest().getRemoteAddr(), data));

            eventBus.message(DB_POST_PUBLISHER_SESSION_CREATE, p, new Reply<PublisherEndpoint>() {
                @Override
                public void ok(final PublisherEndpoint p) {
                    logger.trace("{} succeed for {}", DB_POST_PUBLISHER_SESSION_CREATE, p);

                    eventBus.message(DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT, p, new Reply<Passthrough>() {
                        @Override
                        public void ok(Passthrough passthrough) {
                            utils.succesPassThrough(e, p, DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT, passthrough);

                            eventBus.message(DB_PUBLISHER_LOAD_CONFIG_GET, p, new Reply<Passthrough>() {
                                @Override
                                public void ok(Passthrough passthrough) {
                                    utils.succesPassThrough(e, p, DB_PUBLISHER_LOAD_CONFIG, passthrough);
                                    utils.passthroughEvent(DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT, e, p);
                                }

                                @Override
                                public void fail(Passthrough passthrough) {
                                    utils.failPassThrough(e, p, passthrough);
                                }
                            });
                        }

                        @Override
                        public void fail(Passthrough passthrough) {
                            utils.failPassThrough(e, p, passthrough);
                        }
                    });
                }

                @Override
                public void fail(PublisherEndpoint p) {
                    error(e, p, utils.constructMessage(DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT, "error"));
                }
            });
        }

        r.addEventListener(new OnDisconnect() {
            @Override
            public void onDisconnect(AtmosphereResourceEvent event) {
                logger.debug("Publisher {} disconnected", uuid);
                endpoints.remove(uuid);
            }
        });
        return p;
    }

    private String injectIp(String remoteAddr, String data) {
        return data.replaceAll("\\s+","").replace("\"\"", "\"" + remoteAddr + "\"");
    }

    @Override
    public void error(Envelope e, PublisherEndpoint endpoint, Message m) {
        utils.error(e, endpoint, m);
    }

    @Override
    public void error(Envelope e, AtmosphereResource r, Message m) {
        utils.error(e, r, m);
    }


}
