package com.kronos.plugin.monitor.scan.err

import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.data.BuildErr
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

class BuildErrPrinter(var file: LogFile) {
    fun execute(err: BuildErr) {
        val tt: Writer = StringWriter()
        val printWriter = PrintWriter(tt)
        err.throwable.forEach {
            printWriter.print(it.description)
        }
        file.append(tt.toString())
    }
}