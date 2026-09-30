package com.automation.api.steps;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.automation.api.client.PostsClient;
import com.automation.api.context.ScenarioContext;
import com.automation.api.model.request.CreatePostRequest;
import com.automation.api.model.response.PostResponse;
import com.automation.api.utility.JsonDataLoader;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Map;

public class PostSteps {

  private final PostsClient postsClient;
  private final ScenarioContext scenarioContext;
  private CreatePostRequest createPostRequest;

  public PostSteps(PostsClient postsClient, ScenarioContext scenarioContext) {

    this.postsClient = postsClient;
    this.scenarioContext = scenarioContext;
  }

  @When("the client retrieves post {int}")
  public void retrievePost(int postId) {
    Response response = postsClient.getPostById(postId);
    scenarioContext.setResponse(response);
  }

  @Then("the response status code should be {int}")
  public void verifyStatusCode(int expectedStatusCode) {
    int actualStatusCode = scenarioContext.getResponse().statusCode();

    assertEquals(expectedStatusCode, actualStatusCode, "Unexpected response status code.");
  }

  @Then("the response should contain post ID {int}")
  public void verifyPostId(int expectedPostId) {
    PostResponse post = scenarioContext.getResponse().as(PostResponse.class);

    assertEquals(expectedPostId, post.id(), "Unexpected post ID.");
  }

  @Then("the response should contain a non-empty title")
  public void verifyTitle() {
    PostResponse post = scenarioContext.getResponse().as(PostResponse.class);

    assertFalse(
        post.title() == null || post.title().isBlank(),
        "The post title should not be null or empty.");
  }

  /*
    @When("the client creates a post with the following information:")
    public void createPost(DataTable dataTable) {
      Map<String, String> data = dataTable.asMap(String.class, String.class);

      createPostRequest =
          new CreatePostRequest(
              Integer.parseInt(data.get("userId")), data.get("title"), data.get("body"));

      Response response = postsClient.createPost(createPostRequest);

      scenarioContext.setResponse(response);
    }
  */
  @When("the client creates a post using test data {string}")
  public void createPostUsingTestData(String testDataFile) {
    createPostRequest = JsonDataLoader.load("testdata/" + testDataFile, CreatePostRequest.class);

    Response response = postsClient.createPost(createPostRequest);

    scenarioContext.setResponse(response);
  }

  @Then("the created post should contain the submitted information")
  public void verifyCreatedPostInformation() {
    PostResponse createdPost = scenarioContext.getResponse().as(PostResponse.class);

    assertEquals(createPostRequest.userId(), createdPost.userId(), "Unexpected user ID.");

    assertEquals(createPostRequest.title(), createdPost.title(), "Unexpected post title.");

    assertEquals(createPostRequest.body(), createdPost.body(), "Unexpected post body.");
  }

  @Then("the created post should have an ID")
  public void verifyCreatedPostId() {
    PostResponse createdPost = scenarioContext.getResponse().as(PostResponse.class);

    assertTrue(createdPost.id() > 0, "The created post should have a positive ID.");
  }

  @Then("the response should match the post schema")
  public void verifyPostSchema() {
    scenarioContext
        .getResponse()
        .then()
        .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
  }

  @Then("the response body should be an empty JSON object")
  public void verifyEmptyJsonResponse() {
    Map<String, Object> responseBody = scenarioContext.getResponse().jsonPath().getMap("$");

    assertTrue(responseBody.isEmpty(), "The response body should be an empty JSON object.");
  }

  @Then("the error message should be {string}")
  public void verifyErrorMessage(String expectedMessage) {
    String actualMessage = scenarioContext.getResponse().jsonPath().getString("error");

    assertEquals(expectedMessage, actualMessage, "Unexpected API error message.");
  }

  @When("the client retrieves post {int} without authentication")
  public void retrievePostWithoutAuthentication(int postId) {
    Response response = postsClient.getPostByIdWithoutAuthentication(postId);

    scenarioContext.setResponse(response);
  }

  @When("the client retrieves post {int} with transient retry")
  public void retrievePostWithRetry(int postId) {
    Response response = postsClient.getPostByIdWithRetry(postId);

    scenarioContext.setResponse(response);
  }
}
