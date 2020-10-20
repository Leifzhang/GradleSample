package com.kronos.plugin.repo

import com.kronos.plugin.repo.parse.RepoInflater
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class RepoSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val repoInfo = RepoInflater.inflate(settings.rootDir)
        if (repoInfo.moduleInfoMap.isNotEmpty()) {
            RepoLogger.info("RepoSettingPlugin start work")
        }
        repoInfo.moduleInfoMap.forEach { (s, moduleInfo) ->
            if (moduleInfo.srcBuild) {
                RepoLogger.info("${moduleInfo.name} 加入了工程构建中 ")
                moduleInfo.settingProject()
                settings.includeBuild(moduleInfo.modulePath) { config ->
                    config.dependencySubstitution {
                        moduleInfo.substitute?.apply {
                            it.substitute(it.module(this))
                                .because("Repo work")
                                .with(it.project(":${moduleInfo.name}"))
                        }
                    }
                }
                RepoLogger.info("module:${moduleInfo.name} 已加入到工程依赖 , 分支:" + moduleInfo.curBranch())
            }
        }
    }


}