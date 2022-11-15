package com.kronos.plugin.version.utils

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/10
 *
 */
object PluginManagementExtensions {


    const val PLUGIN_MANAGEMENT_SCRIPT = "initscript {\n" + "    repositories {\n" +
            "        mavenLocal()\n " +
            "\n" +
            "        maven { setUrl(\"https://maven.aliyun.com/repository/central/\") }\n" +
            "        google()" +
            "    }\n" +
            "    dependencies {\n" +
            "        classpath \"com.kronos.plugin:plugin-version:0.2.11\"\n" +
            "    }\n" +
            "}\n"

}
