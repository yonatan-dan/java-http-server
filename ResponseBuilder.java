import java.util.Map;

/**
 * The ResponseBuilder class is responsible for building HTTP responses.
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
            500, "Internal Server Error"
    );

    // Map of content types to their corresponding values
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "html", "text/html",
            "image", "image",
            "icon", "icon",
            "default", "application/octet-stream"
    );

    /**
     * Builds an HTTP response using the given status code, content type, and content.
     *
     * @param statusCode  the status code of the response
     * @param contentType the content type of the response
     * @param content     the content of the response
     * @return the constructed HTTP response
     */

    // default is head value is false . only when we get "HEAD" request type we change it to true
    public String buildResponse(int statusCode, String contentType, String content) {
        return buildResponse(statusCode, contentType, content , false, false, null);
    }

    public String buildResponse(int statusCode, String contentType, String content,
                                Boolean isHead, Boolean isTrace, String request) {
        StringBuilder response = new StringBuilder();

        response.append(HTTP_VERSION)
                .append(" ").append(statusCode)
                .append(" ")
                .append(STATUS_CODES.get(statusCode))
                .append(CRLF);

        response.append("content-type: ")
                .append(CONTENT_TYPES.getOrDefault(contentType, CONTENT_TYPES.get("default")))
                .append(CRLF);

        response.append("content-length: ")
                .append(content.length())
                .append(CRLF);

        // if the request type != head , build the response with the body
        if(!isHead) {
            response.append(CRLF);
            response.append(content);
        }

        if(isTrace){
            response.append("\n");
            response.append("");
            response.append(request); // TODO - check if its true
        }
        return response.toString();
    }

    /**
     * Builds an HTTP response using the given status code, content type, and content.
     * The response is sent in chunks of size CHUNK_SIZE.
     *
     * @param statusCode  the status code of the response
     * @param contentType the content type of the response
     * @param content     the content of the response
     * @return the constructed HTTP response
     */
    public String buildChunkedResponse(int statusCode, String contentType, String content) {
        StringBuilder response = new StringBuilder();

        response.append(HTTP_VERSION).append(" ").append(statusCode).append(" ").append(STATUS_CODES.get(statusCode)).append(CRLF);
        response.append("content-type: ").append(CONTENT_TYPES.getOrDefault(contentType, CONTENT_TYPES.get("default"))).append(CRLF);
        response.append("Transfer-Encoding: chunked").append(CRLF);
        response.append(CRLF);

        int index = 0;
        while (index < content.length()) {
            int endIndex = Math.min(index + CHUNK_SIZE, content.length());
            String chunk = content.substring(index, endIndex);
            response.append(Integer.toHexString(chunk.length())).append(CRLF);
            response.append(chunk).append(CRLF);
            index = endIndex;
        }

        response.append("0").append(CRLF).append(CRLF);  // End of chunks

        return response.toString();
    }
}