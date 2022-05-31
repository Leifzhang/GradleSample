package com.kronos.plugin.monitor.utils

import com.kronos.plugin.monitor.repo.DataRep.Companion.getLogFieFrom
import com.kronos.plugin.monitor.repo.DataRep
import org.gradle.api.invocation.Gradle
import java.lang.StringBuilder
import java.util.*

class UuidFinder(
    private val gradle: Gradle,
    var owner: String,
    var date: String,
    var time: String
) {
    private var uuid: String? = null
    fun genUuid(): String {
        if (uuid != null) {
            return requireNotNull(uuid)
        }
        var uu = uuidShortInner()
        var f = getLogFieFrom(gradle, owner, date, time, uu)
        while (f.exists()) {
            uu = uuidShortInner()
            f = getLogFieFrom(gradle, owner, date, time, uu)
        }
        uuid = uu
        return requireNotNull(uuid)
    }

    private fun uuidShortInner(): String {
        val str = "abcdefghijklmnopqrstuvwxyz0123456789"
        val uuid = StringBuilder()
        val random = Random()
        for (i in 0..3) {
            val ch = str[random.nextInt(str.length)]
            uuid.append(ch)
        }
        return uuid.toString()
    }
}