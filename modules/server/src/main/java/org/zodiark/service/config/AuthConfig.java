package org.zodiark.service.config;

import org.zodiark.service.db.DBResult;

public interface AuthConfig extends DBResult {

    boolean isAuthenticated();

}
