package bank.repository;

import bank.model.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository {

    private final Map<String, Customer> store = new ConcurrentHashMap<>();

    public Customer salvar(Customer Customer) {
        store.put(Customer.id(), Customer);
        return Customer;
    }

    public Optional<Customer> buscarPorId(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Customer> buscarPorCpf(String cpf) {
        return store.values().stream()
                .filter(c -> c.cpf().equals(cpf))
                .findFirst();
    }

    public List<Customer> listarTodos() {
        return new ArrayList<>(store.values());
    }

    public boolean deletar(String id) {
        return store.remove(id) != null;
    }

    public boolean existePorId(String id) {
        return store.containsKey(id);
    }
}