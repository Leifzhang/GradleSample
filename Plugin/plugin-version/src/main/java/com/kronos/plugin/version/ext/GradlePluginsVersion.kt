package com.kronos.plugin.version.ext

import com.kronos.plugin.version.utils.FileUtils.getGradleVersionFile
import com.kronos.plugin.version.utils.FileUtils.getRootProjectDir
import org.gradle.BuildAdapter
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.buildscript

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/8
 *
 */
class GradlePluginsVersion {

    fun execute(settings: Settings) {
        val gradle = settings.gradle
        val root = getRootProjectDir(gradle)
        val gradlePluginsVersion = root.getGradleVersionFile()
        gradle.addBuildListener(object : BuildAdapter() {
            override fun projectsLoaded(gradle: Gradle) {
                super.projectsLoaded(gradle)
                gradle.rootProject {
                    buildscript {
                        gradlePluginsVersion?.let { file ->
                            project.apply(from = file)
                        }
                    }
                }
            }

        })
    }


}