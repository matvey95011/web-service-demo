package ru.example.java.spring.demo.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import ru.example.java.spring.demo.app.entity.User;
import ru.example.java.spring.demo.model.PageUserResponse;
import ru.example.java.spring.demo.model.UserRequest;
import ru.example.java.spring.demo.model.UserResponse;

@Mapper(uses = UserMapper.class)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toDto(User user);

    User toEntity(UserRequest request);

    PageUserResponse toPage(Page<UserResponse> page);
}
