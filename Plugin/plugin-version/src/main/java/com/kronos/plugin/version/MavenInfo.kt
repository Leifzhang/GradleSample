package com.kronos.plugin.version

import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 *
 *  @Author LiABao
 *  @Since 2022/1/20
 *
 */
class MavenInfo(
    val group: String,
    val name: String
)

fun File.getMavenInfo(): MavenInfo? {
    if (exists()) {
        try {
            val yaml = Yaml()
            val modules = yaml.load<HashMap<String, Any>>(this.inputStream())
            val info = modules["info"] as HashMap<String, String>
            if (info["group"].isNullOrEmpty() || info["name"].isNullOrEmpty()) {
                throw  RuntimeException("maven.yaml error yaml")
            }
            return MavenInfo(info["group"] ?: "", info["name"] ?: "")

        } catch (throwable: Throwable) {
            Logger.log("maven.yaml invalid yaml : " + this.absolutePath)
        }
    } else {
        Logger.log("maven.yaml not exists module path:${this.parent}")
    }
    return null
}