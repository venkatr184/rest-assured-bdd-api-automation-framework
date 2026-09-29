package com.automation.api.auth;

import com.automation.api.config.ConfigurationManager;
import java.util.Optional;

/** Provides API-key authentication values without exposing them to test code. */
public final class ApiKeyAuthentication {

  public static final String HEADER_NAME = "X-API-Key";
  private static final String API_KEY_PROPERTY = "api.key";

  /**
   * Returns the configured API key.
   *
   * @return configured API key, or an empty optional when authentication is not configured
   */
  public Optional<String> getApiKey() {
    return ConfigurationManager.getOptionalProperty(API_KEY_PROPERTY);
  }
}
