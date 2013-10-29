package org.zodiark.service.util.mock;

import org.zodiark.service.db.AuthConfig;

public class OKAuthConfig implements AuthConfig {
    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
