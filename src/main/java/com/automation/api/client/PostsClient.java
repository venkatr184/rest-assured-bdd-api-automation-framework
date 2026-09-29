package com.automation.api.client;

import static io.restassured.RestAssured.given;

import com.automation.api.constants.ApiEndpoints;
import com.automation.api.model.request.CreatePostRequest;
import com.automation.api.specification.ApiSpecifications;
import io.restassured.response.Response;

/** Provides reusable operations for interacting with the Posts API. */
public class PostsClient {

  /**
   * Retrieves a post using its identifier.
   *
   * @param postId post identifier
   * @return raw API response
   */
  public Response getPostById(int postId) {
    return given()
        .spec(ApiSpecifications.createRequestSpecification())
        .pathParam("postId", postId)
        .log()
        .ifValidationFails()
        .when()
        .get(ApiEndpoints.POST_BY_ID)
        .then()
        .log()
        .ifValidationFails()
        .spec(ApiSpecifications.createJsonResponseSpecification())
        .extract()
        .response();
  }

  /**
   * Creates a post using the supplied request payload.
   *
   * @param request post-creation request
   * @return raw API response
   */
  public Response createPost(CreatePostRequest request) {
    return given()
        .spec(ApiSpecifications.createRequestSpecification())
        .body(request)
        .log()
        .ifValidationFails()
        .when()
        .post(ApiEndpoints.POSTS)
        .then()
        .log()
        .ifValidationFails()
        .spec(ApiSpecifications.createJsonResponseSpecification())
        .extract()
        .response();
  }

  /**
   * Retrieves a post without applying authentication.
   *
   * @param postId post identifier
   * @return raw API response
   */
  public Response getPostByIdWithoutAuthentication(int postId) {
    return given()
        .spec(ApiSpecifications.createUnauthenticatedRequestSpecification())
        .pathParam("postId", postId)
        .log()
        .ifValidationFails()
        .when()
        .get(ApiEndpoints.POST_BY_ID)
        .then()
        .log()
        .ifValidationFails()
        .spec(ApiSpecifications.createJsonResponseSpecification())
        .extract()
        .response();
  }
}
