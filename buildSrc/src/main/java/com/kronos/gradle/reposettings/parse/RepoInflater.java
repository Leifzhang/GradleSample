package com.kronos.gradle.reposettings.parse;

import com.kronos.gradle.reposettings.model.RepoInfo;

import org.gradle.api.GradleException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * 负责解析 repo.xml 文件
 * <p>
 * repo.xml不会太大，没有使用pull的方式解析xml
 */
public abstract class RepoInflater {

    public static RepoInfo inflate(File projectDir) {
        File repoFile;
        File localRepoFIle = new File(projectDir, "local_repo.xml");
        if (localRepoFIle.exists()) {
            repoFile = localRepoFIle;
        } else {
            repoFile = new File(projectDir, "repo.xml");
        }
        if (!repoFile.exists())
            throw new GradleException("[repo] - local_repo.xml or repo.xml not found under " + projectDir.getAbsolutePath());
        try {
            return new RepoInflateImpl(repoFile).inflate();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }


}
