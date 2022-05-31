package com.kronos.plugin.monitor.scan.analyse.bean

class ETask {
    var task: String? = null
    var errorCode = 0
    override fun toString(): String {
        return "CTask{" +
                "task='" + task + '\'' +
                '}'
    }
}