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
     * Start the Publisher Streaming Session
     */
    String START_PUBLISHER_STREAMING_SESSION = "/publisher/startStreamingSession";
    /**
     * Join an existing Publisher streaming Session
     */
    String JOIN_SUBSCRIBER_STREAMING_SESSION = "/subscriber/joinStreamingSession";
    /**
     * Create the PublisherEndpoint's Session
     */
    String CREATE_PUBLISHER_SESSION = "/publisher/createUserSession";
    /**
     * Create the SubscriberEndpoint's Session
     */
    String CREATE_SUBSCRIBER_SESSION = "/subscriber/createUserSession";

    /**
     * Load the Publisher Session
     */
    String LOAD_PUBLISHER_CONFIG = "/publisher/loadConfig";
    /**
     * Create or prepare the Streaming Session by load the data from the dabatase/web service, and by requesting
     * access to a remote Wowza Endpoint.
     */
    String VALIDATE_PUBLISHER_STREAMING_SESSION = "/publisher/validateStreamingSession";

    /**
     * Validate the Subscriber Streaming Session
     */
    String VALIDATE_SUBSCRIBER_STREAMING_SESSION = "/subscriber/validateStreamingSession";

    /**
     * Send by Wowza when the Streaming Session cannot be started, for whatever reason, for a Publisher.
     */
    String FAILED_PUBLISHER_STREAMING_SESSION = "/publisher/errorStreamingSession";
    /**
     * Send by Wowza when the Streaming Session cannot be started, for whatever reason, for a Subscriber.
     */
    String FAILED_SUBSCRIBER_STREAMING_SESSION = "/subscriber/errorStreamingSession";

    /**
     * A {@link Message#path} sent back to the remote Endpoint
     */
    String ERROR_STREAMING_SESSION = "/error/errorStreamingSession";
    /**
     * Once the Publisher has been authorized to Stream, the PublisherService will
     * fire this event to start the streaming session via the StreamingService.
     */
    String BEGIN_STREAMING_SESSION = "/streaming/begin/publisher";

    /**
     * Send a request to Wowza asking for a Streaming Session Approval
     */
    String WOWZA_CONNECT = "/wowza/connect";

    String WOWZA_OBFUSCATE = "/wowza/obfuscate";

    String WOWZA_DEOBFUSCATE = "/wowza/deobfuscate";

    String WOWZA_OBFUSCATE_OK = "/wowza/obfuscate/ok";

    String WOWZA_DEOBFUSCATE_OK = "/wowza/deobfuscate/ok";

    /**
     * Retrieve the data from an Endpoint
     */
    String DB_CONFIG = "/db/config";

    /**
     * Initialize an Endpoint Session within the remote database/web service endpoint.
     */
    String DB_INIT = "/db/init";

    /**
     * Retrieve the list of banned word for a Streaming Session Chat.
     */
    String DB_WORD = "/db/word";

    /**
     * Validate the state of a Subscriber
     */
    String SUBSCRIBER_VALIDATE_STATE = "/db/validate";
    /**
     * Leave the current Publisher Streaming Session
     */
    String TERMINATE_STREAMING_SESSSION = "/publisher/disconnect";
    /**
     * Leave the current Streaming Session
     */
    String TERMINATE_SUBSCRIBER_STREAMING_SESSSION = "/subscriber/disconnect";

    String SERVER_VALIDATE_OK = "/wowza/validate";

    String REQUEST_ACTION = "/request/action";

    String MESSAGE_ACTION = "/message/action";

    String RETRIEVE_PUBLISHER = "/publisher/retrieve";

    String RETRIEVE_SUBSCRIBER = "/subscriber/retrieve";

    String SUBSCRIBER_ACTION = "/subscriber/action";

    String ACTION_VALIDATE = "/action/validate";

    String ACTION_ACCEPT = "/action/accept";

    String ACTION_ACCEPT_OK = "/action/actionAccepted";

    String ACTION_START = "/action/start";

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

    String ACTION_START_OK = "/action/start/ok";

    String ACTION_TIMER = "/action/timer";

    String ACTION_COMPLETED = "/action/completed";

    String PUBLISHER_ACTION_COMPLETED = "/publisher/actionCompleted";

    String PUBLISHER_ABOUT_READY = "/publisher/ready";

    String SUBSCRIBER_BROWSER_HANDSHAKE = "/subscriber/handshake";

    String SUBSCRIBER_BROWSER_HANDSHAKE_OK = "/subscriber/handshake/OK";

    String BROADCASTER_CREATE = "/broadcaster/create";

    String BROADCASTER_TRACK = "/broadcaster/track";

    String BROADCASTER_DISPATCH = "/broadcaster/dispatch";
    /**
     * Broadcast a message to all chat room
     */
    String BROADCAST_TO_ALL = "/broadcaster/toAll";

    String MONITOR_RESOURCE = "/monitor/configure";

    String DISCONNECTED_RESOURCE = "/db/disconnected";

}