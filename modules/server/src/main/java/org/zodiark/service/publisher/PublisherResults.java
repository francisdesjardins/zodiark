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
package org.zodiark.service.publisher;

import org.zodiark.service.Results;

/**
 * A Result simple JSON POJO for exchanging {@link org.zodiark.service.chat.Message} with {@link PublisherEndpoint}
 */
public class PublisherResults implements Results {

    private String results;
    private String uuid;

    public PublisherResults() {
    }

    public PublisherResults(String results) {
        this.results = results;
    }

    public PublisherResults(String results, String uuid) {
        this.results = results;
        this.uuid = uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResults(String results) {
        this.results = results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResults() {
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUuid() {
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
