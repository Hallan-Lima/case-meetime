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

    // Valores configurados no arquivo de propriedades para integração com o HubSpot.
    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.scopes}")
    private String scopes;

    @Value("${hubspot.auth-url}")
    private String AUTH_URL;

    @Value("${hubspot.token-url}")
    private String TOKEN_URL;

    // Armazena o token de acesso em memória para uso posterior.
    private final AtomicReference<String> accessToken = new AtomicReference<>();

    /**
     * Endpoint para redirecionar o usuário à página de autorização do HubSpot.
     * Constrói a URL de autorização com os parâmetros necessários e redireciona o cliente.
     *
     * @return ResponseEntity com o cabeçalho de redirecionamento.
     */
    @GetMapping("/auth/authorize")
    public ResponseEntity<Void> authorize() {
        // Monta a URL de autorização com os parâmetros necessários.
        String url = String.format(
            "%s?client_id=%s&redirect_uri=%s&scope=%s",
            AUTH_URL, clientId, redirectUri, scopes
        );

        // Configura o cabeçalho HTTP para redirecionar o cliente.
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // HTTP 302 (Found)
    }

    /**
     * Endpoint para lidar com o callback da autorização.
     * Recebe o código de autorização, troca pelo token de acesso e o armazena.
     *
     * @param code Código de autorização recebido do HubSpot.
     * @return ResponseEntity com o token de acesso ou erro.
     */
    @GetMapping("/auth/callback")
    public ResponseEntity<Map<String, Object>> handleOAuthCallback(@RequestParam("code") String code) {
        // RestTemplate é usado para realizar a requisição HTTP para a API do HubSpot.
        RestTemplate restTemplate = new RestTemplate();

        // Monta o corpo da requisição com os parâmetros necessários para obter o token.
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);

        // Configura os cabeçalhos da requisição, indicando que o corpo será enviado como formulário.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Cria a entidade HTTP com o corpo e os cabeçalhos.
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Realiza a requisição POST para a API do HubSpot para trocar o código pelo token.
        ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity, Map.class);

        // Verifica se a resposta foi bem-sucedida e contém o token.
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Salva o token de acesso em memória para uso posterior.
            accessToken.set((String) response.getBody().get("access_token"));
            return ResponseEntity.ok(response.getBody()); // Retorna o token no corpo da resposta.
        } else {
            // Retorna erro caso a autenticação falhe.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Falha na autenticação"));
        }
    }

    /**
     * Endpoint para retornar o token de acesso armazenado.
     *
     * @return ResponseEntity com o token de acesso ou erro caso não exista.
     */
    @GetMapping("/auth/token")
    public ResponseEntity<Map<String, String>> getAccessToken() {
        // Recupera o token armazenado em memória.
        String token = accessToken.get();
        if (token != null) {
            return ResponseEntity.ok(Map.of("access_token", token)); // Retorna o token.
        }
        // Retorna erro caso nenhum token esteja armazenado.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Nenhum token encontrado"));
    }
}
