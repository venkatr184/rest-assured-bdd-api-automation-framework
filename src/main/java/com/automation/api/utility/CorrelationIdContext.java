package com.automation.api.utility;

import java.util.UUID;

/** Maintains a unique correlation ID for the current scenario thread. */
public final class CorrelationIdContext {

  public static final String HEADER_NAME = "X-Correlation-ID";

  private static final ThreadLocal<String> CORRELATION_ID =
      ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

  private CorrelationIdContext() {
    // Prevent object creation.
  }

  /**
   * Returns the correlation ID associated with the current thread.
   *
   * @return current correlation ID
   */
  public static String get() {
    return CORRELATION_ID.get();
  }

  /** Generates and stores a new correlation ID for the current thread. */
  public static void initialize() {
    CORRELATION_ID.set(UUID.randomUUID().toString());
  }

  /** Removes correlation data from the current thread. */
  public static void clear() {
    CORRELATION_ID.remove();
  }
}
