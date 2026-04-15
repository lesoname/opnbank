package bank.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerFactory {

    private static final int DEFAULT_PORT = 8080;

    public static HttpServer create(int port, HttpHandler router) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", router);
        server.setExecutor(null); /* ThreadPool in future? */

        return server;
    }

    public static HttpServer create(HttpHandler router) throws IOException {
        return create(DEFAULT_PORT, router);
    }

}

