package com.kronos.plugin.dep

import com.kronos.plugin.dep.utils.ResourcesUtils.property

/**
 *
 *  @Author LiABao
 *  @Since 2021/6/4
 *
 */
class DependenciesExt {

    val okHttpVersion = '3.12.2'
    val okioVersion = '1.17.2'
    val glideVersion = '3.7.0'
    val rxjavaVersion = '2.2.0'
    val rxandroidVersion = '2.1.0'
    val lottieVersion = '3.3.1' 
    val kotlinVersion = '1.3.61'
    val kotlinCoroutineCoreVersion = '1.3.3'
    val kotlinCoroutineAndroidVersion = '1.3.3'


  val  dep = [
            //工具集合类
            okhttp                   : "com.squareup.okhttp3:okhttp:${okHttpVersion}",
            okio                     : "com.squareup.okio:okio:${okioVersion}",
            glide                    : "com.github.bumptech.glide:glide:${glideVersion}",
            rxjava                   : "io.reactivex.rxjava2:rxjava:${rxjavaVersion}",
            rxandroid                : "io.reactivex.rxjava2:rxandroid:${rxandroidVersion}",
            lottie                   : "com.airbnb.android:lottie:${lottieVersion}",

            kotlinPlugin             : "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}",
            kotlin                   : "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}",
            kotlinCoroutineCore      : "org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutineCoreVersion}",
            kotlinCoroutineAndroid   : "org.jetbrains.kotlinx:kotlinx-coroutines-android:${kotlinCoroutineAndroidVersion}",
          
         val   lifecycleSupport=[
                rxlifecycle_autodispose   : "com.uber.autodispose:autodispose-rxlifecycle:1.1.0",
                rxlifecycle_android       : "com.trello.rxlifecycle2:rxlifecycle-android:2.2.2",
                rxlifecycle_android_lifecycle       : "com.trello.rxlifecycle2:rxlifecycle-android-lifecycle:2.2.2",
                rxlifecycle_android_lifecycle_kotlin: "com.trello.rxlifecycle2:rxlifecycle-android-lifecycle-kotlin:2.2.2",
                ],
            xsupportv4:    "androidx.legacy:legacy-support-v4:1.0.0",
            appcompat:    "androidx.appcompat:appcompat:1.0.0",
            androidxLifecycle:    "androidx.lifecycle:lifecycle-runtime:2.1.0",
            androidxLifecycleKtx:    "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0",
            androidxLifecycleLiveDataKtx    :"androidx.lifecycle:lifecycle-livedata-ktx:2.2.0",
            androidxLifecycleCommon         : "androidx.lifecycle:lifecycle-common:2.1.0",
            androidxLifecycleCommonJava8    : "androidx.lifecycle:lifecycle-common-java8:2.1.0",
            androidxLifecycleCompiler:    "androidx.lifecycle:lifecycle-compiler:2.1.0",
            androidxLifecycleExtensions:    "androidx.lifecycle:lifecycle-extensions:2.1.0",
            androidxLifecycleViewModel_ktx: "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0",
            xrecycleview:    "androidx.recyclerview:recyclerview:1.0.0",
            androidAnnotation:    "androidx.annotation:annotation:1.0.0",
            androidMaterial:    "com.google.android.material:material:1.0.0",
            xcardview:    "androidx.cardview:cardview:1.0.0",
            xgridlayout:    "androidx.gridlayout:gridlayout:1.0.0",
            xconstraintLayout:    "androidx.constraintlayout:constraintlayout:1.1.3",
            xmultdex:    "androidx.multidex:multidex:2.0.0"
            
    ]

}