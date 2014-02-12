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
package org.zodiark.service.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.protocol.Message;
import org.zodiark.server.Context;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.ReplyException;
import org.zodiark.server.annotation.On;
import org.zodiark.service.EndpointUtils;
import org.zodiark.service.RetrieveMessage;
import org.zodiark.service.Session;
import org.zodiark.service.db.Actions;
import org.zodiark.service.db.TransactionId;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.session.StreamingRequest;
import org.zodiark.service.state.EndpointState;
import org.zodiark.service.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.zodiark.protocol.Paths.BEGIN_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.BROADCASTER_TRACK;
import static org.zodiark.protocol.Paths.DB_ENDPOINT_STATE;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_EXTRA;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_FAVORITES_END;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_FAVORITES_START;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_JOIN_ACTION;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_REQUEST_ACTION;
import static org.zodiark.protocol.Paths.ERROR_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.FAILED_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.JOIN_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.MONITOR_RESOURCE;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;
import static org.zodiark.protocol.Paths.RETRIEVE_SUBSCRIBER;
import static org.zodiark.protocol.Paths.SERVICE_SUBSCRIBER;
import static org.zodiark.protocol.Paths.SUBSCRIBER_BROWSER_HANDSHAKE;
import static org.zodiark.protocol.Paths.SUBSCRIBER_BROWSER_HANDSHAKE_OK;
import static org.zodiark.protocol.Paths.TERMINATE_SUBSCRIBER_STREAMING_SESSSION;

/**
 * A Service responsible for managing {@link SubscriberEndpoint}
 */
@On(SERVICE_SUBSCRIBER)
public class SubscriberServiceImpl implements SubscriberService, Session<SubscriberEndpoint> {

    private final ConcurrentHashMap<String, SubscriberEndpoint> endpoints = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(SubscriberServiceImpl.class);

    @Inject
    public Context context;

    @Inject
    public EventBus eventBus;

    @Inject
    public ObjectMapper mapper;

    @Inject
    public StreamingRequest requestClass;

    private EndpointUtils<SubscriberEndpoint> utils;

    @PostConstruct
    public void init() {
        utils = new EndpointUtils(eventBus, mapper, endpoints);
    }

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
        logger.trace("Handling Subscriber Envelop {} to Service {}", e, r.uuid());
        String path = e.getMessage().getPath();
        switch (path) {
            case DB_POST_SUBSCRIBER_SESSION_CREATE:
                createSession(e, r);
                break;
            case JOIN_SUBSCRIBER_STREAMING_SESSION:
                startStreamingSession(e, r);
                break;
            case FAILED_SUBSCRIBER_STREAMING_SESSION:
                errorStreamingSession(e);
                break;
            case TERMINATE_SUBSCRIBER_STREAMING_SESSSION:
                terminateStreamingSession(e, r);
                break;
            case SUBSCRIBER_BROWSER_HANDSHAKE:
                connectEndpoint(e, r);
                break;
            case DB_SUBSCRIBER_AVAILABLE_ACTIONS:
                availableActions(e, r);
                break;
            case DB_SUBSCRIBER_REQUEST_ACTION:
                requestAction(e, r);
                break;
            case DB_SUBSCRIBER_JOIN_ACTION:
                joinAction(e, r);
                break;
            case DB_SUBSCRIBER_FAVORITES_END:
                deleteFavorite(e, r);
                break;
            case DB_SUBSCRIBER_FAVORITES_START:
                addFavorites(e, r);
                break;
            case DB_SUBSCRIBER_EXTRA:
                tipPublisher(e, r);
                break;
            default:
                throw new IllegalStateException("Invalid Message Path" + e.getMessage().getPath());
        }
    }

    private void tipPublisher(final Envelope e, AtmosphereResource r) {
        final SubscriberEndpoint s = utils.retrieve(e.getUuid());

        if (!utils.validate(s, e)) return;

        if (!s.hasSession()) {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
            return;
        }

        eventBus.message(DB_SUBSCRIBER_EXTRA, new RetrieveMessage(s.uuid(), e.getMessage()), new Reply<TransactionId, String>() {
            @Override
            public void ok(TransactionId success) {
                s.transactionId(success);
                logger.debug("Action Accepted for {}", s);
                response(e, s, utils.constructMessage(DB_SUBSCRIBER_EXTRA, utils.writeAsString(s.transactionId())));
            }

            @Override
            public void fail(ReplyException replyException) {
                error(e, s, utils.constructMessage(DB_SUBSCRIBER_EXTRA, "error"));
            }
        });
    }

    private void addFavorites(Envelope e, AtmosphereResource r) {
        SubscriberEndpoint s = utils.retrieve(e.getUuid());
        if (!utils.validate(s, e) || !s.hasSession()) return;

        utils.passthroughEvent(DB_SUBSCRIBER_FAVORITES_START, e);
    }

    private void deleteFavorite(Envelope e, AtmosphereResource r) {
        SubscriberEndpoint s = utils.retrieve(e.getUuid());
        // TODO: Should we keep favorites in memory? JFA -> No
        if (!utils.validate(s, e) || !s.hasSession()) return;

        utils.statusEvent(DB_SUBSCRIBER_FAVORITES_END, e, s);
    }

    private void joinAction(final Envelope e, AtmosphereResource r) {
        final SubscriberEndpoint s = utils.retrieve(e.getUuid());

        if (!utils.validate(s, e)) return;

        if (!s.hasSession() || !s.actionRequested()) {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
        }

        eventBus.message(DB_SUBSCRIBER_JOIN_ACTION, new RetrieveMessage(s.uuid(), e.getMessage()), new Reply<TransactionId, String>() {
            @Override
            public void ok(TransactionId success) {
                s.transactionId(success);
                logger.debug("Action Accepted for {}", s);
                response(e, s, utils.constructMessage(DB_SUBSCRIBER_JOIN_ACTION, utils.writeAsString(s.transactionId())));
            }

            @Override
            public void fail(ReplyException replyException) {
                error(e, s, utils.constructMessage(DB_SUBSCRIBER_JOIN_ACTION, "error"));
            }
        });
    }

    private void requestAction(Envelope e, AtmosphereResource r) {
        SubscriberEndpoint s = utils.retrieve(e.getUuid());

        if (!utils.validate(s, e)) return;

        if (!s.hasSession() || !s.actionRequested()) {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
        }

        // (1) Load DB_SUBSCRIBER_REQUEST_ACTION in memory
        // (2) Wait for Publisher's response. Response is asynchronous until the Publisher disconnect or change state.
        // (3) OK => Public => Subscriber's chat(facturation)  scramble = false, maxDuration =0
        // (4) Ok => Public  avec Timer  scramble = false, maxDuration = XXX
        // (5) OK => scramble = true, maxDuration = 0 ..... (Ping le Publisher pour min et max duration)
        // Facturation plus tardive pour les suivants


        // LA DB peut renvoyée une erreur.


        // Subscriber will be deleted in case an error happens.
        s.actionRequested(true);



        //utils.passthroughEvent(DB_SUBSCRIBER_AVAILABLE_ACTIONS, e);


        //eventBus.message(Paths.MESSAGE_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT)


    }

    @Override
    public void reactTo(String path, Object message, Reply reply) {
        switch (path) {
            case RETRIEVE_SUBSCRIBER:
                retrieveEndpoint(message, reply);
                break;
        }
    }

    private SubscriberEndpoint createEndpoint(AtmosphereResource resource, Message m) {
        SubscriberEndpoint s = context.newInstance(SubscriberEndpoint.class);
        s.uuid(resource.uuid()).resource(resource);
        eventBus.message(BROADCASTER_TRACK, s).message(MONITOR_RESOURCE, resource);
        return s;
    }

    private void message(String path, SubscriberEndpoint s, Message m) {
        eventBus.message(path, new RetrieveMessage(s.uuid(), m));
    }

    @Override
    public SubscriberEndpoint createSession(final Envelope e, AtmosphereResource r) {
        String uuid = e.getUuid();
        SubscriberEndpoint s = endpoints.get(uuid);
        if (s == null || !s.hasSession()) {
            s = createEndpoint(r, e.getMessage());
            endpoints.put(uuid, s);

            final AtomicReference<SubscriberEndpoint> subscriberEndpoint = new AtomicReference<>(s);
            eventBus.message(DB_ENDPOINT_STATE, new RetrieveMessage(s.uuid(), e.getMessage()), new Reply<EndpointState, String>() {

                @Override
                public void ok(EndpointState state) {
                    SubscriberEndpoint s = subscriberEndpoint.get();
                    // Subscriber will be deleted in case an error happens.
                    s.hasSession(true).state(state).publisherEndpoint(retrieve(state.publisherUUID()));
                    utils.statusEvent(DB_POST_SUBSCRIBER_SESSION_CREATE, e, s);
                }

                @Override
                public void fail(ReplyException replyException) {
                    error(e, subscriberEndpoint.get(), utils.constructMessage(DB_ENDPOINT_STATE, "error"));
                }
            });
        } else {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
        }
        return s;
    }

    public PublisherEndpoint retrieve(final String uuid) {
        // TODO: This won't work asynchronous
        final AtomicReference<PublisherEndpoint> publisher = new AtomicReference<>(null);
        eventBus.message(RETRIEVE_PUBLISHER, uuid, new Reply<PublisherEndpoint, String>() {
            @Override
            public void ok(PublisherEndpoint p) {
                publisher.set(p);
            }

            @Override
            public void fail(ReplyException replyException) {
                logger.error("Unable to retrieve publisher {}", uuid);
            }
        });
        return publisher.get();
    }

    private void availableActions(Envelope e, AtmosphereResource r) {
        SubscriberEndpoint s = utils.retrieve(e.getUuid());

        if (!utils.validate(s, e)) return;

        if (!s.hasSession()) {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
        }

        // Subscriber will be deleted in case an error happens.
        s.actionRequested(true);

        eventBus.message(DB_SUBSCRIBER_AVAILABLE_ACTIONS, new RetrieveMessage(s.uuid(), e.getMessage()), new Reply<Actions, String>() {

                    @Override
                    public void ok(Actions actions) {

                    }

                    @Override
                    public void fail(ReplyException replyException) {
                    }
                });



    }


    @Override
    public void error(Envelope e, SubscriberEndpoint endpoint, Message m) {
        utils.error(e, endpoint, m);
    }

    @Override
    public void error(Envelope e, AtmosphereResource r, Message m) {
        utils.error(e, r, m);
    }

    @Override
    public void connectEndpoint(Envelope e, AtmosphereResource r) {
        logger.info("Subscriber Connected {}", e);
        SubscriberEndpoint s = createEndpoint(r, e.getMessage());
        response(e, s, utils.constructMessage(SUBSCRIBER_BROWSER_HANDSHAKE_OK, "OK"));
    }

    @Override
    public void errorStreamingSession(Envelope e) {
        try {
            SubscriberResults result = mapper.readValue(e.getMessage().getData(), SubscriberResults.class);
            SubscriberEndpoint p = endpoints.get(result.getUuid());
            error(e, p, utils.constructMessage(ERROR_STREAMING_SESSION, "error"));
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }
    }

    @Override
    public void terminateStreamingSession(final Envelope e, AtmosphereResource r) {
        String uuid = e.getMessage().getUUID();
        SubscriberEndpoint p = endpoints.get(uuid);
    }

    @Override
    public void retrieveEndpoint(Object subscriberUuid, Reply reply) {
        if (String.class.isAssignableFrom(subscriberUuid.getClass())) {
            SubscriberEndpoint s = endpoints.get(subscriberUuid.toString());
            if (s != null) {
                reply.ok(s);
            } else {
                reply.fail(ReplyException.DEFAULT);
            }
        } else {
            reply.fail(ReplyException.DEFAULT);
        }
    }

    @Override
    public void startStreamingSession(final Envelope e, AtmosphereResource r) {
        UUID uuid = null;
        try {
            uuid = mapper.readValue(e.getMessage().getData(), UUID.class);
        } catch (IOException e1) {
            logger.warn("{}", e1);
        }

        final SubscriberEndpoint s = utils.retrieve(uuid.getUuid());
        eventBus.message(BEGIN_SUBSCRIBER_STREAMING_SESSION, new RetrieveMessage(s.uuid(), e.getMessage()), new Reply<RetrieveMessage, String>() {
            @Override
            public void ok(RetrieveMessage ok) {
                response(e, s, utils.constructMessage(BEGIN_SUBSCRIBER_STREAMING_SESSION, "OK"));
            }

            @Override
            public void fail(ReplyException replyException) {
                error(e, s, utils.constructMessage(BEGIN_SUBSCRIBER_STREAMING_SESSION, "error"));
            }
        });
    }

    @Override
    public void response(Envelope e, SubscriberEndpoint endpoint, Message m) {
        utils.response(e, endpoint, m);
    }


}
