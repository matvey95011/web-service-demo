package ru.example.java.spring.demo.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import ru.example.java.spring.demo.app.entity.Profile;
import ru.example.java.spring.demo.app.entity.User;
import ru.example.java.spring.demo.model.PageProfileResponse;
import ru.example.java.spring.demo.model.ProfileRequest;
import ru.example.java.spring.demo.model.ProfileResponse;

@Mapper(uses = ProfileMapper.class)
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    ProfileResponse toDto(Profile profile);

    Profile toEntity(ProfileRequest request, User user);

    PageProfileResponse toPage(Page<ProfileResponse> page);
}
