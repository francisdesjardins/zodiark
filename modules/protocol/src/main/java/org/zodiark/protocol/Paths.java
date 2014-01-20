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
package org.zodiark.protocol;

/**
 * REST path for firing event using an EventBus, sending messages, etc. The targetted Service is alwaus the first part of the path.
 */
public interface Paths {

    /**
     * An I/O event to start the Publisher Streaming Session
     */
    String START_PUBLISHER_STREAMING_SESSION = "/publisher/startStreamingSession";
    /**
     * An I/O event to join an existing Publisher streaming Session
     */
    String JOIN_SUBSCRIBER_STREAMING_SESSION = "/subscriber/joinStreamingSession";
    /**
     * An I/O event to create the PublisherEndpoint's Session
     */
    String CREATE_PUBLISHER_SESSION = "/publisher/createUserSession";
    /**
     * An I/O event sent Create the SubscriberEndpoint's Session
     */
    String CREATE_SUBSCRIBER_SESSION = "/subscriber/createUserSession";

    /**
     * A Message tolLoad the Publisher Session
     */
    String LOAD_PUBLISHER_CONFIG = "/publisher/loadConfig";
    /**
     * An I/O event to create or prepare the Streaming Session by load the data from the dabatase/web service, and by requesting
     * access to a remote Wowza Endpoint.
     */
    String VALIDATE_PUBLISHER_STREAMING_SESSION = "/publisher/validateStreamingSession";
    /**
     * An I/O event to validate the Subscriber Streaming Session
     */
    String VALIDATE_SUBSCRIBER_STREAMING_SESSION = "/subscriber/validateStreamingSession";
    /**
     * An I/O event Send by Wowza when the Streaming Session cannot be started, for whatever reason, for a Publisher.
     */
    String FAILED_PUBLISHER_STREAMING_SESSION = "/publisher/errorStreamingSession";
    /**
     * An I/O event Send by Wowza when the Streaming Session cannot be started, for whatever reason, for a Subscriber.
     */
    String FAILED_SUBSCRIBER_STREAMING_SESSION = "/subscriber/errorStreamingSession";
    /**
     * An I/O event {@link Message#path} sent back to the remote Endpoint
     */
    String ERROR_STREAMING_SESSION = "/error/errorStreamingSession";
    /**
     * An I/O event and a message. Once the Publisher has been authorized to Stream, the PublisherService will
     * fire this event to start the streaming session via the StreamingService.
     */
    String BEGIN_STREAMING_SESSION = "/streaming/begin/publisher";
    /**
     * Annonce the session
     */
    String DB_PUBLISHER_ANNOUNCE_SESSION = "/v1/publisher/@guid/show/start";


    String DB_SUBSCRIBER_JOIN_SESSION = "/v1/subscriber/@guid/watch/start";
    /**
     * An I/O event. Send a request to Wowza asking for a Streaming Session Approval
     */
    String WOWZA_CONNECT = "/wowza/connect";
    /**
     * A Message to Advise the WowzaService that its needs to start the obfuscation process
     */
    String WOWZA_OBFUSCATE = "/wowza/obfuscate";
    /**
     * A Message to advise the WowzaService that its needs to start the deobfuscation process
     */
    String WOWZA_DEOBFUSCATE = "/wowza/deobfuscate";
    /**
     * An I/O event received from a Wowxa endpoint when the obfuscation process is completed and the action can start.
     */
    String WOWZA_OBFUSCATE_OK = "/wowza/obfuscate/ok";
    /**
     * An I/O event Received from a Wowxa endpoint when the deobfuscation process is completed and the normal behavior
     */
    String WOWZA_DEOBFUSCATE_OK = "/wowza/deobfuscate/ok";
    /**
     * A message to retrieve the data from an Endpoint
     */
    String DB_PUBLISHER_CONFIG = "/v1/publisher/@uuid/session/config";
    /**
     * A message to Initialize an Endpoint Session within the remote database/web service endpoint.
     */
    String DB_PUBLISHER_SESSION_CREATE = "/v1/publisher/@uuid/session/create";
    /**
     * A Message to retrieve the list of banned word for a Streaming Session Chat.
     */
    String DB_WORD = "/db/word";
    /**
     * a Message to validate the state of a Subscriber
     */
    String DB_SUBSCRIBER_VALIDATE_STATE = "/v1/subscriber/@uuid/session/validate";
    /**
     * An I/O event to leave the current Publisher Streaming Session
     */
    String TERMINATE_STREAMING_SESSSION = "/publisher/disconnect";
    /**
     * An I/O event to leave the current Streaming Session
     */
    String TERMINATE_SUBSCRIBER_STREAMING_SESSSION = "/subscriber/disconnect";
    /**
     * An I/O event from Wowza in response to a request for a validation request
     */
    String SERVER_VALIDATE_OK = "/wowza/validate";
    /**
     * An I/O event. Request for an Action
     */
    String REQUEST_ACTION = "/request/action";
    /**
     * An I/O event. A request for Action. This path is used by an Envelope
     */
    String MESSAGE_ACTION = "/message/action";
    /**
     * A Message to retrieve the publisher
     */
    String RETRIEVE_PUBLISHER = "/publisher/retrieve";
    /**
     * A Message to retrieve the subscriber
     */
    String RETRIEVE_SUBSCRIBER = "/subscriber/retrieve";
    /**
     * An I/O event for Action requested by a Subscriber
     */
    String SUBSCRIBER_ACTION = "/subscriber/action";
    /**
     * A message to validate if an Action can be executed by a Publisher, from a Subscriber
     */
    String ACTION_VALIDATE = "/action/validate";
    /**
     * A message to request an Action
     */
    String ACTION_ACCEPT = "/action/accept";
    /**
     * A I/O event from the publisher when accepting an Action
     */
    String ACTION_ACCEPT_OK = "/action/actionAccepted";
    /**
     * A Message sent to the Publisher and Subscriber when an Action is about to start
     */
    String ACTION_START = "/action/start";
    /**
     * A message from a Publisher rejecting an Action
     */
    String ACTION_ACCEPT_REFUSED = "/action/actionRefused";
    /**
     * Join an existing Publisher Session
     */
    String BEGIN_SUBSCRIBER_STREAMING_SESSION = "/streaming/join";
    /**
     * Once accepted, request the Publisher to execute an Action
     */
    String STREAMING_EXECUTE_ACTION = "/streaming/executeAction";
    /**
     * Fired when an Action is completed.
     */
    String STREAMING_COMPLETE_ACTION = "/streaming/completeAction";
    /**
     * An I/O event received when a publisher accept an Action
     */
    String ACTION_START_OK = "/action/start/ok";
    /**
     * An I/O event sent to Publisher/Subscriber when timing an Action
     */
    String ACTION_TIMER = "/action/timer";
    /**
     * An I/O event sent to Publisher announcing the Action is completed.
     */
    String ACTION_COMPLETED = "/action/completed";
    /**
     * An I/O event received from the publisher when an Action is received.
     */
    String PUBLISHER_ACTION_COMPLETED = "/publisher/actionCompleted";
    /**
     * An I/O event sent to Publisher when an Action is ready to be executed
     */
    String PUBLISHER_ABOUT_READY = "/publisher/ready";
    /**
     * An I/O event received from the Subscriber at the beginning of a Session
     */
    String SUBSCRIBER_BROWSER_HANDSHAKE = "/subscriber/handshake";
    /**
     * An I/O event sent to the Subscriber at the beginning of a Session
     */
    String SUBSCRIBER_BROWSER_HANDSHAKE_OK = "/subscriber/handshake/OK";
    /**
     * A message sent to the BroadcasterService to create a Broadcaster
     */
    String BROADCASTER_CREATE = "/broadcaster/create";
    /**
     * A message sent to the BroadcasterService asking to associate a subscriber with a publisher's session
     */
    String BROADCASTER_TRACK = "/broadcaster/track";
    /**
     * Broadcast a message to all chat room
     */
    String BROADCAST_TO_ALL = "/broadcaster/toAll";
    /**
     * A Message for tracking connection's disconnect.
     */
    String MONITOR_RESOURCE = "/monitor/configure";
    /**
     * A Message for initiating publisher/subscriber state in the database/remote webservice
     */
    String DISCONNECTED_RESOURCE = "/db/disconnected";

    String BROADCASTER_DISPATCH = "/broadcaster/dispatch";

    // ===== Service Mapping

    String SERVICE_ACTION = "/action";

    String SERVICE_PUBLISHER = "/publisher";

    String SERVICE_MONITORING = "/monitor";

    String SERVICE_STREAMING = "/streaming";

    String SERVICE_SUBSCRIBER = "/subscriber";

    String SERVICE_WOWZA = "/wowza";
}