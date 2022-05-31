package com.kronos.plugin.monitor.repo

import java.io.File

class LogFile internal constructor(
    private val file: File,
    private val reportTypeFile: ReportTypeFile
) {
    private var finish = false
    private var start = false

    private fun setup() {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
    }

    fun append(txt: String?): LogFile {
        if (finish) {
            return this
        }
        if (!start) {
            file.appendText(reportTypeFile.type.start(reportTypeFile.title) + "\n")
            start = true
        }
        txt?.let { file.appendText(it) }
        return this
    }

    fun hasContent(): Boolean {
        return start
    }

    fun finish() {
        if (finish) {
            return
        }
        if (start) {
            file.appendText("\n" + reportTypeFile.type.end())
        }
        finish = true
    }

    init {
        setup()
    }
}

fun ReportTypeFile.getLog(): LogFile {
    return DataRep.getRep().getLogFile(this)
}
