package ru.example.java.spring.demo.app.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ru.example.java.spring.demo.api.PhoneApi;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.PhoneResponse;

import javax.validation.Valid;
import java.net.URI;

@FeignClient(name = "test", url = "localhost:8080")
public interface TestFeignClient extends PhoneApi {

    ResponseEntity<PhoneResponse> addPhone(
            URI baseUrl,
            @Parameter(name = "userId", description = "", required = true) @PathVariable("userId") Long userId,
            @Parameter(name = "phone", description = "") @Valid @org.springframework.cloud.openfeign.SpringQueryMap PhoneRequest phone
    );

}
