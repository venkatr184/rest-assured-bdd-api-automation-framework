package com.automation.api.context;

import io.restassured.response.Response;

public class ScenarioContext {

  private Response response;

  public Response getResponse() {
    if (response == null) {
      throw new IllegalStateException("No API response is available in the current scenario.");
    }

    return response;
  }

  public void setResponse(Response response) {
    if (response == null) {
      throw new IllegalArgumentException("API response cannot be null.");
    }

    this.response = response;
  }

  public boolean hasResponse() {
    return response != null;
  }

  public void clear() {
    response = null;
  }
}
