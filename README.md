# opnbank

Uma API simples de banco (cadastro de clientes) desenvolvida em Java, sem frameworks externos de servidor, utilizando apenas as bibliotecas nativas do Java (`com.sun.net.httpserver`).

## 🚀 Tecnologias Utilizadas

- **Java 21**: Utilizando recursos modernos como Records.
- **Gradle**: Gerenciador de dependências e automação de build.
- **JUnit 5**: Para testes automatizados.
- **HttpServer (Nativo)**: Servidor HTTP integrado do Java.

## 📦 Estrutura do Projeto

O projeto segue uma arquitetura em camadas para melhor organização:

- `bank.model`: Definição das entidades (Records).
- `bank.repository`: Persistência de dados (em memória).
- `bank.service`: Regras de negócio e validações.
- `bank.controller`: Manipulação de requisições e respostas HTTP.
- `bank.server`: Configuração do servidor e roteamento.
- `bank.util`: Utilitários para conversão de JSON e Form URL Encoded.

## 🛠️ Como Executar

### Pré-requisitos
- Java 21 ou superior instalado.
- Gradle (ou use o `gradlew` incluso).

### Rodando a aplicação
Para iniciar o servidor:
```bash
./gradlew run
```
O servidor estará rodando em `http://localhost:8080`.

### Rodando os testes
```bash
./gradlew test
```

## 🛣️ Endpoints da API

A API suporta requisições nos formatos `application/json` e `application/x-www-form-urlencoded`.

- **GET /customers**: Lista todos os clientes.
- **GET /customers/{id}**: Busca um cliente pelo ID.
- **POST /customers**: Cadastra um novo cliente.
  - Campos obrigatórios: `nome`, `cpf` (11 dígitos), `email`.
- **PUT /customers/{id}**: Atualiza os dados de um cliente.
- **DELETE /customers/{id}**: Remove um cliente.

## 📝 Notas de Versão
- O sistema possui um processador de JSON manual e suporte a formulários para maior flexibilidade em diferentes clientes HTTP.
- Validação de CPF único e campos obrigatórios implementada na camada de serviço.
