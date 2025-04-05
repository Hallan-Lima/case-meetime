package com.meetime.hubspot_integration.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/hubspot")
public class ContactController {

    // URL da API do HubSpot para operações relacionadas a contatos.
    @Value("${hubspot.contacts-api-url}")
    private String CONTACTS_API_URL;

    /**
     * Endpoint para criar um contato no HubSpot.
     * Recebe os dados do contato no corpo da requisição e o token de acesso no cabeçalho.
     *
     * @param contactData Dados do contato a serem enviados para o HubSpot.
     * @param accessToken Token de acesso para autenticação na API do HubSpot.
     * @return ResponseEntity com o resultado da operação.
     */
    @PostMapping("/create-contact")
    public ResponseEntity<String> createContact(@RequestBody Map<String, Object> contactData, 
                                                @RequestHeader("Authorization") String accessToken) {
        // RestTemplate é utilizado para realizar a requisição HTTP.
        RestTemplate restTemplate = new RestTemplate();

        // Configuração dos cabeçalhos da requisição.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Define o tipo de conteúdo como JSON.
        headers.setBearerAuth(accessToken.replace("Bearer ", "")); // Remove "Bearer " se necessário.

        // Criação da entidade HTTP com os dados do contato e os cabeçalhos.
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(contactData, headers);

        try {
            // Envia a requisição POST para a API do HubSpot.
            ResponseEntity<String> response = restTemplate.exchange(CONTACTS_API_URL, HttpMethod.POST, requestEntity, String.class);

            // Captura os cabeçalhos relacionados ao rate limit da API.
            HttpHeaders responseHeaders = response.getHeaders();
            String remainingCalls = responseHeaders.getFirst("X-HubSpot-RateLimit-Remaining"); // Chamadas restantes.
            String resetTime = responseHeaders.getFirst("X-HubSpot-RateLimit-Reset"); // Tempo para reset do limite.

            // Verifica se o limite de chamadas está próximo do fim.
            if (remainingCalls != null && Integer.parseInt(remainingCalls) < 5) {
                // Calcula o tempo de espera com base no cabeçalho ou define um valor padrão.
                long waitTime = resetTime != null ? Long.parseLong(resetTime) * 1000 : 60000; // Em milissegundos.
                System.out.println("Atingindo o limite de chamadas. Aguardando " + (waitTime / 1000) + " segundos...");
                Thread.sleep(waitTime); // Aguarda o tempo necessário para evitar bloqueio.
            }

            // Retorna a resposta da API do HubSpot.
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (InterruptedException e) {
            // Trata interrupções na thread durante o tempo de espera.
            Thread.currentThread().interrupt(); // Restaura o estado de interrupção da thread.
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Aguardando rate limit...");
        } catch (Exception e) {
            // Trata erros gerais durante a execução da requisição.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar contato: " + e.getMessage());
        }
    }
}
