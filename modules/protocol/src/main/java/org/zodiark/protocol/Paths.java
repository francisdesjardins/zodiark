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
package org.zodiark.protocol;

/**
 * REST path for firing event using an EventBus, sending messages, etc. The targetted Service is alwaus the first part of the path.
 */
public interface Paths {

    /**
     * An I/O event to join an existing Publisher streaming Session
     */
    String JOIN_SUBSCRIBER_STREAMING_SESSION = "/subscriber/joinStreamingSession";
    /**
     * An I/O event sent Create the SubscriberEndpoint's Session
     */
    String CREATE_SUBSCRIBER_SESSION = "/subscriber/createUserSession";
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
     * An I/O event. Send a request to Wowza asking for a Streaming Session Approval
     */
    String WOWZA_CONNECT = "/v1/wowza/connect";
    /**
     * A Message to Advise the WowzaService that its needs to start the obfuscation process
     */
    String WOWZA_OBFUSCATE = "/v1//wowza/obfuscate";
    /**
     * A Message to advise the WowzaService that its needs to start the deobfuscation process
     */
    String WOWZA_DEOBFUSCATE = "/v1//wowza/deobfuscate";
    /**
     * An I/O event received from a Wowxa endpoint when the obfuscation process is completed and the action can start.
     */
    String WOWZA_OBFUSCATE_OK = "/v1//wowza/obfuscate/ok";
    /**
     * An I/O event Received from a Wowxa endpoint when the deobfuscation process is completed and the normal behavior
     */
    String WOWZA_DEOBFUSCATE_OK = "/v1//wowza/deobfuscate/ok";
    /**
     * An I/O event to leave the current Streaming Session
     */
    String TERMINATE_SUBSCRIBER_STREAMING_SESSSION = "/subscriber/disconnect";
    /**
     * An I/O event from Wowza in response to a request for a validation request
     */
    String SERVER_VALIDATE_OK = "/v1//wowza/validate";
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

    String SERVICE_ACTION = "/v1/action";

    String SERVICE_PUBLISHER = "/v1/publisher";

    String SERVICE_MONITORING = "/v1/monitor";

    String SERVICE_STREAMING = "/v1/streaming";

    String SERVICE_SUBSCRIBER = "/v1/subscriber";

    String SERVICE_WOWZA = "/v1/wowza";


    // DB-Mapping
    /**
     * A message to retrieve the data from an Endpoint
     */
    String DB_PUBLISHER_CONFIG = "/v1/publisher/@guid/session/config";
    /**
     * A message to Initialize an Endpoint Session within the remote database/web service endpoint.
     */
    String DB_POST_PUBLISHER_SESSION_CREATE = "/v1/publisher/@guid/session/create";
    /**
     * A Message to retrieve the list of banned word for a Streaming Session Chat.
     */
    String DB_GET_WORD_PASSSTHROUGH = "/v1/publisher/@guid/motd";
    /**
     * a Message to validate the state of a Subscriber
     */
    String DB_SUBSCRIBER_VALIDATE_STATE = "/v1/subscriber/@guid/session/validate";
    /**
     * Start Show
     */
    String DB_PUBLISHER_SHOW_START = "/v1/publisher/@guid/show/start";
    /**
     * End Show
     */
    String DB_PUBLISHER_SHOW_END = "/v1/publisher/@guid/show/@showId/end";
    /**
     *
     */
    String DB_POST_SUBSCRIBER_JOIN_SESSION = "/v1/subscriber/@guid/watch/start";

    /**
     *
     */
    String DB_POST_SUBSCRIBER_CHARGE_START = "/v1/subscriber/@guid/watch/@id/charge";
    /**
     *
     */
    String DB_POST_SUBSCRIBER_CHARGE_END = "/v1/subscriber/@guid/watch/@id/end";
    /**
     *
     */
    String DB_POST_PUBLISHER_ONDEMAND_START = "/v1/publisher/@guid/ondemand/start";

    String DB_POST_PUBLISHER_ONDEMAND_KEEPALIVE = "/v1/publisher/@guid/ondemand/keepalive";

    String DB_POST_PUBLISHER_ONDEMAND_END = "/v1/publisher/@guid/ondemand/end";
    /**
     * First this to send to the Publisher as soon as the subscriber join.
     */
    String DB_GET_SUBSCRIBER_STATUS_TO_PUBLISHER_PASSTHROUGHT = "/v1/subscriber/@guid/profile";

    String DB_PUBLISHER_SHARED_PRIVATE_START = "/v1/publisher/@guid/shared/start";
    String DB_PUBLISHER_SHARED_PRIVATE_START_POST = "/v1/publisher/@guid/shared/start";

    String DB_PUBLISHER_SHARED_PRIVATE_END = "/v1/publisher/@guid/shared/end";

    String DB_SUBSCRIBER_AVAILABLE_ACTIONS_PASSTHROUGHT = "/v1/subscriber/@guid/actions";

    String DB_PUBLISHER_AVAILABLE_ACTIONS_PASSTHROUGHT = "/v1/publisher/@guid/settings/actions";
    String DB_SUBSCRIBER_REQUEST_ACTION = "/v1/subscriber/@guid/action/transaction/request";
    String DB_SUBSCRIBER_JOIN_ACTION = "/v1/subscriber/@guid/action/transaction/@id/join";
    String DB_SUBSCRIBER_CHARGE_ACTION = "/v1/subscriber/@guid/action/transaction/@id/charge";
    String DB_SUBSCRIBER_BLOCK = "/v1/publisher/@guid/subscriber/@guid/block";
    String DB_SUBSCRIBER_EJECT = "/v1/publisher/@guid/subscriber/@guid/eject";
    String DB_SUBSCRIBER_END = "/v1/subscriber/@guid/favorite/@id/end";

    // TODO: LoadConfig
    String DB_PUBLISHER_LOAD_CONFIG_PASSTHROUGHT = "/v1/publisher/@guid/settings/ui/";

    String DB_PUBLISHER_SAVE_CONFIG = "/v1/publisher/@guid/settings/ui";
    String DB_PUBLISHER_SAVE_CONFIG_PUT = "_put/v1/publisher/@guid/settings/ui";

    String DB_PUBLISHER_LOAD_CONFIG = "/v1/publisher/@guid/settings/ui";
    String DB_PUBLISHER_LOAD_CONFIG_GET = "_get/v1/publisher/@guid/settings/ui";


    String DB_PUBLISHER_LOAD_CONFIG_ERROR_PASSTHROUGHT = "/v1/publisher/@guid/settings/errors";
    String DB_PUBLISHER_CONFIG_SHOW_AVAILABLE_PASSTHROUGHT = "/v1/publisher/@guid/settings/show";
    String DB_PUBLISHER_SAVE_CONFIG_SHOW = "/v1/publisher/@guid/settings/show/@showTypeId";
    String DB_PUBLISHER_PUBLIC_MODE = "/v1/publisher/@guid/settings/public/start";
    String DB_PUBLISHER_PUBLIC_MODE_END = "/v1/publisher/@guid/settings/public/end";
    String DB_PUBLISHER_ERROR_REPORT = "/v1/publisher/@guid/error/report";
    String DB_PUBLISHER_SUBSCRIBER_PROFILE = "/v1/publisher/@guid/subscriber/@guid/profile";
    String DB_PUBLISHER_SUBSCRIBER_PROFILE_GET = "/v1/publisher/@guid/subscriber/@guid/profile";
    String DB_PUBLISHER_SUBSCRIBER_PROFILE_PUT = "/v1/publisher/@guid/subscriber/@guid/profile";


    String DB_PUBLISHER_ACTIONS = "/v1/publisher/@guid/actions";
    String DB_SUBSCRIBER_EXTRA = "/v1/subscriber/@guid/tip/transaction/request";
    String DB_ENDPOINT_STATE = "/v1/zodiark/session/@guid";

    String DB_SUBSCRIBER_CONFIG_PASSTHROUGHT = "/v1/subscriber/@guid/settings/ui";
}