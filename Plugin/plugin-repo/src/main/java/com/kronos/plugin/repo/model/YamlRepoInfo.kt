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
    return ModuleInfo(name, origin, srcBuild, project, branch)
}


fun parserInclude(map: LinkedHashMap<Any, Any>, project: File): IncludeModuleInfo? {
    val name = map["name"].toString()
    val origin = map["origin"].toString()
    val branch = map["branch"].toString()
    val srcBuild = map["srcBuild"].toString().toBoolean()
    val modules = map["modules"]
    val moduleList = mutableListOf<String>()
    if (modules is MutableList<*>) {
        modules.forEach {
            if (it is String) {
                moduleList.add(it)
            }
        }
    }
    if (moduleList.isEmpty()) {
        return null
    }
    return IncludeModuleInfo(name, origin, srcBuild, project, branch, moduleList)
}