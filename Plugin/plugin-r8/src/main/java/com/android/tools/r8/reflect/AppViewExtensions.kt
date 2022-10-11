package com.android.tools.r8.reflect;

import com.android.tools.r8.graph.*


/**
 * @Author LiABao
 * @Since 2022/10/10
 */


fun AppInfoWithClassHierarchy.reflectResolveMethodOnClassStep2(
    clazz: DexClass,
    methodProto: DexProto,
    methodName: DexString,
    initialResolutionHolder: DexClass
): MethodResolutionResult {
    val result = this.invokeMethod(
        "resolveMethodOnClassStep2",
        arrayOf(
            DexClass::class.java,
            DexProto::class.java,
            DexString::class.java,
            DexClass::class.java
        ), arrayOf(clazz, methodProto, methodName, initialResolutionHolder)
    ) as MethodResolutionResult
    return result
}

fun AppInfoWithClassHierarchy.reflectResolveMethodStep3(
    clazz: DexClass, methodProto: DexProto, methodName: DexString
): MethodResolutionResult {
    val result = this.invokeMethod(
        "resolveMethodOnClassStep2",
        arrayOf(
            DexClass::class.java,
            DexProto::class.java,
            DexString::class.java
        ), arrayOf(clazz, methodProto, methodName)
    ) as MethodResolutionResult
    return result
}