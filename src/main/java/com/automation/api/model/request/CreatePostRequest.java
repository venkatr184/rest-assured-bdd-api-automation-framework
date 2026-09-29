package com.automation.api.model.request;

/**
 * Represents the request payload used to create a post.
 *
 * @param userId identifier of the user creating the post
 * @param title post title
 * @param body post content
 */
public record CreatePostRequest(int userId, String title, String body) {}
