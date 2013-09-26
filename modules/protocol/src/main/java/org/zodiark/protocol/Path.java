/*
 * Copyright 2013 High-Level Technologies
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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Path {

    private String path;
    @JsonIgnore
    private String left;
    @JsonIgnore
    private String right;

    public Path(LeftPathValue l, RightPathValue r) {
        this.path = l.value() + r.value();
    }

    public Path(){
    }

    public Path(String left, String right) {
        this.path = left + right;
    }

    public Path(String path) {
        left = LeftPathValue.deserialize(path).value();
        right = RightPathValue.deserialize(path).value();
        this.path = left + right;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public String left(){
        return left;
    }

    public String right(){
        return right();
    }
}
