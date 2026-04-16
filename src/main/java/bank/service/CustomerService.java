package bank.service;

import bank.model.Customer;
import bank.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * Cadastra um novo Customer.
     *
     * @param  rascunho  Customer sem id (vindo do JSON)
     * @return           Customer com id e data preenchidos
     * @throws IllegalArgumentException se dados forem inválidos
     */
    public Customer cadastrar(Customer rascunho) {
        // Validações
        validarCamposObrigatorios(rascunho);
        validarCpfUnico(rascunho.cpf());

        // Gera ID único e timestamp
        String id = UUID.randomUUID().toString();
        Customer Customer = rascunho.comIdEData(id);

        return repository.salvar(Customer);
    }

    public Optional<Customer> buscarPorId(String id) {
        return repository.buscarPorId(id);
    }

    public List<Customer> listarTodos() {
        return repository.listarTodos();
    }

    public Customer atualizar(String id, Customer dadosNovos) {
        Customer existente = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer não encontrado: " + id));

        validarCamposObrigatorios(dadosNovos);

        // Se o CPF mudou, verifica se o novo CPF já não está em uso
        if (!existente.cpf().equals(dadosNovos.cpf())) {
            validarCpfUnico(dadosNovos.cpf());
        }

        // Cria novo objeto mantendo id e data originais
        Customer atualizado = new Customer(
                existente.id(),
                dadosNovos.nome(),
                dadosNovos.cpf(),
                dadosNovos.balance(),
                dadosNovos.email(),
                existente.criadoEm()
        );

        return repository.salvar(atualizado);
    }

    public boolean deletar(String id) {
        if (!repository.existePorId(id)) {
            throw new IllegalArgumentException(
                    "Customer não encontrado: " + id);
        }
        return repository.deletar(id);
    }

    // --- Métodos privados de validação ---

    private void validarCamposObrigatorios(Customer c) {
        if (c.nome() == null || c.nome().isBlank()) {
            throw new IllegalArgumentException("O campo 'nome' é obrigatório");
        }
        if (c.cpf() == null || c.cpf().isBlank()) {
            throw new IllegalArgumentException("O campo 'cpf' é obrigatório");
        }
        if (c.email() == null || c.email().isBlank()) {
            throw new IllegalArgumentException("O campo 'email' é obrigatório");
        }
        if (!c.cpf().matches("\\d{11}")) {
            throw new IllegalArgumentException(
                    "CPF deve conter exatamente 11 dígitos numéricos");
        }
    }

    private void validarCpfUnico(String cpf) {
        if (repository.buscarPorCpf(cpf).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe um Customer com o CPF: " + cpf);
        }
    }
}