package com.kronos.plugin.dep.utils

import java.util.*

/**
 * @Author LiABao
 * @Since 2021/6/4
 */
object ResourcesUtils {
    fun get(): Properties {
        val stream = javaClass.getResourceAsStream("gradle.properties")
        val properties = Properties()
        stream.use {
            properties.load(it)
        }
        return properties
    }

    fun property(name: String): String {
        return properties.getProperty(name) ?: ""
    }

    private val properties by lazy {
        get()
    }
}
