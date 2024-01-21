import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {
    private Map<String, String> properties;

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

    public String getPort() {
        return properties.get("port");
    }

    public String getRootDirectory() {
        return properties.get("rootDirectory");
    }

    public String getDefaultPage() {
        return properties.get("defaultPage");
    }

    public String getMaxThreads() {
        return properties.get("maxThreads");
    }
}