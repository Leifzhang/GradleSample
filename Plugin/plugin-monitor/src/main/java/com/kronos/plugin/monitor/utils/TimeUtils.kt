package com.kronos.plugin.monitor.utils

object TimeUtils {

    fun toDec(d: Long): String {
        if (d < 1000) {
            return d.toString() + "ms"
        }
        return if (d < 60 * 1000) {
            val sec = d / 1000
            val ms = d % 1000
            if (ms == 0L) {
                sec.toString() + "s"
            } else {
                sec.toString() + "s " + ms + "ms"
            }
        } else {
            val m = d / 1000 / 60
            val sec = d / 1000 % 60
            if (sec == 0L) {
                m.toString() + "m"
            } else {
                m.toString() + "m " + sec + "s"
            }
        }
    }
}