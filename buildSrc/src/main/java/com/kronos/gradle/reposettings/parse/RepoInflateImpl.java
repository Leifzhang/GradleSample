package com.kronos.gradle.reposettings.parse;

import com.kronos.gradle.reposettings.RepoConstants;
import com.kronos.gradle.reposettings.RepoLogger;
import com.kronos.gradle.reposettings.model.ModuleInfo;
import com.kronos.gradle.reposettings.model.RepoInfo;
import com.kronos.gradle.reposettings.model.SubstituteModule;
import com.kronos.gradle.utils.GitUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class RepoInflateImpl {


    private static final String TAG_DEFAULT = "default";
    private static final String TAG_MODULE = "module";


    private static final String ATTRIBUTE_DEFAULT_BRANCH = "branch";

    private static final String ATTRIBUTE_BRANCH = "branch";
    private static final String ATTRIBUTE_SRCBUILD = "srcBuild";

    private static final String ATTRIBUTE_REPO_DIR = "repoDir";


    private static final String ATTRIBUTE_ORIGIN = "origin";
    private static final String ATTRIBUTE_MODULE_NAME = "name";
    private static final String ATTRIBUTE_MODULE_PATH = "path";
    private static final String ATTRIBUTE_SUBSTITUTE = "substitute";


    private static final String TAG_SUBSTITUTE = "substitute";
    private static final String ATTRIBUTE_TARGET_MODULE = "targetModule";
    private static final String ATTRIBUTE_PROJECT = "project";


    private File repoFile;

    //repo 管理的项目默认的根路径
    private File repoProjectsDefaultDir;
    private final File gradleProjectDir;

    public RepoInflateImpl(File repoFile) {
        this.repoFile = repoFile;
        this.gradleProjectDir = repoFile.getParentFile();
        this.repoProjectsDefaultDir = new File(gradleProjectDir.getParentFile(), RepoConstants.defaultSubModulesDirName);
    }


    public RepoInfo inflate() throws IOException, SAXException, ParserConfigurationException {
        return parseRepo(repoFile);
    }


    public RepoInfo parseRepo(File repoFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        FileInputStream inputStream = new FileInputStream(repoFile);
        Document doc = builder.parse(inputStream);

        Element rootElement = doc.getDocumentElement();

        RepoInfo repoInfo = null;
        NodeList defaultNodeList = rootElement.getElementsByTagName(TAG_DEFAULT);
        if (defaultNodeList.getLength() > 1) {
            throw new RuntimeException("[repo] - Make sure there is only one <default/> element in repo.xml");
        } else if (defaultNodeList.getLength() == 1) {


            Element item = (Element) defaultNodeList.item(0);
            File repoDirPath = repoProjectsDefaultDir;

            String branch = item.getAttribute(ATTRIBUTE_DEFAULT_BRANCH);

            final boolean repoDirSetting = item.hasAttribute(ATTRIBUTE_REPO_DIR);
            String repoDir = item.getAttribute(ATTRIBUTE_REPO_DIR);
            if (repoDirSetting && repoDir != null) {
                if (new File(repoDir).isAbsolute()) {
                    repoDirPath = new File(repoDir);
                } else {
                    repoDirPath = new File(gradleProjectDir, repoDir);
                }
            }


            //如果repo.xml 没有设置默认分支，则执行git命令获取当前分支名作为默认的分支
            if (branch == null || branch.length() == 0) {
                branch = GitUtil.curBranchName(gradleProjectDir);
            }

            //如果是临时分支，尝试获取jvm branch
            //如果jvm branch配置还是没有则 默认取master
            if ("HEAD".equals(branch)) {
                //
                final String jvmBranch = System.getProperty("BRANCH");
                if (jvmBranch == null) {
                    branch = "master";
                } else {
                    if (jvmBranch.startsWith("origin/")) {
                        branch = jvmBranch.replace("origin/", "");
                    } else {
                        branch = jvmBranch;
                    }
                }
            }


            RepoLogger.info(" 设置默认分支" + branch);

            repoInfo = new RepoInfo(repoDirPath);
            repoInfo.setDefaultBranch(branch);
            repoInfo.setSrcBuild(Boolean.parseBoolean(item.getAttribute(ATTRIBUTE_SRCBUILD)));


        }

        final NodeList moduleNodeList = rootElement.getElementsByTagName(TAG_MODULE);

        for (int i = 0; i < moduleNodeList.getLength(); i++) {
            final Element moduleElement = (Element) moduleNodeList.item(i);
            parseModuleInfo(repoInfo, moduleElement);
        }


        final NodeList substituteNodeList = rootElement.getElementsByTagName(TAG_SUBSTITUTE);
        for (int i = 0; i < substituteNodeList.getLength(); i++) {
            final Element substituteModuleItem = (Element) substituteNodeList.item(i);
            parseSubstituteModule(repoInfo, substituteModuleItem);
        }
        return repoInfo;

    }

    public void parseModuleInfo(RepoInfo repoInfo, Element element) {
        String name = element.getAttribute(ATTRIBUTE_MODULE_NAME);
        String path = element.getAttribute(ATTRIBUTE_MODULE_PATH);
        String branch = element.getAttribute(ATTRIBUTE_BRANCH);
        String substitute = element.getAttribute(ATTRIBUTE_SUBSTITUTE);
        String origin = element.getAttribute(ATTRIBUTE_ORIGIN);

        boolean srcBuild = repoInfo.getSrcBuild();
        final String srcBuildValue = element.getAttribute(ATTRIBUTE_SRCBUILD);
        if (srcBuildValue != null) {
            srcBuild = Boolean.parseBoolean(srcBuildValue);
        }

        if (branch.trim().isEmpty()) {
            if (repoInfo.getDefaultBranch() != null) {
                branch = repoInfo.getDefaultBranch();
            } else {
                branch = "master";
            }
        }

        final ModuleInfo moduleInfo = new ModuleInfo(name, origin, path, srcBuild, substitute, repoInfo.getRepoManageProjectDir(), branch);

        repoInfo.getModuleInfoMap().put(name, moduleInfo);


    }

    public void parseSubstituteModule(RepoInfo repoInfo, Element element) {
        final String moduleNotation = element.getAttribute(ATTRIBUTE_TARGET_MODULE);
        final String projectNotation = element.getAttribute(ATTRIBUTE_PROJECT);

        final SubstituteModule substituteModule = new SubstituteModule(moduleNotation, projectNotation);
        repoInfo.getSubstituteModules().add(substituteModule);
    }
}
