/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tool.plugintester;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author henrik
 */
public class LandingPage {

  private final WebDriver driver;
  private final String url;

  public LandingPage(WebDriver driver, String url) {
    this.driver = driver;
    this.url = url;

    driver.get(this.url);
    (new WebDriverWait(driver, 600)).until(new ExpectedCondition<Boolean>() {

      public Boolean apply(WebDriver d) {
        d.navigate().refresh();
        System.out.println("Title: " + d.navigate().getTitle());
        return d.getTitle().toLowerCase().startsWith("dashboard [hudson]");
      }
    });
  }
  
  public NewJobPage clickNewJob() {
    WebElement findElement = driver.findElement(By.ByLinkText.linkText("New Job"));
    findElement.click();
    return new NewJobPage(driver);
  }
}
