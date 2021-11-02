package com.kronos.plugin.repo.model

import com.kronos.plugin.repo.RepoLogger
import com.kronos.plugin.repo.utils.GitUtil
import java.io.File
import java.lang.RuntimeException

/**
 *
 *  @Author LiABao
 *  @Since 2021/9/8
 *
 */
open class IncludeModuleInfo(
    val name: String,
    private val origin: String,
    val srcBuild: Boolean,
    private val repoManageProjectDir: File,
    val branch: String,
    val projectNameList: MutableList<String>
) {

    //模块所属git工程的根路径
    var moduleGitRootPath: File


    init {
        val moduleRootDirName = origin.split("/").last().split(".").first()

        moduleGitRootPath = File(repoManageProjectDir, moduleRootDirName)
    }


    fun getModulePath(name: String): File {
        return File(moduleGitRootPath, name)
    }

    private fun isModuleGitDirExist(): Boolean {
        return moduleGitRootPath.exists()
    }

    /**
     *  配置该模块在本地的环境
     *  => 如果模块尚未clone 则执行clone 并切换到对应分支
     *  => 如果模块已经在本地，则直接checkout到对应分支
     */
    fun settingProject() {
        if (!isModuleGitDirExist()) {
            GitUtil.clone(moduleGitRootPath, origin)
            //return
        }


        RepoLogger.progressInfo("moudle $name  project already exists in path $moduleGitRootPath")
        val clean = GitUtil.isClean(moduleGitRootPath)
        val branchChanged = GitUtil.isBranchChanged(moduleGitRootPath, branch)


        //如果分支未变更过，且本地有修改的文件未保存，则终止，需要用户手动处理

        if (branchChanged && !clean) {
            throw RuntimeException(
                "[repo] - moudle ${name} is not clean , please commit " +
                        "or revert changes before checkout branch"
            )

        }


        //本地没有修改的文件则 主动切换分支
        if (clean) {
            //如果是本地已经存在的分支
            if (GitUtil.isLocalExistBranch(moduleGitRootPath, branch)) {

                RepoLogger.progressInfo("moudle ${name}:  file:${moduleGitRootPath}.git/refs/heads/${branch} exists so ${branch} is exist Branch in local")
                GitUtil.checkoutBranch(moduleGitRootPath, branch)
            } else {
                if (GitUtil.isRemoteBranch(moduleGitRootPath, branch)) {
                    RepoLogger.progressInfo("moudle ${name}  isRemoteBranch")

                    GitUtil.checkoutRemoteBranch(moduleGitRootPath, branch)
                } else {
                    GitUtil.checkoutNewBranch(moduleGitRootPath, branch)

                }
            }

            tryPullRebase()
        }

    }


    private fun tryPullRebase() {
        if (!GitUtil.isRemoteBranch(moduleGitRootPath, branch))
            return
        val success = GitUtil.pullRebase(moduleGitRootPath)

        if (!success) {
            throw RuntimeException(
                " execute command [git pull --rebase] for project ${name} failed，please check  to see if  files conflict，" +
                        "if conflict,  resolve it. then try again ,\n file status:\n${
                            GitUtil.fileStatus(
                                moduleGitRootPath
                            )
                        }"
            )
        }

    }


    fun curBranch(): String? {
        return if (moduleGitRootPath.exists()) {
            GitUtil.curBranchName(moduleGitRootPath)
        } else {
            //没有使用源码依赖模式
            null
        }
    }

}