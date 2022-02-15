package com.kronos.plugin.version

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class LibraryMavenPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val rootDir = target.projectDir
        val mavenInfo = File(rootDir, "maven.yaml").getMavenInfo()

        mavenInfo?.apply {
            if (mavenInfo.name != target.name) {
                throw RuntimeException("ModuleName is not the same of maven info ")
            }
            target.group = mavenInfo.group
        }
    }

    companion object {
        // const val CLASSPATH = "classpath"
    }
}