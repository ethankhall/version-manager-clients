package io.ehdev.conrad.client.java.git.internal;

import io.ehdev.conrad.client.java.git.CommitDetails;
import io.ehdev.conrad.client.java.git.GitManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultGitManager implements GitManager {

    private final Repository repo;
    private final Git git;

    public DefaultGitManager(File directory) throws IOException {
        repo = new RepositoryBuilder().findGitDir(directory).build();
        git = new Git(repo);
    }

    @Override
    public CommitDetails getHeadCommitDetails() throws GitAPIException, IOException {
        ObjectId objectId = repo.getRef(Constants.HEAD).getObjectId();
        String message = new RevWalk(repo).parseCommit(objectId).getFullMessage();

        return new CommitDetails(message, objectId.getName());
    }

    @Override
    public List<String> findCommitsFromHead(int historyLength) throws IOException, GitAPIException {
        List<String> value = new ArrayList<>();
        git.log().setMaxCount(historyLength).call().forEach(it -> value.add(it.getId().getName()));
        return value;
    }

    @Override
    public void tag(String version) throws GitAPIException {
        git.tag().setName(String.format("v%s", version)).call();
    }

    @Override
    public boolean hasChanges() throws GitAPIException {
        Status status = git.status().call();
        return !status.isClean();
    }
}
