package com.kronos.plugin.monitor

import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.repo.data.BuildErr
import com.kronos.plugin.monitor.repo.getLog
import com.kronos.plugin.monitor.scan.err.BuildErrPrinter
import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings

class BuildResultMonitor {
    fun setup(target: Settings) {
        target.gradle.addListener(object : BuildAdapter() {

            override fun buildFinished(result: BuildResult) {
                super.buildFinished(result)
                val buildError = ReportTypeFile.BUILD_ERR.getLog()
                val buildErr = BuildErr()
                buildErr.throwable = result.failure
                BuildErrPrinter(buildError).execute(buildErr)
                buildError.finish()
            }
        })
    }
}