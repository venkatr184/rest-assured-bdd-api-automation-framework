package com.automation.api.model.response;

/**
 * Represents a post returned by the Posts API.
 *
 * @param userId identifier of the post owner
 * @param id post identifier
 * @param title post title
 * @param body post content
 */
public record PostResponse(int userId, int id, String title, String body) {}
