package com.todo.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("cloudinary")
public class CloudinaryConfig {
    /** The Constant CLOUD_NAME. */
    private String cloudName;

    /** The Constant API_KEY. */
    private String apiKey;

    /** The Constant API_SECRET. */
    private String apiSecret;

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public CloudinaryConfig(String cloudName, String apiKey, String apiSecret) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public CloudinaryConfig() {
    }
}
