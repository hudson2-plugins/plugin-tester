package org.hudsonci.tools.plugintester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.version.Version;

/**
 *
 * @author henrik
 */
public class ArtifactResolver {

  public static final Map<String, Artifact> CORE_VERSIONS = new HashMap<String, Artifact>();
  private static final Artifact HUDSON_212 = new DefaultArtifact("org.jvnet.hudson.main", "hudson-war", "war", "2.1.2");
  private static final Artifact HUDSON_220 = new DefaultArtifact("org.jvnet.hudson.main:hudson-war:2.2.0");
  private static final Artifact HUDSON_300M1 = new DefaultArtifact("org.eclipse.hudson.main:hudson-war:3.0.0-M1");

  static {
    CORE_VERSIONS.put("2.1.2", HUDSON_212);
    CORE_VERSIONS.put("2.2.0", HUDSON_220);
    CORE_VERSIONS.put("3.0.0-M1", HUDSON_300M1);
  }
  private String repoName = "plugintest";
  private String remoteRepo = "http://repo1.maven.org/maven2/";
  private String localRepo = "~/.m2/repository";

  ArtifactResolver(String localRepo, String remoteRepo) {
    this.localRepo = localRepo;
    this.remoteRepo = remoteRepo;
  }

  public List<Version> getAvailableVersions(String groupId, String artifactId,String minVersion) throws Exception {
    Artifact artifact = new DefaultArtifact(groupId+":"+artifactId+":["+minVersion+ ",)");

    RepositorySystem repoSystem = newRepositorySystem();
    RepositorySystemSession session = newSession(repoSystem);
    RemoteRepository central = new RemoteRepository(repoName, "default", remoteRepo);
    
    VersionRangeRequest rangeRequest = new VersionRangeRequest();
    rangeRequest.setArtifact(artifact);
    rangeRequest.addRepository(central);

    VersionRangeResult rangeResult = repoSystem.resolveVersionRange(session, rangeRequest);

    List<Version> versions = rangeResult.getVersions();

    return versions;
  }

  public Artifact resolve(Artifact artifact) throws Exception {
    List<Artifact> artifacts = new ArrayList<Artifact>();
    artifacts.add(artifact);
    List<Artifact> result = resolve(artifacts);
    return result.get(0);
  }

  public List<Artifact> resolve(List<Artifact> artifacts) throws Exception {

    RepositorySystem repoSystem = newRepositorySystem();
    RepositorySystemSession session = newSession(repoSystem);
    RemoteRepository central = new RemoteRepository(repoName, "default", remoteRepo);

    List<ArtifactRequest> requests = new ArrayList<ArtifactRequest>();
    for (Artifact artifact : artifacts) {
      ArtifactRequest artifactRequest = new ArtifactRequest();
      artifactRequest.setArtifact(artifact);
      artifactRequest.addRepository(central);
      requests.add(artifactRequest);
    }

    List<ArtifactResult> results = repoSystem.resolveArtifacts(session, requests);
    List<Artifact> resolved = new ArrayList<Artifact>();
    for (ArtifactResult result : results) {
      resolved.add(result.getArtifact());
    }
    return resolved;
  }

  private RepositorySystem newRepositorySystem() throws Exception {
    return new DefaultPlexusContainer().lookup(RepositorySystem.class);
  }
  
  private RepositorySystemSession newSession(RepositorySystem system) {
    MavenRepositorySystemSession session = new MavenRepositorySystemSession();

    LocalRepository local = new LocalRepository(localRepo);
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(local));

    return session;
  }
}
