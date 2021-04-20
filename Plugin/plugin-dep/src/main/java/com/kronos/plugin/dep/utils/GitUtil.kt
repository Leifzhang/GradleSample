package com.kronos.plugin.dep.utils

import java.io.File


object GitUtil {
    //get Cur branch
    fun curBranch(): String {
        return CmdUtil.executeForOutput("git rev-parse --abbrev-ref HEAD", null, false)
            .replace("\n", "")
    }

    @JvmStatic
    fun curBranchName(dir: File): String {
        return CmdUtil.executeForOutput("git rev-parse --abbrev-ref HEAD", dir)
            .replace("\n", "")
    }

    fun clone(dir: File, gitUrl: String, branch: String): Boolean {
        return CmdUtil.execute("git clone $gitUrl -l $dir -b $branch", null, true)
    }

    fun clone(dir: File, gitUrl: String): Boolean {
        return CmdUtil.execute("git clone ${gitUrl} -l ${dir}", null, true)
    }

    fun slightClone(dir: File, gitUrl: String, branch: String, depth: Int): Boolean {
        return CmdUtil.execute("git clone ${gitUrl} -l ${dir}", null, true)

    }

    fun shallowClone(dir: File, url: String, branch: String, depth: Int): Boolean {
        return CmdUtil.execute("git clone --depth=${depth} -b ${branch} $url -l ${dir.name} -")
    }

    fun unShallow(dir: File): Boolean {
        return CmdUtil.execute("git pull --unshallow", dir)
    }


    fun isClean(dir: File): Boolean {
        return CmdUtil.executeForOutput("git status -s", dir).trim() == "";
    }

    fun fileStatus(dir: File): String {
        return CmdUtil.executeForOutput("git status -s ", dir)
    }

    fun isLocalBranch(dir: File, branch: String): Boolean {
        return File(dir, ".git/refs/heads/$branch").exists()
    }

    fun isBranchChanged(dir: File, branch: String): Boolean {
        return !curBranchName(dir).equals(branch)
    }

    fun checkoutBranch(dir: File, branch: String) {
        CmdUtil.execute("git checkout ${branch}", dir)
    }

    fun checkoutRemoteBranch(dir: File, branch: String) {
        CmdUtil.execute("git checkout -b $branch origin/$branch", dir)
    }

    fun checkoutNewBranch(dir: File, branch: String) {
        CmdUtil.execute("git checkout -b $branch", dir)
    }

    fun isRemoteBranch(dir: File, branch: String): Boolean {
        CmdUtil.execute("git fetch", dir)
        val text = CmdUtil.executeForOutput("git branch -r")
        return text.contains("origin/$branch")
    }

    fun isLocalExistBranch(dir: File, branch: String): Boolean {
        return File(dir, ".git/refs/heads/$branch").exists()
    }

    fun pullRebase(dir: File): Boolean {
        return CmdUtil.execute("git pull --rebase", dir, false)
    }

}
