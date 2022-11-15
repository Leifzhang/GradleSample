package com.kronos.plugin.version.utils

import com.kronos.plugin.version.utils.PluginManagementExtensions.PLUGIN_MANAGEMENT_SCRIPT
import org.gradle.api.initialization.Settings
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/9
 *
 */
class IncludeBuildInsertScript {

    fun execute(target: Settings, root: File, script: File?) {
        val initFile = getBuildTemp(root, "global.settings.pluginManagement.gradle")
        if (initFile.exists()) {
            initFile.delete()
        }
        script?.let {
            initFile.appendText("gradle.ext.fawkesScriptFile=\"${it.path}\"\n")
        }
        initFile.appendText(PLUGIN_MANAGEMENT_SCRIPT)
        initFile.appendText("gradle.apply plugin: com.kronos.plugin.version.PluginVersionGradlePlugin.class\r\n")
      /*  initFile.appendText(
            "buildscript {\n" +
                    "    repositories {\n" +
                    "        mavenLocal()\n" +
                    "    }\n" +
                    "    dependencies {\n" +
                    "        classpath \"com.kronos.plugin:plugin-version:0.2.11\"\n" +
                    "\n" +
                    "    }\n" +
                    "}"
        )*/
        val fileList = mutableListOf<File>().apply {
            addAll(target.gradle.startParameter.initScripts)
        }
        fileList.add(initFile)
        target.gradle.startParameter.initScripts = fileList
    }

    fun getBuildTemp(root: File, path: String): File {
        val result = File(root.canonicalPath + File.separator + "build" + File.separator + path)
        touch(result)
        return result
    }

    private fun touch(file: File) {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
    }
}
