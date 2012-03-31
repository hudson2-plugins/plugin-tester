package org.hudsonci.tools.plugintester.pages.job;

import org.hudsonci.tools.plugintester.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

/**
 *
 * @author henrik
 */
public class ConfigureJob extends Page {

  @FindBy(name="_.displayname")
  WebElement displayNameField;
  
  @FindBy(xpath="//form[@name='config']")
  WebElement form;
  
  private String jobName;
  
  ConfigureJob(WebDriver driver, String name) {
    super(driver,name +" Config [Hudson]");
    this.jobName = name;
    PageFactory.initElements(driver, this);        
  }
  
  public void setDisplayName(String text) {
    displayNameField.clear();
    displayNameField.sendKeys(text);    
  }
  
  public String getDisplayName() {
    return displayNameField.getAttribute("value");
  }
  
  public DisplayJob save() {
    form.submit();
    return new DisplayJob(driver,jobName);
  }
  
  
  
}
