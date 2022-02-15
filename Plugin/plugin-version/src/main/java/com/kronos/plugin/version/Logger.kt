package com.kronos.plugin.version

import org.gradle.api.logging.Logging

/**
 *
 *  @Author LiABao
 *  @Since 2022/1/20
 *
 */
object Logger {

    var debug = false
    const val DEBUG_CACHE_INFO = "DEBUG_CACHE_INFO"
    private val LOGGER by lazy {
        Logging.getLogger(Logger::class.java)
    }

    @JvmStatic
    fun log(msg: Any?) {
        msg?.let {
            LOGGER.lifecycle(" Monitor : $msg")
        }
    }

    @JvmStatic
    fun debug(msg: Any?) {
        if (debug) {
            msg?.let {
                LOGGER.lifecycle(" Monitor : $msg")
            }
        }
    }

    @JvmStatic
    fun getDebugCacheInfo(): Boolean {
        return System.getProperty(DEBUG_CACHE_INFO) == true.toString()
    }

    @JvmStatic
    fun setDebugCacheInfo(debugCacheInfo: Boolean) {
        System.setProperty(DEBUG_CACHE_INFO, debugCacheInfo.toString());
    }

    @JvmStatic
    fun msg(msg: Any?) {
        LOGGER.lifecycle(msg.toString())
    }

}

fun Any?.log() {
    Logger.log(this)
}

fun Any?.debug() {
    Logger.debug(this)
}