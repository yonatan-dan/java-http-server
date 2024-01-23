import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * The Server class represents a server that listens for incoming client connections
 * and handles them efficiently using a thread pool and a Semaphore to limit the number of concurrent connections.
 */
public class Server {
    private ConfigReader configReader;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Semaphore semaphore;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    /**
     * Initializes a new instance of the Server class.
     * Sets up the server socket, thread pool, and semaphore.
     */
    public Server() {
        try {
            configReader = new ConfigReader("config.ini");
            int port = Integer.parseInt(configReader.getPort());
            int maxThreads = Integer.parseInt(configReader.getMaxThreads());
            serverSocket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(maxThreads);
            semaphore = new Semaphore(maxThreads);
            System.out.println("server is listening on port " + port);
        } catch (IOException e) {
            System.out.println("server can't listening to port : " + e);
        }
    }

    /**
     * Starts the server, listening for incoming client connections and handling them
     * using a thread pool with a fixed size of threads.
     * The server will continuously accept incoming connections and assign a separate thread
     * (RequestHandler) to handle each connection (until it gets to maximum thread number).
     * If the maximum number of threads is reached, the server will wait until a thread is available.
     */
    public void start() {
        while (true) {
            try {
                semaphore.acquire();
                Socket clientSocket = serverSocket.accept();
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        new RequestHandler(clientSocket, configReader).handleRequest();
                    } catch (Exception e) {
                        System.out.println("Error handling request: " + e);
                    }
                }, executorService);

                future.thenRun(() -> {
                    semaphore.release();
                    System.out.println("client handled, releasing thread");
                });
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e);
            } catch (IOException e) {
                System.out.println("failed to starts the server and listening for incoming client connections : " + e);
            }
        }
    }
}