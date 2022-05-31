package com.kronos.plugin.monitor.scan.cmd

import com.kronos.plugin.monitor.repo.data.BuildCmd
import org.gradle.api.invocation.Gradle

class CmdCollege {

    fun execute(gradle: Gradle): BuildCmd {
        val cmd = BuildCmd()
        gradle.startParameter.taskNames.forEach {
            cmd.taskNames.add(it)
        }
        gradle.startParameter.initScripts.forEach {
            cmd.initScripts.add(it.path)
        }
        gradle.startParameter.projectProperties.forEach {
            cmd.projectProperties[it.key] = it.value
        }
        gradle.startParameter.systemPropertiesArgs.forEach {
            cmd.systemPropertiesArgs[it.key] = it.value
        }
        return cmd
    }
}