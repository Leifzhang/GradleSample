package com.kronos.plugin.monitor.scan.analyse.console

import com.kronos.plugin.monitor.scan.analyse.bean.ETask
import com.kronos.plugin.monitor.scan.analyse.console.ResultConsole

class TaskConsole(private val index: Int, private val cTask: ETask) {
    fun print(printStream: ResultConsole.Printer) {
        printStream.println("" + index + ". " + cTask.task)
    }

    override fun toString(): String {
        return """
            $index. ${cTask.task}
            
            """.trimIndent()
    }
}