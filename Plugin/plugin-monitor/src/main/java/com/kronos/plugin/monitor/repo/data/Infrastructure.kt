package com.kronos.plugin.monitor.repo.data

class Infrastructure {

    var system: String? = null
    var gitEmail: String? = null
    var gitUser: String? = null
    var buildOwner: String? = null
    var envUserName: String? = null
    var javaEnvUserName: String? = null
    var hostname: String? = null
    var pid: String? = null
    var cpu = 0
    var maxWorkerCount = 0
    var javaVersion: String? = null
    var username: String? = null
    var freeMemory: String? = null
    var runtimeFreeMemory: String? = null
    var runtimeMaxMemory: String? = null
    var totalMemory: String? = null
    var runtimeTotalMemory: String? = null

    override fun toString(): String {
        return "Infrastructure{" +
                "system='" + system + '\'' +
                ", cpu=" + cpu +
                ", maxWorkerCount=" + maxWorkerCount +
                ", javaVersion='" + javaVersion + '\'' +
                ", username='" + username + '\'' +
                ", freeMemory=" + freeMemory +
                ", runtimeFreeMemory=" + runtimeFreeMemory +
                ", runtimeMaxMemory=" + runtimeMaxMemory +
                ", totalMemory=" + totalMemory +
                ", runtimeTotalMemory=" + runtimeTotalMemory +
                '}'
    }

    fun toDec(): String {
        return """
               system=$system
               gitEmail=$gitEmail
               buildOwner=$buildOwner
               gitUser=$gitUser
               envUserName=$envUserName
               javaEnvUserName=$javaEnvUserName
               hostname=$hostname
               pid=$pid
               cpu=$cpu
               maxWorkerCount=$maxWorkerCount
               javaVersion=$javaVersion
               username=$username
               freeMemory=$freeMemory
               runtimeFreeMemory=$runtimeFreeMemory
               runtimeMaxMemory=$runtimeMaxMemory
               totalMemory=$totalMemory
               runtimeTotalMemory=$runtimeTotalMemory
               
               """.trimIndent()
    }
}