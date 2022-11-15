package com.kronos.plugin.version.extensions

import com.kronos.plugin.version.utils.FileUtils
import org.gradle.api.Action
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.DependencyResolutionManagement
import org.gradle.api.initialization.resolve.MutableVersionCatalogContainer
import java.io.File
import javax.inject.Inject

/**
 *
 *  @Author LiABao
 *  @Since 2022/11/14
 *
 */

interface CatalogsExtensions {

    fun versionCatalogs(action: Action<in MutableVersionCatalogContainer>)
}

abstract class CatalogsExtensionsImp @Inject constructor(settings: Settings) :
    CatalogsExtensions {

    var script: File? = null
    val rootProjectDir = FileUtils.getRootProjectDir(settings.gradle)
    private val container = settings.dependencyResolutionManagement.versionCatalogs
    private val dependency = settings.dependencyResolutionManagement

    override fun versionCatalogs(action: Action<in MutableVersionCatalogContainer>) {
        action.execute(container)
    }

    fun dependencyResolutionManagement(action: Action<in DependencyResolutionManagement>) {
        action.execute(dependency)
    }
}