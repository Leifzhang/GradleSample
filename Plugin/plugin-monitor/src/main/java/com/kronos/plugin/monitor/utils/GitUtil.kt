package com.kronos.plugin.monitor.utils

import java.io.File


object GitUtil {

    @JvmStatic
    fun curBranchName(dir: File): String {
        return CmdUtil.executeForOutput("git rev-parse --abbrev-ref HEAD", dir)
            .replace("\n", "")
    }


    @JvmStatic
    fun getCommit(dir: File): String {
        return CmdUtil.executeForOutput("git log --max-count=1 --pretty=%H ", dir)
    }

    @JvmStatic
    fun fileStatus(dir: File): String {
        return CmdUtil.executeForOutput("git status -s ", dir)
    }

}
