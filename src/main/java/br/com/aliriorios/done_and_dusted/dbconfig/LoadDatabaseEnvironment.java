package br.com.aliriorios.done_and_dusted.dbconfig;

import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
public class LoadDatabaseEnvironment {
    private final Map<String, Object> databaseEnv;

    public LoadDatabaseEnvironment() {
        Yaml yaml = new Yaml();
        try {
            /* Reading variables from "database.yml" */
            String filePath = Paths.get("database/database.yml").toAbsolutePath().toString();

            try (InputStream inputStream = new FileInputStream(filePath)) {
                this.databaseEnv = yaml.load(inputStream);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error to load file 'database.yml'", e);
        }
    }

    public String getUrl() {
        return (String) databaseEnv.get("url");
    }

    public String getUsername() {
        return (String) databaseEnv.get("username");
    }

    public String getPassword() {
        return (String) databaseEnv.get("password");
    }
}
