package io.ehdev.conrad.client.java.scm.git;

import io.ehdev.conrad.client.java.scm.git.internal.DefaultGitManager;

import java.io.File;
import java.io.IOException;

public class GitManagerBuilder {

    private final File gitRepo;

    public GitManagerBuilder(File gitRepo) {
        this.gitRepo = gitRepo;
    }

    public GitManager build() throws IOException {
        return new DefaultGitManager(gitRepo);
    }
}
