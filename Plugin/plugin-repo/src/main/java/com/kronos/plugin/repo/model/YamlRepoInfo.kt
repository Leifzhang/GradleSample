package com.kronos.plugin.repo.model

import java.io.File

/**
 * @Author LiABao
 * @Since 2021/4/20
 */
fun parser(map: LinkedHashMap<Any, Any>, project: File): ModuleInfo {
    val name = map["name"].toString()
    val origin = map["origin"].toString()
    val branch = map["branch"].toString()
    val srcBuild = map["srcBuild"].toString().toBoolean()
    val substitute = map["substitute"].toString()
    return ModuleInfo(name, origin, srcBuild, substitute, project, branch)
}