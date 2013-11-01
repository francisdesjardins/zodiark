package org.zodiark.service;

import org.zodiark.service.db.DBResult;

public interface AuthConfig extends DBResult {

    boolean isAuthenticated();

}
