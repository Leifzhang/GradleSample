package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.repo.data.DaemonWork
import com.kronos.plugin.monitor.repo.getLog
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import org.gradle.workers.internal.ExecuteWorkItemBuildOperationType


/**
 *
 *  @Author LiABao
 *  @Since 2022/5/30
 *
 */
class ProcessScanner : BaseOperationNotificationListener {

    private val log = ReportTypeFile.PROCESS_LOG.getLog()
    private val list: ArrayList<ExecuteWorkItemBuildOperationType.Details> = ArrayList()
    private val map: HashMap<ExecuteWorkItemBuildOperationType.Details, DaemonWork> = HashMap()

    override fun buildFinish() {
        log.finish()
    }

    override fun started(notification: BuildOperationStartedNotification) {
        if (notification.notificationOperationDetails is ExecuteWorkItemBuildOperationType.Details) {
            list.add(notification.notificationOperationDetails as ExecuteWorkItemBuildOperationType.Details)
            val details: ExecuteWorkItemBuildOperationType.Details =
                notification.notificationOperationDetails as ExecuteWorkItemBuildOperationType.Details
            val work = DaemonWork()
            work.className = details.className
            work.displayName = details.displayName
            work.start = notification.notificationOperationStartedTimestamp
            map[details] = work
        }
    }

    override fun progress(notification: BuildOperationProgressNotification) {

    }

    override fun finished(notification: BuildOperationFinishedNotification) {
        if (notification.notificationOperationDetails is ExecuteWorkItemBuildOperationType.Details) {
            val details = notification.notificationOperationDetails
            val work = map[details]
            if (work != null) {
                work.time = notification.notificationOperationFinishedTimestamp - work.start;
                work.success = notification.getNotificationOperationFailure() == null
                log.append(work.toDec() + "\n")
            }
        }
    }
}