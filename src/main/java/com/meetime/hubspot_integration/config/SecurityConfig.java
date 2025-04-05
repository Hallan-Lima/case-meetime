package com.meetime.hubspot_integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Configura a cadeia de filtros de segurança para a aplicação.
     * Utiliza o HttpSecurity para definir as regras de autorização, desativar proteções padrão
     * e ajustar o comportamento de autenticação.
     *
     * @param http objeto HttpSecurity para configurar a segurança HTTP.
     * @return uma instância de SecurityFilterChain configurada.
     * @throws Exception caso ocorra algum erro na configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configura as regras de autorização para as requisições HTTP.
            .authorizeHttpRequests(auth -> auth
                // Permite acesso irrestrito aos endpoints que começam com "/auth/" e "/hubspot/".
                .requestMatchers("/auth/**", "/hubspot/**").permitAll()
                // Exige autenticação para qualquer outra requisição.
                .anyRequest().authenticated()
            )

            // Desativa a proteção contra CSRF (Cross-Site Request Forgery).
            .csrf(csrf -> csrf.disable())

            // Desativa o formulário de login padrão do Spring Security.
            .formLogin(form -> form.disable())

            // Desativa a autenticação HTTP Basic.
            .httpBasic(basic -> basic.disable());

        // Retorna a cadeia de filtros configurada.
        return http.build();
    }
}