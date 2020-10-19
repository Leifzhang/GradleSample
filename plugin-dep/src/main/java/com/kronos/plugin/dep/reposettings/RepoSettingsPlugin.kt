package com.kronos.plugin.dep.reposettings

import com.kronos.plugin.dep.reposettings.parse.RepoInflater
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class RepoSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        RepoLogger.info("RepoSettingPlugin start work")

        val repoInfo = RepoInflater.inflate(settings.rootDir)

        repoInfo.moduleInfoMap.forEach { (s, module) ->
            if (module.srcBuild) {
                RepoLogger.info("${module.name} 加入了工程构建中 ");

                module.settingProject()

                settings.include(":${module.name}")
                RepoLogger.info("${module.name} 路径为 ${module.modulePath}");

                settings.project(":${module.name}").projectDir = module.modulePath

                RepoLogger.info("moudle:${module.name} 已加入到工程依赖 , 分支:" + module.curBranch())
            }
        }

    }


}