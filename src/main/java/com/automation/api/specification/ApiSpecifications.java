package com.automation.api.specification;

import static org.hamcrest.Matchers.lessThan;

import com.automation.api.auth.ApiKeyAuthentication;
import com.automation.api.config.ConfigurationManager;
import com.automation.api.utility.CorrelationIdContext;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/** Creates reusable REST Assured request and response specifications. */
public final class ApiSpecifications {

  private ApiSpecifications() {
    // Prevent object creation.
  }

  /**
   * Creates a new request specification for an API operation.
   *
   * @return configured request specification
   */
  public static RequestSpecification createRequestSpecification() {
    RequestSpecBuilder builder = createRequestSpecificationBuilder();

    ApiKeyAuthentication authentication = new ApiKeyAuthentication();

    authentication
        .getApiKey()
        .ifPresent(apiKey -> builder.addHeader(ApiKeyAuthentication.HEADER_NAME, apiKey));

    return builder.build();
  }

  private static RequestSpecBuilder createRequestSpecificationBuilder() {
    return new RequestSpecBuilder()
        .setBaseUri(ConfigurationManager.getRequiredProperty("base.url"))
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .addHeader(CorrelationIdContext.HEADER_NAME, CorrelationIdContext.get())
        .setConfig(createRestAssuredConfig());
  }

  /**
   * Creates a JSON response specification with the configured response-time limit.
   *
   * @return configured response specification
   */
  public static ResponseSpecification createJsonResponseSpecification() {
    long responseTimeLimit =
        Long.parseLong(ConfigurationManager.getRequiredProperty("response.time.limit.ms"));

    return new ResponseSpecBuilder()
        .expectContentType(ContentType.JSON)
        .expectResponseTime(lessThan(responseTimeLimit))
        .build();
  }

  private static RestAssuredConfig createRestAssuredConfig() {
    int connectionTimeout =
        Integer.parseInt(ConfigurationManager.getRequiredProperty("http.connection.timeout.ms"));

    int socketTimeout =
        Integer.parseInt(ConfigurationManager.getRequiredProperty("http.socket.timeout.ms"));

    int connectionManagerTimeout =
        Integer.parseInt(
            ConfigurationManager.getRequiredProperty("http.connection.manager.timeout.ms"));

    HttpClientConfig httpClientConfig =
        HttpClientConfig.httpClientConfig()
            .setParam("http.connection.timeout", connectionTimeout)
            .setParam("http.socket.timeout", socketTimeout)
            .setParam("http.connection-manager.timeout", connectionManagerTimeout);

    LogConfig logConfig =
        LogConfig.logConfig()
            .blacklistHeader("Authorization")
            .blacklistHeader("Proxy-Authorization")
            .blacklistHeader("X-API-Key")
            .blacklistHeader("Cookie")
            .blacklistHeader("Set-Cookie");

    return RestAssuredConfig.config().httpClient(httpClientConfig).logConfig(logConfig);
  }

  /**
   * Creates a request specification without authentication.
   *
   * @return unauthenticated request specification
   */
  public static RequestSpecification createUnauthenticatedRequestSpecification() {
    return createRequestSpecificationBuilder().build();
  }
}
