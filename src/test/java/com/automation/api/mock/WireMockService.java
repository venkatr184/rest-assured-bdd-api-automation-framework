package com.automation.api.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.automation.api.auth.ApiKeyAuthentication;
import com.github.tomakehurst.wiremock.WireMockServer;

public final class WireMockService {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  private static WireMockServer server;
  private static final String TEST_API_KEY = "portfolio-test-api-key";
  private static final String API_KEY_PROPERTY = "api.key";

  private WireMockService() {
    // Prevent object creation.
  }

  public static synchronized void start() {
    if (!isEnabled() || isRunning()) {
      return;
    }

    server = new WireMockServer(options().dynamicPort());
    server.start();

    System.setProperty(API_KEY_PROPERTY, TEST_API_KEY);
    registerStubs();

    System.setProperty("base.url", server.baseUrl());
  }

  public static synchronized void stop() {
    if (!isRunning()) {
      return;
    }

    server.stop();
    server = null;
    System.clearProperty(API_KEY_PROPERTY);
    System.clearProperty("base.url");
  }

  private static boolean isEnabled() {
    return Boolean.parseBoolean(System.getProperty("wiremock.enabled", "true"));
  }

  private static boolean isRunning() {
    return server != null && server.isRunning();
  }

  private static void registerStubs() {
    registerUnauthorizedStub();
    registerGetPostStubs();
    registerCreatePostStub();
    registerServerErrorStub();
    registerPostNotFoundStub();
  }

  private static void registerGetPostStubs() {
    for (int postId = 1; postId <= 3; postId++) {
      String responseBody =
          """
                    {
                      "userId": 1,
                      "id": %d,
                      "title": "WireMock post %d",
                      "body": "Deterministic response from WireMock"
                    }
                    """
              .formatted(postId, postId);

      server.stubFor(
          get(urlEqualTo("/posts/" + postId))
              .withHeader(ApiKeyAuthentication.HEADER_NAME, equalTo(TEST_API_KEY))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                      .withBody(responseBody)));
    }
  }

  private static void registerCreatePostStub() {
    String expectedRequestBody =
        """
            {
              "userId": 101,
              "title": "REST Assured framework",
              "body": "Creating a post through an API"
            }
            """;

    String responseBody =
        """
            {
              "userId": 101,
              "id": 101,
              "title": "REST Assured framework",
              "body": "Creating a post through an API"
            }
            """;

    server.stubFor(
        post(urlEqualTo("/posts"))
            .withHeader(CONTENT_TYPE, containing(APPLICATION_JSON))
            .withHeader(ApiKeyAuthentication.HEADER_NAME, equalTo(TEST_API_KEY))
            .withRequestBody(equalToJson(expectedRequestBody))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withBody(responseBody)));
  }

  private static void registerPostNotFoundStub() {
    server.stubFor(
        get(urlPathMatching("/posts/.*"))
            .atPriority(10)
            .withHeader(ApiKeyAuthentication.HEADER_NAME, equalTo(TEST_API_KEY))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withBody("{}")));
  }

  private static void registerServerErrorStub() {
    String responseBody =
        """
            {
              "error": "Internal server error"
            }
            """;

    server.stubFor(
        get(urlEqualTo("/posts/500"))
            .withHeader(ApiKeyAuthentication.HEADER_NAME, equalTo(TEST_API_KEY))
            .willReturn(
                aResponse()
                    .withStatus(500)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withBody(responseBody)));
  }

  private static void registerUnauthorizedStub() {
    String responseBody =
        """
	            {
	              "error": "Unauthorized"
	            }
	            """;

    server.stubFor(
        get(urlPathMatching("/posts/.*"))
            .atPriority(1)
            .withHeader(ApiKeyAuthentication.HEADER_NAME, absent())
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withBody(responseBody)));
  }
}
