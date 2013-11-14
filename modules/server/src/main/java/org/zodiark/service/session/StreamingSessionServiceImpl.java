/*
 * Copyright 2013 Jeanfrancois Arcand
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
import org.zodiark.server.EventBusListener;
import org.zodiark.server.annotation.Inject;
import org.zodiark.server.annotation.On;
import org.zodiark.service.action.Action;
import org.zodiark.service.config.PublisherConfig;
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
import static org.zodiark.protocol.Paths.STREAMING_COMPLETE_ACTION;
import static org.zodiark.protocol.Paths.STREAMING_EXECUTE_ACTION;
import static org.zodiark.protocol.Paths.WOWZA_DEOBFUSCATE;
import static org.zodiark.protocol.Paths.WOWZA_OBFUSCATE;

@On("/streaming")
public class StreamingSessionServiceImpl implements StreamingSessionService {

    private final Logger logger = LoggerFactory.getLogger(StreamingSessionServiceImpl.class);

    @Inject
    public Context context;

    @Inject
    public EventBus eventBus;

    private final ConcurrentHashMap<String, StreamingSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void serve(Envelope e, AtmosphereResource r) {
    }

    @Override
    public void serve(String event, Object message, EventBusListener l) {
        logger.trace("Handling {}", event);

        switch(event) {
            case BEGIN_STREAMING_SESSION:
                PublisherEndpoint p = PublisherEndpoint.class.cast(message);

                boolean hasStreamingSession = hasStreamingSession(p);
                if (hasStreamingSession) {
                    terminate(p, l);
                } else {
                    initiate(p, l);
                }
                break;
            case BEGIN_SUBSCRIBER_STREAMING_SESSION:
                join(SubscriberEndpoint.class.cast(message), l);
                break;
            case STREAMING_EXECUTE_ACTION:
                Action a = Action.class.cast(message);
                executeAction(a, l);
                break;
            case STREAMING_COMPLETE_ACTION:
                p = PublisherEndpoint.class.cast(message);
                completeAction(p);
                break;
            default:
                logger.error("Not Supported {}", event);
        }

    }

    @Override
    public void completeAction(PublisherEndpoint p) {
        p.actionInProgress(false);
        final StreamingSession session = sessions.get(p.uuid());
        if (session == null) {
            throw new IllegalStateException("No live session for " + p.uuid());
        }

        final Action completedAction = session.pendingAction();
        session.pendingAction(null);

        eventBus.dispatch(WOWZA_DEOBFUSCATE, session, new EventBusListener<StreamingSession>() {
            @Override
            public void completed(StreamingSession session) {
                logger.trace("Wowza de-obfuscation executed {}", completedAction);
                session.completeAction(completedAction);
            }

            @Override
            public void failed(StreamingSession action) {
                logger.error("Error finishing Session {}", action);
            }
        });
    }

    @Override
    public void executeAction(final Action a, final EventBusListener l) {

        PublisherEndpoint p = a.subscriber().publisherEndpoint();
        final StreamingSession session = sessions.get(p.uuid());
        if (session == null) {
            throw new IllegalStateException("No live session for " + p.uuid());
        }
        session.pendingAction(a);

        eventBus.dispatch(WOWZA_OBFUSCATE, session, new EventBusListener<StreamingSession>() {
            @Override
            public void completed(StreamingSession session) {
                logger.trace("Wowza obfuscation executed {}", a);
                session.executeAction(a);
                // TODO: Do we need to call the subscriber
                l.completed(a);
            }

            @Override
            public void failed(StreamingSession action) {
                l.failed(action);
            }
        });

    }

    @Override
    public void terminate(PublisherEndpoint p, EventBusListener l) {
        logger.trace("Terminating streaming session {}", p);
        StreamingSession s = sessions.remove(p.uuid());
        s.terminate();
    }

    @Override
    public void initiate(PublisherEndpoint p, EventBusListener l) {
        logger.trace("Starting streaming session {}", p);
        StreamingSession s = sessionType(p);
        sessions.put(p.uuid(), s);
        s.publisher(p);

        eventBus.dispatch(BROADCAST_TO_ALL, p);
        s.initAndAct();

        l.completed(p);
    }

    @Override
    public void join(SubscriberEndpoint s, EventBusListener l) {
        boolean hasStreamingSession = hasStreamingSession(s.publisherEndpoint());
        if (!hasStreamingSession) {
            l.failed(s);
        } else {
            StreamingSession streamingSession = sessions.get(s.publisherEndpoint().uuid());
            streamingSession.validateAndJoin(s, l);
        }

    }

    private boolean hasStreamingSession(PublisherEndpoint p) {
        return sessions.get(p.uuid()) == null ? false : true;
    }

    private StreamingSession sessionType(PublisherEndpoint p) {
        PublisherConfig config = p.config();

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
