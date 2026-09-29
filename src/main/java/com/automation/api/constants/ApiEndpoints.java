package com.automation.api.constants;

/** Defines endpoint paths used by the Posts API client. */
public final class ApiEndpoints {

  public static final String POSTS = "/posts";
  public static final String POST_BY_ID = POSTS + "/{postId}";

  private ApiEndpoints() {
    // Prevent object creation.
  }
}
