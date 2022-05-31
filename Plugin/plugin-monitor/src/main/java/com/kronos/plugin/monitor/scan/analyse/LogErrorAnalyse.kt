package com.kronos.plugin.monitor.scan.analyse

import com.kronos.plugin.monitor.scan.analyse.ErrorReport
import com.kronos.plugin.monitor.scan.analyse.task.issue.IssueMatch
import com.kronos.plugin.monitor.scan.analyse.task.issue.KotlinBuilderErrorAnalyse
import com.kronos.plugin.monitor.scan.analyse.task.issue.JavaBuilderErrorAnalyse
import com.kronos.plugin.monitor.scan.analyse.task.issue.CnJavaBuilderErrorAnalyse
import java.io.IOException
import java.util.ArrayList

class LogErrorAnalyse(report: ErrorReport?) {

    private val issueMatches: MutableList<IssueMatch> = ArrayList()
    fun analyse(category: String?, message: String) {
        try {
            val lines: List<String> = message.lines()
            var issueMatch: IssueMatch? = null
            for (line in lines) {
                if (issueMatch == null) {
                    issueMatch = findBestIssueMatch(line)
                }
                if (issueMatch != null) {
                    val eio = issueMatch.found(line)
                    if (!eio) {
                        issueMatch = findBestIssueMatch(line)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun findBestIssueMatch(line: String): IssueMatch? {
        for (issueMatch in issueMatches) {
            if (issueMatch.eioStart(line)) {
                return issueMatch
            }
        }
        return null
    }

    init {
        issueMatches.add(KotlinBuilderErrorAnalyse(report))
        issueMatches.add(JavaBuilderErrorAnalyse(report))
        issueMatches.add(CnJavaBuilderErrorAnalyse(report))
    }
}