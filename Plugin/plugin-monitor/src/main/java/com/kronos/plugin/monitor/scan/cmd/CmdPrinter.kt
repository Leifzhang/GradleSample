package com.kronos.plugin.monitor.scan.cmd

import com.kronos.plugin.monitor.repo.LogFile
import com.kronos.plugin.monitor.repo.data.BuildCmd
import java.lang.StringBuilder

class CmdPrinter(var file: LogFile) {
    fun execute(cmd: BuildCmd?) {
        val stringBuilder = StringBuilder("./gradlew ")
        cmd?.taskNames?.forEach {
            stringBuilder.append(" $it")
        }
        cmd?.initScripts?.forEach {
            stringBuilder.append(" --init-script $it");
        }
        cmd?.projectProperties?.forEach {
            stringBuilder.append(" -P" + it.key + "=" + it.value)
        }
        cmd?.systemPropertiesArgs?.forEach {
            stringBuilder.append(" -D" + it.key + "=" + it.value)
        }
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        file.append(stringBuilder.toString())
    }
}