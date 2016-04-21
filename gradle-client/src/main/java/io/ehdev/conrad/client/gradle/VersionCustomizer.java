package io.ehdev.conrad.client.gradle;

import io.ehdev.conrad.client.java.http.VersionEntry;


public interface VersionCustomizer {
  String configure(VersionEntry current);
}
