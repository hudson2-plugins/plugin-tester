package org.hudsonci.tools.plugintester.pages.job;

import org.hudsonci.tools.plugintester.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;


public class DisplayJob extends Page{

  @FindBy(how = How.LINK_TEXT, linkText = "Configure")
  private WebElement configureLink;
  
  private final String jobName;
  
  
  public DisplayJob(WebDriver driver,String jobName) {
    super(driver, jobName + " [Hudson]");
    this.jobName = jobName;
    PageFactory.initElements(driver, this);
  }
  
  public ConfigureJob configure() {
    configureLink.click();
    return new ConfigureJob(driver,jobName);
  }
}
