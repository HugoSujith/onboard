package com.hugo.metalbroker.security;

import com.hugo.metalbroker.exceptions.SessionCreationPolicyFailureException;
import com.hugo.metalbroker.service.implementation.BrokerUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final BrokerUserDetailsServiceImpl userDetailsService;

    public SecurityConfiguration(BrokerUserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/register", "/login").permitAll()
                        .anyRequest().authenticated()
                );
        http.httpBasic(Customizer.withDefaults());
        http.sessionManagement(session -> {
            try {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            } catch (Exception e) {
                throw new SessionCreationPolicyFailureException(e.getMessage());
            }
        });
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(this.passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 16, 1, 15625, 16);
    }
}
