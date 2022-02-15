package com.kronos.plugin.version.utils

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/10
 *
 */
object PluginManagementExtensions {

    val BUILD_VERSION = "+"

    val PLUGIN_MANAGEMENT_SCRIPT = "initscript {\n" + "    repositories {\n" +
            "        mavenLocal() " +
            "    dependencies {\n" +
            "        classpath \"com.bilibili.build:fawkes-library:${BUILD_VERSION}\"\n" +
            "    }\n" +
            "}\n"

}
