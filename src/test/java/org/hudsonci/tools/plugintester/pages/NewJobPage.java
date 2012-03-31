/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester.pages;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

/**
 *
 * @author henrik
 */
public class NewJobPage extends Page {

  @FindBy(id = "name")
  WebElement inputField;
  
  @FindBy(xpath="//input[@type='radio'][@name='mode'][@value='hudson.model.FreeStyleProject$DescriptorImpl']")
  WebElement freestyleJobRadio;

  @FindBy(xpath="//input[@type='radio'][@name='mode'][@value='hudson.model.FreeStyleProject$DescriptorImpl']")
  WebElement matrixJobRadio;  
  
  public NewJobPage(WebDriver driver) {    
    super(driver,"New Job [Hudson]");    
    PageFactory.initElements(driver, this);
  }

  public void createFreestyleJob(String name) {
    inputField.clear();
    inputField.sendKeys(name);
    freestyleJobRadio.click();
    
  }  
}
