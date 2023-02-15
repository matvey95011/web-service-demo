package ru.example.java.spring.demo.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.example.java.spring.demo.app.controller.TestFeignClient;
import ru.example.java.spring.demo.model.PhoneRequest;
import ru.example.java.spring.demo.model.PhoneResponse;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestFeignClient feignClient;
    private RestTemplate restTemplate;
//    private WebClient webClient;


    public void test() {
        ResponseEntity<PhoneResponse> phoneResponseResponseEntity = feignClient.addPhone(1L, new PhoneRequest());
    }

}
