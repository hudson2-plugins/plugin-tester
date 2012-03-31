/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester.tests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hudsonci.tools.plugintester.BaseTest;
import org.hudsonci.tools.plugintester.pages.LandingPage;
import org.hudsonci.tools.plugintester.pages.NewJobPage;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author henrik
 */

public class DisplayNameTest extends BaseTest{

  @DataProvider(name="displayVersions")
  public Object[][] getDisplayVersions() {
    Object[] v1 = { "1.1"};
    Object[] v0 = { "1.0"};
    Object[][] versions = { v0/*,v1*/};
    return versions;
  }
  
  @Test(dataProvider="displayVersions")
  
  public void gotoNewJobPage(ITestContext context) throws IOException {
      LandingPage landing = (LandingPage) context.getAttribute("landing");
      NewJobPage newJobPage = landing.newJob();
      newJobPage.createFreestyleJob("MyProject");
      
  }

  @Override
  protected Artifact getPluginNeeded() {
    return new DefaultArtifact("org.hudsonci.plugins:displayname:1.1");
  }  
}
