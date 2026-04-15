package bank.controller;

import bank.model.Customer;
import bank.service.CustomerService;
import bank.server.Router;
import bank.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /**
     * Ponto de entrada: recebe qualquer requisição em /customers
     * e despacha para o metodo correto baseado no metodo HTTP.
     */
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET"    -> handleGet(exchange, path);
                case "POST"   -> handlePost(exchange);
                case "PUT"    -> handlePut(exchange, path);
                case "DELETE" -> handleDelete(exchange, path);
                default       -> Router.sendResponse(exchange, 405,
                        "{\"erro\": \"Método não permitido: " + method + "\"}");
            }
        } catch (IllegalArgumentException e) {
            Router.sendResponse(exchange, 400,
                    "{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- GET /customers  e  GET /customers/{id} ---
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String id = extractId(path);

        if (id == null) {
            // GET /customers → lista todos
            List<Customer> todos = service.listarTodos();
            Router.sendResponse(exchange, 200, JsonUtil.toJsonArray(todos));
        }

        else {
            // GET /customers/{id} → busca por ID
            service.buscarPorId(id).ifPresentOrElse(
                    Customer -> {
                        try {
                            Router.sendResponse(exchange, 200,
                                    JsonUtil.toJson(Customer));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            Router.sendResponse(exchange, 404,
                                    "{\"erro\": \"Customer não encontrado\"}");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    }

    // --- POST /customers ---
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        
        Customer rascunho;
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            rascunho = JsonUtil.fromForm(body);
        } else {
            rascunho = JsonUtil.fromJson(body);
        }
        
        Customer criado = service.cadastrar(rascunho);
        Router.sendResponse(exchange, 201, JsonUtil.toJson(criado));
    }

    // --- PUT /customers/{id} ---
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        String id = extractId(path);

        if (id == null) {
            Router.sendResponse(exchange, 400,
                    "{\"erro\": \"ID é obrigatório na URL: PUT /customers/{id}\"}");
            return;
        }

        String body = readBody(exchange);
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

        Customer dados;
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            dados = JsonUtil.fromForm(body);
        } else {
            dados = JsonUtil.fromJson(body);
        }

        Customer atualizado = service.atualizar(id, dados);
        Router.sendResponse(exchange, 200, JsonUtil.toJson(atualizado));
    }

    // --- DELETE /customers/{id} ---
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String id = extractId(path);

        if (id == null) {
            Router.sendResponse(exchange, 400,
                    "{\"erro\": \"ID é obrigatório na URL: DELETE /customers/{id}\"}");
            return;
        }

        service.deletar(id);
        Router.sendResponse(exchange, 204, "");
    }

    // --- Utilitários do Controller ---

    /**
     * Extrai o ID da URL.
     * "/customers"           → null
     * "/customers/"          → null
     * "/customers/abc-123"   → "abc-123"
     */
    private String extractId(String path) {
        String[] parts = path.split("/");
        // "/customers/abc" → ["", "Customers", "abc"]

        if (parts.length >= 3 && !parts[2].isBlank()) {
            return parts[2];
        }
        return null;
    }

    /**
     * Lê o corpo da requisição como String.
     */
    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}