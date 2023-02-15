package ru.example.java.spring.demo.app.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.example.java.spring.demo.app.config.properties.AppSecurityProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final String BEARER = "Bearer ";
    private final JWTVerifier verifier;

    public AuthorizationFilter(AuthenticationManager authenticationManager, AppSecurityProperties securityProperties) {
        super(authenticationManager);
        Algorithm algorithm = Algorithm.HMAC512(securityProperties.getSecretKey());
        this.verifier = JWT.require(algorithm).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (StringUtils.isEmpty(request.getHeader(AUTHORIZATION))) {
            chain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader.startsWith(BEARER)) {
            try {
                DecodedJWT decodedJWT = checkToken(authorizationHeader);
                authorization(decodedJWT);
            } catch (Exception e) {
                errorLogging(response, e);
            }
        }

        chain.doFilter(request, response);
    }

    private void authorization(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void errorLogging(HttpServletResponse response, Exception e) throws IOException {
        log.error("Error logging in : {}", e.getMessage());

        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    private DecodedJWT checkToken(String authorizationHeader) {
        String token = authorizationHeader.substring(BEARER.length());
        return verifier.verify(token);
    }
}

