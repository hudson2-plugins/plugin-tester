/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.tools.plugintester;

import java.util.List;
import org.sonatype.aether.artifact.Artifact;
import org.testng.TestException;

/**
 *
 * @author henrik
 */
public class Util {
  
  public static Object[][] convertList(List<Artifact> collection) {
    
    if (collection == null || collection.size()==0) {
      throw new TestException("No parameters to convert to Object[][]");
    }
    
    int size = collection.size();
    
    Object[][] result = new Object[size][];
    for (int i = 0;i < size; i++) {
      Object[] entry = { collection.get(i) };
      result[i] = entry;      
    }
    return result;
  }
}
