package com.kronos.plugin.version

import com.kronos.plugin.version.ext.GradlePluginsVersion
import com.kronos.plugin.version.pluginManagement.DefaultPluginManagementAction
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.initialization.DefaultSettings

/**
 * @Author LiABao
 * @Since 2022/2/10
 */
class PluginVersionGradlePlugin : Plugin<Gradle> {

    override fun apply(target: Gradle) {
        target.settingsEvaluated {
            pluginManagement(DefaultPluginManagementAction(this))
            GradlePluginsVersion().execute(this)
        }
        target.addBuildListener(object : BuildAdapter() {
            override fun settingsEvaluated(settings: Settings) {
                super.settingsEvaluated(settings)
                val defaultSettings = settings as DefaultSettings
                val oldChildren = mutableListOf<ProjectDescriptor>().apply {
                    addAll((defaultSettings.rootProject.children))
                }
                settings.rootProject.children.clear()
                settings.rootProject.children.addAll(oldChildren)
            }

            override fun projectsEvaluated(gradle: Gradle) {
                super.projectsEvaluated(gradle)
                gradle.rootProject.buildFile.walkTopDown().firstOrNull {
                    it.name == "settings.gradle"
                }
                val rootProject = gradle.rootProject
                rootProject.configurations.all {
                    resolutionStrategy {
                        preferProjectModules()
                        dependencySubstitution {
                            all {
                                if (requested is ModuleComponentSelector) {
                                    val selector = requested as ModuleComponentSelector
                                    val group = selector.group
                                    val module = selector.module
                                    val p = rootProject.allprojects.find { p ->
                                        p.group.toString() == group
                                                && p.name == module
                                    }
                                    if (p != null) {
                                        Logger.debug("select   $requested local project")
                                        useTarget(project(p.path), "selected local project")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

}