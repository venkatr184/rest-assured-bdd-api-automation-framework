package com.automation.api.hooks;

import com.automation.api.context.ScenarioContext;
import com.automation.api.mock.WireMockService;
import com.automation.api.utility.CorrelationIdContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class TestHooks {

  private final ScenarioContext scenarioContext;

  public TestHooks(ScenarioContext scenarioContext) {
    this.scenarioContext = scenarioContext;
  }

  @BeforeAll
  public static void beforeAllScenarios() {
    WireMockService.start();
  }

  @AfterAll
  public static void afterAllScenarios() {
    WireMockService.stop();
  }

  @Before
  public void beforeScenario(Scenario scenario) {
    CorrelationIdContext.initialize();

    scenario.log(
        "Starting scenario: "
            + scenario.getName()
            + " | Correlation ID: "
            + CorrelationIdContext.get());
  }

  @After
  public void afterScenario(Scenario scenario) {
    try {
      if (!scenario.isFailed()) {
        WireMockService.verifyCorrelationId(CorrelationIdContext.get());
      }

      attachResponseWhenFailed(scenario);
    } finally {
      scenarioContext.clear();
      CorrelationIdContext.clear();
    }
  }

  private void attachResponseWhenFailed(Scenario scenario) {
    if (!scenario.isFailed() || !scenarioContext.hasResponse()) {
      return;
    }

    String responseBody = scenarioContext.getResponse().asPrettyString();

    scenario.attach(responseBody, "application/json", "API response");
  }
}
