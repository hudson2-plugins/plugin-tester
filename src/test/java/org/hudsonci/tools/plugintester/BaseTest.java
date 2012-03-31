/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.hudsonci.tools.plugintester.pages.LandingPage;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;
import org.sonatype.aether.artifact.Artifact;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

/**
 *
 * @author henrik
 */
public abstract class BaseTest {

  private static final String HUDSON_HOME_BASEDIR = "target/hudson";
  private static final String DOWNLOAD_BASEDIR = "target/download";
  private static final String PLUGIN_FOLDERNAME = "plugins";

  @Parameters({"browser", "core-version", "hudson-port","local-repo","remote-repo"})
  @BeforeMethod
  public void start(ITestContext context, String browser, String coreVersion, String portString, String localRepo,String remoteRepo) throws Exception{
        
    System.out.println("browser: " + browser);
    System.out.println("core-version: " + coreVersion);
    System.out.println("port: " + portString);
    System.out.println("local-repo: " + localRepo);
    System.out.println("remote-repo: " + remoteRepo);
    
    context.setAttribute("browser", browser);
    context.setAttribute("port", portString);    
    context.setAttribute("core-version", coreVersion);      
    
    
    ArtifactResolver resolver = new ArtifactResolver(localRepo,remoteRepo);
    // Get list of needed plugins
    List<Artifact> plugins = getPluginsNeeded();
    List<Artifact> resolvedPlugins = resolver.resolve(plugins);
    
    // add required core to list of artifacts    
    Artifact coreArtifact = resolver.resolve(ArtifactResolver.CORE_VERSIONS.get(coreVersion));
  
   
    // prepare hudson home 
    File hudsonHome = new File(HUDSON_HOME_BASEDIR);
    File pluginFolder = new File(hudsonHome, PLUGIN_FOLDERNAME);
    File downloadFolder = new File(DOWNLOAD_BASEDIR);

    if (hudsonHome.exists()) {
      FileUtils.deleteDirectory(hudsonHome);
    }
    hudsonHome.mkdirs();
    pluginFolder.mkdirs();

   
    
    // install plugins
    for (Artifact artifact : resolvedPlugins) {
      FileUtils.copyFileToDirectory(artifact.getFile(), pluginFolder);
    }

    // Start jetty
    System.setProperty("HUDSON_HOME", hudsonHome.getAbsolutePath());
    Server server = new Server(Integer.parseInt(portString));
    WebAppContext webapp = new WebAppContext();
    webapp.setContextPath("/hudson");
    webapp.setWar(coreArtifact.getFile().getAbsolutePath());
    server.setHandler(webapp);
    server.start();

    // Start selenium
    WebDriver driver = null;
    if ("htmlunit".equals(browser)) {
      driver = new HtmlUnitDriver();
    }

    if ("firefox".equals(browser)) {
      driver = new FirefoxDriver();
    }

    if ("chrome".equals(browser)) {
      driver = new ChromeDriver();
    }

    if (driver == null) {
      throw new RuntimeException("Unknown browser:" + browser);
    }
    // setup context    
    context.setAttribute("server", server);
    context.setAttribute("driver", driver);
    
    // load landing page
    String url = "http://localhost:" + context.getAttribute("port") + "/hudson";
    driver.get(url);
    LandingPage page = PageFactory.initElements(driver, LandingPage.class);
    context.setAttribute("landing", page);
  }

  @AfterMethod
  public void stopSelenium(ITestContext context) throws Exception {
    Server server = (Server) context.getAttribute("server");
    server.stop();    
    WebDriver driver = (WebDriver) context.getAttribute("driver");
    driver.quit();
  }

  protected List<Artifact> getPluginsNeeded() {
    List<Artifact> plugins = new ArrayList<Artifact>();    
    plugins.add(getPluginNeeded());
    return plugins;
  }
  
  protected Artifact getPluginNeeded() {
    return null;
  }
}
