package com.kronos.plugin.version

import com.kronos.plugin.version.utils.FileUtils
import com.kronos.plugin.version.utils.IncludeBuildInsertScript
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/9
 *
 */
class PluginsVersionPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        FileUtils.getRootProjectDir(target.gradle)?.let {
            IncludeBuildInsertScript().execute(target, it)
        }
        target.gradle.plugins.apply(PluginVersionGradlePlugin::class.java)
        target.dependencyResolutionManagement.versionCatalogs {
            register("support") {
                alias("coreKtx").to("androidx.core:core-ktx:1.3.2")
            }
            register("plugin"){
                alias("agpPlugin").to("com.android.tools.build:gradle:7.1.1")
            }
        }
    }


}