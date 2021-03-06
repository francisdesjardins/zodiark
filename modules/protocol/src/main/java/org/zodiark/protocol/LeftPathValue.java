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

public enum LeftPathValue {

    REQUEST("/request"), RESPONSE("/response"), MESSAGE("/message"), REACT("/react"), OK("ok"), CUSTOM("custom");

    private String value;

    LeftPathValue(String value) {
        this.value = value.startsWith("/") ? value : "/" + value;
    }

    public static LeftPathValue deserialize(String v) {
        String value = v.length() > 1 ? v.split("/")[1] : v;
        return CUSTOM.value("/" + value);
    }

    final String value() {
        return this.value.toLowerCase();
    }

    final LeftPathValue value(String value) {
        this.value = value;
        return this;
    }
}
