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
package org.zodiark.service.session;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zodiark.protocol.Envelope;
import org.zodiark.server.Context;
import org.zodiark.server.EventBus;
import org.zodiark.server.Reply;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.action.Action;
import org.zodiark.service.config.PublisherState;
import org.zodiark.service.publisher.PublisherEndpoint;
import org.zodiark.service.session.impl.PrivateStreamingSession;
import org.zodiark.service.session.impl.ProtectedStreamingSession;
import org.zodiark.service.session.impl.PublicStreamingSession;
import org.zodiark.service.session.impl.SharedPrivateStreamingSession;
import org.zodiark.service.session.impl.ViewStreamingSession;
import org.zodiark.service.subscriber.SubscriberEndpoint;

import java.util.concurrent.ConcurrentHashMap;

import static org.zodiark.protocol.Paths.BEGIN_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.BEGIN_SUBSCRIBER_STREAMING_SESSION;
import static org.zodiark.protocol.Paths.BROADCAST_TO_ALL;
import static org.zodiark.protocol.Paths.SERVICE_STREAMING;
import static org.zodiark.protocol.Paths.STREAMING_COMPLETE_ACTION;
import static org.zodiark.protocol.Paths.STREAMING_EXECUTE_ACTION;
import static org.zodiark.protocol.Paths.WOWZA_DEOBFUSCATE;
import static org.zodiark.protocol.Paths.WOWZA_OBFUSCATE;

/**
 * The Default StreamingService implementation.
 */
@On(SERVICE_STREAMING)
public class StreamingSessionServiceImpl implements StreamingSessionService {

    private final Logger logger = LoggerFactory.getLogger(StreamingSessionServiceImpl.class);

    @Inject
    public Context context;

    @Inject
    public EventBus eventBus;

    private final ConcurrentHashMap<String, StreamingSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void reactTo(Envelope e, AtmosphereResource r, Reply reply) {
    }

    @Override
    public void reactTo(String path, Object message, Reply reply) {
        logger.trace("Handling {}", path);

        switch (path) {
            case BEGIN_STREAMING_SESSION:
                PublisherEndpoint p = PublisherEndpoint.class.cast(message);

                boolean hasStreamingSession = hasStreamingSession(p);
                if (hasStreamingSession) {
                    terminate(p, reply);
                } else {
                    initiate(p, reply);
                }
                break;
            case BEGIN_SUBSCRIBER_STREAMING_SESSION:
                join(SubscriberEndpoint.class.cast(message), reply);
                break;
            case STREAMING_EXECUTE_ACTION:
                Action a = Action.class.cast(message);
                executeAction(a, reply);
                break;
            case STREAMING_COMPLETE_ACTION:
                p = PublisherEndpoint.class.cast(message);
                completeAction(p);
                break;
            default:
                logger.error("Not Supported {}", path);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeAction(PublisherEndpoint publisherEndpoint) {
        publisherEndpoint.actionInProgress(false);
        final StreamingSession session = sessions.get(publisherEndpoint.uuid());
        if (session == null) {
            throw new IllegalStateException("No live session for " + publisherEndpoint.uuid());
        }

        final Action completedAction = session.pendingAction();
        session.pendingAction(null);

        eventBus.message(WOWZA_DEOBFUSCATE, session, new Reply<StreamingSession>() {
            @Override
            public void ok(StreamingSession session) {
                logger.trace("Wowza de-obfuscation executed {}", completedAction);
                session.completeAction(completedAction);
            }

            @Override
            public void fail(StreamingSession action) {
                logger.error("Error finishing Session {}", action);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAction(final Action a, final Reply reply) {

        PublisherEndpoint p = a.subscriber().publisherEndpoint();
        final StreamingSession session = sessions.get(p.uuid());
        if (session == null) {
            throw new IllegalStateException("No live session for " + p.uuid());
        }
        session.pendingAction(a);

        eventBus.message(WOWZA_OBFUSCATE, session, new Reply<StreamingSession>() {
            @Override
            public void ok(StreamingSession session) {
                logger.trace("Wowza obfuscation executed {}", a);
                session.executeAction(a);
                // TODO: Do we need to call the subscriber
                reply.ok(a);
            }

            @Override
            public void fail(StreamingSession action) {
                reply.fail(action);
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate(PublisherEndpoint publisherEndpoint, Reply reply) {
        logger.trace("Terminating streaming session {}", publisherEndpoint);
        StreamingSession s = sessions.remove(publisherEndpoint.uuid());
        s.terminate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initiate(PublisherEndpoint publisherEndpoint, Reply reply) {
        logger.trace("Starting streaming session {}", publisherEndpoint);
        StreamingSession s = sessionType(publisherEndpoint);
        sessions.put(publisherEndpoint.uuid(), s);
        s.publisher(publisherEndpoint);

        eventBus.message(BROADCAST_TO_ALL, publisherEndpoint);
        s.initAndAct();

        reply.ok(publisherEndpoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void join(SubscriberEndpoint subscriberEndpoint, Reply reply) {
        boolean hasStreamingSession = hasStreamingSession(subscriberEndpoint.publisherEndpoint());
        if (!hasStreamingSession) {
            reply.fail(subscriberEndpoint);
        } else {
            StreamingSession streamingSession = sessions.get(subscriberEndpoint.publisherEndpoint().uuid());
            streamingSession.validateAndJoin(subscriberEndpoint, reply);
        }

    }

    private boolean hasStreamingSession(PublisherEndpoint p) {
        return sessions.get(p.uuid()) == null ? false : true;
    }

    private StreamingSession sessionType(PublisherEndpoint p) {
        PublisherState config = p.config();

        switch (config.sessionType()) {
            case PUBLIC:
                return context.newInstance(PublicStreamingSession.class);
            case PRIVATE:
                return context.newInstance(PrivateStreamingSession.class);
            case SHARED_PRIVATE:
                return context.newInstance(SharedPrivateStreamingSession.class);
            case VIEW:
                return context.newInstance(ViewStreamingSession.class);
            case PROTECTED:
                return context.newInstance(ProtectedStreamingSession.class);
            default:
                throw new IllegalStateException("Unsupported Session " + config.sessionType());
        }
    }

}
