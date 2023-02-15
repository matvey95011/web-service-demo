package ru.example.java.spring.demo.app.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import ru.example.java.spring.demo.model.UserCriteriaFilter;
import ru.example.java.spring.demo.model.UserRequest;
import ru.example.java.spring.demo.model.UserResponse;

import java.util.List;

public interface UserService {

    @NotNull
    List<UserResponse> findAll();

    @NotNull
    Page<UserResponse> findByCriteria(@NotNull UserCriteriaFilter filter);

    @NotNull
    UserResponse findById(@NotNull Long id);

    @NotNull
    UserResponse createUser(@NotNull UserRequest request);

    @NotNull
    UserResponse updateUser(@NotNull String email, @NotNull UserRequest request);

    void deleteById(@NotNull Long id);

}
