import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

//todo-ck should restructure package so properties are in src/main/resources to take advantage of maven for building for diff. environments
public class Secrets {
    private final Properties properties = new Properties();

    public Secrets() {
        try {
            properties.load(Files.newInputStream(Paths.get("src/resources/secrets.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getApiSecret() {
        return properties.getProperty("api.secret");
    }
}
