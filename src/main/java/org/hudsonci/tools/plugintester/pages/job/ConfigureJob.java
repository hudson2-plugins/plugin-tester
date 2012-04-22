package org.hudsonci.tools.plugintester.pages.job;

import org.hudsonci.tools.plugintester.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 *
 * @author henrik
 */
public class ConfigureJob extends Page {


  @FindBy(xpath="//form[@name='config']")  
  public WebElement configureForm;
  
  @FindBy(xpath="//span[@id='yui-gen21']/span/button")
  public WebElement saveButton;

  // The following elements are for the Displayname plugin
  
  @FindBy(name="_.displayname")
  public WebElement displayNameField;
      
  // The following elements are for the Throttle concurrent builds plugin
  @FindBy(name="throttleEnabled")
  public WebElement throtteEnableCheckbox;
  
  @FindBy(xpath="//input[@name='throttleOption'][@value='project']")
  public WebElement throttleByProjectRadio;

  @FindBy(xpath="//input[@name='throttleOption'][@value='category']")
  public WebElement throttleByCategoryRadio;
  
  @FindBy(name="_.maxConcurrentTotal")
  public WebElement throttleMaxTotalField;  

  @FindBy(name="_.maxConcurrentPerNode")
  public WebElement throttleMaxPerNodeField;    
  
  // The following elements are for the build timeout plugin
  @FindBy(name="hudson-plugins-build_timeout-BuildTimeoutWrapper")
  public WebElement buildTimeoutEnableCheckbox;
  
  @FindBy(name="build-timeout.timeoutMinutes")
  public WebElement buildTimeoutMinutesField;

  @FindBy(name="build-timeout.failBuild")
  public WebElement buildTimeoutFailCheckbox;
       
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
    saveButton.click();
    System.out.println("Submitted form");
    return new DisplayJob(driver,jobName);
    
    
  }   
  
  
}
