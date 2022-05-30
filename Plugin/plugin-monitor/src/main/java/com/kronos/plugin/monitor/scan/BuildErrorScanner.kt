package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.DataRep
import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.scan.analyse.ErrorReport
import com.kronos.plugin.monitor.scan.analyse.LogErrorAnalyse
import com.kronos.plugin.monitor.scan.analyse.StyleLogErrorAnalyse
import com.kronos.plugin.monitor.scan.analyse.task.IssueDevFindTask
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.LogLevel
import org.gradle.internal.logging.events.LogEvent
import org.gradle.internal.logging.events.StyledTextOutputEvent
import org.gradle.internal.operations.OperationIdentifier
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification


/**
 *
 *  @Author LiABao
 *  @Since 2022/5/30
 *
 */
class BuildErrorScanner : BuildOperationNotificationListener {

    private val log = DataRep.getRep().getLogFile(ReportTypeFile.BUILD_REPORT)

    val errorReport: ErrorReport = ErrorReport(log)
    private val logAnalyse: LogErrorAnalyse = LogErrorAnalyse(errorReport)
    private val styleLogErrorAnalyse: StyleLogErrorAnalyse = StyleLogErrorAnalyse(errorReport)
    private var startId: Long = -1L

    override fun started(notification: BuildOperationStartedNotification) {
        if (startId == -1L) {
            if (notification.notificationOperationId is OperationIdentifier) {
                val id = notification.notificationOperationId as OperationIdentifier
                startId = id.id
            }
        }
    }

    override fun progress(notification: BuildOperationProgressNotification) {
        if (notification.notificationOperationProgressDetails is LogEvent) {
            val d = notification.notificationOperationProgressDetails as LogEvent
            if (d.logLevel == LogLevel.ERROR) {
                logAnalyse.analyse(d.category, d.message);
            }
        } else if (notification.notificationOperationProgressDetails is StyledTextOutputEvent) {
            val d = notification.notificationOperationProgressDetails as StyledTextOutputEvent
            if (d.logLevel == LogLevel.ERROR) {
                val stringBuilder = StringBuilder()
                d.spans.forEach {
                    stringBuilder.append(it.text)
                }
                styleLogErrorAnalyse.analyse(d.category, stringBuilder.toString());
            }

        }
    }


    override fun finished(notification: BuildOperationFinishedNotification) {
        if (notification.notificationOperationId is OperationIdentifier) {
            val id = notification.notificationOperationId as OperationIdentifier
            if (id.id == startId) {
                if (errorReport.isIll) {
                    try {
                        IssueDevFindTask(errorReport).execute()
                        errorReport.reportAll(true)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }

        }
    }
}