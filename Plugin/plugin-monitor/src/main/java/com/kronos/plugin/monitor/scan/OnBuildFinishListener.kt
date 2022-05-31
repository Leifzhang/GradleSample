package com.kronos.plugin.monitor.scan

import org.gradle.internal.operations.notify.BuildOperationNotificationListener

/**
 *
 *  @Author LiABao
 *  @Since 2022/5/31
 *
 */
interface OnBuildFinishListener {
    fun buildFinish()
}

interface BaseOperationNotificationListener : OnBuildFinishListener,
    BuildOperationNotificationListener