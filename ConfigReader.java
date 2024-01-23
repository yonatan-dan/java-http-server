import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ConfigReader class is responsible for reading the server configuration from a file.
 * The configuration includes the server port, root directory, default page, maximum number of threads, and image extensions.
 * The configuration is stored in a Map, with the configuration name as the key and the configuration value as the value.
 */
public class ConfigReader {
    private Map<String, String> properties;

    public ConfigReader(String filePath) throws IOException {
            this.loadConfig(filePath);
    }

    /**
     * Loads the configuration from a file.
     *
     * @param filePath the path to the configuration file
     * @throws IOException if an I/O error occurs
     */
    public void loadConfig(String filePath) throws IOException {
        properties = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=", 2);
            if (parts.length >= 2) {
                String key = parts[0];
                String value = parts[1];
                properties.put(key, value);
            }
        }
        reader.close();
    }

    /**
     * Returns the server port.
     *
     * @return the server port
     */
    public String getPort() {
        return properties.get("port");
    }

    /**
     * Returns the root directory.
     *
     * @return the root directory
     */
    public String getRootDirectory() {
        return properties.get("root");
    }

    /**
     * Returns the default page.
     *
     * @return the default page
     */
    public String getDefaultPage() {
        return properties.get("defaultPage");
    }

    /**
     * Returns the maximum number of threads.
     *
     * @return the maximum number of threads
     */
    public String getMaxThreads() {
        return properties.get("maxThreads");
    }

    /**
     * Returns the image extensions.
     *
     * @return the image extensions
     */
    public String[] getImageExtensions() {
        return properties.get("imageExtensions").split(",");
    }
}