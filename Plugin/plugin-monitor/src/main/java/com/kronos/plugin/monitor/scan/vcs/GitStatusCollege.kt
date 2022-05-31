package com.kronos.plugin.monitor.scan.vcs

import com.kronos.plugin.monitor.utils.GitUtil.getCommit
import com.kronos.plugin.monitor.utils.GitUtil.fileStatus
import com.kronos.plugin.monitor.utils.GitUtil.curBranchName
import com.kronos.plugin.monitor.repo.data.GitStatus
import com.kronos.plugin.monitor.scan.vcs.GitStatusCollege.GitStatusTask
import com.kronos.plugin.monitor.utils.FileUtils
import java.io.File
import java.util.ArrayList

class GitStatusCollege {

    fun execute(): List<GitStatus> {
        val gitStatuses: MutableList<GitStatus> = ArrayList()
        gitStatuses.add(GitStatusTask(FileUtils.rootFile.absolutePath, "root").execute())
        return gitStatuses
    }

    class GitStatusTask internal constructor(var wp: String, var path: String) {
        fun execute(): GitStatus {
            val gitStatus = GitStatus()
            gitStatus.path = path
            val file = File(wp)
            if (file.exists() && File(file, ".git").exists()) {
                gitStatus.commit = getCommit(file)
                gitStatus.status = fileStatus(file)
                gitStatus.branch = curBranchName(file)
            } else {
                gitStatus.status = "当前仓库git有异常"
            }
            return gitStatus
        }
    }
}