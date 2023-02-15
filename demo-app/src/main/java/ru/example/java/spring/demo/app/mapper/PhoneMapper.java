package ru.example.java.spring.demo.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import ru.example.java.spring.demo.app.entity.Phone;
import ru.example.java.spring.demo.app.entity.User;
import ru.example.java.spring.demo.model.PagePhoneResponse;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.PhoneResponse;

@Mapper(uses = PhoneMapper.class)
public interface PhoneMapper {

    PhoneMapper INSTANCE = Mappers.getMapper(PhoneMapper.class);

    PhoneResponse toDto(Phone phone);

    Phone toEntity(PhoneRequest request, User user);

    PagePhoneResponse toPage(Page<PhoneResponse> page);
}
