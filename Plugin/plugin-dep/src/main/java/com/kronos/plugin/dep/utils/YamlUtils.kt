package com.kronos.plugin.dep.utils

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

    fun inflate(projectDir: File) {
        val yaml = Yaml()
        val f = File(projectDir, "repo-include.yaml")
        val dir = File(projectDir.parentFile, "subModules")
        //   val repoInfo = RepoInfo(dir)
        if (f.exists()) {
            val repoInfoYaml = yaml.load<LinkedHashMap<String, Any>>(FileInputStream(f))
            if (repoInfoYaml.containsKey("modules")) {
                val modulesList = repoInfoYaml["modules"]
                /*   if (modulesList is MutableList<*>) {
                       modulesList.forEach {
                           if (it is LinkedHashMap<*, *>) {
                               val module = parser(it as LinkedHashMap<Any, Any>, dir)
                               Log.info("moduleName:${module.name}")
                               repoInfo.moduleInfoMap[module.name] = module
                           }
                       }
                   }*/
            }
        }
    }
}