package src;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The src.HTTPRequest class is responsible for parsing an HTTP request header.
 * It extracts the request type, requested page, whether the requested page is an image,
 * content length, referer, user agent, and parameters from the request header.
 */
public class HTTPRequest {
    private final String[] imageExtensions;
    private String type;
    private String requestedPage;
    private String contentType;
    private String referer;
    private String userAgent;
    private boolean chunked;
    private boolean isValid;
    private int contentLength;
    private Map<String, String> parameters;
    private Map<String, String> requestBody;

    /**
     * Constructs an src.HTTPRequest object and parses the provided request header.
     *
     * @param requestHeader the HTTP request header to parse
     */
    public HTTPRequest(String requestHeader, String[] imgExtensions, String body) {
        parameters = new HashMap<>();
        requestBody = new HashMap<>();
        String[] lines = requestHeader.split("\n");
        imageExtensions = imgExtensions;
        isValid = true; // assume the request is valid until proven otherwise
        try {
            for (String line : lines) {
                parseTypeAndRequestedPage(line);
                parseContentLength(line);
                parseReferer(line);
                parseUserAgent(line);
                parseChunked(line);
            }
            determineContentType();
            parseBody(body);
        } catch (Exception e) {
            System.out.println("Error parsing request header: " + e.getMessage());
            isValid = false;

        }
    }

    /**
     * Determines the content type of the requested page.
     * The content type is determined by the extension of the requested page.
     */
    private void determineContentType() {
        String[] parts = requestedPage.split("\\.");
        String extension = parts[parts.length - 1];

        for (String imageExtension : imageExtensions) {
            if (imageExtension.equals(extension)) {
                contentType = "image";
                return;
            }
        }

        if (extension.equals("html")) {
            contentType = "html";
            return;
        }

        if (extension.equals("ico")) {
            contentType = "icon";
            return;
        }

        contentType = "default";
    }

    /**
     * Parses the request type and requested page from a line of the request header.
     *
     * @param line a line of the request header
     */
    private void parseTypeAndRequestedPage(String line) {
        if (line.startsWith("GET") || line.startsWith("POST") ||
                line.startsWith("HEAD") || line.startsWith("TRACE")){
            String[] parts = line.split(" ");
            if (parts.length >= 2) {
                type = parts[0];
                requestedPage = parts[1];
                parseParameters(requestedPage);
                if (this.requestedPage.equals("/")) { // handle default page
                    requestedPage = "/index.html";
                }
            }
        }
    }

    /**
     * Parses the parameters from the requested page.
     *
     * @param requestedPage the requested page
     */
    private void parseParameters(String requestedPage) {
        if (requestedPage.contains("?")) {
            String[] pageParts = requestedPage.split("\\?");
            this.requestedPage = pageParts[0];
            String[] paramParts = pageParts[1].split("&");
            for (String param : paramParts) {
                String[] keyValue = param.split("=");
                if (keyValue.length >= 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    /**
     * Parses the content length from a line of the request header.
     *
     * @param line a line of the request header
     */
    private void parseContentLength(String line) {
        if (line.startsWith("Content-Length: ")) {
            try {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            } catch (NumberFormatException e) {
                contentLength = 0;
            }
        }
    }

    /**
     * Parses the referer from a line of the request header.
     *
     * @param line a line of the request header
     */
    private void parseReferer(String line) {
        if (line.startsWith("Referer: ")) {
            referer = line.split(": ", 2)[1];
        }
    }

    /**
     * Parses the user agent from a line of the request header.
     *
     * @param line a line of the request header
     */
    private void parseUserAgent(String line) {
        if (line.startsWith("User-Agent: ")) {
            userAgent = line.split(": ", 2)[1];
        }
    }

    /**
     * Parses the chunked from a line of the request header.
     *
     * @param line a line of the request header
     */
    private void parseChunked(String line) {
        if (line.toLowerCase().startsWith("chunked: ")) {
            chunked = "yes".equals(line.split(": ")[1].trim().toLowerCase());
        }
    }

    /**
     * Parses the body from a line of the request header.
     *
     * @param body a line of the request header
     */
    private void parseBody(String body) throws Exception {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            String name = parts[0];
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            requestBody.put(name, value);
        }
    }

    // Getters

    /**
     * Returns the request type.
     *
     * @return the request type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the requested page.
     *
     * @return the requested page
     */

    public String getRequestedPage() {
        return requestedPage;
    }

    /**
     * Returns the content type.
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the content length.
     *
     * @return the content length
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * Returns the referer.
     *
     * @return the referer
     */
    public String getReferer() {
        return referer;
    }

    /**
     * Returns the user agent.
     *
     * @return the user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Returns whether the request is valid.
     * @return whether the request is valid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Returns the parameters.
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Returns whether the request is chunked.
     * @return whether the request is chunked
     */
    public boolean isChunked() {
        return chunked;
    }

    /**
     * Returns the request body.
     * @return the request body
     */
    public Map<String, String> getRequestFormBody() {
        return requestBody;
    }
}
