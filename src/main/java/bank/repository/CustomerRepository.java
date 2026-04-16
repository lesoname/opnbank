package bank.repository;

import bank.model.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository {

    private final Map<String, Customer> store = new ConcurrentHashMap<>();

    public Customer save(Customer Customer) {
        store.put(Customer.id(), Customer);
        return Customer;
    }

    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Customer> findByCpf(String cpf) {
        return store.values().stream()
                .filter(c -> c.cpf().equals(cpf))
                .findFirst();
    }

    public List<Customer> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}