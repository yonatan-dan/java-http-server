import java.util.HashMap;
import java.util.Map;

/**
 * The HTTPRequest class is responsible for parsing an HTTP request header.
 * It extracts the request type, requested page, whether the requested page is an image,
 * content length, referer, user agent, and parameters from the request header.
 */
public class HTTPRequest {
    private final String[] imageExtensions;
    private String type;
    private String requestedPage;
    private String contentType;
    private int contentLength;
    private String referer;
    private String userAgent;
    private boolean chunked;
    private Map<String, String> parameters;
    private boolean isValid;

    /**
     * Constructs an HTTPRequest object and parses the provided request header.
     *
     * @param requestHeader the HTTP request header to parse
     */
    public HTTPRequest(String requestHeader, String[] imgExtensions) {
        parameters = new HashMap<>();
        String[] lines = requestHeader.split("\n");
        imageExtensions = imgExtensions;
        isValid = true;
        try {
            for (String line : lines) {
                parseTypeAndRequestedPage(line);
                parseContentLength(line);
                parseReferer(line);
                parseUserAgent(line);
                parseChunked(line);
            }
            determineContentType();
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
        if (line.startsWith("GET") || line.startsWith("POST")) {
            String[] parts = line.split(" ");
            if (parts.length >= 2) {
                type = parts[0];
                requestedPage = parts[1];
                parseParameters(requestedPage);
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
}
