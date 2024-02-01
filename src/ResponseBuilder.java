package src;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.nio.charset.StandardCharsets;

/**
 * The src.ResponseBuilder class is responsible for building HTTP responses.
 * It uses predefined status codes and content types to construct the response.
 */
public class ResponseBuilder {
    private static final String CRLF = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final int CHUNK_SIZE = 1024;

    // Map of status codes to their corresponding messages
    private static final Map<Integer, String> STATUS_CODES = Map.of(
            200, "OK",
            404, "Not Found",
            501, "Not Implemented",
            400, "Bad Request",
            500, "Internal src.Server Error"
    );

    // Map of content types to their corresponding values
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "html", "text/html",
            "image", "image",
            "icon", "icon",
            "default", "application/octet-stream"
    );

    /**
     * Handles the HTTP response.
     * Constructs the response headers and writes them to the output stream.
     * If the request type is not HEAD, it also writes the content bytes to the output stream.
     * If the request type is TRACE, it appends the request to the output stream.
     *
     * @param statusCode the status code of the response
     * @param contentType the content type of the response
     * @param contentBytes the content of the response in bytes
     * @param requestType the type of the HTTP request
     * @param outputStream the output stream to write the response to
     * @param request the original HTTP request
     * @throws IOException if an I/O error occurs
     */
    public void handleResponse(int statusCode, String contentType, byte[] contentBytes,
                               String requestType, OutputStream outputStream, String request) throws IOException {
        Boolean isHead = requestType.equals("HEAD");
        Boolean isTrace = requestType.equals("TRACE");

        // Build the header
        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders
                .append(HTTP_VERSION)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(STATUS_CODES.get(statusCode))
                .append(CRLF);
        responseHeaders
                .append("Content-Type: ")
                .append(CONTENT_TYPES.getOrDefault(contentType, CONTENT_TYPES.get("default")))
                .append(CRLF);
        responseHeaders
                .append("Content-Length: ")
                .append(contentBytes.length)
                .append(CRLF);
        responseHeaders
                .append("Connection: close")
                .append(CRLF);
        responseHeaders
                .append(CRLF);

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));

        System.out.println(responseHeaders);

        // Write body if not a HEAD request
        if (!isHead) {
            outputStream.write(contentBytes);
        }

        // Special handling for TRACE
        if (isTrace) {
            outputStream.write(("\n" + request).getBytes(StandardCharsets.UTF_8));
        }

        outputStream.flush();
    }

    /**
     * Handles the HTTP response with chunked transfer encoding.
     * Constructs the response headers and writes them to the output stream.
     * Then writes the content bytes to the output stream in chunks.
     *
     * @param statusCode the status code of the response
     * @param contentType the content type of the response
     * @param contentBytes the content of the response in bytes
     * @param outputStream the output stream to write the response to
     * @throws IOException if an I/O error occurs
     */
    public void handleChunkedResponse(int statusCode, String contentType, byte[] contentBytes,
                                      OutputStream outputStream) throws IOException {
        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders
                .append(HTTP_VERSION)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(STATUS_CODES.get(statusCode))
                .append(CRLF);
        responseHeaders
                .append("Content-Type: ")
                .append(CONTENT_TYPES.getOrDefault(contentType, CONTENT_TYPES.get("default")))
                .append(CRLF);
        responseHeaders
                .append("Transfer-Encoding: chunked")
                .append(CRLF);
        responseHeaders
                .append("Connection: close")
                .append(CRLF);
        responseHeaders
                .append(CRLF);

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));

        System.out.println(responseHeaders);

        int index = 0;
        while (index < contentBytes.length) {
            int chunkSize = Math.min(CHUNK_SIZE, contentBytes.length - index);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(contentBytes, index, chunk, 0, chunkSize);

            outputStream.write(Integer.toHexString(chunkSize).getBytes(StandardCharsets.UTF_8));
            outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));

            outputStream.write(chunk);
            outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));

            index += chunkSize;
        }

        // End of chunks
        outputStream.write("0".getBytes(StandardCharsets.UTF_8));
        outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));
        outputStream.write(CRLF.getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
    }
}