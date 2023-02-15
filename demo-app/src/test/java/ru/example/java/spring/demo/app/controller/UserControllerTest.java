package ru.example.java.spring.demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.java.spring.demo.app.exception.EntityExistsApplicationException;
import ru.example.java.spring.demo.app.exception.EntityNotFoundApplicationException;
import ru.example.java.spring.demo.app.service.UserService;
import ru.example.java.spring.demo.model.UserResponse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = EnableWebSecurity.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserResponse USER_FULL_RS = convertJsonToObject("json/UserResponse.json", UserResponse.class);
    private final List<UserResponse> USER_FULL_RS_LIST = List.of(USER_FULL_RS);
    private final String USER_FULL_RS_JSON = convertJsonToString("json/UserResponse.json");
    private final String USER_RQ_JSON = convertJsonToString("json/UserRequest.json");

    private final String PREFIX = "/api/v1/user";
    private final String GET_ALL_USER_URL = PREFIX + "/find/all";
    private final String GET_USER_BY_ID_URL = PREFIX + "/1";
    private final String POST_CREATE_USER_URL = PREFIX + "/create";
    private final String PATCH_UPDATE_USER_URL = PREFIX + "/update";
    private final String DELETE_USER_BY_ID_URL = PREFIX + "/1";

    @Test
    @DisplayName("Получение всех User")
    void getAllUsers_success() throws Exception {
        when(userService.findAll()).thenReturn(USER_FULL_RS_LIST);

        String userListAsString = objectMapper.writeValueAsString(USER_FULL_RS_LIST);

        mockMvc.perform(get(GET_ALL_USER_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(userListAsString));
    }

    @Test
    @DisplayName("Получение всех User. Список User - пуст")
    void getAllUsers_list_is_empty() throws Exception {
        when(userService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(GET_ALL_USER_URL))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("Получение User по id")
    void getUserById_success_200() throws Exception {
        when(userService.findById(anyLong())).thenReturn(USER_FULL_RS);

        mockMvc.perform(get(GET_USER_BY_ID_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(USER_FULL_RS_JSON));
    }

    @Test
    @DisplayName("Получение User по id. User не найден")
    void getUserById_not_found_404() throws Exception {
        when(userService.findById(anyLong())).thenThrow(EntityNotFoundApplicationException.class);

        mockMvc.perform(get(GET_USER_BY_ID_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Создание User")
    void createUser_success_201() throws Exception {
        when(userService.createUser(any())).thenReturn(USER_FULL_RS);

        mockMvc.perform(post(POST_CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_RQ_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Создание существующего User")
    void createUser_is_exist_409() throws Exception {
        when(userService.createUser(any())).thenThrow(EntityExistsApplicationException.class);

        mockMvc.perform(post(POST_CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_RQ_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Создание не валидного User")
    void createUser_not_valid_400() throws Exception {
        when(userService.createUser(any())).thenThrow(EntityExistsApplicationException.class);

        mockMvc.perform(post(POST_CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Обновление User")
    void updateUser_success_200() throws Exception {
        mockSecurityContext();
        when(userService.updateUser(anyString(), any())).thenReturn(USER_FULL_RS);

        mockMvc.perform(patch(PATCH_UPDATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_RQ_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(USER_FULL_RS_JSON));
    }

    @Test
    @DisplayName("Обновление User. Данные не валидны")
    void updateUser_not_valid_400() throws Exception {
        mockMvc.perform(patch(PATCH_UPDATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление User. Обновляемый User не найден")
    void updateUser_not_found_404() throws Exception {
        mockSecurityContext();
        when(userService.updateUser(anyString(), any())).thenThrow(EntityNotFoundApplicationException.class);

        mockMvc.perform(patch(PATCH_UPDATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_RQ_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Обновление User. User с новым email - существует")
    void updateUser_not_found_409() throws Exception {
        mockSecurityContext();
        when(userService.updateUser(anyString(), any())).thenThrow(EntityExistsApplicationException.class);

        mockMvc.perform(patch(PATCH_UPDATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_RQ_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Удаление User по id")
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteById(anyLong());

        mockMvc.perform(delete(DELETE_USER_BY_ID_URL))
                .andExpect(status().isOk());
    }

    private void mockSecurityContext() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Object principal = Mockito.mock(Object.class);

        SecurityContextHolder.setContext(context);

        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(principal.toString()).thenReturn("email");
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
            log.error("Fail to create Object");
            log.error(e.getMessage());
            return null;
        }
    }

    private String convertJsonToString(String path) {
        try {
            log.info("convertJsonToString(), Path: {}", path);

            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            return new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).toURI())));
        } catch (IOException | URISyntaxException e) {
            log.error("Fail to create Object");
            log.error(e.getMessage());
            return null;
        }
    }

}