package com.meetime.hubspot_integration.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hubspot/webhook")
public class WebhookController {

    @PostMapping("/contact")
    public ResponseEntity<String> receiveContactWebhook(@RequestBody List<Map<String, Object>> events) {
        try {
            for (Map<String, Object> event : events) {
                String eventType = (String) event.get("eventType");

                if ("contact.creation".equals(eventType)) {
                    System.out.println("Novo contato criado: " + event);
                }
            }
            return ResponseEntity.ok("Webhook processado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar webhook: " + e.getMessage());
        }
    }
}
