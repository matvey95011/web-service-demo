package ru.example.java.spring.demo.app.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.example.java.spring.demo.app.config.properties.AppSecurityProperties;
import ru.example.java.spring.demo.model.AuthorizationRequest;
import ru.example.java.spring.demo.model.AuthorizationResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static ru.example.java.spring.demo.app.config.properties.AppConstants.DEFAULT_USER_PASSWORD;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AppSecurityProperties securityProperties;

    @SneakyThrows(IOException.class)
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("AuthenticationFilter.attemptAuthentication()");
        AuthorizationRequest authRequest = new ObjectMapper().readValue(request.getInputStream(), AuthorizationRequest.class);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), DEFAULT_USER_PASSWORD, new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("AuthenticationFilter.successfulAuthentication()");
        User user = (User) authResult.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC512(securityProperties.getSecretKey());

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + securityProperties.getExpirationTime()))
                .sign(algorithm);

        response.addHeader("token", token);

        AuthorizationResponse auth = new AuthorizationResponse()
                .email(user.getUsername())
                .token(token);
        new ObjectMapper().writeValue(response.getOutputStream(), auth);
    }
}
