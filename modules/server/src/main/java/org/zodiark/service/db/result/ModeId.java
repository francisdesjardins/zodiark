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
package org.zodiark.service.db.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModeId {

    public enum MODE { VOID, PUBLIC, PRIVATE, SHAREDPRIVATE, VIEW }

    public final static ModeId VOID = new ModeId(0, MODE.VOID);
    public final static ModeId PUBLIC = new ModeId(1, MODE.PUBLIC);
    public final static ModeId PRIVATE = new ModeId(2, MODE.PRIVATE);
    public final static ModeId MIDPRIVATE = new ModeId(3, MODE.VIEW);
    public final static ModeId SHAREDPRIVATE = new ModeId(4, MODE.SHAREDPRIVATE);
    public final static ModeId AUDIOTEL = new ModeId(5, MODE.SHAREDPRIVATE);

    private int modeId;
    private MODE mode;

    public ModeId(int modeId) {
        this.modeId = modeId;
    }

    public ModeId(int modeId, MODE mode) {
        this.modeId = modeId;
    }

    @JsonProperty
    public ModeId modeId(int modeId) {
        this.modeId = modeId;
        return this;
    }

    public int modeId() {
        return modeId;
    }

    public MODE mode(){
        return mode;
    }

}
