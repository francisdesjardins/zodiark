package org.zodiark.service.subscriber;

import org.zodiark.service.Results;

public class SubscriberResults implements Results {

    private String results;
    private String uuid;

    public SubscriberResults() {}

    public SubscriberResults(String results) {
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
