package com.kronos.plugin.monitor.scan.analyse.task

import com.kronos.plugin.monitor.utils.CmdUtil.executeForOutput
import com.kronos.plugin.monitor.utils.Logger.log
import com.kronos.plugin.monitor.scan.analyse.ErrorReport
import com.kronos.plugin.monitor.scan.analyse.bean.EIssue
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

internal class IssueDevFindTask(private val errorReport: ErrorReport) {
    fun execute() {
        for (ciIsue in errorReport.ciIsues) {
            setCode(ciIsue)
            if (ciIsue.EProject == null) {
                ciIsue.EProject = errorReport.findProject(ciIsue.file)
            }
            ciIsue.EProject?.addCIssues(ciIsue)
        }
    }

    private fun setCode(ciIsue: EIssue) {
        try {
            val f = File(ciIsue.file)
            var cmd = " git blame --show-email -l " + f.name
            var start = ciIsue.line - 2
            if (start <= 1) {
                start = 1
            }
            var owner: String? = null
            cmd += " -L $start,+5"
            val lines: List<String> = executeForOutput(cmd, f.parentFile).lines()
            val dev = Pattern.compile("(\\w+) .*\\(<(.+)> (.*) (\\d+)\\) (.*)")
            val stringBuffer = StringBuffer()
            for (line in lines) {
                val matcher = dev.matcher(line)
                if (matcher.find()) {
                    val commit = matcher.group(1)
                    val email = matcher.group(2)
                    val dateStr = matcher.group(3)
                    val lineN = matcher.group(4)
                    val code = matcher.group(5)
                    var user = email
                    if (email.contains("@")) {
                        user = email.substring(0, email.indexOf("@"))
                    }
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ")
                    var d: Date? = null
                    try {
                        d = dateFormat.parse(dateStr)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm")
                    simpleDateFormat.timeZone = TimeZone.getTimeZone("UT+8:00")
                    val sDate = simpleDateFormat.format(d)
                    if (lineN.toInt() == ciIsue.line) {
                        owner = user
                        stringBuffer.append(
                            """${commit.substring(0, 8)} ${formatUser(user)} $sDate $lineN * $code
"""
                        )
                    } else {
                        stringBuffer.append(
                            """${commit.substring(0, 8)} ${formatUser(user)} $sDate $lineN   $code
"""
                        )
                    }
                }
            }
            ciIsue.owner = owner
            ciIsue.code = stringBuffer.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            log("获取相关代码失败 " + e.message)
        }
    }

    private fun formatUser(user: String): String {
        return if (user.length > 11) {
            user.substring(0, 11)
        } else user + "           ".substring(0, 11 - user.length)
    }
}