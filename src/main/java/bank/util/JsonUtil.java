package bank.util;

import bank.model.Customer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class JsonUtil {

    /**
     * Converte um Customer para uma String JSON.
     *
     * Exemplo de saída:
     * {
     *   "id": "abc-123",
     *   "nome": "Leonardo",
     *   "cpf": "12345678901",
     *   "email": "leo@email.com",
     *   "criadoEm": "2025-01-15T10:30:00"
     * }
     */
    public static String toJson(Customer c) {
        return """
            {
              "id": %s,
              "nome": %s,
              "cpf": %s,
              "email": %s,
              "criadoEm": %s
            }""".formatted(
                quote(c.id()),
                quote(c.nome()),
                quote(c.cpf()),
                quote(c.email()),
                quote(c.criadoEm())
        );
    }

    /**
     * Converte uma lista de Customers para um JSON array.
     */
    public static String toJsonArray(List<Customer> Customers) {
        String items = Customers.stream()
                .map(JsonUtil::toJson)
                .collect(Collectors.joining(","));
        return "[" + items + "]";
    }

    /**
     * Deserializa um JSON para um Customer.
     *
     * Estratégia: buscar cada campo manualmente.
     * Sim, é manual. Sim, funciona. Sim, é proposital.
     */
    public static Customer fromJson(String json) {
        String nome  = extractField(json, "nome");
        String cpf   = extractField(json, "cpf");
        String email = extractField(json, "email");
        return new Customer(nome, cpf, email);
    }

    /**
     * Deserializa um formulário (x-www-form-urlencoded) para um Customer.
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
        return new Customer(
                params.get("nome"),
                params.get("cpf"),
                params.get("email")
        );
    }

    // --- Métodos auxiliares ---

    /**
     * Extrai o valor de um campo JSON simples.
     * Procura o padrão "campo": "valor" e retorna "valor".
     */
    private static String extractField(String json, String field) {
        // Padrão que aceita:
        // 1. Aspas duplas ou simples (ou nada) em volta da chave
        // 2. Chave case-insensitive
        // 3. Aspas duplas ou simples em volta do valor
        // 4. Espaços arbitrários
        
        String pattern = "(?i)[\"']?" + field + "[\"']?\\s*:\\s*[\"']";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        
        if (!m.find()) return null;
        
        int start = m.end();
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