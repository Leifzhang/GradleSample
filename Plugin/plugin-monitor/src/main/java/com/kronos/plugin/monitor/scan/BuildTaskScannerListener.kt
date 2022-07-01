package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.utils.TimeUtils
import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.api.internal.tasks.execution.ExecuteTaskBuildOperationDetails
import org.gradle.api.internal.tasks.execution.ExecuteTaskBuildOperationType
import org.gradle.api.invocation.Gradle
import org.gradle.internal.operations.notify.BuildOperationFinishedNotification
import org.gradle.internal.operations.notify.BuildOperationNotificationListener
import org.gradle.internal.operations.notify.BuildOperationProgressNotification
import org.gradle.internal.operations.notify.BuildOperationStartedNotification
import java.util.*
import kotlin.collections.HashMap

/**
 *
 *  @Author LiABao
 *  @Since 2022/6/14
 *
 */
class BuildTaskScannerListener(
    gradle: Gradle,
    private val invoker: ModuleTasksData.() -> Unit = {},
    private val finishListener: BuildTaskScannerListener.() -> Unit = {}
) : BuildOperationNotificationListener {

    var map = HashMap<Any, BuildOperationStartedNotification>()
    private val values = Collections.synchronizedMap(linkedMapOf<String, ModuleTasksData>())
    val topK = PriorityQueue<ModuleTasksData>()

    init {
        gradle.addBuildListener(object : BuildAdapter() {
            override fun buildFinished(result: BuildResult) {
                super.buildFinished(result)
                values.forEach {
                    invoker.invoke(it.value)
                    if (topK.size < K) {
                        topK.add(it.value)
                    } else {
                        val min = topK.peek()
                        if (it.value.totalCosta > min.totalCosta) {
                            topK.poll()
                            topK.offer(it.value)
                        }
                    }
                }
                finishListener.invoke(this@BuildTaskScannerListener)
            }
        })

    }

    override fun started(notification: BuildOperationStartedNotification) {
        val id = notification.notificationOperationId
        map[id] = notification
    }

    override fun progress(notification: BuildOperationProgressNotification) {

    }

    override fun finished(notification: BuildOperationFinishedNotification) {

        if (notification.notificationOperationDetails is ExecuteTaskBuildOperationDetails) {
            try {
                val d: ExecuteTaskBuildOperationDetails =
                    notification.notificationOperationDetails as ExecuteTaskBuildOperationDetails
                val result: ExecuteTaskBuildOperationType.Result =
                    notification.notificationOperationResult as ExecuteTaskBuildOperationType.Result
                map[notification.notificationOperationId]?.also { get ->
                    val stringBuffer = StringBuffer()
                    val exTime =
                        notification.notificationOperationFinishedTimestamp - get.notificationOperationStartedTimestamp
                    val cachingDis = result.cachingDisabledReasonMessage
                    if (cachingDis != null) {
                        stringBuffer.append("disable caching:")
                        stringBuffer.append("\n   ")
                        stringBuffer.append(cachingDis)
                        stringBuffer.append("\n")
                    }
                    if (result.upToDateMessages != null && result.upToDateMessages!!.isNotEmpty()) {
                        stringBuffer.append("disable up to date:")
                        stringBuffer.append("\n")
                        result.upToDateMessages?.forEach {
                            stringBuffer.append(it)
                            stringBuffer.append("\n")
                        }
                        stringBuffer.append("\n")
                    }
                    if (result.originExecutionTime != null) {
                        result.originExecutionTime?.apply {
                            if (exTime > this) {
                                stringBuffer.append("!!!比上次cache任务多执行 " + TimeUtils.toDec(exTime - this))
                            } else {
                                stringBuffer.append("比上次cache任务减少执行 " + TimeUtils.toDec(this - exTime))
                            }
                        }
                        stringBuffer.append("\n")
                    }
                    val taskBuildData = d.create(exTime, stringBuffer.toString())
                    val taskKey = taskBuildData.getKey()
                    if (!values.containsKey(taskKey)) {
                        values[taskKey] = ModuleTasksData(taskKey)
                    }
                    values[taskKey]?.addTaskBuildData(taskBuildData)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    companion object {
        const val K = 3
    }
}


fun ExecuteTaskBuildOperationDetails.create(coast: Long, extra: String): TaskBuildData {
    return TaskBuildData(
        coast, task.name, task.path, task.description, task.state, extra
    )
}
