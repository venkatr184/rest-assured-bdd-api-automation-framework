package com.automation.api.specification;

import static org.hamcrest.Matchers.lessThan;

import com.automation.api.auth.ApiKeyAuthentication;
import com.automation.api.config.ConfigurationManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
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
        .setConfig(createRestAssuredConfig());
  }

  /**
   * Creates a JSON response specification with the configured response-time limit.
   *
   * @return configured response specification
   */
  public static ResponseSpecification createJsonResponseSpecification() {
    long timeout = Long.parseLong(ConfigurationManager.getRequiredProperty("request.timeout"));

    return new ResponseSpecBuilder()
        .expectContentType(ContentType.JSON)
        .expectResponseTime(lessThan(timeout))
        .build();
  }

  private static RestAssuredConfig createRestAssuredConfig() {
    LogConfig logConfig =
        LogConfig.logConfig()
            .blacklistHeader("Authorization")
            .blacklistHeader("Proxy-Authorization")
            .blacklistHeader("X-API-Key")
            .blacklistHeader("Cookie")
            .blacklistHeader("Set-Cookie");

    return RestAssuredConfig.config().logConfig(logConfig);
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
