package ru.example.java.spring.demo.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.example.java.spring.demo.app.mapper.ProfileMapper;
import ru.example.java.spring.demo.app.service.ProfileService;
import ru.example.java.spring.demo.api.ProfileApi;
import ru.example.java.spring.demo.model.PageProfileResponse;
import ru.example.java.spring.demo.model.ProfileCriteriaFilter;
import ru.example.java.spring.demo.model.ProfileRequest;
import ru.example.java.spring.demo.model.ProfileResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileController implements ProfileApi {

    private final ProfileService profileService;

    @Override
    public ResponseEntity<List<ProfileResponse>> findAllProfile() {
        return ResponseEntity.ok(profileService.findAll());
    }

    @Override
    public ResponseEntity<PageProfileResponse> findAllProfileByCriteria(ProfileCriteriaFilter filter) {
        var response = profileService.findByCriteria(filter);
        return ResponseEntity.ok(ProfileMapper.INSTANCE.toPage(response));
    }

    @Override
    public ResponseEntity<ProfileResponse> findProfileById(Long id) {
        return ResponseEntity.ok(profileService.findById(id));
    }

    @Override
    public ResponseEntity<ProfileResponse> addProfile(Long userId, ProfileRequest profile) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(profileService.createProfile(userId, profile));
    }

    @Override
    public ResponseEntity<ProfileResponse> updateProfile(ProfileRequest profile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(profileService.updateProfile(email, profile));
    }

    @Override
    public ResponseEntity<Void> deleteProfile(Long id) {
        profileService.deleteById(id);
        return null;
    }
}
