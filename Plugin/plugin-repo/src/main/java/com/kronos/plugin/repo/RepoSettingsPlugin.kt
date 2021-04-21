package com.kronos.plugin.repo

import com.kronos.plugin.repo.parse.RepoInflater
import com.kronos.plugin.repo.utils.YamlUtils
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class RepoSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.gradle.addBuildListener(object : BuildAdapter() {

            override fun settingsEvaluated(settings: Settings) {
                super.settingsEvaluated(settings)
                var repoInfo = YamlUtils.inflate(settings.rootDir)
                if (repoInfo.moduleInfoMap.isEmpty()) {
                    repoInfo = RepoInflater.inflate(settings.rootDir)
                }
                if (repoInfo.moduleInfoMap.isNotEmpty()) {
                    RepoLogger.info("RepoSettingPlugin start work")
                } else {
                    return
                }
                repoInfo.moduleInfoMap.forEach { (s, moduleInfo) ->
                    if (moduleInfo.srcBuild) {
                        RepoLogger.info("${moduleInfo.name} 加入了工程构建中 ")
                        moduleInfo.settingProject()
                        settings.includeBuild(moduleInfo.moduleGitRootPath) { config ->
                            config.dependencySubstitution {
                                if (moduleInfo.substitute?.isNotEmpty() == true) {
                                    moduleInfo.substitute.apply {
                                        it.substitute(it.module(this)).because("Repo work")
                                            .with(it.project(moduleInfo.projectNotationPath))
                                    }
                                }
                            }
                        }
                        RepoLogger.info("module:${moduleInfo.name} 已加入到工程依赖 , 分支:" + moduleInfo.curBranch())
                    }
                }

            }
        })
    }


}