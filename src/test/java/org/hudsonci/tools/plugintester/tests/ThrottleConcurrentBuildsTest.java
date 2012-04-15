package org.hudsonci.tools.plugintester.tests;

import java.util.ArrayList;
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

public class ThrottleConcurrentBuildsTest extends PageTest {

  @DataProvider
  public static Object[][] dp() {
    ArtifactResolver resolver = ArtifactResolver.getInstance();
    List<Artifact> pluginVersions = resolver.getPluginVersions("org.jenkins-ci.plugins", "throttle-concurrents", "[1.7,)");    
    return Util.convertList(pluginVersions);
  }

  @Factory(dataProvider = "dp")
  public ThrottleConcurrentBuildsTest(Artifact requiredPlugin) {
    super(requiredPlugin);
  }

  @Test 
  public void canBeConfigured() {
    LandingPage landing = getLandingPage();
    ConfigureJob configureJob = landing.createJob().createFreestyleJob("ThrottleConcurrentBuildsTest");

    // Assert the advanced fields are not visible yet
    
    Assert.assertFalse(configureJob.throttleByProjectRadio.isDisplayed());
    Assert.assertFalse(configureJob.throttleByCategoryRadio.isDisplayed());
    Assert.assertFalse(configureJob.throttleMaxTotalField.isDisplayed());
    Assert.assertFalse(configureJob.throttleMaxPerNodeField.isDisplayed());        
    
    // Enable and assert fields visible
    configureJob.throtteEnableCheckbox.click();
    Assert.assertTrue(configureJob.throttleByProjectRadio.isDisplayed());
    Assert.assertTrue(configureJob.throttleByCategoryRadio.isDisplayed());
    Assert.assertTrue(configureJob.throttleMaxTotalField.isDisplayed());
    Assert.assertTrue(configureJob.throttleMaxPerNodeField.isDisplayed());
    
    // configure throttle
    configureJob.throttleByProjectRadio.click();
    configureJob.throttleMaxPerNodeField.clear();
    configureJob.throttleMaxPerNodeField.sendKeys("1");
    configureJob.throttleMaxTotalField.clear();
    configureJob.throttleMaxTotalField.sendKeys("3");        
    
    // save job
    DisplayJob job = configureJob.save();
    
    // Check that values are saved
    configureJob = job.configure();
    Assert.assertTrue(configureJob.throtteEnableCheckbox.isSelected());
    Assert.assertTrue(configureJob.throttleByProjectRadio.isDisplayed());
    Assert.assertTrue(configureJob.throttleByProjectRadio.isSelected());
    Assert.assertTrue(configureJob.throttleByCategoryRadio.isDisplayed());
    Assert.assertTrue(configureJob.throttleMaxTotalField.isDisplayed());
    Assert.assertEquals(configureJob.throttleMaxTotalField.getAttribute("value"),"3");
    Assert.assertTrue(configureJob.throttleMaxPerNodeField.isDisplayed());    
    Assert.assertEquals(configureJob.throttleMaxPerNodeField.getAttribute("value"),"1");
  }
}
