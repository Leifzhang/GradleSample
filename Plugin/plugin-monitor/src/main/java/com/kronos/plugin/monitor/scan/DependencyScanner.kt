package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.repo.getLog
import com.kronos.plugin.monitor.utils.log
import org.gradle.api.invocation.Gradle
import org.gradle.internal.impldep.org.eclipse.jgit.lib.internal.WorkQueue
import java.lang.StringBuilder
import java.util.*
import kotlin.concurrent.thread

/**
 * @Author: 小灵通
 * @Date: 2022.07.19
 * @Email: xiaolingtong@bilibili.com
 **/
class DependencyScanner(gradle: Gradle) {

    init {
        // 仅有当 taskGraph 上是由 assemble task 触发时, 打印 app project runtime dependency
        gradle.taskGraph.whenReady {
            this.allTasks.firstOrNull { task -> task.name.startsWith("package") }
                ?.run {
                    thread {
                        try {
                            val log = ReportTypeFile.PACKAGE_DEPENDENCY.getLog()
                            val variantRuntimeClassPath = name.substringAfter("package")
                                .decapitalize(Locale.ROOT) + "RuntimeClasspath"
                            val configurations =
                                listOf(project.configurations.getByName(variantRuntimeClassPath))
                            KronosDependencyReport().generateReport(
                                this.project,
                                configurations,
                                log.file
                            )
                            val sb = StringBuilder()
                            sb.append(ReportTypeFile.PACKAGE_DEPENDENCY.type.start(ReportTypeFile.PACKAGE_DEPENDENCY.title))
                            log.file.readLines().forEach {
                                sb.append(
                                if (it.startsWith("+---") || it.startsWith("\\---")) {
                                        "<p class=\"big\" style=\"color:orange\"> $it </p>"
                                    } else {
                                        "<p>$it</p>"
                                }
                                )
                            }
                            sb.append(ReportTypeFile.PACKAGE_DEPENDENCY.type.end())
                            log.file.writeText(sb.toString())
                        } catch (throwable: Throwable) {
                            throwable.printStackTrace()
                        }

                    }
                }
        }
    }
}