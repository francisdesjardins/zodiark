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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zodiark.server.Context;
import org.zodiark.server.annotation.Inject;

import java.io.IOException;

public class Result {

    @JsonIgnore
    @Inject
    public Context context;

    @JsonIgnore
    @Inject
    public ObjectMapper mapper;

    private String result;
    private Data data;

    public Result() {
    }

    public Result(String result, Data data) {
        this.result = result;
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public Result setResult(String result) {
        this.result = result;
        return this;
    }

    public Data getData() {
        return data;
    }

    public Result setData(Data data) {
        this.data = data;
        return this;
    }

    public <T> T transform(Class<T> transform) {
        if (data.getContent() != null && !data.getContent().equals("null")) {
            try {
                return mapper.readerForUpdating(context.newInstance(transform)).readValue(data.getContent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return context.newInstance(transform);
        }
    }
}
