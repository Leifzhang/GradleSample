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
                //RepoLogger.setProject(settings.rootProject)
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
                        RepoLogger.info("${moduleInfo.name} 通过includeBuild 加入了工程构建中 ")
                        moduleInfo.settingProject()
                        settings.includeBuild(moduleInfo.moduleGitRootPath)
                        RepoLogger.info("module:${moduleInfo.name} 已加入到工程依赖 , 分支:" + moduleInfo.curBranch())
                    }
                }
                repoInfo.includeModuleInfo.forEach { (s, moduleInfo) ->
                    moduleInfo.settingProject()
                    moduleInfo.projectNameList.forEach {
                        settings.include(":${it}")
                        RepoLogger.info("$it 路径为 ${moduleInfo.getModulePath(it)}");
                        settings.project(":${it}").projectDir =
                            moduleInfo.getModulePath(it)
                        RepoLogger.info("moudle:${it} 已加入到工程依赖 , 分支:" + moduleInfo.curBranch())
                    }
                }
            }
        })
    }


}