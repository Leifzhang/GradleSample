package com.kronos.plugin.version.strategy

import org.gradle.api.Action
import org.gradle.plugin.management.PluginResolutionStrategy
import java.io.File

/**
 * @Author LiABao
 * @Since 2022/2/9
 */
class GradlePluginsStrategy(root: File) : Action<PluginResolutionStrategy> {

    private val strategyList by lazy {
        root.parserStrategyList()
    }

    override fun execute(strategy: PluginResolutionStrategy) {
        if (strategyList.isNullOrEmpty()) {
            return
        }
        strategy.eachPlugin { plugin ->
            val key = plugin.target.id.id
            if (strategyList.containsKey(key)) {
                strategyList[key]?.apply {
                    pluginMavenUrl?.let {
                        plugin.useModule(it)
                    }
                }
            }
        }
    }

}