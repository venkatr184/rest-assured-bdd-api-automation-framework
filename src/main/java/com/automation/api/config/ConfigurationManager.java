package com.automation.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationManager {

    private static final String DEFAULT_ENVIRONMENT = "dev";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigurationManager() {
        // Prevent object creation.
    }

    private static Properties loadProperties() {
        String environment =
                System.getProperty("environment", DEFAULT_ENVIRONMENT);

        String configurationFile =
                "config/" + environment + ".properties";

        Properties properties = new Properties();

        try (InputStream inputStream =
                     ConfigurationManager.class
                             .getClassLoader()
                             .getResourceAsStream(configurationFile)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Configuration file was not found: "
                                + configurationFile);
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load configuration file: "
                            + configurationFile,
                    exception);
        }
    }

    public static String getRequiredProperty(String key) {
        String value = System.getProperty(key);

        if (value == null || value.isBlank()) {
            value = System.getenv(toEnvironmentVariable(key));
        }

        if (value == null || value.isBlank()) {
            value = PROPERTIES.getProperty(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required configuration is missing: " + key);
        }

        return value.trim();
    }

    private static String toEnvironmentVariable(String key) {
        return key.toUpperCase().replace('.', '_');
    }
}