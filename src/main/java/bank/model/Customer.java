package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

/**
  Record que representa um Customer do banco.
  Campos:
    id        → identificador único (UUID como String)
    nome      → nome completo
    cpf       → CPF (sem formatação, apenas dígitos)
    big       → balance
    email     → endereço de e-mail
    criadoEm  → data/hora de criação (ISO 8601)
 **/
public record Customer(
        String id,
        String nome,
        String cpf,
        BigDecimal balance,
        String email,
        String criadoEm
) {

    public Customer(String nome, String cpf, BigDecimal balance, String email) {
        this(null, nome, cpf, balance, email, null);
    }

    public Customer comIdEData(String id) {
        String agora = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new Customer(id, this.nome, this.cpf, this.balance, this.email, agora);
    }
}