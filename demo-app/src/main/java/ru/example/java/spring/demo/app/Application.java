package ru.example.java.spring.demo.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import ru.example.java.spring.demo.app.service.PhoneService;
import ru.example.java.spring.demo.app.service.ProfileService;
import ru.example.java.spring.demo.app.service.UserService;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.ProfileRequest;
import ru.example.java.spring.demo.model.UserRequest;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@SpringBootApplication
//@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile({"local"})
    public CommandLineRunner CommandLineRunnerBean(
            UserService userService, ProfileService profileService, PhoneService phoneService
    ) {
        return (args) -> {
            userService.createUser(createUserRequest("test", "test", 1));
            profileService.createProfile(1L, createProfileRequest(BigDecimal.valueOf(10000)));
            phoneService.createPhone(1L, createPhoneRequest("+7 (999) 888 77-66"));

            createUsers(20, userService);
            createProfiles(15, profileService);
            createPhones(20, phoneService);
        };
    }

    private PhoneRequest createPhoneRequest(String number) {
        var phone = new PhoneRequest();
        phone.setValue(number);
        return phone;
    }

    private ProfileRequest createProfileRequest(BigDecimal cash) {
        var profile = new ProfileRequest();
        profile.setCash(cash);
        return profile;
    }

    private UserRequest createUserRequest(String name, String email, Integer age) {
        var userRequest = new UserRequest();

        userRequest.setName(name);
        userRequest.setEmail(email);
        userRequest.setAge(age);

        return userRequest;
    }

    private void createProfiles(int count, ProfileService profile) {
        LongStream.rangeClosed(2, count)
                .forEach(it ->
                        profile.createProfile(it,
                                createProfileRequest(BigDecimal.valueOf(
                                        new Random().nextInt(1_000_000 - 100_000) + 100_000))));
    }

    private void createUsers(int count, UserService service) {
        IntStream.rangeClosed(0, count)
                .forEach(it -> service.createUser(createUserRequest(randomString(), randomString(), 42)));
    }

    private void createPhones(int count, PhoneService service) {
        LongStream.rangeClosed(1, count)
                .forEach(it -> {
                    for (int i = 0; i < 4; i++) {
                        service.createPhone(it, createPhoneRequest(randomPhoneNumber()));
                    }
                });
    }

    private String randomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String randomPhoneNumber() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 57; // letter 'z'
        int targetStringLength = 12;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 48 || i <= 57))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
