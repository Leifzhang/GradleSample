package com.kronos.plugin.monitor.scan.info

import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.data.Infrastructure

class InfrastructurePrinter(var file: LogFile) {

    fun execute(infrastructure: Infrastructure) {
        file.append(infrastructure.toDec())
    }
}