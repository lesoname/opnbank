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
     * Entry point: receives any request at /customers
     * and dispatches to the correct method based on the HTTP method.
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
                        "{\"error\": \"Method not allowed: " + method + "\"}");
            }
        } catch (IllegalArgumentException e) {
            Router.sendResponse(exchange, 400,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- GET /customers and GET /customers/{id} ---
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String id = extractId(path);

        if (id == null) {
            // GET /customers → list all
            List<Customer> all = service.findAll();
            Router.sendResponse(exchange, 200, JsonUtil.toJsonArray(all));
        }

        else {
            // GET /customers/{id} → find by ID
            service.findById(id).ifPresentOrElse(
                    customer -> {
                        try {
                            Router.sendResponse(exchange, 200,
                                    JsonUtil.toJson(customer));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            Router.sendResponse(exchange, 404,
                                    "{\"error\": \"Customer not found\"}");
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
        
        Customer draft;
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            draft = JsonUtil.fromForm(body);
        } else {
            draft = JsonUtil.fromJson(body);
        }
        
        Customer created = service.register(draft);
        Router.sendResponse(exchange, 201, JsonUtil.toJson(created));
    }

    // --- PUT /customers/{id} ---
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        String id = extractId(path);

        if (id == null) {
            Router.sendResponse(exchange, 400,
                    "{\"error\": \"ID is required in URL: PUT /customers/{id}\"}");
            return;
        }

        String body = readBody(exchange);
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

        Customer data;
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            data = JsonUtil.fromForm(body);
        } else {
            data = JsonUtil.fromJson(body);
        }

        Customer updated = service.update(id, data);
        Router.sendResponse(exchange, 200, JsonUtil.toJson(updated));
    }

    // --- DELETE /customers/{id} ---
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String id = extractId(path);

        if (id == null) {
            Router.sendResponse(exchange, 400,
                    "{\"error\": \"ID is required in URL: DELETE /customers/{id}\"}");
            return;
        }

        service.delete(id);
        Router.sendResponse(exchange, 204, "");
    }

    // --- Controller Utilities ---

    /**
     * Extracts the ID from the URL.
     * "/customers"           → null
     * "/customers/"          → null
     * "/customers/abc-123"   → "abc-123"
     */
    private String extractId(String path) {
        String[] parts = path.split("/");
        // "/customers/abc" → ["", "customers", "abc"]

        if (parts.length >= 3 && !parts[2].isBlank()) {
            return parts[2];
        }
        return null;
    }

    /**
     * Reads the request body as String.
     */
    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}