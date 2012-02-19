/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tool.plugintester;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author henrik
 */
public class LandingPage {

  private final WebDriver driver;
  private final String url;

  @FindBy(partialLinkText="New Job")
  WebElement newJobLink;
  
  public LandingPage(WebDriver driver, String url) {
    this.driver = driver;
    this.url = url;

    driver.get(this.url);
    (new WebDriverWait(driver, 600)).until(new ExpectedCondition<Boolean>() {

      public Boolean apply(WebDriver d) {
        return d.getTitle().toLowerCase().startsWith("dashboard [hudson]");
      }
    });
  }
  
  public NewJobPage clickNewJob() {    
    newJobLink.click();
    return new NewJobPage(driver);
  }
}
