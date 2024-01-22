import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class RequestHandler {
    private ConfigReader configReader;
    private HTTPRequest httpRequest;
    private ResponseBuilder responseBuilder;
    private Socket clientSocket;

    public RequestHandler(Socket clientSocket, ConfigReader configReader) {
        this.clientSocket = clientSocket;
        this.configReader = configReader;
        responseBuilder = new ResponseBuilder();
    }

    public void handleRequest() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            // read the entire request as a string
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }
            String request = requestBuilder.toString();

            httpRequest = new HTTPRequest(request, configReader.getImageExtensions());

            System.out.println(request);

            if (!httpRequest.isValid()) {  // Check if the request is valid
                out.println(responseBuilder.buildResponse(400, null, null));
                out.flush();
                return;
            }

            String method = httpRequest.getType();
            if (!method.equals("GET") && !method.equals("POST")) {  // Only GET and POST are supported
                out.println(responseBuilder.buildResponse(501, null, null));
                out.flush();
                return;
            }

            String filePath = configReader.getRootDirectory() + sanitizePath(httpRequest.getRequestedPage());
            if (!Files.exists(Paths.get(filePath))) {
                out.println(responseBuilder.buildResponse(404, null, null));
                out.flush();
                return;
            }

            String fileContent = readFileContent(filePath);
            String response;
            if (httpRequest.isChunked()) {
                response = responseBuilder.buildChunkedResponse(200, httpRequest.getContentType(), fileContent);
            } else {
                response = responseBuilder.buildResponse(200, httpRequest.getContentType(), fileContent);
            }

            System.out.println(response.split("\r\n\r\n")[0]);  // Print the response header
            out.println(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(responseBuilder.buildResponse(500, null, null));
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private String sanitizePath(String path) {
        try {
            return Paths.get(path).normalize().toString();
        } catch (InvalidPathException e) {
            return null;
        }
    }

    private String readFileContent(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}