/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hudsonci.tools.plugintester.ArtifactResolver;
import org.hudsonci.tools.plugintester.PageTest;
import org.hudsonci.tools.plugintester.Util;
import org.hudsonci.tools.plugintester.pages.LandingPage;
import org.hudsonci.tools.plugintester.pages.job.ConfigureJob;
import org.hudsonci.tools.plugintester.pages.job.DisplayJob;
import org.sonatype.aether.artifact.Artifact;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 *
 * @author henrik
 */
public class BuildTimeoutTest extends PageTest {

  @DataProvider
  public static Object[][] dp() {
    ArtifactResolver resolver = ArtifactResolver.getInstance();
    List<Artifact> jenkinsVersions = resolver.getPluginVersions("org.jenkins-ci.plugins", "build-timeout", "[1.6,)");
    List<Artifact> hudsonVersions = resolver.getPluginVersions("org.jvnet.hudson.plugin", "build-timeout", "[1.6,)");
    List<Artifact> pluginVersions = new ArrayList<Artifact>();
    pluginVersions.addAll(hudsonVersions);
    pluginVersions.addAll(jenkinsVersions);
    return Util.convertList(pluginVersions);
  }

  @Factory(dataProvider = "dp")
  public BuildTimeoutTest(Artifact requiredPlugin) {
    super(requiredPlugin);
  }

  @Test
  public void canBeConfigured() {
    LandingPage landing = getLandingPage();
    ConfigureJob configureJob = landing.createJob().createFreestyleJob("BuildTimeoutTest_canBeConfigured");

    Assert.assertFalse(configureJob.buildTimeoutMinutesField.isDisplayed());
    Assert.assertFalse(configureJob.buildTimeoutFailCheckbox.isDisplayed());

    configureJob.buildTimeoutEnableCheckbox.click();

    Assert.assertTrue(configureJob.buildTimeoutMinutesField.isDisplayed());
    Assert.assertTrue(configureJob.buildTimeoutFailCheckbox.isDisplayed());

    configureJob.buildTimeoutMinutesField.sendKeys("4");

    DisplayJob display = configureJob.save();

    configureJob = display.configure();
    Assert.assertTrue(configureJob.buildTimeoutMinutesField.isDisplayed());
    Assert.assertTrue(configureJob.buildTimeoutFailCheckbox.isDisplayed());

    Assert.assertEquals(configureJob.buildTimeoutMinutesField.getAttribute("value"), "4");
  }

  @Test
  public void buildTimeoutAtLeastTreeMinutes() {
    LandingPage landing = getLandingPage();
    ConfigureJob configureJob = landing.createJob().createFreestyleJob("BuildTimeoutTest_buildTimeoutAtLeastTreeMinutes");

    // Set minutes to one
    configureJob.buildTimeoutEnableCheckbox.click();

    configureJob.buildTimeoutMinutesField.sendKeys("1");
    DisplayJob display = configureJob.save();

    configureJob = display.configure();
    Assert.assertEquals(configureJob.buildTimeoutMinutesField.getAttribute("value"), "3");

    // set minutes to two
    configureJob.buildTimeoutMinutesField.clear();
    configureJob.buildTimeoutMinutesField.sendKeys("2");
    display = configureJob.save();

    configureJob = display.configure();
    Assert.assertEquals(configureJob.buildTimeoutMinutesField.getAttribute("value"), "3");
  }
}
