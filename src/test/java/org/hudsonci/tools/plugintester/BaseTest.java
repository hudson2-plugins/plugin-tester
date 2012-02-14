/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.hudsonci.tool.plugintester.LandingPage;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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

  @Parameters({"browser", "core-version", "hudson-port"})
  @BeforeClass
  public void startHudson(ITestContext context, String browser, String coreVersion, String portString) throws IOException, Exception {
    System.out.println("browser: " + browser);
    System.out.println("core-version: " + coreVersion);
    System.out.println("port: " + portString);


    File hudsonHome = new File(HUDSON_HOME_BASEDIR);
    File pluginFolder = new File(hudsonHome, PLUGIN_FOLDERNAME);
    File downloadFolder = new File(DOWNLOAD_BASEDIR);

    if (hudsonHome.exists()) {
      FileUtils.deleteDirectory(hudsonHome);
    }
    hudsonHome.mkdirs();

    pluginFolder.mkdirs();
    installRequiredPlugins();

    System.setProperty("HUDSON_HOME", hudsonHome.getAbsolutePath());
    Server server = new Server(Integer.parseInt(portString));
    WebAppContext webapp = new WebAppContext();
    webapp.setContextPath("/hudson");
    webapp.setWar("target/download/hudson-" + coreVersion + ".war");
    server.setHandler(webapp);
    server.start();
    
    context.setAttribute("server", server);
    context.setAttribute("port", portString);
    context.setAttribute("browser", browser);
    context.setAttribute("coreVersion", coreVersion);
    
  }

  @AfterClass
  public void shutdownHudson(ITestContext context) throws Exception {
    Server server = (Server) context.getAttribute("server");
    server.stop();
  }
  
  @BeforeMethod
  public void startSelenium(ITestContext context)  {
    String browser = (String) context.getAttribute("browser");
    
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
      throw new RuntimeException("Unknown browser:" +browser);
    }
    context.setAttribute("driver", driver);
    String url = "http://localhost:"+context.getAttribute("port")+"/hudson";
    LandingPage page = new LandingPage(driver, url);
    context.setAttribute("landing", page);   
  }
  
  @AfterMethod
  public void stopSelenium(ITestContext context)  {
    WebDriver driver = (WebDriver) context.getAttribute("driver");
    driver.quit();
  }

  protected abstract void installRequiredPlugins() throws IOException;

  protected void installPlugin(String name) throws IOException {
    File hudsonHome = new File(HUDSON_HOME_BASEDIR);
    File pluginFolder = new File(hudsonHome, PLUGIN_FOLDERNAME);
    File downloadFolder = new File(DOWNLOAD_BASEDIR);
    FileUtils.copyFileToDirectory(new File(downloadFolder, name), pluginFolder);
  }
}
