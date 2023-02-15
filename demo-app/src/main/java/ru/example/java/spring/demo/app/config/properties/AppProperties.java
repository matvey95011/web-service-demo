package ru.example.java.spring.demo.app.config.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter
@Configuration
@EqualsAndHashCode
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "application")
public class AppProperties {

    private BigDecimal maxIncreaseCashPercent = new BigDecimal(2);
    private BigDecimal rateIncreaseCashPercent = new BigDecimal("1.1");

    @ConditionalOnProperty(prefix = "application", name = "max-increase-cash-percent")
    public void setMaxIncreaseCashPercent(Double maxIncreaseCashPercent) {
        this.maxIncreaseCashPercent = BigDecimal.valueOf((maxIncreaseCashPercent + 100) / 100);
    }

    @ConditionalOnProperty(prefix = "application", name = "rate-increase-cash-percent")
    public void setRateIncreaseCashPercent(Double rateIncreaseCashPercent) {
        this.rateIncreaseCashPercent = BigDecimal.valueOf(rateIncreaseCashPercent / 100 + 1);
    }

}
