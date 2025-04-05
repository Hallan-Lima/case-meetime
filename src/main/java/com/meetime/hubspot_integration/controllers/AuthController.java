package com.meetime.hubspot_integration.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class AuthController {

    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.scopes}")
    private String scopes;

    private static final String AUTH_URL = "https://app.hubspot.com/oauth/authorize";
    private static final String TOKEN_URL = "https://api.hubapi.com/oauth/v1/token";

    // Armazena o token de acesso para futuras chamadas
    private final AtomicReference<String> accessToken = new AtomicReference<>();

    // 🔹 Endpoint para redirecionar o usuário para a página de autorização do HubSpot
    @GetMapping("/auth/authorize")
    public ResponseEntity<Void> authorize() {
        String url = String.format(
            "%s?client_id=%s&redirect_uri=%s&scope=%s",
            AUTH_URL, clientId, redirectUri, scopes
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 🔹 Endpoint para lidar com o callback da autorização e salvar o token
    @GetMapping("/auth/callback")
    public ResponseEntity<Map<String, Object>> handleOAuthCallback(@RequestParam("code") String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            accessToken.set((String) response.getBody().get("access_token"));  // Salva o token
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Falha na autenticação"));
        }
    }

    // 🔹 Endpoint para retornar o token salvo (para debug)
    @GetMapping("/auth/token")
    public ResponseEntity<Map<String, String>> getAccessToken() {
        String token = accessToken.get();
        if (token != null) {
            return ResponseEntity.ok(Map.of("access_token", token));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Nenhum token encontrado"));
    }
}
