package racegrid.api.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="racegrid")
public class RacegridProps {
    private boolean skipAuth;

    public boolean isSkipAuth() {
        return skipAuth;
    }

    public void setSkipAuth(boolean skipAuth) {
        this.skipAuth = skipAuth;
    }
}
