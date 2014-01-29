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

public class Protocol {

    private String version = "1.0";
    private String name = "zodiark";

    public Protocol() {
    }

    public Protocol(String version, String name){
        this.name = name;
        this.version = version;
    }

    public Protocol(String v){
        String[] p = v.split("/");
        if (p.length < 2) throw new IllegalStateException("Invalid Protocol");
        this.name = p[0];
        this.version = p[1];
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + "/" + version;
    }
}
