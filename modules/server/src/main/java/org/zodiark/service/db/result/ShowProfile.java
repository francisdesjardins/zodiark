/*
 * Copyright 2013 Jeanfrancois Arcand
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

public class ShowProfile {
    private int cameraWidth;
    private int cameraHeight;
    private int cameraFPS;
    private int cameraQuality;
    private int bandwidthOut;
    private int bandwidthIn;

    public int getCameraWidth() {
        return cameraWidth;
    }

    public void setCameraWidth(int cameraWidth) {
        this.cameraWidth = cameraWidth;
    }

    public int getCameraHeight() {
        return cameraHeight;
    }

    public void setCameraHeight(int cameraHeight) {
        this.cameraHeight = cameraHeight;
    }

    public int getCameraFPS() {
        return cameraFPS;
    }

    public void setCameraFPS(int cameraFPS) {
        this.cameraFPS = cameraFPS;
    }

    public int getCameraQuality() {
        return cameraQuality;
    }

    public void setCameraQuality(int cameraQuality) {
        this.cameraQuality = cameraQuality;
    }

    public int getBandwidthOut() {
        return bandwidthOut;
    }

    public void setBandwidthOut(int bandwidthOut) {
        this.bandwidthOut = bandwidthOut;
    }

    public int getBandwidthIn() {
        return bandwidthIn;
    }

    public void setBandwidthIn(int bandwidthIn) {
        this.bandwidthIn = bandwidthIn;
    }
}
