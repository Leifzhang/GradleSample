package com.kronos.plugin

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.tools.r8.A8
import com.android.tools.r8.Diagnostic
import com.android.tools.r8.utils.DexResourceProvider
import org.apache.tools.ant.taskdefs.ManifestTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 *
 *  @Author LiABao
 *  @Since 2022/10/10
 *
 */
class A8CheckPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val androidComponents =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        //val baseAppModuleExtension = project.extensions.getByType(BaseAppModuleExtension::class.java)
        androidComponents.onVariants { applicationVariant ->

            val paths = ArrayList<Path>()
            /*   applicationVariant.sources
               for (f: variant.getCompileClasspath (null).files) {
                   paths.add(f.toPath())
               }*/
            androidComponents.sdkComponents.bootClasspath.get().forEach {
                paths.add(it.asFile.toPath())
            }
            project.afterEvaluate {
                val assembleTask =
                    project.tasks.findByName("assemble${applicationVariant.name.capitalize()}")
                project.tasks.register(
                    "a8Check${applicationVariant.name.capitalize()}Task", A8Task::class.java
                ) {
                    it.classPath.set(paths)
                    it.apkFolder.set(applicationVariant.artifacts.get(SingleArtifact.APK))
                    it.builtArtifactsLoader.set(applicationVariant.artifacts.getBuiltArtifactsLoader())
                    it.mustRunAfter(assembleTask)
                }
            }

        }


    }
}