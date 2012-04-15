package org.hudsonci.tools.plugintester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.*;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.version.Version;
import org.testng.TestException;

public class ArtifactResolver {

  public static final Map<String, Artifact> CORE_VERSIONS = new HashMap<String, Artifact>();
  private static final Artifact HUDSON_212 = new DefaultArtifact("org.jvnet.hudson.main", "hudson-war", "war", "2.1.2");
  private static final Artifact HUDSON_220 = new DefaultArtifact("org.jvnet.hudson.main", "hudson-war", "war", "2.2.0");
  private static final Artifact HUDSON_300M0 = new DefaultArtifact("org.eclipse.hudson.main", "hudson-war", "war", "3.0.0-M0");
  private static final Artifact HUDSON_300M0_1 = new DefaultArtifact("org.eclipse.hudson.main", "hudson-war", "war", "3.0.0-M0-1");
  private static final Artifact HUDSON_300M1 = new DefaultArtifact("org.eclipse.hudson.main", "hudson-war", "war", "3.0.0-M1");
  private static final Artifact HUDSON_300M2 = new DefaultArtifact("org.eclipse.hudson.main", "hudson-war", "war", "3.0.0-M2");
  private static final Artifact HUDSON_LATEST = new DefaultArtifact("org.eclipse.hudson.main", "hudson-war", "war", "LATEST");
  
  private static final ArtifactResolver instance = new ArtifactResolver();

  static {
    CORE_VERSIONS.put("2.1.2", HUDSON_212);
    CORE_VERSIONS.put("2.2.0", HUDSON_220);
    CORE_VERSIONS.put("3.0.0-M0", HUDSON_300M0);
    CORE_VERSIONS.put("3.0.0-M0-1", HUDSON_300M0_1);
    CORE_VERSIONS.put("3.0.0-M1", HUDSON_300M1);
    CORE_VERSIONS.put("3.0.0-M2", HUDSON_300M2);
    CORE_VERSIONS.put("LATEST", HUDSON_LATEST);
  }

  public static ArtifactResolver getInstance() {
    return instance;
  }
  private final LocalRepository localRepository;
  private List<RemoteRepository> remoteRepositories = new ArrayList<RemoteRepository>();

  private ArtifactResolver() {
    this.localRepository = new LocalRepository(System.getProperty("maven.repo.local"));
    System.out.println("Local repository: "+ localRepository.getBasedir());
    String remotes = System.getProperty("remotes");

    for (String remote : remotes.split(",")) {      
      RemoteRepository repo = new RemoteRepository(remote, "default", System.getProperty("repo.")+remote);
      System.out.println("Remote repository: "+ remote + " -> "+ repo.getUrl());
      remoteRepositories.add(repo);
    }

  }

  public Artifact resolve(Artifact artifact) {
    List<Artifact> artifacts = new ArrayList<Artifact>();
    artifacts.add(artifact);
    List<Artifact> result = resolve(artifacts);
    return result.get(0);
  }

  public List<Artifact> resolve(List<Artifact> artifacts) {
    try {
      // setup aether
      RepositorySystem repoSystem = newRepositorySystem();
      RepositorySystemSession session = newSession(repoSystem);

      // create the request
      List<ArtifactRequest> requests = new ArrayList<ArtifactRequest>();
      for (Artifact artifact : artifacts) {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(remoteRepositories);
        requests.add(artifactRequest);
      }
      List<ArtifactResult> results = repoSystem.resolveArtifacts(session, requests);

      // convert to artifacts
      List<Artifact> resolved = new ArrayList<Artifact>();
      for (ArtifactResult result : results) {
        resolved.add(result.getArtifact());
      }

      return resolved;
    } catch (ArtifactResolutionException ex) {
      throw new TestException("Failed to resolve artifacts: " + artifacts, ex);
    }
  }

  public List<Artifact> getPluginVersions(String groupId, String artifactId, String range) {
    try {
      List<Artifact> result = new ArrayList<Artifact>();

      // setup aether
      RepositorySystem repoSystem = newRepositorySystem();
      RepositorySystemSession session = newSession(repoSystem);

      // create the request
      VersionRangeRequest rangeRequest = new VersionRangeRequest();
      Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + range);
      rangeRequest.setArtifact(artifact);
      rangeRequest.setRepositories(remoteRepositories);
      List<Version> versions = repoSystem.resolveVersionRange(session, rangeRequest).getVersions();

      // convert to artifacts
      for (Version version : versions) {
        Artifact resolvedArtifact = new DefaultArtifact(groupId, artifactId, "hpi", version.toString());
        result.add(resolvedArtifact);
      }
      return result;
    } catch (VersionRangeResolutionException ex) {
      throw new TestException("Failed to get available versions for: " + groupId + ":" + artifactId, ex);
    }
  }

  private RepositorySystemSession newSession(RepositorySystem system) {
    MavenRepositorySystemSession session = new MavenRepositorySystemSession();
    session.setTransferListener(new TransferListener());
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepository));

    return session;
  }

  private RepositorySystem newRepositorySystem() {
    try {
      return new DefaultPlexusContainer().lookup(RepositorySystem.class);
    } catch (ComponentLookupException ex) {
      throw new TestException("Could not init aether repository system", ex);
    } catch (PlexusContainerException ex) {
      throw new TestException("Could not init aether repository system", ex);
    }
  }
}
