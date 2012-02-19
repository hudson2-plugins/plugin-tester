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

  @FindBy(how= How.LINK_TEXT, linkText="New Job")
  WebElement newJobLink;
  
  public LandingPage(WebDriver driver) {
    this.driver = driver;
    System.out.println("Created new landing page");
  }
  
  public NewJobPage clickNewJob() {
    System.out.println("Clicking "+ newJobLink);
    newJobLink.click();
    return new NewJobPage(driver);
  }
}
