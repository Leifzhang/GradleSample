package com.kronos.plugin.monitor

import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.repo.data.BuildErr
import com.kronos.plugin.monitor.repo.getLog
import com.kronos.plugin.monitor.scan.err.BuildErrPrinter
import com.kronos.plugin.monitor.utils.Logger
import org.gradle.api.invocation.Gradle
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.internal.impldep.org.junit.internal.runners.statements.Fail
import org.gradle.tooling.events.FailureResult
import javax.inject.Inject

/**
 *
 *  @Author LiABao
 *  @Since 2022/12/1
 *
 */
// build.gradle.kts

abstract class BuildListenerService :
    BuildService<BuildListenerService.Params>,
    org.gradle.tooling.events.OperationCompletionListener {
    private var projectsEvaluated = false

    interface Params : BuildServiceParameters

    fun onProjectsEvaluated() {
        Logger.log("onProjectsEvaluated()")
        projectsEvaluated = true
    }

    override fun onFinish(event: org.gradle.tooling.events.FinishEvent) {
        Logger.log("BuildListenerService got event ${event.javaClass.canonicalName}")
        if (event.result is FailureResult) {
            val buildError = ReportTypeFile.BUILD_ERR.getLog()
            val errorEvent = event as FailureResult
            val failures = errorEvent.failures
            failures.forEach {
                Logger.log("build fail message:\r\n ${it.message}")
                Logger.log("build fail description:\r\n ${it.description}")
            }
            val buildErr = BuildErr(failures)
            BuildErrPrinter(buildError).execute(buildErr)
            buildError.finish()
        }
    }
}
