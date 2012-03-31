/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 *
 * @author henrik
 */
public class Page {
  
  protected final WebDriver driver;

  public Page(WebDriver driver) {
    this(driver,null);
  }
  
  public Page(WebDriver driver,String expectedTitle) {
    this.driver = driver;
    waitForFooter();
    if (expectedTitle != null) {
      Assert.assertEquals(driver.getTitle().toLowerCase(), expectedTitle.toLowerCase());
    }
      
  }
  
  
  
  protected final void waitForFooter() {
    WebDriverWait _wait = new WebDriverWait(driver, 60);
    _wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Hudson ver.")));    
  }
  
}
