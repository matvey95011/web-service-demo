package ru.example.java.spring.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.example.java.spring.demo.app.config.properties.AppConstants;
import ru.example.java.spring.demo.app.entity.User;
import ru.example.java.spring.demo.app.exception.EntityExistsApplicationException;
import ru.example.java.spring.demo.app.exception.EntityNotFoundApplicationException;
import ru.example.java.spring.demo.app.service.UserService;
import ru.example.java.spring.demo.app.repository.UserRepository;
import ru.example.java.spring.demo.model.UserRequest;
import ru.example.java.spring.demo.model.UserResponse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class
})
@Disabled("need to fix")
class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private ChangeBalanceService changeBalanceService;

    @Autowired
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final User USER = convertJsonToObject("json/User.json", User.class);
    private final UserResponse USER_RS = convertJsonToObject("json/UserResponse.json", UserResponse.class);
    private final List<User> USER_LIST = List.of(USER);
    private final UserRequest USER_RQ = convertJsonToObject("json/UserRequest.json", UserRequest.class);

    @Test
    @DisplayName("Получение списка всех пользователей")
    void findAll_success() {
        when(userRepository.findAll()).thenReturn(USER_LIST);

        List<UserResponse> allUsers = userService.findAll();

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(1, allUsers.size());

        assertThat(allUsers)
                .isNotEmpty()
                .containsOnly(USER_RS);
    }

    @Test
    @DisplayName("Полученный список пользователей - пустой")
    void findAll_empty_list() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserResponse> allUsers = userService.findAll();

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(0, allUsers.size());
        assertThat(allUsers).isEmpty();
    }

    @Test
    @DisplayName("Поиск User по id")
    void findById_success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(USER));

        UserResponse user = userService.findById(1L);

        assertThat(user)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("profile", "phones")
                .isEqualTo(USER);
    }

    @Test
    @DisplayName("User по id не найден")
    void findById_not_found_user_exception() {
        assertThatThrownBy(() -> {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
            userService.findById(1L);
        }).isInstanceOf(EntityNotFoundApplicationException.class)
                .hasMessageContaining(AppConstants.USER_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Создание нового User")
    void createUser_success() {
        when(userRepository.save(any())).thenReturn(USER);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserResponse user = userService.createUser(USER_RQ);

        assertThat(user)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("profile", "phones")
                .isEqualTo(USER);
    }

    @Test
    @DisplayName("Создание нового User с существующим email")
    void createUser_email_exist_exception() {
        assertThatThrownBy(() -> {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(USER));
            userService.createUser(USER_RQ);
        }).isInstanceOf(EntityExistsApplicationException.class)
                .hasMessageContaining(AppConstants.EMAIL_IS_EXIST_MSG);
    }

    @Test
    @DisplayName("Обновление User")
    void updateUser_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(USER));
        when(userRepository.save(any())).thenReturn(USER);

        UserResponse user = userService.updateUser(USER.getEmail(), USER_RQ);

        assertThat(user)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("profile", "phones")
                .isEqualTo(USER);
    }

    @Test
    @DisplayName("Обновление User - User не найден")
    void updateUser_not_found_exception() {
        assertThatThrownBy(() -> {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            userService.updateUser(USER.getEmail(), USER_RQ);
        }).isInstanceOf(EntityNotFoundApplicationException.class)
                .hasMessageContaining(AppConstants.USER_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Обновление User - email уже существует")
    void updateUser_email_exist_exception() {
        assertThatThrownBy(() -> {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(USER));
            when(userRepository.save(any())).thenReturn(Optional.ofNullable(USER));

            USER_RQ.setEmail("EXIST_EMAIL");
            userService.updateUser(USER.getEmail(), USER_RQ);
        }).isInstanceOf(EntityExistsApplicationException.class)
                .hasMessageContaining(AppConstants.EMAIL_IS_EXIST_MSG);
    }

    @Test
    @DisplayName("Удаление User по id")
    void deleteById_success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(USER));
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteById(1L);

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(userRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Удаление не существующего User")
    void deleteById_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        userService.deleteById(1L);

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(userRepository, times(0))
                .deleteById(anyLong());
    }

    private <T> T convertJsonToObject(String path, Class<T> tClass) {
        try {
            log.info("convertJsonToObject(), Path: {}, Class: {}", path, tClass);

            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

            return objectMapper.readValue(
                    new File(Objects.requireNonNull(resource).toURI()),
                    tClass
            );
        } catch (URISyntaxException | IOException e) {
            log.error("Fain to create Object");
            log.error(e.getMessage());
            return null;
        }
    }
}