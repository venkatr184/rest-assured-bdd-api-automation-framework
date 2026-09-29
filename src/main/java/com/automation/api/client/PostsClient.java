package com.automation.api.client;

import com.automation.api.constants.ApiEndpoints;
import com.automation.api.model.request.CreatePostRequest;
import com.automation.api.specification.ApiSpecifications;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PostsClient {

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
}