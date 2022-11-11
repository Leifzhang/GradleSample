package com.kronos.plugin.version.utils

import org.gradle.api.invocation.Gradle
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/10
 *
 */
object FileUtils {

    fun File?.getGradleVersionFile(): File? {
        if (this == null) {
            return null
        }
        val file = File(this, "plugins.gradle")
        if (file.exists()) {
            return file
        }
        return null
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