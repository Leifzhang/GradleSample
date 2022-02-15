package com.kronos.plugin.version

import com.kronos.plugin.version.ext.GradlePluginsVersion
import com.kronos.plugin.version.pluginManagement.DefaultPluginManagementAction
import org.gradle.api.Plugin
import org.gradle.api.invocation.Gradle

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
    }

}