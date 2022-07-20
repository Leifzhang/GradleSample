package com.kronos.plugin.monitor.scan

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer
import org.gradle.api.tasks.diagnostics.internal.ReportGenerator
import org.gradle.api.tasks.diagnostics.internal.dependencies.AsciiDependencyReportRenderer
import org.gradle.configuration.GradleLauncherMetaData
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import java.util.*

/**
 * @Author: 小灵通
 * @Date: 2022.07.19
 * @Email: xiaolingtong@bilibili.com
 * 在 TaskGraph 生成后输出包含 package task 的模块依赖
 **/
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KronosDependencyReport {

    private val renderer: DependencyReportRenderer = AsciiDependencyReportRenderer()

    fun generateReport(
        project: Project,
        configurations: List<Configuration>,
        outputFile: File
    ) {
        reportGenerator(outputFile).generateReport(
            TreeSet(setOf(project))
        ) {
            generate(configurations)
        }
    }

    private fun generate(configurations: Iterable<Configuration>) {
        val sortedConfigurations: SortedSet<Configuration> =
            TreeSet(Comparator.comparing { obj: Configuration -> obj.name })
        sortedConfigurations.addAll(configurations)
        for (configuration in sortedConfigurations) {
            renderer.startConfiguration(configuration)
            renderer.render(configuration)
            renderer.completeConfiguration(configuration)
        }
    }

    private fun reportGenerator(outputFile: File): ReportGenerator {
        return ReportGenerator(
            renderer,
            GradleLauncherMetaData(),
            outputFile,
            object : StyledTextOutputFactory {
                override fun create(p0: String?): StyledTextOutput? {
                    return null
                }

                override fun create(p0: Class<*>?): StyledTextOutput? {
                    return null

                }

                override fun create(p0: Class<*>?, p1: LogLevel?): StyledTextOutput? {
                    return null

                }

                override fun create(p0: String?, p1: LogLevel?): StyledTextOutput? {
                    return null
                }

            }
        )
    }
}