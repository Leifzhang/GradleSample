package com.kronos.plugin.monitor

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class KronosMonitorPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        KronosMonitor().setup(target)
    }
}