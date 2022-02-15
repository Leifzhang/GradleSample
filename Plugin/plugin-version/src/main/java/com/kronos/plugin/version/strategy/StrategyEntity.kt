package com.kronos.plugin.version.strategy

import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/2/10
 *
 */
data class StrategyEntity(
    val pluginId: String?,
    val pluginMavenUrl: String?
)


fun File?.parserStrategyList(): HashMap<String, StrategyEntity> {
    val file = File(this, "gradlePlugins.yaml")
    if (!file.exists()) {
        return hashMapOf()
    }
    val yaml = Yaml()
    val map = yaml.load<HashMap<String, Any>>(file.inputStream())
    val plugins = map["plugins"] as MutableList<*>

    val strategyList = hashMapOf<String, StrategyEntity>()
    plugins.forEach {
        if (it is LinkedHashMap<*, *>) {
            val pluginsMap = it as LinkedHashMap<String, String>
            val strategy = StrategyEntity(
                pluginsMap["pluginId"],
                pluginsMap["pluginMavenUrl"]
            )
            strategyList[strategy.pluginId ?: ""] = strategy
        }
    }
    return strategyList
}