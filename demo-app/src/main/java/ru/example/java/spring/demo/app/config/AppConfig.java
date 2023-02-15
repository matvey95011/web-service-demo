package ru.example.java.spring.demo.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.example.java.spring.demo.app.service.ProfileService;
import ru.example.java.spring.demo.app.service.impl.ChangeBalanceService;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ChangeBalanceService scheduledJob(ProfileService profileService) {
        return new ChangeBalanceService(profileService);
    }

}
