package com.automation.api.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

/** Loads typed test data from JSON resources on the classpath. */
public final class JsonDataLoader {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonDataLoader() {
    // Prevent object creation.
  }

  /**
   * Loads and deserializes a JSON classpath resource.
   *
   * @param resourcePath classpath-relative JSON resource path
   * @param targetType class into which the JSON should be deserialized
   * @param <T> target object type
   * @return deserialized test-data object
   * @throws IllegalStateException when the resource cannot be loaded
   */
  public static <T> T load(String resourcePath, Class<T> targetType) {

    try (InputStream inputStream =
        JsonDataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {

      if (inputStream == null) {
        throw new IllegalStateException("Test-data resource was not found: " + resourcePath);
      }

      return OBJECT_MAPPER.readValue(inputStream, targetType);

    } catch (IOException exception) {
      throw new IllegalStateException(
          "Unable to load test-data resource: " + resourcePath, exception);
    }
  }
}
