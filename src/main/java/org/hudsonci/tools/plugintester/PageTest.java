package org.hudsonci.tools.plugintester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.hudsonci.tools.plugintester.pages.LandingPage;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.sonatype.aether.artifact.Artifact;
import org.testng.ITestContext;
import org.testng.TestException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class PageTest {


  private static final String HUDSON_HOME_BASEDIR = "target/hudson";
  private static final String DOWNLOAD_BASEDIR = "target/download";
  private static final String PLUGIN_FOLDERNAME = "plugins";
  
  
  protected final List<Artifact> requiredPlugins;
  protected WebDriver driver;
  protected Server server;
  protected String baseUrl;
  
  
  public PageTest( List<Artifact> requiredPlugins) {
    this.requiredPlugins = requiredPlugins;
  }

  public PageTest(Artifact requiredPlugin) {
    this.requiredPlugins = new ArrayList<Artifact>(1);
    requiredPlugins.add(requiredPlugin);
  }
  
  protected LandingPage getLandingPage() {
    return new LandingPage(driver,baseUrl);    
  }

  
    
  @BeforeClass
  public void start(ITestContext context) throws Exception {
    System.out.println("Before Class:" +System.identityHashCode(this));
    try {
      // get parameters
      Integer port = Integer.parseInt(System.getProperty("hudson-port"));
      String coreVersion = System.getProperty("core-version");

      System.out.println("Hudson port: " +port);
      System.out.println("Core version: " +coreVersion);
      // prepare hudson home 
      File hudsonHome = new File(HUDSON_HOME_BASEDIR);
      File pluginFolder = new File(hudsonHome, PLUGIN_FOLDERNAME);

      if (hudsonHome.exists()) {
        FileUtils.deleteDirectory(hudsonHome);
      }
      hudsonHome.mkdirs();
      pluginFolder.mkdirs();      
      
      // get resolver
      ArtifactResolver resolver = ArtifactResolver.getInstance();
      // Get list of needed plugins
      Artifact core = resolver.resolve(ArtifactResolver.CORE_VERSIONS.get(coreVersion));
      List<Artifact> resolvedPlugins = resolver.resolve(requiredPlugins);

      // install plugins
      for (Artifact artifact : resolvedPlugins) {
        System.out.println("Installing plugin: "+artifact);
        FileUtils.copyFileToDirectory(artifact.getFile(), pluginFolder);
      }

      // Start jetty
      System.setProperty("HUDSON_HOME", hudsonHome.getAbsolutePath());
      System.setProperty("hudson.model.UpdateCenter.never", "true");
      System.setProperty("hudson.model.WorkspaceCleanupThread.disabled", "true");
      server = new Server(port);
      WebAppContext webapp = new WebAppContext();
      webapp.setContextPath("/hudson");
      webapp.setWar(core.getFile().getAbsolutePath());
      server.setHandler(webapp);
      server.start();

      //context.setAttribute("server", server);
      baseUrl = "http://localhost:" + port + "/hudson";
      driver = new FirefoxDriver();    
    } catch (Exception ex) {
      throw new TestException("Failed to start hudson" , ex);
    }
    
    
  }

  @AfterClass
  public void stop(ITestContext context) throws Exception {
    System.out.println("After Class:" +System.identityHashCode(this));    
    driver.quit();
    server.stop();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+ " {" + "requiredPlugins=" + requiredPlugins +  '}';
  }
  
  
  
}
