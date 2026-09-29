package com.automation.api.specification;

import static org.hamcrest.Matchers.lessThan;

import com.automation.api.config.ConfigurationManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class ApiSpecifications {

  private ApiSpecifications() {
    // Prevent object creation.
  }

  public static RequestSpecification createRequestSpecification() {
    return new RequestSpecBuilder()
        .setBaseUri(ConfigurationManager.getRequiredProperty("base.url"))
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .setConfig(createRestAssuredConfig())
        .build();
  }

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
}
