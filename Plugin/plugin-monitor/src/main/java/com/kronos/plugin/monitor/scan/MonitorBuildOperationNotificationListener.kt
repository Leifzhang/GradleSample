package com.kronos.plugin.monitor.scan

import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.api.invocation.Gradle
import java.util.ArrayList

class MonitorBuildOperationNotificationListener(
    gradle: Gradle?
) : BuildOperationNotificationListener {

    var list: MutableList<BaseOperationNotificationListener> = ArrayList()

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
            list.add(InfoScanner(gradle))
            list.add(BuildErrorScanner(gradle))
            list.add(ProcessScanner())
            list.add(BuildTaskScanner(gradle))
            list.add(ReportScanner(gradle))
            gradle.addBuildListener(object : BuildAdapter() {

                override fun buildFinished(result: BuildResult) {
                    list.forEach {
                        it.buildFinish()
                    }
                }
            })
        }

    }
}