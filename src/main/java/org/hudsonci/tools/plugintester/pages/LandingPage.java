package org.hudsonci.tools.plugintester.pages;

import org.hudsonci.tools.plugintester.Page;
import org.hudsonci.tools.plugintester.pages.job.CreateJob;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

/**
 *
 * @author henrik
 */
public class LandingPage extends Page {

  private static final int MAX_STARTUP_WAIT = 60;
  
  @FindBy(linkText="New Job")
  WebElement createJobLink;
  
  @FindBy(linkText = "Manage Hudson")
  WebElement managehudsonLink;

    
  public LandingPage(WebDriver driver,String baseUrl) {
    super(driver);
    this.driver.get(baseUrl);
    
    for (int i = 0; i < MAX_STARTUP_WAIT; i++) {
      if ("dashboard [hudson]".equalsIgnoreCase(driver.getTitle())) {
        break;
      }
      try {
        Thread.sleep(2000);
        driver.navigate().refresh();
      } catch (InterruptedException ex) {
        // ignore
      }
    }
    Assert.assertEquals(driver.getTitle().toLowerCase(), "dashboard [hudson]".toLowerCase());
    PageFactory.initElements(driver,this);
  }


  public CreateJob createJob() {
    createJobLink.click();
    return new CreateJob(driver);
  }
}
