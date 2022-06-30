package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.repo.getLog
import org.gradle.api.invocation.Gradle
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification

/**
 * @Author LiABao
 * @Since 2022/6/13
 */
class BuildTaskScanner(gradle: Gradle) : BaseOperationNotificationListener {

    private val log = ReportTypeFile.BUILD_TASK_COSTA.getLog().apply {

    }
    private val listener: BuildTaskScannerListener by lazy {
        BuildTaskScannerListener(gradle, {
            log.append(log())
        }, {
            log.append("\r\n\r\n\r\n")
            log.append("Top 3 Project:")
            for (index in 0 until topK.size) {
                val it = topK.poll()
                log.append(it.log(false))
            }
        })
    }

    override fun buildFinish() {

    }

    override fun started(buildOperationStartedNotification: BuildOperationStartedNotification) {
        listener.started(buildOperationStartedNotification)
    }

    override fun progress(buildOperationProgressNotification: BuildOperationProgressNotification) {
        listener.progress(buildOperationProgressNotification)
    }

    override fun finished(buildOperationFinishedNotification: BuildOperationFinishedNotification) {
        listener.finished(buildOperationFinishedNotification)
    }
}