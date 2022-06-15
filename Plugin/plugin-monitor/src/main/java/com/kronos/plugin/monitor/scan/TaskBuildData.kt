package com.kronos.plugin.monitor.scan

import com.kronos.plugin.monitor.utils.TimeUtils
import org.gradle.api.internal.tasks.TaskStateInternal


data class TaskBuildData(
    val costa: Long, val taskName: String, val taskPath: String,
    val taskDesc: String?,
    val taskState: TaskStateInternal,
    val extra: String
) {

    fun buildLog(): String {
        var text = ""
        text += "<p class=\"small\""
        if (isWarning()) {
            text += "style=\"color:red\""
        }
        text += ">"
        text += " $taskName  ${taskState.outcome.toString()} "
        text += " ${TimeUtils.toDec(costa)}"
        if (isWarning()) {
            text += " -----fbi warning----- "
        }
        text += "</p>"
        return text
    }

    private fun isWarning(): Boolean {
        if (costa > 1000 * 60) {
            return true
        }
        return false
    }


    fun getKey(): String {
        return taskPath.replace(taskName, "").let {
            it.subSequence(0, it.length - 1).toString()
        }
    }
}


class ModuleTasksData(val path: String) : Comparable<ModuleTasksData>, Comparator<ModuleTasksData> {

    val tasks = mutableListOf<TaskBuildData>()
    private var max: TaskBuildData? = null
    private var min: TaskBuildData? = null
    var totalCosta: Long = 0

    fun addTaskBuildData(value: TaskBuildData) {
        tasks.add(value)
        if ((max?.costa ?: 0) < value.costa) {
            max = value
        }
        if ((min?.costa ?: Long.MAX_VALUE) > value.costa) {
            min = value
        }
        totalCosta += value.costa
    }

    fun log(expand: Boolean = true): String {
        if (tasks.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        sb.apply {
            sb.append("<p class=\"small\">")
            append("project $path")
            append("  总耗时: ${TimeUtils.toDec(totalCosta)}")
            max?.apply {
                append("  最耗时任务: ").append(taskName).append("  ${TimeUtils.toDec(this.costa)}")
                sb.append("</p>")
            }
            if (expand) {
                tasks.forEach {
                    append(it.buildLog())
                }
            }
            // 想了想没用
            /* min?.apply {
                      append("minCostaTask: ").append(taskName)
                      append("\r\n")
                  }*/
        }
        sb.append("\r\n\r\n")
        return sb.toString()
    }

    override fun compare(o1: ModuleTasksData?, o2: ModuleTasksData?): Int {
        return (o1?.totalCosta ?: 0L).compareTo(o2?.totalCosta ?: 0)
    }

    override fun compareTo(other: ModuleTasksData): Int {
        return totalCosta.compareTo(other.totalCosta)
    }
}