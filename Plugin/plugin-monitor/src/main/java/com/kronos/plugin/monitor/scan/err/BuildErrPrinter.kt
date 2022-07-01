package com.kronos.plugin.monitor.scan.err

import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.data.BuildErr
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

class BuildErrPrinter(var file: LogFile) {
    fun execute(err: BuildErr) {
        if (err.throwable != null) {
            val tt: Writer = StringWriter()
            val printWriter = PrintWriter(tt)
            err.throwable.printStackTrace(printWriter)
            file.append(tt.toString())
        }
    }
}