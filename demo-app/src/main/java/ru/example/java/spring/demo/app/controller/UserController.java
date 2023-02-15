package ru.example.java.spring.demo.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.example.java.spring.demo.app.mapper.UserMapper;
import ru.example.java.spring.demo.app.service.UserService;
import ru.example.java.spring.demo.api.UserApi;
import ru.example.java.spring.demo.model.PageUserResponse;
import ru.example.java.spring.demo.model.UserCriteriaFilter;
import ru.example.java.spring.demo.model.UserRequest;
import ru.example.java.spring.demo.model.UserResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<List<UserResponse>> findAllUser() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    public ResponseEntity<PageUserResponse> findAllUserByCriteria(UserCriteriaFilter filter) {
        var response = userService.findByCriteria(filter);
        return ResponseEntity.ok(UserMapper.INSTANCE.toPage(response));
    }

    @Override
    public ResponseEntity<UserResponse> findUserById(Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    public ResponseEntity<UserResponse> addUser(UserRequest user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UserRequest user) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(userService.updateUser(email, user));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteById(id);
        return null;
    }
}
