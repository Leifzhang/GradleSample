package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.DataRep
import com.kronos.plugin.monitor.repo.ReportTypeFile
import com.kronos.plugin.monitor.scan.info.InfrastructureCollege
import com.kronos.plugin.monitor.scan.info.InfrastructurePrinter
import com.kronos.plugin.monitor.scan.vcs.GitStatusCollege
import com.kronos.plugin.monitor.scan.vcs.GitStatusPrinter
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
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
class InfoScanner(settings: Settings?, gradle: Gradle?) : BuildOperationNotificationListener {

    init {
        val log = DataRep.getRep().getLogFile(ReportTypeFile.INFRASTRUCTURE)
        val gitLog = DataRep.getRep().getLogFile(ReportTypeFile.VCS_STATUS)
        val infrastructure = InfrastructureCollege().execute(gradle)
        InfrastructurePrinter(log).execute(infrastructure)
        val status = GitStatusCollege().execute()
        GitStatusPrinter(gitLog).execute(status)
    }

    override fun started(notification: BuildOperationStartedNotification) {

    }

    override fun progress(notification: BuildOperationProgressNotification) {
    }

    override fun finished(notification: BuildOperationFinishedNotification) {

    }
}