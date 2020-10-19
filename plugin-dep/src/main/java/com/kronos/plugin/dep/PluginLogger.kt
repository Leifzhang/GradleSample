package com.kronos.plugin.dep

object PluginLogger {
    private const val TAG = "【DepDefinitionPlugin】"

    fun warn(msg: String) {
        println(wrapMsg(msg))
    }

    fun info(msg: String) {
        println(wrapMsg(msg))
    }

    private fun wrapMsg(msg: String): String {
        return "$TAG:${msg}"
    }
}