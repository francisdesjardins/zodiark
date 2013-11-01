package org.zodiark.service.util.mock;

import org.zodiark.service.AuthConfig;

public class OKAuthConfig implements AuthConfig {
    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
