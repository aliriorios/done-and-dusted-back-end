package br.com.aliriorios.done_and_dusted.dbconfig;

import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
public class DatabaseConfig {
    private final Map<String, Object> databaseConfig;

    public DatabaseConfig () {
        Yaml yaml = new Yaml();
        try {
            String filePath = Paths.get("database/databse.yml").toAbsolutePath().toString();

            try (InputStream inputStream = new FileInputStream(filePath)) {
                this.databaseConfig = yaml.load(inputStream);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error to load file 'database.yml'", e);
        }
    }

    public String getUrl() {
        return (String) databaseConfig.get("url");
    }

    public String getUsername() {
        return (String) databaseConfig.get("username");
    }

    public String getPassword() {
        return (String) databaseConfig.get("password");
    }
}
