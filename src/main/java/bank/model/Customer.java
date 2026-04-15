package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
  Record que representa um Customer do banco.
  Campos:
    id        → identificador único (UUID como String)
    nome      → nome completo
    cpf       → CPF (sem formatação, apenas dígitos)
    email     → endereço de e-mail
    criadoEm  → data/hora de criação (ISO 8601)
 **/
public record Customer(
        String id,
        String nome,
        String cpf,
        String email,
        String criadoEm
) {

    public Customer(String nome, String cpf, String email) {
        this(null, nome, cpf, email, null);
    }

    public Customer comIdEData(String id) {
        String agora = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new Customer(id, this.nome, this.cpf, this.email, agora);
    }
}