package com.kronos.plugin.version.utils

import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.initialization.ClassLoaderScopeRegistry
import org.gradle.internal.classpath.DefaultClassPath

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

    fun createCoreClassLoader(
        classLoaderScopeRegistry: ClassLoaderScopeRegistry,
        scriptHandler: ScriptHandler
    ): ClassLoader? {
        val configurations = scriptHandler.configurations
        val config = configurations.create("fawkes_build_core_classpaths")
        config.isVisible = false
        config.isTransitive = true
        config.isCanBeConsumed = false
        config.description = "The fawkes build  classpath"
        scriptHandler.dependencies.add(
            config.name,
            "com.kronos.plugin:plugin-version:0.2.11"
        )
        return classLoaderScopeRegistry.coreAndPluginsScope.createChild("fawkes")
            .export(DefaultClassPath.of(config.files)).exportClassLoader
    }


}
