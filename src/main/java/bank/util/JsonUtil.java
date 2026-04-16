package bank.util;

import bank.model.Customer;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class JsonUtil {

    /**
     * Converts a Customer to a JSON String.
     *
     * Output example:
     * {
     *   "id": "abc-123",
     *   "name": "Leonardo",
     *   "cpf": "12345678901",
     *   "balance": "212,00"
     *   "email": "leo@email.com",
     *   "createdAt": "2025-01-15T10:30:00"
     * }
     */
    public static String toJson(Customer c) {
        return """
            {
              "id": %s,
              "name": %s,
              "cpf": %s,
              "balance": %s,
              "email": %s,
              "createdAt": %s
            }""".formatted(
                quote(c.id()),
                quote(c.name()),
                quote(c.cpf()),
                quote(String.valueOf(c.balance())),
                quote(c.email()),
                quote(c.createdAt())
        );
    }

    /**
     * Converts a list of Customers to a JSON array.
     */
    public static String toJsonArray(List<Customer> customers) {
        String items = customers.stream()
                .map(JsonUtil::toJson)
                .collect(Collectors.joining(","));
        return "[" + items + "]";
    }

    /**
     * Deserializes a JSON to a Customer.
     *
     * Strategy: find each field manually.
     * Yes, it is manual. Yes, it works. Yes, it is intentional.
     */
    public static Customer fromJson(String json) {
        String name         = extractField(json, "name");
        String cpf          = extractField(json, "cpf");
        String balanceStr      = extractField(json, "balance");
        BigDecimal balance  = (balanceStr != null && !balanceStr.isEmpty())
                                                    ? new BigDecimal(balanceStr)
                                                    : BigDecimal.ZERO;
        String email        = extractField(json, "email");
        return new Customer(name, cpf, balance, email);
    }

    /**
     * Deserializes a form (x-www-form-urlencoded) to a Customer.
     */
    public static Customer fromForm(String body) {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                params.put(key.toLowerCase(), value);
            }
        }
        String balanceStr = params.get("balance");
        BigDecimal balance  = (balanceStr != null && !balanceStr.isEmpty())
                ? new BigDecimal(balanceStr)
                : BigDecimal.ZERO;

        return new Customer(
                params.get("name"),
                params.get("cpf"),
                balance,
                params.get("email")
        );
    }

    // --- Helper methods ---

    /**
     * Extracts the value of a simple JSON field.
     * Looks for the pattern "field": "value" and returns "value".
     */
    private static String extractField(String json, String field) {
        // Padrão que aceita:
        // 1. Aspas duplas ou simples (ou nada) em volta da chave
        // 2. Chave case-insensitive
        // 3. Aspas duplas ou simples em volta do valor
        // 4. Espaços arbitrários
        
        String pattern = "(?i)[\"']?" + field + "[\"']?\\s*:\\s*[\"']";
        Pattern fieldPattern = Pattern.compile(pattern);
        Matcher matcher = fieldPattern.matcher(json);
        
        if (!matcher.find()) return null;
        
        int start = matcher.end();
        // O valor termina na mesma aspa (simples ou dupla) que começou
        char quote = json.charAt(start - 1);
        int end = json.indexOf(quote, start);
        
        return end >= 0 ? json.substring(start, end) : null;
    }

    /**
     * Coloca aspas em volta de um valor. Retorna "null" se for null.
     */
    private static String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
}