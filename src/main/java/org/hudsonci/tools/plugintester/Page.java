package org.hudsonci.tools.plugintester;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
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
    this.driver = driver;
  }
  
  public Page(WebDriver driver,String expectedTitle) {
    this.driver = driver;
    System.out.println("Loading page: "+ this.getClass() + " with title: " +  driver.getTitle());
    waitForFooter();
    
    if (expectedTitle != null) {
      Assert.assertEquals(driver.getTitle().toLowerCase(), expectedTitle.toLowerCase());
    }
    System.out.println("Loaded page: "+ this.getClass() + " with title: " +  driver.getTitle());  
  }
  
  public WebDriver getDriver(){
    return driver;
  }
  
  protected final void waitForFooter() {
    WebDriverWait _wait = new WebDriverWait(driver, 60);
    _wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Hudson ver.")));    
  }

  public WebElement findElement(By by) {
    return driver.findElement(by);
  }
  
  public List<WebElement> findElements(By by) {
    return driver.findElements(by);
  }  
  
  public String getTitle() {
    return driver.getTitle();
  }
}
