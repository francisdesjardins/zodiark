/*
 * Copyright 2014 Jeanfrancois Arcand
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
package org.zodiark.service.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.zodiark.server.annotation.Inject;

import java.io.IOException;

public class RestResult {
    
    @Inject
    public ObjectMapper mapper;

    private String status;
    private RestMessage message;

    public RestResult(String status, RestMessage message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RestMessage getMessage() {
        return message;
    }

    public void setMessage(RestMessage message) {
        this.message = message;
    }
    
    public <T> T transform(Class<T> transform) {
        try {
            return mapper.readValue(message.getJson(), transform);
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }
    }   
}
