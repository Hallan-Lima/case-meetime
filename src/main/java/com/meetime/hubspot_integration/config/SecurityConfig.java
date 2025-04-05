package com.meetime.hubspot_integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/hubspot/**").permitAll()
                .anyRequest().authenticated() // Protege outros endpoints
            )
            .csrf(csrf -> csrf.disable()) // Desativa CSRF
            .formLogin(form -> form.disable()) // Desativa a tela de login padrão
            .httpBasic(basic -> basic.disable()); // Desativa autenticação básica

        return http.build();
    }
}