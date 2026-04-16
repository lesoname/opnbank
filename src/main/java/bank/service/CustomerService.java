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
     * Registers a new Customer.
     *
     * @param  draft  Customer without id (from JSON)
     * @return        Customer with id and date filled
     * @throws IllegalArgumentException if data is invalid
     */
    public Customer register(Customer draft) {
        // Validations
        validateRequiredFields(draft);
        validateUniqueCpf(draft.cpf());

        // Generates unique ID and timestamp
        String id = UUID.randomUUID().toString();
        Customer customer = draft.withIdAndDate(id);

        return repository.save(customer);
    }

    public Optional<Customer> findById(String id) {
        return repository.findById(id);
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer update(String id, Customer newDetails) {
        Customer existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + id));

        validateRequiredFields(newDetails);

        // If CPF changed, check if the new CPF is already in use
        if (!existing.cpf().equals(newDetails.cpf())) {
            validateUniqueCpf(newDetails.cpf());
        }

        // Create new object maintaining original id and date
        Customer updated = new Customer(
                existing.id(),
                newDetails.name(),
                newDetails.cpf(),
                newDetails.balance(),
                newDetails.email(),
                existing.createdAt()
        );

        return repository.save(updated);
    }

    public boolean delete(String id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Customer not found: " + id);
        }
        return repository.delete(id);
    }

    // --- Private validation methods ---

    private void validateRequiredFields(Customer c) {
        if (c.name() == null || c.name().isBlank()) {
            throw new IllegalArgumentException("Field 'name' is required");
        }
        if (c.cpf() == null || c.cpf().isBlank()) {
            throw new IllegalArgumentException("Field 'cpf' is required");
        }
        if (c.email() == null || c.email().isBlank()) {
            throw new IllegalArgumentException("Field 'email' is required");
        }
        if (!c.cpf().matches("\\d{11}")) {
            throw new IllegalArgumentException(
                    "CPF must contain exactly 11 numeric digits");
        }
    }

    private void validateUniqueCpf(String cpf) {
        if (repository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException(
                    "A Customer with CPF " + cpf + " already exists");
        }
    }
}