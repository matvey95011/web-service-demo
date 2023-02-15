package ru.example.java.spring.demo.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.java.spring.demo.app.exception.EntityExistsApplicationException;
import ru.example.java.spring.demo.app.exception.EntityNotFoundApplicationException;
import ru.example.java.spring.demo.app.entity.User;
import ru.example.java.spring.demo.app.mapper.UserMapper;
import ru.example.java.spring.demo.app.repository.UserRepository;
import ru.example.java.spring.demo.app.repository.specification.UserSpecification;
import ru.example.java.spring.demo.app.service.UserService;
import ru.example.java.spring.demo.model.UserCriteriaFilter;
import ru.example.java.spring.demo.model.UserRequest;
import ru.example.java.spring.demo.model.UserResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.example.java.spring.demo.app.config.properties.AppConstants.*;
import static ru.example.java.spring.demo.app.utils.CommonSort.getOrderSort;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final List<String> DEFAULT_SORT = List.of("email|ASC", "id|DESC");
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username).orElseThrow(() -> {
            throw new UsernameNotFoundException(username);
        });

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), passwordEncoder.encode(DEFAULT_USER_PASSWORD), Collections.emptyList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull Page<UserResponse> findByCriteria(@NotNull UserCriteriaFilter filter) {
        var spec = new UserSpecification(filter);
        var page = PageRequest.of(
                filter.getPageNumber(),
                filter.getPageSize(),
                getOrderSort(filter.getSort(), DEFAULT_SORT));

        return userRepository.findAll(spec, page)
                .map(UserMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull UserResponse findById(@NotNull Long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toDto)
                .orElseThrow(() -> {
                    throw new EntityNotFoundApplicationException(USER_NOT_FOUND_MSG);
                });
    }

    @Override
    @Transactional
    public @NotNull UserResponse createUser(@NotNull UserRequest request) {
        checkEmail(request.getEmail());
        var newUser = userRepository.save(UserMapper.INSTANCE.toEntity(request));
        return UserMapper.INSTANCE.toDto(newUser);
    }

    @Override
    @Transactional
    public @NotNull UserResponse updateUser(@NotNull String email, @NotNull UserRequest request) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new EntityNotFoundApplicationException(USER_NOT_FOUND_MSG);
        });
        var updatedUser = userRepository.save(update(user, request));
        return UserMapper.INSTANCE.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(@NotNull Long id) {
        userRepository.findById(id)
                .ifPresent(userRepository::delete);
    }

    private @NotNull User update(@NotNull User user, @NotNull UserRequest request) {
        if (!user.getEmail().equals(request.getEmail())) {
            checkEmail(request.getEmail());
            user.setEmail(request.getEmail());
        }
        user.setName(request.getName());
        user.setAge(request.getAge());
        return user;
    }

    private void checkEmail(String email) {
        userRepository.findByEmail(email).ifPresent(__ -> {
            throw new EntityExistsApplicationException(EMAIL_IS_EXIST_MSG);
        });
    }
}
