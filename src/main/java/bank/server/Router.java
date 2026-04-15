package bank.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import bank.controller.CustomerController;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Router implements HttpHandler {

    private final CustomerController CustomerController;

    public Router(CustomerController CustomerController) {
        this.CustomerController = CustomerController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            // Rota: /customers e /customers/{id}
            if (path.equals("/customers") || path.startsWith("/customers/")) {
                CustomerController.handle(exchange);
            } else {
                sendResponse(exchange, 404,
                        "{\"erro\": \"Rota não encontrada: " + path + "\"}");
            }
        } catch (Exception e) {
            System.err.println("Erro interno: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500,
                    "{\"erro\": \"Erro interno do servidor\"}");
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