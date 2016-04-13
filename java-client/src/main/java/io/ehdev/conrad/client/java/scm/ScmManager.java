package io.ehdev.conrad.client.java.scm;

import io.ehdev.conrad.client.java.exception.ScmException;
import io.ehdev.conrad.client.java.http.VersionEntry;

import java.io.IOException;
import java.util.List;

public interface ScmManager {
    CommitDetails getCurrentCommitDetails() throws ScmException, IOException;

    List<String> getPreviousCommitIds(int length) throws ScmException, IOException;

    void tag(VersionEntry version) throws ScmException;

    boolean hasChanges() throws ScmException;
}
