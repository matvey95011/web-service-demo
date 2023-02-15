package ru.example.java.spring.demo.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.example.java.spring.demo.app.mapper.PhoneMapper;
import ru.example.java.spring.demo.app.service.PhoneService;
import ru.example.java.spring.demo.api.PhoneApi;
import ru.example.java.spring.demo.model.PagePhoneResponse;
import ru.example.java.spring.demo.model.PhoneCriteriaFilter;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.PhoneResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PhoneController implements PhoneApi {

    private final PhoneService phoneService;

    @Override
    public ResponseEntity<List<PhoneResponse>> findAllPhone() {
        return ResponseEntity.ok(phoneService.findAll());
    }

    @Override
    public ResponseEntity<PagePhoneResponse> findAllPhoneByCriteria(PhoneCriteriaFilter filter) {
        var response = phoneService.findByCriteria(filter);
        return ResponseEntity.ok(PhoneMapper.INSTANCE.toPage(response));
    }

    @Override
    public ResponseEntity<PhoneResponse> findPhoneById(Long id) {
        return ResponseEntity.ok(phoneService.findById(id));
    }

    @Override
    public ResponseEntity<PhoneResponse> addPhone(Long userId, PhoneRequest phone) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(phoneService.createPhone(userId, phone));
    }

    @Override
    public ResponseEntity<PhoneResponse> updatePhone(Long phoneId, PhoneRequest phone) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(phoneService.updatePhone(email, phoneId, phone));
    }

    @Override
    public ResponseEntity<Void> deletePhone(Long id) {
        phoneService.deleteById(id);
        return null;
    }
}
