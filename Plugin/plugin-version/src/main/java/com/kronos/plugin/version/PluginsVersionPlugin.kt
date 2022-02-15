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
    }


}