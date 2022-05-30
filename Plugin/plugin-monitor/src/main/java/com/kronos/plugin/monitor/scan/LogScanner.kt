package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.DataRep
import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.scan.cmd.CmdCollege
import com.kronos.plugin.monitor.scan.cmd.CmdPrinter
import com.kronos.plugin.monitor.utils.Logger
import com.kronos.plugin.monitor.utils.TimeUtils
import org.gradle.api.internal.tasks.execution.ExecuteTaskBuildOperationDetails
import org.gradle.api.internal.tasks.execution.ExecuteTaskBuildOperationType
import org.gradle.api.invocation.Gradle
import org.gradle.internal.featurelifecycle.DefaultDeprecatedUsageProgressDetails
import org.gradle.internal.logging.events.LogEvent
import org.gradle.internal.logging.events.ProgressStartEvent
import org.gradle.internal.logging.events.StyledTextOutputEvent
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import java.util.function.Consumer

/**
 *
 *  @Author LiABao
 *  @Since 2022/5/30
 *
 */
class LogScanner(gradle: Gradle) : BuildOperationNotificationListener {

    var log: LogFile = DataRep.getRep().getLogFile(ReportTypeFile.CONSOLE_LOG)
    var map = HashMap<Any, BuildOperationStartedNotification>()

    init {
        val cmd = CmdCollege().execute(gradle)
        CmdPrinter(log).execute(cmd)
    }

    override fun started(notification: BuildOperationStartedNotification) {
        val id = notification.notificationOperationId
        map[id] = notification
    }

    override fun progress(notification: BuildOperationProgressNotification) {
        val sb = StringBuilder("")
        if (notification.notificationOperationProgressDetails is LogEvent) {
            val d: LogEvent = notification.notificationOperationProgressDetails as LogEvent
            sb.append(d.message).append("\n")
        } else if (notification.notificationOperationProgressDetails is StyledTextOutputEvent) {
            val d: StyledTextOutputEvent =
                notification.notificationOperationProgressDetails as StyledTextOutputEvent
            d.spans.forEach {
                sb.append(it.text)
            }
        } else if (notification.notificationOperationProgressDetails is ProgressStartEvent) {
            val d: ProgressStartEvent =
                notification.notificationOperationProgressDetails as ProgressStartEvent
        } else if (notification.notificationOperationProgressDetails is DefaultDeprecatedUsageProgressDetails) {

        } else {
            log.append("未知 notification $notification")
        }
        log.append(sb.toString())
    }

    override fun finished(notification: BuildOperationFinishedNotification) {
        if (notification.notificationOperationDetails is ExecuteTaskBuildOperationDetails) {
            try {
                val d: ExecuteTaskBuildOperationDetails =
                    notification.notificationOperationDetails as ExecuteTaskBuildOperationDetails
                val result: ExecuteTaskBuildOperationType.Result =
                    notification.notificationOperationResult as ExecuteTaskBuildOperationType.Result
                val get = map[notification.notificationOperationId]
                val stringBuffer = StringBuffer()
                stringBuffer.append("Task \${d.taskPath}: " + d.task.state.outcome.toString() + " ")
                if (get != null) {
                    val exTime =
                        notification.notificationOperationFinishedTimestamp - get.notificationOperationStartedTimestamp
                    stringBuffer.append(TimeUtils.toDec(exTime))
                    stringBuffer.append("\n")
                    val cachingDis = result.cachingDisabledReasonMessage
                    if (cachingDis != null) {
                        stringBuffer.append("disable caching:")
                        stringBuffer.append("\n   ")
                        stringBuffer.append(cachingDis)
                        stringBuffer.append("\n")
                    }
                    if (result.upToDateMessages != null && !result.upToDateMessages!!.isEmpty()) {
                        stringBuffer.append("disable up to date:")
                        stringBuffer.append("\n")
                        result.upToDateMessages?.forEach {
                            stringBuffer.append("  $it")
                            stringBuffer.append("\n")
                        }
                        stringBuffer.append("\n")
                    }

                    if (result.originExecutionTime != null) {
                        val orExTime = result.originExecutionTime!!
                        if (exTime > orExTime) {
                            stringBuffer.append("!!!比上次cache任务多执行 " + TimeUtils.toDec(exTime - orExTime))
                        } else {
                            stringBuffer.append("比上次cache任务减少执行 " + TimeUtils.toDec(orExTime - exTime))
                        }
                        stringBuffer.append("\n")
                    }
                } else {
                    stringBuffer.append("not found started notification")
                    stringBuffer.append("\n")
                }
                log.append(stringBuffer.toString())
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}