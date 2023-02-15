package ru.example.java.spring.demo.app.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import ru.example.java.spring.demo.model.ProfileCriteriaFilter;
import ru.example.java.spring.demo.model.ProfileRequest;
import ru.example.java.spring.demo.model.ProfileResponse;

import java.util.List;

public interface ProfileService {

    @NotNull
    List<ProfileResponse> findAll();

    @NotNull
    Page<ProfileResponse> findByCriteria(@NotNull ProfileCriteriaFilter filter);

    @NotNull
    ProfileResponse findById(@NotNull Long id);

    @NotNull
    ProfileResponse createProfile(@NotNull Long userId, @NotNull ProfileRequest request);

    @NotNull
    ProfileResponse updateProfile(@NotNull String email, @NotNull ProfileRequest request);

    void deleteById(@NotNull Long profileId);

    void changeAllCash();

}
