package com.kronos.plugin.monitor.scan.analyse.bean

import com.kronos.plugin.monitor.utils.FileUtils
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

class EProject(var dir: File) {
    var owners: ArrayList<String> = ArrayList()

    var name: String? = null
    var path: String? = null
    var eTasks: MutableList<ETask> = ArrayList()

    @JvmField
    var eIssues: MutableList<EIssue> = ArrayList()
    fun addCIssues(issue: EIssue) {
        eIssues.add(issue)
    }

    fun addTask(task: ETask) {
        eTasks.add(task)
    }

    private fun initOwners() {
        var CONTRIBUTORS: File? = dir
        while (CONTRIBUTORS != null && !File(CONTRIBUTORS, CONTRIBUTORS_FILE).exists()) {
            CONTRIBUTORS = CONTRIBUTORS.parentFile
            if (CONTRIBUTORS === FileUtils.rootFile.parentFile) {
                break
            }
        }
        if (CONTRIBUTORS == null || !File(CONTRIBUTORS, CONTRIBUTORS_FILE).exists()) {
            return
        }
        try {
            val strings = File(CONTRIBUTORS, CONTRIBUTORS_FILE).readLines();
            var owner = false
            strings.forEach { item ->
                if (item.startsWith("#")) {
                    if (Pattern.matches("#\\s*Owner\\s*", item)) {
                        owner = true
                        return@forEach
                    }
                }
                if (owner) {
                    val trim = item.trim()
                    if (trim.isNotEmpty()) {
                        owners.add(trim);
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace();
            throw  RuntimeException(e)
        }
    }

    companion object {
        const val CONTRIBUTORS_FILE = "CONTRIBUTORS.md"
    }

    init {
        initOwners()
    }
}