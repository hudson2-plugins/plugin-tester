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
public class DisplayNameTest extends PageTest {

  @DataProvider()
  public static Object[][] dp() {
    ArtifactResolver resolver = ArtifactResolver.getInstance();
    List<Artifact> pluginVersions = resolver.getPluginVersions("org.hudsonci.plugins", "displayname", "[0,)");
    return Util.convertList(pluginVersions);
  }

  @Factory(dataProvider="dp")
  public DisplayNameTest(Artifact requiredPlugin) {
    super(requiredPlugin);
  }

  @Test
  public void gotoNewJobPage() {
    LandingPage landing = getLandingPage();
    ConfigureJob configureJob = landing.createJob().createFreestyleJob("DisplayNameTest");
    configureJob.setDisplayName("Hello word");
    DisplayJob job = configureJob.save();

    configureJob = job.configure();
    Assert.assertEquals(configureJob.getDisplayName(), "Hello word");
  }  
}
