package com.kronos.plugin.monitor

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.registerIfAbsent
import javax.inject.Inject

class KronosMonitorPlugin @Inject constructor(
    private val buildEventsListenerRegistry: BuildEventsListenerRegistry
) : Plugin<Settings> {

    override fun apply(target: Settings) {
        KronosMonitor().setup(target)
        target.gradle.startParameter.projectProperties = emptyMap()
        /* target.gradle.run {
             val service =
                 sharedServices.registerIfAbsent("traceService", BuildListenerService::class) {
                     parameters {
                         // configure service here
                     }
                 }
             buildEventsListenerRegistry.onTaskCompletion(service)
             projectsEvaluated {
                 service.get().onProjectsEvaluated()
             }
         }*/
    }
}