package com.meetime.hubspot_integration.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hubspot/webhook")
public class WebhookController {

    /**
     * Endpoint para receber webhooks relacionados a contatos do HubSpot.
     * Este método processa eventos enviados pelo HubSpot, como a criação de contatos.
     *
     * @param events Lista de eventos enviados pelo HubSpot no corpo da requisição.
     *               Cada evento é representado como um mapa de chave-valor.
     * @return ResponseEntity indicando o sucesso ou falha no processamento do webhook.
     */
    @PostMapping("/contact")
    public ResponseEntity<String> receiveContactWebhook(@RequestBody List<Map<String, Object>> events) {
        try {
            // Itera sobre a lista de eventos recebidos.
            for (Map<String, Object> event : events) {
                // Extrai o tipo do evento do mapa.
                String eventType = (String) event.get("eventType");

                // Verifica se o evento é do tipo "contact.creation".
                if ("contact.creation".equals(eventType)) {
                    System.out.println("Novo contato criado: " + event);
                }
            }
            // Retorna uma resposta de sucesso após processar todos os eventos.
            return ResponseEntity.ok("Webhook processado com sucesso!");
        } catch (Exception e) {
            // Trata exceções que possam ocorrer durante o processamento.
            // Retorna uma resposta de erro com o status HTTP 400 (Bad Request).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar webhook: " + e.getMessage());
        }
    }
}
