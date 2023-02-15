package ru.example.java.spring.demo.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import ru.example.java.spring.demo.app.service.ProfileService;

//@Service
@RequiredArgsConstructor
public class ChangeBalanceService {

    private final ProfileService profileService;

    @Scheduled(fixedDelayString = "${application.time-balance-change-in-milliseconds}")
    public void scheduleChangeBalance() {
        profileService.changeAllCash();
    }

}
