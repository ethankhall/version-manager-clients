package tech.crom.client.gradle;

import groovy.lang.Closure;
import tech.crom.client.java.RepoDetails;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;


public class VersionManagerExtension extends RepoDetails {

  private VersionCustomizer versionCustomizer;
  private VersionRequester versionRequester;

  /**
   * Configure the version string for the project.
   * @param closure needs to be of type {@link VersionCustomizer}
   */
  public void customize(Closure closure) {
    customize(DefaultGroovyMethods.asType(closure, VersionCustomizer.class));
  }

  public void customize(VersionCustomizer versionCustomizer) {
    this.versionCustomizer = versionCustomizer;
  }

  public VersionCustomizer getVersionCustomizer() {
    return versionCustomizer;
  }

  public VersionRequester getVersionRequester() {
    return versionRequester;
  }

  public void setVersionRequester(VersionRequester versionRequester) {
    this.versionRequester = versionRequester;
  }
}
