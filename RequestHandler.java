import java.io.*;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;

public class RequestHandler {
    private ConfigReader configReader;
    private HTTPRequest httpRequest;
    private ResponseBuilder responseBuilder;
    private Socket clientSocket;
    private String requestHeaders;
    private static final String DEFAULT_CONTENT_TYPE = "default";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_HEAD = "HEAD";
    private static final String HTTP_TRACE = "TRACE";

    /**
     * Constructs a RequestHandler object.
     *
     * @param clientSocket the client socket
     * @param configReader the ConfigReader object
     * used to read the server configuration from a file
     */
    public RequestHandler(Socket clientSocket, ConfigReader configReader) {
        this.clientSocket = clientSocket;
        this.configReader = configReader;
        responseBuilder = new ResponseBuilder();
    }

    /**
     * Handles the HTTP request.
     * Reads the request header, parses it, and sends the appropriate response.
     */
    public void handleRequest() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();

            httpRequest = readRequestAndCreateHttpRequestInstance(in);

            if (!httpRequest.isValid()) {  // Check if the request is valid
                responseBuilder.handleResponse(
                        400, DEFAULT_CONTENT_TYPE, new byte[0], httpRequest.getType(), outputStream, requestHeaders
                );
                return;
            }

            String method = httpRequest.getType();
            if (!method.equals(HTTP_GET) && !method.equals(HTTP_POST) &&
                    !method.equals(HTTP_HEAD) && !method.equals(HTTP_TRACE)) {
                responseBuilder.handleResponse(
                        501, DEFAULT_CONTENT_TYPE, new byte[0], httpRequest.getType(), outputStream, requestHeaders
                );
                return;
            }

            if (method.equals(HTTP_POST) && httpRequest.getRequestedPage().equals("/params_info.html")) {
                String content = handleParamsInfoPostRequest();
                responseBuilder.handleResponse(
                        200, httpRequest.getContentType(), content.getBytes(), httpRequest.getType(), outputStream, requestHeaders
                );
                return;
            }

            String filePath = configReader.getRootDirectory() + sanitizePath(httpRequest.getRequestedPage());
            if (!Files.exists(Paths.get(filePath))) {
                responseBuilder.handleResponse(
                        404, DEFAULT_CONTENT_TYPE, new byte[0], httpRequest.getType(), outputStream, requestHeaders
                );
                return;
            }

            byte[] fileContent = readFileContent(filePath);
            if (httpRequest.isChunked()) {
                responseBuilder.handleChunkedResponse(
                        200, httpRequest.getContentType(), fileContent, outputStream
                );
            } else {
                responseBuilder.handleResponse(
                        200, httpRequest.getContentType(), fileContent, httpRequest.getType(), outputStream, requestHeaders
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                responseBuilder.handleResponse(
                        500, DEFAULT_CONTENT_TYPE, new byte[0], httpRequest.getType(), clientSocket.getOutputStream(), requestHeaders
                );
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Sanitizes the path of the requested page.
     * Removes all redundant characters from the path.
     *
     * @param path the path of the requested page
     * @return the sanitized path
     */
    private String sanitizePath(String path) {
        try {
            return Paths.get(path).normalize().toString();
        } catch (InvalidPathException e) {
            return "";
        }
    }

    /**
     * Reads the content of a file.
     *
     * @param filePath the path to the file
     * @return the content of the file
     */
    private byte[] readFileContent(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "".getBytes();
        }
    }

    /**
     * Reads the request header and creates an HTTPRequest instance.
     *
     * @param in the BufferedReader object used to read the request header
     * @return the HTTPRequest instance
     * @throws IOException if an I/O error occurs
     */
    private HTTPRequest readRequestAndCreateHttpRequestInstance(BufferedReader in) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            requestBuilder.append(line).append("\r\n");
        }

        // add the body of the request
        if (in.ready()) {
            requestBuilder.append("\r\n");
            while (in.ready()) {
                requestBuilder.append((char) in.read());
            }
        }
        String request = requestBuilder.toString();
        // print only rhe request header
        this.requestHeaders = request.split("\r\n\r\n")[0];
        String body = request.split("\r\n\r\n").length > 1 ? request.split("\r\n\r\n")[1] : "";
        System.out.println(this.requestHeaders);
        return new HTTPRequest(request, configReader.getImageExtensions(), body);
    }

    /**
     * Handles a POST request to the /params_info.html page.
     * @return the content of the response
     */
    private String handleParamsInfoPostRequest() {
        Map<String, String> params = httpRequest.getRequestFormBody();

        StringBuilder content = new StringBuilder("<html><body>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            content.append("<p>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</p>");
        }
        content.append("</body></html>");
        return content.toString();
    }
}