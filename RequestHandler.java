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
        responseBuilder = new ResponseBuilder();
    }

    public void handleRequest() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String request = in.readLine();
            httpRequest = new HTTPRequest(request, configReader);

            System.out.println(request);

            String method = httpRequest.getType();
            if (!method.equals("GET") && !method.equals("POST") &&
                    !method.equals("HEAD") && !method.equals("TRACE")) {
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
            String response = responseBuilder.buildResponse(200, "html", fileContent);

            System.out.println(response.split("\r\n\r\n")[0]);  // Print the response header
            out.println(response);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
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