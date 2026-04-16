package bank.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import bank.controller.CustomerController;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Router implements HttpHandler {

    private final CustomerController customerController;

    public Router(CustomerController customerController) {
        this.customerController = customerController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            // Route: /customers and /customers/{id}
            if (path.equals("/customers") || path.startsWith("/customers/")) {
                customerController.handle(exchange);
            } else {
                sendResponse(exchange, 404,
                        "{\"error\": \"Route not found: " + path + "\"}");
            }
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500,
                    "{\"error\": \"Internal server error\"}");
        }
    }

    public static void sendResponse(HttpExchange exchange,
                                    int statusCode,
                                    String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}