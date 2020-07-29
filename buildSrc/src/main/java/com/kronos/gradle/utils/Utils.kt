package com.kronos.gradle.utils

import com.kronos.gradle.PluginLogger
import java.net.HttpURLConnection
import java.net.URI

object Utils {
    /**
     * 第三方库依赖定义  远端地址
     */
    fun getDepRemoteUrl(curBranch: String): String {
        return "https://github.com/Leifzhang/DepPlugin/raw/$curBranch/dep.gradle"
    }

    fun isDepUrlExist(url: String): Boolean {
        var success = false
        try {
            val connection = URI(url).toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()
            success = connection.responseCode == 200
        } catch (e: Exception) {
            println("异常 输出 ${e.message}")
        } finally {
            return success;
        }

    }

    fun getRemoteDepConfigUrl(branch: String, urlTransByBranch: (arg1: String) -> String): String {
        var url = urlTransByBranch(branch)
        if (!isDepUrlExist(url)) {
            //尝试截取release preRelease 并判断远程url是否存在
            var fuzzyBranch: String? = null
            if (branch.contains("pre_release")) {
                fuzzyBranch = branch.substring(branch.indexOf("pre_release"), branch.length)
            } else if (branch.contains("release")) {
                fuzzyBranch = branch.substring(branch.indexOf("release"), branch.length)
            }
            //模糊匹配失败，兜底master分支
            if (fuzzyBranch.isNullOrEmpty()) {
                fuzzyBranch = "master";
            }
            println("自动匹配分支 => $fuzzyBranch")

            url = if (isDepUrlExist(urlTransByBranch(fuzzyBranch))) {
                urlTransByBranch(fuzzyBranch);
            } else {
                //如果fuzzyBranch不存在，则使用master
                if (isDepUrlExist(urlTransByBranch("master"))) {
                    urlTransByBranch("master")
                } else {
                    throw RuntimeException("无法找到能够匹配当前分支$branch 的远程配置,请检查$url 是否存在")
                }
            }
            PluginLogger.warn(
                "【DepConfig】【警告】由于分支$branch 对应的远程配置文件并不存在，" +
                        "根据自动策略将自动使用 $url 的配置文件"
            )
        }
        return url

    }


}