package com.kronos.plugin.monitor.scan.analyse.bean

import com.kronos.plugin.monitor.scan.analyse.bean.EProject

class EIssue {
    var EProject: EProject? = null
    var msg: String? = null
    var file: String? = null
    var line: Int = 0
    var type: String? = null
    var dev: String? = null
    var owner: String? = null
    var tool: String? = null
    var code: String? = null
    var errorCode = 0

    override fun toString(): String {
        return "CIIsue{" +
                "msg='" + msg + '\'' +
                ", file='" + file + '\'' +
                ", line='" + line + '\'' +
                ", dev='" + dev + '\'' +
                ", owner='" + owner + '\'' +
                ", tool='" + tool + '\'' +
                '}'
    }
}