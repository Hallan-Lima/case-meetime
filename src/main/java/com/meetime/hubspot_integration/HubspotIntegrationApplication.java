package com.meetime.hubspot_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot.
 * Esta classe é responsável por inicializar e configurar o contexto da aplicação.
 */
@SpringBootApplication
public class HubspotIntegrationApplication {

    /**
     * Método principal (entry point) da aplicação.
     */
    public static void main(String[] args) {
        SpringApplication.run(HubspotIntegrationApplication.class, args);
    }
}
