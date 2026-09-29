package com.automation.api.model.request;

public record CreatePostRequest(
        int userId,
        String title,
        String body) {
}