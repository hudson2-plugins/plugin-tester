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
public class CreateJob extends Page {

  @FindBy(id = "name")
  WebElement nameField;
  
  @FindBy(xpath="//input[@type='radio'][@name='mode'][@value='hudson.model.FreeStyleProject$DescriptorImpl']")
  WebElement freestyleJobRadio;

  @FindBy(xpath="//input[@type='radio'][@name='mode'][@value='hudson.model.FreeStyleProject$DescriptorImpl']")
  WebElement matrixJobRadio;
  
  @FindBy(xpath="//input[@type='radio'][@name='mode'][@value='hudson.model.FreeStyleProject$DescriptorImpl']")
  WebElement copyJobRadio;    

  @FindBy(id = "copy")
  WebElement copyFromField;  
  
  public CreateJob(WebDriver driver) {    
    super(driver,"New Job [Hudson]");    
    PageFactory.initElements(driver, this);
  }

  public ConfigureJob createFreestyleJob(String name) {
    nameField.clear();
    nameField.sendKeys(name);
    freestyleJobRadio.click();
    freestyleJobRadio.submit();
    return new ConfigureJob(driver,name);    
  }
  
  public ConfigureJob createMatrixJob(String name) {
    nameField.clear();
    nameField.sendKeys(name);
    matrixJobRadio.click();
    matrixJobRadio.submit();
    return new ConfigureJob(driver,name);    
  }
  
  public ConfigureJob copyJob(String oldName,String newName) {
    nameField.clear();
    nameField.sendKeys(newName);   
    copyFromField.clear();
    copyFromField.sendKeys(oldName);
    copyJobRadio.click();
    copyJobRadio.submit();
    return new ConfigureJob(driver, newName);
  }
  
}
