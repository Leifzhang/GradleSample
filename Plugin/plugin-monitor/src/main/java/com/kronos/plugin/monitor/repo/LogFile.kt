package com.kronos.plugin.monitor.repo

import java.io.File

class LogFile internal constructor(
    private val file: File,
    private val reportTypeFile: ReportTypeFile
) {
    private var finish = false
    private var start = false

    fun setup() {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
    }

    fun append(txt: String?) {
        if (finish) {
            return
        }
        if (!start) {
            start = true
            file.writeText(reportTypeFile.type.start(reportTypeFile.title))
        }
        txt?.let { file.writeText(it) }
    }

    fun hasContent(): Boolean {
        return start
    }

    fun finish() {
        if (finish) {
            return
        }
        append(reportTypeFile.type.end())
        finish = true
    }

    init {
        setup()
    }
}