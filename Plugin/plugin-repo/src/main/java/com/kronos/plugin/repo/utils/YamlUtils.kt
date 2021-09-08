package com.kronos.plugin.repo.utils

import com.kronos.plugin.repo.model.RepoInfo
import com.kronos.plugin.repo.model.parser
import com.kronos.plugin.repo.model.parserInclude
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

/**
 *
 *  @Author LiABao
 *  @Since 2021/4/20
 *
 */
object YamlUtils {

    fun inflate(projectDir: File): RepoInfo {
        val yaml = Yaml()
        val f = File(projectDir, "repo.yaml")
        val dir = File(projectDir.parentFile, "subModules")
        val repoInfo = RepoInfo(dir)
        if (f.exists()) {
            val repoInfoYaml = yaml.load<LinkedHashMap<String, Any>>(FileInputStream(f))
            if (repoInfoYaml.containsKey("modules")) {
                val modulesList = repoInfoYaml["modules"]
                if (modulesList is MutableList<*>) {
                    modulesList.forEach {
                        if (it is LinkedHashMap<*, *>) {
                            val module = parser(it as LinkedHashMap<Any, Any>, dir)
                            Log.info("moduleName:${module.name}")
                            repoInfo.moduleInfoMap[module.name] = module
                        }
                    }
                }
            }
            inflateInclude(projectDir, repoInfo)
        }
        return repoInfo
    }

    fun inflateInclude(projectDir: File, repo: RepoInfo) {
        val yaml = Yaml()
        val dir = File(projectDir.parentFile, "subModules")
        val f = File(projectDir, "repo-include.yaml")
        if (f.exists()) {
            val repoInfoYaml = yaml.load<LinkedHashMap<String, Any>>(FileInputStream(f))
            if (repoInfoYaml.containsKey("projects")) {
                val modulesList = repoInfoYaml["projects"]
                if (modulesList is MutableList<*>) {
                    modulesList.forEach {
                        if (it is LinkedHashMap<*, *>) {
                            val module = parserInclude(it as LinkedHashMap<Any, Any>, dir)
                            module?.let {
                                repo.includeModuleInfo[module.name] = module
                            }
                        }
                    }
                }
            }
        }
    }
}