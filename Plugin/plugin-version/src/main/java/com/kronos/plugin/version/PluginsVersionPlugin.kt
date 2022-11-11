package com.kronos.plugin.version

import com.kronos.plugin.version.utils.FileUtils
import com.kronos.plugin.version.utils.IncludeBuildInsertScript
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/9
 *
 */
class PluginsVersionPlugin @Inject constructor(private val factory: ObjectFactory) :
    Plugin<Settings> {

    override fun apply(target: Settings) {
        FileUtils.getRootProjectDir(target.gradle)?.let {
            IncludeBuildInsertScript().execute(target, it)
        }
        target.gradle.plugins.apply(PluginVersionGradlePlugin::class.java)
        target.enableFeaturePreview("VERSION_CATALOGS")
        target.dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            versionCatalogs {
                val root = FileUtils.getRootProjectDir(target.gradle)
                root?.let { file ->
                    register("libs") {
                        val toml = File(file, "dependencies.versions.toml")
                        //    val settings = SettingsImp
                        from(factory.fileCollection().from(toml))
                    }
                }
            }
        }
    }


}