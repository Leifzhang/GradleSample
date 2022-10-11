package com.kronos.plugin.repo

import com.kronos.plugin.repo.parse.RepoInflater
import com.kronos.plugin.repo.utils.YamlUtils
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.initialization.Settings

class RepoSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.gradle.gradleHomeDir
        settings.gradle.addProjectEvaluationListener(object : ProjectEvaluationListener {
            override fun beforeEvaluate(project: Project) {
                project.configurations.all {
                    it.resolutionStrategy.dependencySubstitution.all { depend ->
                        if (depend.requested is ModuleComponentSelector) {
                            val moduleRequested = depend.requested as ModuleComponentSelector
                            val p = project.rootProject.allprojects.find { p ->
                                (p.group == moduleRequested.group && p.name == moduleRequested.module)
                            }
                            if (p != null) {
                                depend.useTarget(project.project(p.path), "selected local project")
                            }

                        }
                    }
                }
            }

            override fun afterEvaluate(project: Project, p1: ProjectState) {

            }

        })
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