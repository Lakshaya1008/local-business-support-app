package com.example.localbusiness.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CashfreeConfig {
    
    @Value("${cashfree.app-id}")
    private String appId;
    
    @Value("${cashfree.secret-key}")
    private String secretKey;
    
    @Value("${cashfree.environment}")
    private String environment;
    
    @Value("${cashfree.webhook-secret}")
    private String webhookSecret;

    public String getAppId() {
        return appId;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public String getWebhookSecret() {
        return webhookSecret;
    }
    
    public boolean isProduction() {
        return "production".equalsIgnoreCase(environment);
    }
} 