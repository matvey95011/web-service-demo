package ru.example.java.spring.demo.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.example.java.spring.demo.app.config.properties.AppSecurityProperties;
import ru.example.java.spring.demo.app.filter.AuthenticationFilter;
import ru.example.java.spring.demo.app.filter.AuthorizationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AppSecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        settingAuthorizeRequests(http);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilter(new AuthorizationFilter(authenticationManager(), securityProperties))
                .addFilter(new AuthenticationFilter(authenticationManager(), securityProperties));

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private void settingAuthorizeRequests(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(HttpMethod.PATCH)
                .authenticated();
    }
}
