package com.automation.api.utility;

import com.automation.api.config.ConfigurationManager;
import io.restassured.response.Response;
import java.util.Set;
import java.util.function.Supplier;

/** Executes idempotent API operations with controlled transient-failure retries. */
public final class ApiRetryExecutor {

  private static final Set<Integer> RETRYABLE_STATUS_CODES = Set.of(429, 502, 503, 504);

  /**
   * Executes an API operation and retries configured transient responses.
   *
   * @param operation API operation to execute
   * @return the successful response or the final unsuccessful response
   */
  public Response execute(Supplier<Response> operation) {
    int maximumAttempts =
        Integer.parseInt(ConfigurationManager.getRequiredProperty("retry.max.attempts"));

    long retryDelay = Long.parseLong(ConfigurationManager.getRequiredProperty("retry.delay.ms"));

    Response response = null;

    for (int attempt = 1; attempt <= maximumAttempts; attempt++) {
      response = operation.get();

      if (!shouldRetry(response) || attempt == maximumAttempts) {
        return response;
      }

      waitBeforeRetry(retryDelay, attempt);
    }

    throw new IllegalStateException("Retry execution ended without producing a response.");
  }

  private boolean shouldRetry(Response response) {
    return RETRYABLE_STATUS_CODES.contains(response.statusCode());
  }

  private void waitBeforeRetry(long baseDelay, int completedAttempt) {
    long delay = baseDelay * completedAttempt;

    try {
      Thread.sleep(delay);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();

      throw new IllegalStateException("API retry was interrupted.", exception);
    }
  }
}
