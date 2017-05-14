package tech.crom.client.java.scm.git.internal;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.SshSessionFactory;
import tech.crom.client.java.exception.ScmException;
import tech.crom.client.java.http.VersionEntry;
import tech.crom.client.java.scm.CommitDetails;
import tech.crom.client.java.scm.git.GitManager;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DefaultGitManager implements GitManager {

    private final Repository repo;
    private final Git git;

    static {
        SshSessionFactory.setInstance(new GitSessionFactory());
    }

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
            for (RevCommit revCommit : git.log().setMaxCount(length).call()) {
                value.add(revCommit.getId().getName());
            }
            return value;
        } catch (GitAPIException e) {
            throw new ScmException(e);
        }
    }

    @Override
    public void tag(VersionEntry version) throws ScmException {
        try {
            git.tag()
                    .setAnnotated(true)
                    .setName(String.format("v%s", version.getVersion()))
                    .setMessage("Tagged by Crom at " + ZonedDateTime.now().toString())
                    .call();
            git.push().setPushTags().call();
        } catch (GitAPIException e) {
            throw new ScmException(e);
        }
    }
}
