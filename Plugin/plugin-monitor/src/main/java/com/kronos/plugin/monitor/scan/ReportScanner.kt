package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.repo.DataRep
import com.kronos.plugin.monitor.utils.FileUtils
import com.kronos.plugin.monitor.utils.Logger
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.tasks.execution.ExecuteTaskBuildOperationDetails
import org.gradle.api.invocation.Gradle
import org.gradle.initialization.BuildRequestMetaData
import org.gradle.internal.operations.OperationIdentifier
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/5/30
 *
 */
class ReportScanner(private val gradle: Gradle) : BuildOperationNotificationListener {

    var startId: Long = -1
    var buildResult: Int = 1
    var isBuildOperation: Boolean = false
    var isReleaseBuild = false
    var starttime: Long = 0
    var tasksCount: Int = 0
    var uptodateCount = 0
    var cacheCount = 0
    var executedCount = 0
    var syncTask = false
    var afterSyncTaskConsumingTime: Long = 0

    init {
        if (gradle is GradleInternal) {
            val buildRequestMetaData = gradle.services.get(BuildRequestMetaData::class.java)
            starttime = (buildRequestMetaData.startTime / 1000)
        }
        if (gradle.startParameter.taskNames.size == 0 && isIdea(gradle)) {
            syncTask = true
        }
        gradle.taskGraph.addTaskExecutionGraphListener { taskExecutionGraph ->
            taskExecutionGraph.allTasks.forEach {
                val taskname = it.name.toString()
                if (taskname.contains("package") && (taskname.endsWith("Debug")
                            || taskname.endsWith("Release"))
                ) {
                    isBuildOperation = true
                    if (taskname.contains("Release")) {
                        isReleaseBuild = true
                    }
                }
            }
        }
    }


    fun isIdea(gradle: Gradle): Boolean {
        return gradle.startParameter.systemPropertiesArgs.containsKey("idea.version")
    }

    override fun started(notification: BuildOperationStartedNotification) {
        if (startId == -1L) {
            if (notification.notificationOperationId is OperationIdentifier) {
                val id = notification.notificationOperationId as OperationIdentifier
                startId = id.id
            }
        }
    }

    override fun progress(notification: BuildOperationProgressNotification) {

    }

    override fun finished(notification: BuildOperationFinishedNotification) {
        if (notification.notificationOperationDetails is ExecuteTaskBuildOperationDetails
            || notification.notificationOperationDetails.javaClass.name.startsWith("org.gradle.launcher.exec.RunAsBuildOperationBuildActionRunner")
        ) {
            buildResult = 2
            tasksCount += 1
        }
        if (notification.notificationOperationDetails is ExecuteTaskBuildOperationDetails) {
            val d = notification.notificationOperationDetails as ExecuteTaskBuildOperationDetails
            if (d.task.state.outcome.toString().contains("EXECUTED")) {
                executedCount += 1
            }
            if (d.task.state.outcome.toString().contains("UP_TO_DATE")) {
                uptodateCount += 1
            }
            if (d.task.state.outcome.toString().contains("FROM_CACHE")) {
                cacheCount += 1
            }
        }

        if (notification.notificationOperationId is OperationIdentifier) {
            val operationIdentifier = notification.notificationOperationId as OperationIdentifier
            val id = operationIdentifier.id
            if (id == startId) {
                val file = File(FileUtils.rootFile, "build" + File.separator + "task")
                if (file.exists()) {
                    file.listFiles().forEach {
                        val file1 =
                            File(DataRep.getRep().getDir(), "task" + File.separator + it.getName())
                    }

                }
            }
        }

    }
}