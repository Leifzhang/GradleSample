package com.kronos.plugin.dep.reposettings

import com.kronos.plugin.dep.reposettings.model.ModuleInfo
import com.kronos.plugin.dep.reposettings.model.SubstituteModule
import com.kronos.plugin.dep.reposettings.parse.RepoInflater
import org.gradle.api.Plugin
import org.gradle.api.Project

class RepoBuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {


        val repoInfo = RepoInflater.inflate(project.rootProject.projectDir)

        repoInfo.moduleInfoMap.forEach { (_, moduleInfo) ->
            if (moduleInfo.srcBuild) {
                RepoLogger.info("module ${moduleInfo.name} 开启了源码依赖")
            }
        }

        applySubstituteModule(project, repoInfo.substituteModules)

        applyModule(project, repoInfo.moduleInfoMap)


    }

    private fun applySubstituteModule(
        rootProject: Project,
        substituteModules: List<SubstituteModule>
    ) {
        rootProject.subprojects.forEach { target ->
            target.afterEvaluate {

                substituteModules.forEach { substituteModule ->
                    target.configurations.all { it ->
                        it.resolutionStrategy {
                            it.dependencySubstitution.substitute(
                                it.dependencySubstitution.module(
                                    substituteModule.targetModule
                                )
                            )
                                .because("Repo work")
                                .with(it.dependencySubstitution.project(substituteModule.project))
                        }

                    }
                }

            }
        }
    }


    private fun applyModule(project: Project, moduleInfoMap: Map<String, ModuleInfo>) {
        project.subprojects.forEach { target ->
            target.afterEvaluate {


                moduleInfoMap.forEach { (_, moduleInfo) ->

                    if (moduleInfo.srcBuild && target.name != moduleInfo.name) {
                        println("project name = ${target.name} ,moduleInfoname = ${moduleInfo.name} ,srcBuild = " + moduleInfo.srcBuild)

                        target.configurations.all { it ->
                            if (moduleInfo.needSubstitute()) {
                                moduleInfo.substitute!!
                                val group = moduleInfo.substitute.split(":")[0]
                                val name = moduleInfo.substitute.split(":")[1]

                                it.resolutionStrategy {

                                    it.dependencySubstitution.substitute(
                                        it.dependencySubstitution.module(
                                            moduleInfo.substitute
                                        )
                                    )
                                        .because("Repo work")
                                        .with(it.dependencySubstitution.project(moduleInfo.projectNotationPath))
                                }
                            }


                        }
                    }
                }
            }
        }
    }

}