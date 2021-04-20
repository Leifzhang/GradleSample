package com.kronos.plugin.repo.model

/**
 * @Author LiABao
 * @Since 2021/4/20
 */
data class YamlRepoInfo(
    val src: Boolean,
    val modules: MutableList<ModuleInfo>
)

