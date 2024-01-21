import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Server class represents a server that listens for incoming client connections
 * and handles them efficiently using a thread pool.
 */
public class Server {
    private ConfigReader configReader;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private int currentThreads;


    /**
     * initializes a new instance of the Server class
     */
    public Server() {

        try {
            configReader = new ConfigReader("config.ini");
            //load configuration from config file
            int port = Integer.parseInt(configReader.getPort());
            int maxThreads = Integer.parseInt(configReader.getMaxThreads());
            // create a ServerSocket to listen on chosen port and a thread pool
            serverSocket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(maxThreads);
            System.out.println("server is listening on port " + port);
        } catch (IOException e) {
            System.out.println("server can't listening to port : " + e);
        }
    }

    /**
     * starts the server, listening for incoming client connections and handling them
     * using a thread pool with fix size of threads.
     *
     * the server will continuously accept incoming connections and assign a separate thread
     * (RequestHandler) to handle each connection (until it gets to maximum thread number).
     */
    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                // check if the maximum number of threads is reached
                if (currentThreads < 10) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        currentThreads++;
                        new RequestHandler(clientSocket, configReader).handleRequest();
                    }, executorService);

                    // print a callback to release the thread when the CompletableFuture is completed
                    future.thenRun(() -> {
                        currentThreads--;
                        System.out.println("client handled, releasing thread");
                    });
                } else {
                    // when maximum number of threads is reached
                    System.out.println("max threads limitation - client will have to wait");
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("failed to starts the server and listening for incoming client connections : " + e);
            }
        }
    }
}