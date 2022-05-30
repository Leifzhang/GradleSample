package com.kronos.plugin.monitor.scan

import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import java.util.ArrayList

class MonitorBuildOperationNotificationListener(
    settings: Settings?,
    gradle: Gradle?
) : BuildOperationNotificationListener {

    var list: MutableList<BuildOperationNotificationListener> = ArrayList()

    override fun started(notification: BuildOperationStartedNotification) {
        list.forEach {
            it.started(notification);
        }
    }

    override fun progress(notification: BuildOperationProgressNotification) {
        list.forEach {
            it.progress(notification);
        }
    }

    override fun finished(notification: BuildOperationFinishedNotification) {
        list.forEach {
            it.finished(notification);
        }
    }

    init {
        gradle?.apply {
            list.add(LogScanner(gradle))
            list.add(InfoScanner(settings, gradle))
            list.add(BuildErrorScanner())
            list.add(ProcessScanner(gradle))
            list.add(ReportScanner(gradle))
        }
    }
}