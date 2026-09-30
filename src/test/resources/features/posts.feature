@api @posts
Feature: Retrieve and create posts

  As an API consumer
  I want to manage posts
  So that post information can be retrieved and created

  @smoke
  Scenario Outline: Retrieve an existing post
    When the client retrieves post <postId>
    Then the response status code should be 200
    And the response should match the post schema
    And the response should contain post ID <postId>
    And the response should contain a non-empty title

    Examples:
      | postId |
      | 1      |
      | 2      |
      | 3      |

  @regression
  Scenario: Create a new post
    When the client creates a post with the following information:
      | userId | 101                            |
      | title  | REST Assured framework         |
      | body   | Creating a post through an API |
    Then the response status code should be 201
    And the response should match the post schema
    And the created post should contain the submitted information
    And the created post should have an ID

  @negative
  Scenario: Retrieve a post that does not exist
    When the client retrieves post 999999
    Then the response status code should be 404
    And the response body should be an empty JSON object

  @negative
  Scenario: API returns an internal server error
    When the client retrieves post 500
    Then the response status code should be 500
    And the error message should be "Internal server error"
    
  @security @negative
  Scenario: Reject a request without authentication
    When the client retrieves post 1 without authentication
    Then the response status code should be 401
    And the error message should be "Unauthorized"

  @resilience @regression
  Scenario: Recover from a transient service failure
    When the client retrieves post 503 with transient retry
    Then the response status code should be 200
    And the response should match the post schema
    And the response should contain post ID 503
