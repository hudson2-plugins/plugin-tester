/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester.tests;

import java.util.List;
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
    List<Artifact> pluginVersions = resolver.getPluginVersions("org.jenkins-ci.plugins", "build-timeout", "[1.6,)");    
    return Util.convertList(pluginVersions);
  }

  @Factory(dataProvider = "dp")
  public BuildTimeoutTest(Artifact requiredPlugin) {
    super(requiredPlugin);
  }
  
  @Test 
  public void canBeConfigured() {
    LandingPage landing = getLandingPage();
    ConfigureJob configureJob = landing.createJob().createFreestyleJob("BuildTimeoutTest");
    
    Assert.assertFalse(configureJob.buildTimeoutMinutesField.isDisplayed());
    Assert.assertFalse(configureJob.buildTimeoutFailCheckbox.isDisplayed());
    
    configureJob.buildTimeoutEnableCheckbox.click();

    Assert.assertTrue(configureJob.buildTimeoutMinutesField.isDisplayed());
    Assert.assertTrue(configureJob.buildTimeoutFailCheckbox.isDisplayed());
    
    configureJob.buildTimeoutMinutesField.sendKeys("2");
    DisplayJob display = configureJob.save();
    
    configureJob = display.configure();

    Assert.assertTrue(configureJob.buildTimeoutMinutesField.isDisplayed());
    Assert.assertTrue(configureJob.buildTimeoutFailCheckbox.isDisplayed());
    
    Assert.assertEquals(configureJob.buildTimeoutMinutesField.getAttribute("value"),"2");    
  }  
}
