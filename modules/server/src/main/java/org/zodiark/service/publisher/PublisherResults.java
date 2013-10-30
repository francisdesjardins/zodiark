package org.zodiark.service.publisher;

import org.zodiark.service.Results;

public class PublisherResults implements Results {

    private String results;
    private String uuid;

    public PublisherResults() {}

    public PublisherResults(String results) {
        this.results = results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getResults() {
        return results;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
