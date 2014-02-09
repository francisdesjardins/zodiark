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
import org.zodiark.server.EndpointUtils;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.On;
import org.zodiark.service.Session;
import org.zodiark.service.action.Action;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.session.StreamingRequest;
import org.zodiark.service.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.zodiark.protocol.Paths.ACTION_VALIDATE;
import static org.zodiark.protocol.Paths.BEGIN_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.BROADCASTER_TRACK;
import static org.zodiark.protocol.Paths.DB_POST_SUBSCRIBER_SESSION_CREATE;
import static org.zodiark.protocol.Paths.DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT;
import static org.zodiark.protocol.Paths.ERROR_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.FAILED_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.JOIN_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.MONITOR_RESOURCE;
import static org.zodiark.protocol.Paths.RETRIEVE_PUBLISHER;
import static org.zodiark.protocol.Paths.RETRIEVE_SUBSCRIBER;
import static org.zodiark.protocol.Paths.SERVICE_SUBSCRIBER;
import static org.zodiark.protocol.Paths.SUBSCRIBER_ACTION;
import static org.zodiark.protocol.Paths.SUBSCRIBER_BROWSER_HANDSHAKE;
import static org.zodiark.protocol.Paths.SUBSCRIBER_BROWSER_HANDSHAKE_OK;
import static org.zodiark.protocol.Paths.TERMINATE_SUBSCRIBER_STREAMING_SESSSION;
import static org.zodiark.protocol.Paths.VALIDATE_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.WOWZA_CONNECT;

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
            case VALIDATE_SUBSCRIBER_STREAMING_SESSION:
                createOrJoinStreamingSession(e, r);
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
            case SUBSCRIBER_ACTION:
                requestForAction(e, r);
                break;
            case SUBSCRIBER_BROWSER_HANDSHAKE:
                connectEndpoint(e, r);
                break;
            case DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT:
                availableActions(e, r);
                break;
            default:
                throw new IllegalStateException("Invalid Message Path" + e.getMessage().getPath());
        }
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
        s.uuid(resource.uuid()).message(m).resource(resource);
        eventBus.message(BROADCASTER_TRACK, s).message(MONITOR_RESOURCE, resource);
        return s;
    }

    @Override
    public SubscriberEndpoint createSession(final Envelope e, AtmosphereResource r) {
        String uuid = e.getUuid();
        SubscriberEndpoint s = endpoints.get(uuid);
        if (s == null || !s.hasSession()) {
            s = createEndpoint(r, e.getMessage());
            endpoints.put(uuid, s);
            // Subscriber will be deleted in case an error happens.
            s.hasSession(true);
            utils.statusEvent(DB_POST_SUBSCRIBER_SESSION_CREATE, e, s);
        } else {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
        }
        return s;
    }

    private void availableActions(Envelope e, AtmosphereResource r) {
        SubscriberEndpoint s = utils.retrieve(e.getUuid());

        if (!utils.validate(s, e)) return;

        if (!s.hasSession()) {
            error(e, r, new Message().setPath("/error").setData("unauthorized"));
        }

        utils.passthroughEvent(DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT, e);
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
      public void requestForAction(final Envelope e, AtmosphereResource r) {
          Message m = e.getMessage();
          final SubscriberEndpoint s = endpoints.get(e.getUuid());

          if (s == null) {
              throw new IllegalStateException("No Subscriber associated with " + e.getUuid());
          }

          try {
              Action a = mapper.readValue(m.getData(), Action.class);
              a.subscriber(s);
              eventBus.message(ACTION_VALIDATE, a, new Reply<Action>() {
                  @Override
                  public void ok(Action action) {
                      logger.debug("Action Accepted for {}", action.getSubscriberUUID());
                      response(e, endpoints.get(action.getSubscriberUUID()), utils.constructMessage(ACTION_VALIDATE, "OK"));
                  }

                  @Override
                  public void fail(Action action) {
                      error(e, s, utils.constructMessage(ACTION_VALIDATE, "error"));
                  }
              });
          } catch (IOException e1) {
              logger.warn("{}", e1);
          }
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
      public void createOrJoinStreamingSession(final Envelope e, AtmosphereResource r) {
          String uuid = e.getUuid();
          try {
              final SubscriberEndpoint s = utils.retrieve(uuid);
              final StreamingRequest request = mapper.readValue(e.getMessage().getData(), requestClass.getClass());

              eventBus.message(RETRIEVE_PUBLISHER, request.getPublisherUUID(), new Reply<PublisherEndpoint>() {
                  @Override
                  public void ok(PublisherEndpoint p) {
                      s.wowzaServerUUID(request.getWowzaUUID()).publisherEndpoint(p);

                      // TODO: Callback is not called at the moment as the dispatching to Wowza is asynchronous
                      // TODO: Unit test
                      eventBus.message(WOWZA_CONNECT, s);
                  }

                  @Override
                  public void fail(PublisherEndpoint p) {
                      error(e, s, utils.constructMessage(VALIDATE_SUBSCRIBER_STREAMING_SESSION, "error"));
                  }
              });
          } catch (IOException e1) {
              logger.warn("{}", e1);
          }

      }

      @Override
      public void retrieveEndpoint(Object subscriberUuid, Reply reply) {
          if (String.class.isAssignableFrom(subscriberUuid.getClass())) {
              SubscriberEndpoint s = endpoints.get(subscriberUuid.toString());
              if (s != null) {
                  reply.ok(s);
              } else {
                  reply.fail(null);
              }
          } else {
              reply.fail(null);
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

          SubscriberEndpoint s = utils.retrieve(uuid.getUuid());
          eventBus.message(BEGIN_SUBSCRIBER_STREAMING_SESSION, s, new Reply<SubscriberEndpoint>() {
              @Override
              public void ok(SubscriberEndpoint s) {
                  response(e, s, utils.constructMessage(BEGIN_SUBSCRIBER_STREAMING_SESSION, "OK"));
              }

              @Override
              public void fail(SubscriberEndpoint s) {
                  error(e, s, utils.constructMessage(BEGIN_SUBSCRIBER_STREAMING_SESSION, "error"));
              }
          });
      }

      @Override
      public void response(Envelope e, SubscriberEndpoint endpoint, Message m) {
            utils.response(e, endpoint, m);
      }


}
