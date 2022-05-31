package com.kronos.plugin.version

import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/5/13
 *
 */
class BabelCacheSettings : Plugin<Settings> {

    override fun apply(target: Settings) {
        target.gradle.addBuildListener(object : BuildAdapter() {

            override fun beforeSettings(settings: Settings) {
                super.beforeSettings(settings)
                val root = getRootProjectDir(settings.gradle)
                val buildFile = File(root, "build/xxxxx")
                val babelFile = File(root, "xxxxx")
                if (buildFile.exists() && babelFile.exists()) {
                    val lines = buildFile.inputStream().bufferedReader().use {
                        it.readLines()
                    }
                    val babelLines = babelFile.inputStream().bufferedReader().use {
                        it.readLines()
                    }
                    val difference = lines.minus(babelLines.toSet())
                    difference.forEach {
                        println(it)
                    }
                }
                System.getenv()
                if (babelFile.exists()) {
                    val parent = buildFile.parentFile
                    if (parent.exists()) {
                        parent.mkdirs()
                    }
                    buildFile.outputStream().use { out ->
                        out.write(babelFile.inputStream().readBytes())
                    }
                }

            }
        })
    }

    fun getRootFile(gradle: Gradle): File? {
        return gradle.startParameter.currentDir
    }

    fun getRootProjectDir(gradle: Gradle): File? {
        var f = getRootFile(gradle)
        var root: File? = null
        while (f != null) {
            if (File(f, "settings.gradle").exists()) {
                root = f
            }
            f = f.parentFile
        }
        return root
    }
}