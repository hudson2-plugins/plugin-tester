package org.hudsonci.tools.plugintester;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
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
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.PageFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.version.Version;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

/**
 *
 * @author henrik
 */
public abstract class BaseTest {

  private static final String HUDSON_HOME_BASEDIR = "target/hudson";
  private static final String DOWNLOAD_BASEDIR = "target/download";
  private static final String PLUGIN_FOLDERNAME = "plugins";

  @Parameters(value={"browser", "core-version", "hudson-port", "local-repo","remote-repo"})
  @BeforeTest
  public void beforeTest(ITestContext context, String browser, String coreVersion, String portString, String localRepo,String remoteRepo) {
    System.out.println("Browser: " + browser);
    System.out.println("Core version: " + coreVersion);
    System.out.println("Port: " + portString);
    System.out.println("Local repository: " + localRepo);
    System.out.println("Remote reposioty: " + remoteRepo);    
  
    context.setAttribute("browser", browser);
    context.setAttribute("port", portString);    
    context.setAttribute("core-version", coreVersion); 
    
    ArtifactResolver resolver = new ArtifactResolver(localRepo,remoteRepo);
    context.setAttribute("resolver", resolver);    
  }
  
  
  @BeforeMethod
  public void start(ITestContext context,Object[] testParameters) throws Exception{
    
    String browser = (String) context.getAttribute("browser");
    String portString = (String) context.getAttribute("port");    
    String coreVersion =  (String) context.getAttribute("core-version");     
    
    ArtifactResolver resolver = (ArtifactResolver) context.getAttribute("resolver");
    // Get list of needed plugins
    List<Artifact> plugins = getPluginsNeeded(testParameters);
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
      HtmlUnitDriver hdriver = new HtmlUnitDriver();
      driver = hdriver; 
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
    WebDriver driver = (WebDriver) context.getAttribute("driver");
    driver.quit();
    server.stop();        
    context.removeAttribute("server");
    context.removeAttribute("driver");
  }

  protected List<Artifact> getPluginsNeeded(Object[] testParameters) {
    List<Artifact> plugins = new ArrayList<Artifact>();    
    plugins.add(getPluginNeeded(testParameters));
    return plugins;
  }
  
  protected Artifact getPluginNeeded(Object[] testParameters) {
    return null;
  }
  
  protected Object[][] getAvailablePluginVersions(ITestContext context, String groupID,String artifactId,String minVersion) throws Exception {
    ArtifactResolver resolver = (ArtifactResolver) context.getAttribute("resolver");
    List<Version> availableVersions = resolver.getAvailableVersions(groupID, artifactId, minVersion);
    Object[][] invocation = new Object[availableVersions.size()][];
    
    for (int i=0;i < availableVersions.size(); i++) {
      Object[] params = { availableVersions.get(i).toString() };
      invocation[i] = params;
    }    
    return invocation;    
  }
}
