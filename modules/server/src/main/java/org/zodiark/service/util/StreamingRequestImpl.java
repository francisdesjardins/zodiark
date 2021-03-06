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
package org.zodiark.service.util;

import org.zodiark.service.session.StreamingRequest;

/**
 * Simple implementation of a {@link StreamingRequest}
 */
public class StreamingRequestImpl implements StreamingRequest {

    private String publisherUUID;
    private String wowzaUUID;

    public StreamingRequestImpl(){}

    public StreamingRequestImpl(String publisherUUID, String wowzaUUID) {
        this.wowzaUUID = wowzaUUID;
        this.publisherUUID = publisherUUID;
    }

    public void setPublisherUUID(String publisherUUID) {
        this.publisherUUID = publisherUUID;
    }

    public void setWowzaUUID(String wowzaUUID) {
        this.wowzaUUID = wowzaUUID;
    }

    @Override
    public String getPublisherUUID() {
        return publisherUUID;
    }

    @Override
    public String getWowzaUUID() {
        return wowzaUUID;
    }
}
