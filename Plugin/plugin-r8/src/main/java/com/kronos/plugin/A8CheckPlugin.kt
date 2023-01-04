package com.kronos.plugin

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.kronos.plugin.extension.A8Rules
import com.kronos.plugin.utils.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/10/10
 *
 */
class A8CheckPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(EXE, A8Rules::class.java)

        project.configurations.maybeCreate("test")
        project.configurations.all {

        }
        project.plugins.withId("com.android.application") {
            val androidComponents =
                project.extensions.findByType(BaseAppModuleExtension::class.java) ?: return@withId
            val a8Rules = project.extensions.findByType(A8Rules::class.java)
            project.afterEvaluate {
                androidComponents.applicationVariants.forEach { variant ->
                    val a8Task = project.tasks.register(
                        "a8Check${variant.name.capitalize()}Task", A8Task::class.java
                    ) { task ->
                        task.apkFolder.set(variant.outputs.first().outputFile)
                        task.variantName.set(variant.name)
                        if (a8Rules?.rules != null) {
                            task.rules.set(a8Rules.rules)
                        } else {
                            task.rules.set(
                                File(
                                    FileUtils.getRootFile(project.gradle),
                                    ".buildscripts/a8_ruls.txt"
                                )
                            )

                        }

                        task.output.set(
                            File(
                                project.buildDir,
                                "a8/${variant.name}/a8_error_msg.txt"
                            )
                        )
                    }
                    variant.assembleProvider.get().finalizedBy(a8Task)
                }
            }
        }
    }

    companion object {
        const val EXE = "a8"
    }
}