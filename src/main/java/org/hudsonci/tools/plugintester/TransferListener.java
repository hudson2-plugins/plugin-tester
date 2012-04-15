package org.hudsonci.tools.plugintester;

import org.sonatype.aether.transfer.TransferCancelledException;
import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.aether.transfer.TransferResource;

/**
 *
 * @author henrik
 */
class TransferListener implements org.sonatype.aether.transfer.TransferListener {

  public void transferInitiated(TransferEvent event) throws TransferCancelledException {
    System.out.println("Download Initiated: " + buildUrl(event) + ",size= " + event.getResource().getContentLength());
  }

  public void transferStarted(TransferEvent event) throws TransferCancelledException {
    System.out.println("Download started: " + buildUrl(event) + ",size= " + event.getResource().getContentLength());
  }

  public void transferProgressed(TransferEvent event) throws TransferCancelledException {
    // ignore
  }

  public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
    System.out.println("Download failed: " + buildUrl(event) +", exception= "+event.getException());
  }

  public void transferSucceeded(TransferEvent event) {
    System.out.println("Download completed: " +buildUrl(event) + " to "+event.getResource().getFile());
  }

  public void transferFailed(TransferEvent event) {
    System.out.println("Download failed: " + buildUrl(event) +", exception= "+event.getException());
  }
  
  private String buildUrl(TransferEvent event) {
    TransferResource resource = event.getResource();
    return resource.getResourceName() + "(" + resource.getRepositoryUrl()+ ")";
  }
  
}
