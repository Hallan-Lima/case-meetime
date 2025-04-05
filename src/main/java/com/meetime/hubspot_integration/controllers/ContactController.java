package com.meetime.hubspot_integration.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/hubspot")
public class ContactController {

    private static final String CONTACTS_API_URL = "https://api.hubapi.com/crm/v3/objects/contacts";

    @PostMapping("/create-contact")
    public ResponseEntity<String> createContact(@RequestBody Map<String, Object> contactData, 
                                                @RequestHeader("Authorization") String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Configuração do Header com o token de acesso
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken.replace("Bearer ", "")); // Se necessário, remover "Bearer "

        // Criando a requisição
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(contactData, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(CONTACTS_API_URL, HttpMethod.POST, requestEntity, String.class);

            // Capturando os headers de rate limit
            HttpHeaders responseHeaders = response.getHeaders();
            String remainingCalls = responseHeaders.getFirst("X-HubSpot-RateLimit-Remaining");
            String resetTime = responseHeaders.getFirst("X-HubSpot-RateLimit-Reset");

            // Se o limite estiver próximo do fim, esperar o tempo necessário
            if (remainingCalls != null && Integer.parseInt(remainingCalls) < 5) {
                long waitTime = resetTime != null ? Long.parseLong(resetTime) * 1000 : 60000; // Espera em milissegundos
                System.out.println("Atingindo o limite de chamadas. Aguardando " + (waitTime / 1000) + " segundos...");
                Thread.sleep(waitTime);
            }

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Aguardando rate limit...");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar contato: " + e.getMessage());
        }
    }
}
