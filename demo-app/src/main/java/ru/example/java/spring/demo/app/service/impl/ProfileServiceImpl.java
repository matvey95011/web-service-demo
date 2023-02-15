package ru.example.java.spring.demo.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.java.spring.demo.app.exception.EntityExistsApplicationException;
import ru.example.java.spring.demo.app.exception.EntityNotFoundApplicationException;
import ru.example.java.spring.demo.app.config.properties.AppProperties;
import ru.example.java.spring.demo.app.mapper.ProfileMapper;
import ru.example.java.spring.demo.app.repository.ProfileRepository;
import ru.example.java.spring.demo.app.repository.UserRepository;
import ru.example.java.spring.demo.app.repository.specification.ProfileSpecification;
import ru.example.java.spring.demo.app.service.ProfileService;
import ru.example.java.spring.demo.model.ProfileCriteriaFilter;
import ru.example.java.spring.demo.model.ProfileRequest;
import ru.example.java.spring.demo.model.ProfileResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ru.example.java.spring.demo.app.config.properties.AppConstants.*;
import static ru.example.java.spring.demo.app.utils.CommonSort.getOrderSort;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final List<String> DEFAULT_SORT = List.of("cash|ASC", "id|DESC");
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    @Override
    @Transactional(readOnly = true)
    public @NotNull List<ProfileResponse> findAll() {
        return profileRepository.findAll().stream()
                .map(ProfileMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull Page<ProfileResponse> findByCriteria(@NotNull ProfileCriteriaFilter filter) {
        var spec = new ProfileSpecification(filter);
        var page = PageRequest.of(
                filter.getPageNumber(),
                filter.getPageSize(),
                getOrderSort(filter.getSort(), DEFAULT_SORT));

        return profileRepository.findAll(spec, page)
                .map(ProfileMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull ProfileResponse findById(@NotNull Long id) {
        return profileRepository.findById(id)
                .map(ProfileMapper.INSTANCE::toDto)
                .orElseThrow(() -> {
                    throw new EntityNotFoundApplicationException(PROFILE_NOT_FOUND_MSG);
                });
    }

    @Override
    @Transactional
    public @NotNull ProfileResponse createProfile(@NotNull Long userId, @NotNull ProfileRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundApplicationException(USER_NOT_FOUND_MSG);
        });

        if (user.getProfile() != null) {
            throw new EntityExistsApplicationException(PROFILE_IS_EXIST_MSG);
        }

        var profile = ProfileMapper.INSTANCE.toEntity(request, user);
        profile.setMaxCash(profile.getCash().multiply(appProperties.getMaxIncreaseCashPercent()));

        var newProfile = profileRepository.save(profile);
        return ProfileMapper.INSTANCE.toDto(newProfile);
    }

    @Override
    @Transactional
    public @NotNull ProfileResponse updateProfile(@NotNull String email, @NotNull ProfileRequest request) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new EntityNotFoundApplicationException(USER_NOT_FOUND_MSG);
        });

        if (user.getProfile() == null) {
            throw new EntityNotFoundApplicationException(PROFILE_NOT_FOUND_MSG);
        }

        var profile = user.getProfile();
        profile.setCash(request.getCash());
        profile.setMaxCash(request.getCash().multiply(appProperties.getMaxIncreaseCashPercent()));

        return ProfileMapper.INSTANCE.toDto(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public void deleteById(@NotNull Long profileId) {
        profileRepository.findById(profileId)
                .ifPresent(profileRepository::delete);
    }

    @Override
    @Transactional
    public void changeAllCash() {
        var updatedCash = profileRepository.findAll().stream()
                .filter(it -> it.getCash().compareTo(it.getMaxCash()) <= 0)
                .peek(it -> {
                    BigDecimal tempCash = it.getCash().multiply(appProperties.getRateIncreaseCashPercent());

                    if (tempCash.compareTo(it.getMaxCash()) >= 0) {
                        it.setCash(it.getMaxCash());
                    } else {
                        it.setCash(tempCash);
                    }
                })
                .collect(Collectors.toList());
        profileRepository.saveAll(updatedCash);
    }

}
