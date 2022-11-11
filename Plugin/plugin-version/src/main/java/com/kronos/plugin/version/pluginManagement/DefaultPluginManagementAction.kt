package com.kronos.plugin.version.pluginManagement

import com.kronos.plugin.version.strategy.GradlePluginsStrategy
import com.kronos.plugin.version.utils.FileUtils
import org.gradle.api.Action
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.management.PluginManagementSpec

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/10
 *
 */
class DefaultPluginManagementAction(private val settings: Settings) : Action<PluginManagementSpec> {

    private val root = FileUtils.getRootProjectDir(settings.gradle)

    override fun execute(pluginManagement: PluginManagementSpec) {
        root?.let {
            pluginManagement.resolutionStrategy(GradlePluginsStrategy(it))
        }
        pluginManagement.repositories {
            mavenLocal()
        }
    }
}