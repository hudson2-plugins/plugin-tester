/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tool.plugintester;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author henrik
 */
public class NewJobPage {
  
  private final WebDriver driver;

  public NewJobPage(WebDriver driver) {
    this.driver = driver;
    
    (new WebDriverWait(driver, 600)).until(new ExpectedCondition<Boolean>() {

      public Boolean apply(WebDriver d) {
        System.out.println("Title: " + d.getTitle());
        return d.getTitle().toLowerCase().startsWith("new job [hudson]");
      }
    });    
  }
  
  
}
