package com.kronos.plugin.monitor.scan.vcs;

import com.kronos.plugin.monitor.repo.data.GitStatus;
import com.kronos.plugin.monitor.utils.FileUtils;

import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GitStatusCollege {

    public List<GitStatus> execute() {
        List<GitStatus> gitStatuses = new ArrayList<>();
        gitStatuses.add(new GitStatusTask(FileUtils.rootFile.getAbsolutePath(), "root").execute());
        return gitStatuses;
    }

    public static class GitStatusTask {
        public String wp;
        public String path;

        GitStatusTask(String wp, String path) {
            this.wp = wp;
            this.path = path;
        }

        public GitStatus execute() {
            GitStatus gitStatus = new GitStatus();
            gitStatus.path = path;
            if (new File(wp).exists() && new File(wp, ".git").exists()) {
                //     gitStatus.commit = GitTools.getCommit(wp);
                //   gitStatus.status = ProcessWarp.process(GitTools.statusDetail(wp)).text;
                // gitStatus.branch = ProcessWarp.process(GitTools.currentBranch(wp)).text;
            } else {
                //  gitStatus.status = "当前仓库没有权限或没有拉取下来"
            }
            return gitStatus;
        }
    }
}
