package io.ehdev.conrad.client.java.git;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;

public interface GitManager {

    CommitDetails getHeadCommitDetails() throws GitAPIException, IOException;

    List<String> findCommitsFromHead(int historyLength) throws GitAPIException, IOException;

    void tag(String version) throws GitAPIException;

    boolean hasChanges() throws GitAPIException;
}
