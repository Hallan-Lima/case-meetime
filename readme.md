# Hubspot Integration 0.1.0

## **Sumário**
1. [Introdução](#introdução)
2. [Tech Stack](#tech-stack)
3. [Instalação e Execução](#instalação-e-execução)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Funcionalidades](#funcionalidades)

## **Introdução**
A `Hubspot Integration` é uma aplicação Spring Boot projetada para integrar sistemas com a API do HubSpot. Ela permite:
- Autenticação OAuth2 com o HubSpot.
- Criação de contatos diretamente na API do HubSpot.
- Processamento de webhooks para eventos relacionados a contatos.
- Gerenciamento de tokens de acesso para autenticação segura.

Esta aplicação é para apresentar as funcionalidades de automatização e integração com o HubSpot, facilitando a sincronização de dados e o processamento de eventos.

Para este projeto também foi utilizado os recursos de hospedagem do Google Cloud, onde foi hospedado este serviço. Podendo ser acesso através da URL "https://case-hub-meetime-1088074327665.us-central1.run.app" por um breve período.

## **Tech Stack**
A aplicação foi construída utilizando as seguintes tecnologias e ferramentas:
- **Java 17** – Linguagem base do projeto.
- **Spring Boot** – Framework principal para desenvolvimento de aplicações.
    - Módulos usados: `Spring Web`, `Spring Security`, `Spring Boot DevTools`.
- **Apache Maven** – Gerenciador de dependências e build da aplicação.
- **Docker** – Para containerização da aplicação.

## **Instalação e Execução**
### **Pré-requisitos**
- Java 17 instalado.
- Maven configurado no ambiente.
- Docker (opcional, para execução em container).
- Configurar as propriedades no arquivo `application.properties`.

### **Instalando o Projeto**
1. Clone o repositório:
``` bash
   git clone https://github.com/Hallan-Lima/case-meetime
   cd case-meetime
```
2. Compile o projeto:
``` bash
   mvn clean install
```

### **Executando o Projeto**
1. Certifique-se de configurar o arquivo `application.properties` com os valores adequados (veja [application.properties](#configura%C3%A7%C3%A3o)).

2. Inicie a aplicação:
``` bash
   mvn spring-boot:run
```

3. A aplicação será inicializada no terminal e estará disponível na porta configurada (padrão: 8080).

### **Executando com Docker**
1. Construa a imagem Docker:

2. Inicie a aplicação:
``` bash
    docker build -t hubspot-integration -f docker/dockerfile .
```

3. Execute o container:
``` bash
    docker run -p 8080:8080 hubspot-integration
```

### **Configuração**
Certifique-se de alterar as propriedades de configuração (exemplo `application.properties`):
``` properties
# Configuração do HubSpot
hubspot.auth-url=https://app.hubspot.com/oauth/authorize
hubspot.token-url=https://api.hubapi.com/oauth/v1/token
hubspot.contacts-api-url=https://api.hubapi.com/crm/v3/objects/contacts
hubspot.client-id=seu-client-id
hubspot.client-secret=seu-client-secret
hubspot.redirect-uri=https://sua-url.com/auth/callback
hubspot.scopes=crm.objects.contacts.read+crm.objects.contacts.write

# Configuração do servidor
server.port=8080
spring.security.enabled=false
```

## **Estrutura do Projeto**
O projeto segue uma estrutura bem organizada para manter a manutenção simples e código limpo:
``` plaintext
src/main/java/com/meetime/hubspot_integration/
├── config/
│   └── SecurityConfig.java             # Configuração de segurança da aplicação.
├── controllers/
│   ├── AuthController.java             # Controlador para autenticação OAuth2.
│   ├── ContactController.java          # Controlador para operações relacionadas a contatos.
│   └── WebhookController.java          # Controlador para processar webhooks do HubSpot.
├── HubspotIntegrationApplication.java  # Classe principal do projeto.
└── resources/
    └── application.properties          # Arquivo de configurações.
```

## **Funcionalidades**
1. **Autenticação OAuth2**:
    - Acesse a URL no endereço `/auth/authorize`.
    - Redireciona o usuário para a página de autorização do HubSpot.
    - Troca o código de autorização pelo token de acesso.
    - Armazena o token de acesso em memória para uso posterior.

2. **Criação de Contatos**:
    - Permite criar contatos no HubSpot via API.
    - Envia os dados do contato no corpo da requisição para a URL no endereço ``/hubspot/create-contact``.
    
    ``` json
    {
        "properties": {
            "email": "aaaaa9@email.com",
            "firstname": "aaaaa9",
            "lastname": "aaaaa9"
        }
    }


3. **Processamento de Webhooks**:
    - Recebe eventos de criação de contatos enviados pelo HubSpot.
    - Processa os eventos e realiza ações com base no tipo do evento.

4. **Gerenciamento de Tokens**:
    - Endpoint `/auth/token` para recuperar o token de acesso armazenado (apenas para debug).