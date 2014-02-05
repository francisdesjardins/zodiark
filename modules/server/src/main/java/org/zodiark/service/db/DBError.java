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

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBError {
    public enum TYPE {UNEXPECTED, CONNECTION_ERROR, DB_ERROR}

    private TYPE type;
    private String error;
    private Exception exception;

    public DBError() {
        type = TYPE.UNEXPECTED;
    }

    public TYPE type() {
        return type;
    }

    public DBError type(TYPE type) {
        this.type = type;
        return this;
    }

    @JsonProperty("error")
    public DBError error(String error) {
        this.error = error;
        return this;
    }

    public String error() {
        return error;
    }

    public DBError exception(Exception exception) {
        this.exception = exception;
        return this;
    }

    public Exception exception() {
        return exception;
    }

}
