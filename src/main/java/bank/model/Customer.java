package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

/**
  Record representing a bank Customer.
  Fields:
    id        → unique identifier (UUID as String)
    name      → full name
    cpf       → CPF (unformatted, digits only)
    balance   → current balance
    email     → email address
    createdAt → creation date/time (ISO 8601)
 **/
public record Customer(
        String id,
        String name,
        String cpf,
        BigDecimal balance,
        String email,
        String createdAt
) {

    public Customer(String name, String cpf, BigDecimal balance, String email) {
        this(null, name, cpf, balance, email, null);
    }

    public Customer withIdAndDate(String id) {
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new Customer(id, this.name, this.cpf, this.balance, this.email, now);
    }
}