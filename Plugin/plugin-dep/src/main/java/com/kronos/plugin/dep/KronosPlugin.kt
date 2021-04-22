package com.kronos.plugin.dep

import com.kronos.plugin.dep.task.ApplyRemoteDepTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class KronosPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val task = project.tasks.create(
            "DepDefinitionPluginTask", ApplyRemoteDepTask::class.java
        ) {
            it.group = "kronos"
        }
        project.allprojects {
            task.apply()
        }
        task.apply()
    }

}