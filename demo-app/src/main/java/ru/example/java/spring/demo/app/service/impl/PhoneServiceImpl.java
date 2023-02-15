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
import ru.example.java.spring.demo.app.exception.IllegalArgumentApplicationException;
import ru.example.java.spring.demo.app.mapper.PhoneMapper;
import ru.example.java.spring.demo.app.repository.PhoneRepository;
import ru.example.java.spring.demo.app.repository.UserRepository;
import ru.example.java.spring.demo.app.repository.specification.PhoneSpecification;
import ru.example.java.spring.demo.app.service.PhoneService;
import ru.example.java.spring.demo.model.PhoneCriteriaFilter;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.PhoneResponse;

import java.util.List;
import java.util.stream.Collectors;

import static ru.example.java.spring.demo.app.config.properties.AppConstants.*;
import static ru.example.java.spring.demo.app.utils.CommonSort.getOrderSort;
import static ru.example.java.spring.demo.app.utils.PhoneValidator.isValid;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    private final List<String> DEFAULT_SORT = List.of("value|ASC", "id|DESC");
    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public @NotNull List<PhoneResponse> findAll() {
        return phoneRepository.findAll().stream()
                .map(PhoneMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull Page<PhoneResponse> findByCriteria(@NotNull PhoneCriteriaFilter filter) {
        var spec = new PhoneSpecification(filter);
        var page = PageRequest.of(
                filter.getPageNumber(),
                filter.getPageSize(),
                getOrderSort(filter.getSort(), DEFAULT_SORT));

        return phoneRepository.findAll(spec, page)
                .map(PhoneMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public @NotNull PhoneResponse findById(@NotNull Long phoneId) {
        return phoneRepository.findById(phoneId)
                .map(PhoneMapper.INSTANCE::toDto)
                .orElseThrow(() -> {
                    throw new EntityNotFoundApplicationException(PHONE_NOT_FOUND_MSG);
                });
    }

    @Override
    @Transactional
    public @NotNull PhoneResponse createPhone(@NotNull Long userId, @NotNull PhoneRequest request) {
        validation(request.getValue());
        var user = userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundApplicationException(USER_NOT_FOUND_MSG);
        });
        var newPhone = phoneRepository.save(PhoneMapper.INSTANCE.toEntity(request, user));
        return PhoneMapper.INSTANCE.toDto(newPhone);
    }

    @Override
    @Transactional
    public @NotNull PhoneResponse updatePhone(
            @NotNull String email,
            @NotNull Long phoneId,
            @NotNull PhoneRequest request
    ) {
        validation(request.getValue());

        var phone = phoneRepository.findById(phoneId).orElseThrow(() -> {
            throw new EntityNotFoundApplicationException(PHONE_NOT_FOUND_MSG);
        });

        if (!phone.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentApplicationException(ACCESS_DENIED_MSG);
        }

        phone.setValue(request.getValue());
        var savedPhone = phoneRepository.save(phone);
        return PhoneMapper.INSTANCE.toDto(savedPhone);
    }

    @Override
    @Transactional
    public void deleteById(@NotNull Long phoneId) {
        phoneRepository.findById(phoneId)
                .ifPresent(phoneRepository::delete);
    }

    private void validation(String value) {
        isValid(value);
        if (phoneRepository.existsPhoneByValue(value)) {
            throw new EntityExistsApplicationException(PHONE_IS_EXIST_MSG);
        }
    }
}
