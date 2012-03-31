package org.hudsonci.tools.plugintester.pages;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

/**
 *
 * @author henrik
 */
public class LandingPage extends Page {

  private static final int MAX_STARTUP_WAIT = 60;
  @FindBy(how = How.LINK_TEXT, linkText = "New Job")
  WebElement newJobLink;
  @FindBy(how = How.LINK_TEXT, linkText = "Manage Hudson")
  WebElement managehudsonLink;

  public LandingPage(WebDriver driver) {
    super(driver);
    for (int i = 0; i < MAX_STARTUP_WAIT; i++) {
      if ("dashboard [hudson]".equalsIgnoreCase(driver.getTitle())) {
        break;
      }
      try {
        Thread.sleep(2000);
        driver.navigate().refresh();
        System.out.println("attemp "+i);
      } catch (InterruptedException ex) {
        // ignore
      }

    }
    Assert.assertEquals(driver.getTitle().toLowerCase(), "dashboard [hudson]".toLowerCase());
  }

  public NewJobPage newJob() {
    newJobLink.click();
    return new NewJobPage(driver);
  }
}
