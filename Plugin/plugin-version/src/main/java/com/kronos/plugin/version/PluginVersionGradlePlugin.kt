package com.kronos.plugin.version

import com.kronos.plugin.version.ext.GradlePluginsVersion
import com.kronos.plugin.version.extensions.CatalogsExtensionsImp
import com.kronos.plugin.version.pluginManagement.DefaultPluginManagementAction
import com.kronos.plugin.version.utils.extra
import com.kronos.plugin.version.utils.getExtra
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.initialization.DefaultSettings
import org.gradle.kotlin.dsl.apply
import java.io.File

/**
 * @Author LiABao
 * @Since 2022/2/10
 */
class PluginVersionGradlePlugin : Plugin<Gradle> {

    override fun apply(target: Gradle) {

        target.beforeSettings {
            if (this.gradle.parent != null) {
                settings.pluginManager.apply(PluginsVersionPlugin::class.java)
            }
        }
        target.settingsEvaluated {
            pluginManagement(DefaultPluginManagementAction(this))
            GradlePluginsVersion().execute(this)
            if (this.gradle.parent != null) {
                gradle.extra()?.getExtra<String>("fawkesScriptFile")?.apply {
                         apply(from = File(this))
                }
            }
        }
    }

}