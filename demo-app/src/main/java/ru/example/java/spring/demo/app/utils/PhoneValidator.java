package ru.example.java.spring.demo.app.utils;

import lombok.extern.slf4j.Slf4j;
import ru.example.java.spring.demo.app.config.properties.AppConstants;
import ru.example.java.spring.demo.app.entity.Phone;
import ru.example.java.spring.demo.app.exception.EntityNotFoundApplicationException;
import ru.example.java.spring.demo.app.exception.IllegalArgumentApplicationException;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class PhoneValidator {

    public static void isValid(String phoneNumber) {
        Pattern pattern = Pattern.compile(
                "^(\\+?\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{2}[- .]?\\d{2}$");

        if (!pattern.matcher(phoneNumber).matches()) {
            log.error("The specified phone number is invalid. phoneNumber = {}", phoneNumber);
            throw new IllegalArgumentApplicationException(AppConstants.PHONE_IS_NOT_VALID_MSG);
        }
        log.info("Phone number is valid. phoneNumber = {}", phoneNumber);
    }

    public static void userContainsPhone(List<Phone> phoneList, long phoneId) {
        if (phoneList == null || phoneList.isEmpty()) {
            log.error("User haven't phones");
            throw new EntityNotFoundApplicationException(AppConstants.PHONE_LIST_IS_EXIST_MSG);
        }

        phoneList.stream()
                .mapToLong(Phone::getId)
                .filter(it -> it == phoneId)
                .findAny()
                .ifPresent(__ -> {
                    log.error("The user is not allowed to edit the phone number");
                    throw new EntityNotFoundApplicationException(AppConstants.ACCESS_DENIED_MSG);
                });
    }

}
