package org.zodiark.service.publisher;

import org.zodiark.service.Results;

public class PublisherResults implements Results {

    private String results;

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
}
