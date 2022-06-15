package com.kronos.plugin.monitor

import com.kronos.plugin.monitor.repo.DataRep
import com.kronos.plugin.monitor.scan.MonitorBuildOperationNotificationListener
import com.kronos.plugin.monitor.utils.FileUtils
import com.kronos.plugin.monitor.utils.OwnerProvider
import com.kronos.plugin.monitor.utils.UuidFinder
import com.kronos.plugin.monitor.utils.extra
import org.gradle.api.initialization.Settings
import org.gradle.initialization.DefaultSettings
import org.gradle.internal.operations.notify.BuildOperationNotificationListenerRegistrar
import org.gradle.kotlin.dsl.extra
import java.text.SimpleDateFormat
import java.util.*


class KronosMonitor {

    fun setup(target: Settings) {
        FileUtils.setup(target.gradle)

        val uuidFinder = get(target)
        DataRep.getRep().setup(
            target.gradle, uuidFinder.owner,
            uuidFinder.date, uuidFinder.time, uuidFinder.genUuid()
        )
        if (!target.gradle.startParameter.isBuildScan && target is DefaultSettings) {
            val registrar =
                target.gradle.services.get(BuildOperationNotificationListenerRegistrar::class.java)
            registrar.register(
                MonitorBuildOperationNotificationListener(target.gradle)
            )
        }
        BuildResultMonitor().setup(target)
        target.gradle.startParameter.systemPropertiesArgs
    }
}

fun get(settings: Settings): UuidFinder {
    val gradle = settings.gradle
    val extra = gradle.extra()
    if (extra != null) {
        return if (extra.has("uuidfinder")) {
            extra["uuidfinder"] as UuidFinder
        } else {
            val owner = getOwnerFrom()
            val tmp = Date()
            val dateStr = SimpleDateFormat("yy_MM_dd").format(tmp)
            val timeStr = SimpleDateFormat("HH_mm_ss").format(tmp)
            val finder = UuidFinder(settings.gradle, owner, dateStr, timeStr)
            extra.set("uuidfinder", finder)
            return finder
        }
    }
    return if (settings.extra.has("uuidfinder")) {
        settings.extra["uuidfinder"] as UuidFinder
    } else {
        val owner = getOwnerFrom()
        val tmp = Date()
        val dateStr = SimpleDateFormat("yy_MM_dd").format(tmp)
        val timeStr = SimpleDateFormat("HH_mm_ss").format(tmp)
        val finder = UuidFinder(settings.gradle, owner, dateStr, timeStr)
        settings.extra.set("uuidfinder", finder)
        return finder
    }
}

fun getOwnerFrom(): String {
    return OwnerProvider().get()
}

