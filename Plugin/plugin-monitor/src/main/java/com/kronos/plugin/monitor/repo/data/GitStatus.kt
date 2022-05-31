package com.kronos.plugin.monitor.repo.data

class GitStatus {

    var path: String? = null
    var commit: String? = null
    var branch: String? = null
    var status: String? = null

    override fun toString(): String {
        return "GitStatus{" +
                "path='" + path + '\'' +
                ", commit='" + commit + '\'' +
                ", branch='" + branch + '\'' +
                ", status='" + status + '\'' +
                '}'
    }

    fun toDec(): String {
        return """
               path=$path
               branch=$branch
               commit=$commit
               status=$status
               
               """.trimIndent()
    }
}