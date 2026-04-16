package bank;

import bank.controller.CustomerController;
import bank.repository.CustomerRepository;
import bank.server.HttpServerFactory;
import bank.server.Router;
import bank.service.CustomerService;
import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Data layer
        CustomerRepository repository = new CustomerRepository();

        // 2. Business layer
        CustomerService service = new CustomerService(repository);

        // 3. Presentation layer
        CustomerController controller = new CustomerController(service);

        // 4. Infrastructure
        Router router = new Router(controller);
        HttpServer server = HttpServerFactory.create(router);

        // 5. Start the server
        server.start();

        System.out.println("===================================");
        System.out.println("  opnbank v0.1.0");
        System.out.println("  Server running on :8080");
        System.out.println("===================================");
    }
}