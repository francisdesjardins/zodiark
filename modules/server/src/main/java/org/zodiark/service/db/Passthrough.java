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
package org.zodiark.service.db;

public class Passthrough implements Result {
    private String passthrough = "";
    private Exception exception;

    public Passthrough() {}

    public Passthrough response(String passthrough) {
        this.passthrough = passthrough;
        return this;
    }

    public String response() {
        return passthrough;
    }

    public Exception exception(){
        return exception;
    }

    public Passthrough exception(Exception exception) {
        this.exception = exception;
        return this;
    }
}
