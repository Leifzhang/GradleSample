package com.kronos.plugin.monitor.scan.analyse.task.issue

import com.kronos.plugin.monitor.scan.analyse.ErrorCodeConstant
import com.kronos.plugin.monitor.scan.analyse.ErrorReport
import com.kronos.plugin.monitor.scan.analyse.bean.EIssue
import java.util.regex.Pattern

class KotlinBuilderErrorAnalyse(private val errorReport: ErrorReport) : IssueMatch {
    override fun found(txt: String): Boolean {
        val matcher = issuePattern.matcher(txt)
        if (matcher.find()) {
            val issue = EIssue()
            issue.file = matcher.group(1)
            issue.line = Integer.valueOf(matcher.group(2))
            issue.msg = txt
            issue.tool = "kotlin"
            issue.errorCode = ErrorCodeConstant.KOTLIN_ISSUE
            errorReport.report(issue)
        }
        return false
    }

    override fun eioStart(txt: String): Boolean {
        return issuePattern.matcher(txt).find()
    }

    companion object {
        var issuePattern = Pattern.compile("^e: (.*): \\((\\d+), (\\d+)\\): (.*)")
    }
}