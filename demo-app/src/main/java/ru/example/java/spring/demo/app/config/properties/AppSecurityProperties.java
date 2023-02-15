package ru.example.java.spring.demo.app.config.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Getter
@Configuration
@EqualsAndHashCode
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "application.security")
public class AppSecurityProperties implements Serializable {
    private final static long serialVersionUID = 1L;

    private String secretKey = "very-secret-key";
    private Long expirationTime = 3_600_000L * 24;

    @ConditionalOnProperty(prefix = "application.security", name = "secret-key")
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @ConditionalOnProperty(prefix = "application.security", name = "expiration-time")
    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

}
