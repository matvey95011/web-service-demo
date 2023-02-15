package ru.example.java.spring.demo.app.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import ru.example.java.spring.demo.model.PhoneCriteriaFilter;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.PhoneResponse;

import java.util.List;

public interface PhoneService {

    @NotNull
    List<PhoneResponse> findAll();

    @NotNull
    Page<PhoneResponse> findByCriteria(@NotNull PhoneCriteriaFilter filter);

    @NotNull
    PhoneResponse findById(@NotNull Long phoneId);

    @NotNull
    PhoneResponse createPhone(@NotNull Long userId, @NotNull PhoneRequest request);

    @NotNull
    PhoneResponse updatePhone(@NotNull String email, @NotNull Long phoneId, @NotNull PhoneRequest request);

    void deleteById(@NotNull Long phoneId);

}
