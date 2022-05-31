package com.kronos.plugin.monitor.repo.data

class DaemonWork {
    var displayName: String? = null
    var className: String? = null
    var start: Long = 0
    var time: Long = 0
    var success = false

    fun toDec(): String {
        return """
               displayName=$displayName
               className=$className
               time=$time
               success=$success
               
               """.trimIndent()
    }
}