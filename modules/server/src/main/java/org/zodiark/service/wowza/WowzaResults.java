package org.zodiark.service.wowza;

import org.zodiark.service.Results;

public class WowzaResults implements Results {
    private String results;

    public WowzaResults() {}

    public WowzaResults(String results) {
        this.results = results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getResults() {
        return results;
    }
}
