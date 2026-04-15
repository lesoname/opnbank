package bank;

import bank.controller.CustomerController;
import bank.repository.CustomerRepository;
import bank.server.HttpServerFactory;
import bank.server.Router;
import bank.service.CustomerService;
import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Camada de dados
        CustomerRepository repository = new CustomerRepository();

        // 2. Camada de negócio
        CustomerService service = new CustomerService(repository);

        // 3. Camada de apresentação
        CustomerController controller = new CustomerController(service);

        // 4. Infraestrutura
        Router router = new Router(controller);
        HttpServer server = HttpServerFactory.create(router);

        // 5. Inicia o servidor
        server.start();

        System.out.println("===================================");
        System.out.println("  opnbank v0.1.0");
        System.out.println("  Servidor rodando em :8080");
        System.out.println("===================================");
    }
}