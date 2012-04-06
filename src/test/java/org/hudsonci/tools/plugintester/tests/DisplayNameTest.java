/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester.tests;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hudsonci.tools.plugintester.ArtifactResolver;
import org.hudsonci.tools.plugintester.BaseTest;
import org.hudsonci.tools.plugintester.pages.job.ConfigureJob;
import org.hudsonci.tools.plugintester.pages.job.DisplayJob;
import org.hudsonci.tools.plugintester.pages.LandingPage;
import org.hudsonci.tools.plugintester.pages.job.CreateJob;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author henrik
 */

public class DisplayNameTest extends BaseTest{

  @DataProvider(name="displayVersions")
  public Object[][] getDisplayVersions(ITestContext context) throws Exception {
    ArtifactResolver resolver = (ArtifactResolver) context.getAttribute("resolver");
    return getAvailablePluginVersions(context,"org.hudsonci.plugins", "displayname", "0");
  }
  
  @Test(dataProvider="displayVersions")  
  public void gotoNewJobPage(ITestContext context,String version) throws IOException {
      LandingPage landing = (LandingPage) context.getAttribute("landing");
      ConfigureJob configureJob = landing.createJob().createFreestyleJob("MyProject");
      configureJob.setDisplayName("Hello word");
      DisplayJob job = configureJob.save();
      
      configureJob = job.configure();
      Assert.assertEquals(configureJob.getDisplayName(),"Hello word");
  }

  @Override
  protected Artifact getPluginNeeded(Object[] testParameters) {
    String currentVersion = (String) testParameters[1];
    return new DefaultArtifact("org.hudsonci.plugins", "displayname", "hpi", currentVersion);
  }  
}
