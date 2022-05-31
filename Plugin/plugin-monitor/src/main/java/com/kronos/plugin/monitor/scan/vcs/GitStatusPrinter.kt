package com.kronos.plugin.monitor.scan.vcs

import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.data.GitStatus

class GitStatusPrinter(var file: LogFile) {
    fun execute(gitStatuses: List<GitStatus>?) {
        gitStatuses?.forEach {
            file.append(it.toDec())
            file.append("\n")
        }
    }
}