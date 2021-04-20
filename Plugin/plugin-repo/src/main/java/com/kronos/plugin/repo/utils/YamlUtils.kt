package com.kronos.plugin.repo.utils

import com.kronos.plugin.repo.model.YamlRepoInfo
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 *
 *  @Author LiABao
 *  @Since 2021/4/20
 *
 */
object YamlUtils {

    fun inflate(projectDir: File) {
        val yaml = Yaml()
        val f = File(projectDir, "repo.yaml")
        val map = yaml.load<YamlRepoInfo>(FileInputStream(f))
    }
}