package tech.crom.client.gradle;

import tech.crom.client.java.http.VersionEntry;


public interface VersionCustomizer {
  String configure(VersionEntry current);
}
