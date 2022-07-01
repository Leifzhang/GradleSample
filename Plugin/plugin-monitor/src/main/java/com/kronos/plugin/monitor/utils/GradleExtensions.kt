package com.kronos.plugin.monitor.utils

import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ExtraPropertiesExtension

/**
 *
 *  @Author LiABao
 *  @Since 2022/5/31
 *
 */

fun Gradle.extra(): ExtraPropertiesExtension? {
    return if (this is ExtensionAware) {
        return this.extensions.extraProperties
    } else {
        null
    }
}