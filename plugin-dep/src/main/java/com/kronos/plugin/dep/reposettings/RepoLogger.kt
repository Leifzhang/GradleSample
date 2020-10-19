package com.kronos.plugin.dep.reposettings

import org.gradle.api.Project
import org.gradle.api.logging.Logging

object RepoLogger {

    @JvmStatic
    fun setProject(project: Project) {
        LOGGER = project.logger
    }

    private var LOGGER = Logging.getLogger(Project::class.java)
    private const val TAG = "【RepoPlugin】"
    private const val TAG_PROGRESS = "【RepoPlugin】[progress]"


    @JvmStatic
    fun warn(msg: String) {
        println(wrapMsg(msg))
        LOGGER.warn(wrapMsg(msg))
    }

    @JvmStatic
    fun error(msg: String) {
        println(wrapMsg(msg))
        LOGGER.error(wrapMsg(msg))
    }

    @JvmStatic
    fun info(msg: String) {
        println(wrapMsg(msg))
        LOGGER.info(wrapMsg(msg))
    }

    @JvmStatic
    fun progressInfo(msg: String) {
        println("$TAG_PROGRESS:${msg}")
    }

    @JvmStatic
    private fun wrapMsg(msg: String): String {
        return "$TAG:${msg}"
    }

}