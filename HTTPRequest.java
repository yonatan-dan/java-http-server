import java.util.HashMap;
import java.util.Map;

/**
 * The HTTPRequest class is responsible for parsing an HTTP request header.
 * It extracts the request type, requested page, whether the requested page is an image,
 * content length, referer, user agent, and parameters from the request header.
 */
public class HTTPRequest {
    private String type;
    private String requestedPage;
    private boolean isImage;
    private int contentLength;
    private String referer;
    private String userAgent;
    private Map<String, String> parameters;

    private ConfigReader configReader;

    /**
     * Constructs an HTTPRequest object and parses the provided request header.
     *
     * @param requestHeader the HTTP request header to parse
     */
    public HTTPRequest(String requestHeader, ConfigReader configReader) {
        parameters = new HashMap<>();
        String[] lines = requestHeader.split("\n");
        this.configReader = configReader;
        try {
            for (String line : lines) {
                parseTypeAndRequestedPage(line);
                parseContentLength(line);
                parseReferer(line);
                parseUserAgent(line);
            }
        } catch (Exception e) {
            System.out.println("Error parsing request header: " + e.getMessage());
        }
    }

    /**
     * Returns whether the request is valid.
     *
     * @return whether the request is valid
     */
    public boolean isValid() {
        return type != null && !type.isEmpty() && requestedPage != null && !requestedPage.isEmpty();
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

                // get allowed extensions from config file and check if requested page is an image
                String[] allowedExtensions = configReader.getImageExtensions();
                isImage = false;
                for (String extension : allowedExtensions) {
                    if (requestedPage.endsWith(extension)) {
                        isImage = true;
                        break;
                    }
                }
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
     * Returns whether the requested page is an image.
     *
     * @return whether the requested page is an image
     */
    public boolean isImage() {
        return isImage;
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
}
