/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester;

import java.io.IOException;
import org.hudsonci.tool.plugintester.LandingPage;
import org.testng.ITestContext;
import org.testng.annotations.Test;

/**
 *
 * @author henrik
 */

public class DisplayNameTest extends BaseTest{

  @Override
  protected void installRequiredPlugins() throws IOException{
    installPlugin("displayname-1.1.hpi");
  }  
  
  @Test
  public void gotoNewJobPage(ITestContext context) throws IOException {
    LandingPage landing = (LandingPage) context.getAttribute("landing");
    landing.clickNewJob();
  }


  
}
