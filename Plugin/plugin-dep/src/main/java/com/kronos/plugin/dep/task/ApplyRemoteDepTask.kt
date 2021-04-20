package com.kronos.plugin.dep.task

import com.kronos.plugin.dep.DepExtension
import com.kronos.plugin.dep.PluginLogger
import com.kronos.plugin.dep.utils.GitUtil
import com.kronos.plugin.dep.utils.Utils
import com.kronos.plugin.dep.utils.Utils.getRemoteDepConfigUrl
import org.codehaus.groovy.runtime.GStringImpl
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.util.*

open class ApplyRemoteDepTask : DefaultTask() {

    init {
        group = "abc"
    }

    @TaskAction
    fun apply() {

        val depExtension: DepExtension = if (project.file("depconfig.properties").exists()) {
            val config = Properties()
            config.load(project.file("depconfig.properties").inputStream())
            val branch = config.getOrDefault("branch", "")
            val forceUseConfiguredVersion = config.getOrDefault("forceUseConfigVersion", "true")
            DepExtension(branch as String, forceUseConfiguredVersion == "true")
        } else {
            DepExtension("", true)
        }

        val depUrls = getProjectDepUrls(depExtension)

        depUrls.forEach { url -> applyUrl(project, url) }

        if (depExtension.forceUseConfigVersion) {
            forceUseDepConfigVersion(project)
        }

    }


    private fun applyUrl(project: Project, url: String) {
        PluginLogger.info("依赖配置】: apply from:${url}")
        project.apply(mapOf(Pair("from", url)))
    }

    private fun forceUseDepConfigVersion(project: Project) {
        project.subprojects {
            val depModuleSelectorNotations = mutableListOf<String>()

            project.extensions.extraProperties.properties.forEach { (key, any) ->

                if (any != null && key != null && key.endsWith("dep")) {
                    if (any is Map<*, *>) {
                        PluginLogger.info("依赖配置】: 强制对齐版本${key}")
                        collectDepModuleVersionSelectorNotations(depModuleSelectorNotations, any)
                    }
                }
            }

            it.configurations.all { configuration ->
                configuration.resolutionStrategy.force(depModuleSelectorNotations)
            }
        }


    }

    /**
     * @param list 收集到的依赖被加入到该集合中
     * @param depConfig:配置，其中key为配置名,value为配置的gav标识。
     * 从指定的map 中收集所有依赖的标识符到指定列表中
     */
    private fun collectDepModuleVersionSelectorNotations(
        list: MutableList<String>,
        depConfig: Map<*, *>?
    ) {
        depConfig?.values?.forEach {
            if (it is Map<*, *>) {
                collectDepModuleVersionSelectorNotations(list, it)
            } else if (it is String || it is GStringImpl) {
                val notation: String = it.toString()
                println("[强制依赖]:$notation")

                list.add(notation)
            } else {
                it?.let {
                    println("can't handle dep config value type" + it::class.java)
                }
            }
        }
    }


    companion object {

        fun getProjectDepUrls(depExtension: DepExtension): List<String> {

            var curBranch = GitUtil.curBranch()
            PluginLogger.info("工程当前分支为: $curBranch")
            if (depExtension.branch.isNotEmpty()) {
                curBranch = depExtension.branch
                PluginLogger.info("DepConfig 配置 指定了分支 $curBranch")
            }

            val depConfigUrls = mutableListOf<String>()

            val depRemoteConfig = getRemoteDepConfigUrl(curBranch) {
                Utils.getDepRemoteUrl(it)
            }
            depConfigUrls.add(depRemoteConfig)


            return depConfigUrls
        }
    }


}