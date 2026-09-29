package com.automation.api.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;

public final class WireMockService {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  private static WireMockServer server;

  private WireMockService() {
    // Prevent object creation.
  }

  public static synchronized void start() {
    if (!isEnabled() || isRunning()) {
      return;
    }

    server = new WireMockServer(options().dynamicPort());
    server.start();

    registerStubs();

    System.setProperty("base.url", server.baseUrl());
  }

  public static synchronized void stop() {
    if (!isRunning()) {
      return;
    }

    server.stop();
    server = null;
    System.clearProperty("base.url");
  }

  private static boolean isEnabled() {
    return Boolean.parseBoolean(System.getProperty("wiremock.enabled", "true"));
  }

  private static boolean isRunning() {
    return server != null && server.isRunning();
  }

  private static void registerStubs() {
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
            .willReturn(
                aResponse()
                    .withStatus(500)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withBody(responseBody)));
  }
}
