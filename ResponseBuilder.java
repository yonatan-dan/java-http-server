import java.util.Map;

/**
 * The ResponseBuilder class is responsible for building HTTP responses.
 * It uses predefined status codes and content types to construct the response.
 */
public class ResponseBuilder {
    private static final String CRLF = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1";

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
     * @param statusCode the status code of the response
     * @param contentType the content type of the response
     * @param content the content of the response
     * @return the constructed HTTP response
     */

    // default is head value is false . only when we get "HEAD" request type we change it to true
    public String buildResponse(int statusCode, String contentType, String content) {
        return buildResponse(statusCode, contentType, content ,  false, false);
    }

    public String buildResponse(int statusCode, String contentType, String content, Boolean isHead, Boolean isTrace) {
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

        //if the request type != head , build the response with the body
        if(!isHead) {
            response.append(CRLF);
            response.append(content);
        }

        if(isTrace){
            //TODO - add the request ..
        }
        return response.toString();
    }
}