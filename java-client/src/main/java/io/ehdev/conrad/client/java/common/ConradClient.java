package io.ehdev.conrad.client.java.common;

import io.ehdev.conrad.client.java.exception.ConradException;
import io.ehdev.conrad.client.java.http.VersionEntry;

public interface ConradClient {

    VersionEntry claimVersion() throws ConradException;

    VersionEntry getCurrentVersion() throws ConradException;

    VersionEntry getCurrentVersion(int historyLength) throws ConradException;
}
