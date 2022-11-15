package com.kronos.plugin.version

import com.kronos.plugin.version.extensions.CatalogsExtensions
import com.kronos.plugin.version.extensions.CatalogsExtensionsImp
import com.kronos.plugin.version.utils.FileUtils
import com.kronos.plugin.version.utils.IncludeBuildInsertScript
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.invocation.Gradle
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.apply
import org.joor.Reflect
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
            // versionCatalogs.
            // cataglogs?.versionCatalogs(versionCatalogs)
            /*  versionCatalogs {
                  val root = FileUtils.getRootProjectDir(target.gradle)
                  root?.let { file ->
                      register("libs") {
                          val toml = File(file, "dependencies.versions.toml")
                          from(factory.fileCollection().from(toml))

                      }

                  }
              }*/
        }
    }


}