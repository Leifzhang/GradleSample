package com.kronos.plugin.version

import com.kronos.plugin.version.extensions.CatalogsExtensionsImp
import com.kronos.plugin.version.utils.FileUtils
import com.kronos.plugin.version.utils.IncludeBuildInsertScript
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import javax.inject.Inject

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/9
 *
 */

class PluginsVersionPlugin constructor() :
    Plugin<Settings> {

    override fun apply(target: Settings) {
        val catalogs =
            target.extensions.create("catalogs", CatalogsExtensionsImp::class.java, target)
        target.gradle.plugins.apply(PluginVersionGradlePlugin::class.java)
        target.gradle.settingsEvaluated {
            FileUtils.getRootProjectDir(target.gradle)?.let {
                IncludeBuildInsertScript().execute(target, it, catalogs.script)
            }
        }
        target.enableFeaturePreview("VERSION_CATALOGS")
        target.dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        }
    }


}