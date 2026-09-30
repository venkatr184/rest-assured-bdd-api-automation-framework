package com.automation.api.reporting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AllureEnvironmentWriter {

  private static final Path RESULTS_DIRECTORY = Path.of("target", "allure-results");

  private static final Path ENVIRONMENT_FILE = RESULTS_DIRECTORY.resolve("environment.properties");

  private AllureEnvironmentWriter() {
    // Prevent object creation.
  }

  public static void write() {
    Properties environment = new Properties();

    environment.setProperty("Execution Environment", System.getProperty("environment", "dev"));

    environment.setProperty(
        "API Execution Mode", isWireMockEnabled() ? "WireMock" : "External API");

    environment.setProperty("Java Version", System.getProperty("java.version"));

    environment.setProperty(
        "Operating System", System.getProperty("os.name") + " " + System.getProperty("os.version"));

    environment.setProperty("Architecture", System.getProperty("os.arch"));

    Properties junitProperties = loadJunitPlatformProperties();

    environment.setProperty(
        "Parallel Execution",
        junitProperties.getProperty("cucumber.execution.parallel.enabled", "false"));

    environment.setProperty(
        "Parallelism",
        junitProperties.getProperty(
            "cucumber.execution.parallel.config.fixed.parallelism", "not configured"));

    try {
      Files.createDirectories(RESULTS_DIRECTORY);

      try (OutputStream outputStream = Files.newOutputStream(ENVIRONMENT_FILE)) {

        environment.store(outputStream, "API automation execution environment");
      }

    } catch (IOException exception) {
      throw new IllegalStateException("Unable to create Allure environment metadata.", exception);
    }
  }

  private static boolean isWireMockEnabled() {
    return Boolean.parseBoolean(System.getProperty("wiremock.enabled", "true"));
  }

  private static Properties loadJunitPlatformProperties() {
    Properties properties = new Properties();

    try (InputStream inputStream =
        AllureEnvironmentWriter.class
            .getClassLoader()
            .getResourceAsStream("junit-platform.properties")) {

      if (inputStream != null) {
        properties.load(inputStream);
      }

      return properties;

    } catch (IOException exception) {
      throw new IllegalStateException("Unable to load JUnit Platform configuration.", exception);
    }
  }
}
