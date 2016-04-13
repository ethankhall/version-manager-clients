package io.ehdev.conrad.client.java.scm.git.internal;

import io.ehdev.conrad.client.java.exception.ScmException;
import io.ehdev.conrad.client.java.http.VersionEntry;
import io.ehdev.conrad.client.java.scm.CommitDetails;
import io.ehdev.conrad.client.java.scm.git.GitManager;
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
    public boolean hasChanges() throws ScmException {
        try {
            Status status = git.status().call();
            return !status.isClean();
        } catch (GitAPIException e) {
            throw new ScmException(e);
        }
    }

    @Override
    public CommitDetails getCurrentCommitDetails() throws ScmException, IOException {
        ObjectId objectId = repo.getRef(Constants.HEAD).getObjectId();
        String message = new RevWalk(repo).parseCommit(objectId).getFullMessage();

        return new CommitDetails(message, objectId.getName());
    }

    @Override
    public List<String> getPreviousCommitIds(int length) throws ScmException, IOException {
        try {
            List<String> value = new ArrayList<>();
            git.log().setMaxCount(length).call().forEach(it -> value.add(it.getId().getName()));
            return value;
        } catch (GitAPIException e) {
            throw new ScmException(e);
        }
    }

    @Override
    public void tag(VersionEntry version) throws ScmException {
        try {
            git.tag().setName(String.format("v%s", version.getVersion())).call();
            git.push().setPushTags().call();
        } catch (GitAPIException e) {
            throw new ScmException(e);
        }
    }
}
