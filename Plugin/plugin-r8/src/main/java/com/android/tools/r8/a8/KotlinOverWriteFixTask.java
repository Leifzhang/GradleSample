package com.android.tools.r8.a8;

import com.android.tools.r8.graph.AppInfo;
import com.android.tools.r8.graph.AppInfoWithClassHierarchy;
import com.android.tools.r8.graph.DexAnnotationSet;
import com.android.tools.r8.graph.DexClass;
import com.android.tools.r8.graph.DexEncodedMethod;
import com.android.tools.r8.graph.DexItemFactory;
import com.android.tools.r8.graph.DexMethod;
import com.android.tools.r8.graph.DexProgramClass;
import com.android.tools.r8.graph.DexType;
import com.android.tools.r8.graph.MethodAccessFlags;
import com.android.tools.r8.graph.MethodResolutionResult;
import com.android.tools.r8.graph.ParameterAnnotationsList;
import com.android.tools.r8.ir.code.Invoke;
import com.android.tools.r8.ir.code.Position;
import com.android.tools.r8.ir.synthetic.ForwardMethodSourceCode;
import com.android.tools.r8.reflect.AppViewExtensionsKt;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class KotlinOverWriteFixTask {
    private AppInfoWithClassHierarchy appInfo;

    public KotlinOverWriteFixTask(AppInfo app) {
        this.appInfo = AppInfoWithClassHierarchy.createForDesugaring(app);
    }

    public void execute() {
        for (DexProgramClass aClass : appInfo.classes()) {
            checkOverWriteMethod(aClass);
        }
    }

    private void checkOverWriteMethod(DexProgramClass aClass) {
        if (aClass.isNotProgramClass()) {
            return;
        }
        if (aClass.accessFlags.isAbstract()) {
            return;
        }
        if (aClass.accessFlags.isInterface()) {
            return;
        }
        Set<DexEncodedMethod> methods = new HashSet<>();
        getAllAbstractMethod(aClass, methods);
        getAllInterfaceMethod(aClass, methods);
        for (DexEncodedMethod method : methods) {
            MethodResolutionResult resolutionResult = AppViewExtensionsKt.
                    reflectResolveMethodOnClassStep2(appInfo, aClass, method.getProto(),
                            method.getReference().name, aClass);
            if (resolutionResult != null) {
                DexEncodedMethod singleTarget = resolutionResult.getSingleTarget();
                if (singleTarget.isAbstract()) {
//                    reportMissingOverWriteMethod(method.method, aClass.type, "method is Abstract");
                }
            } else {
                resolutionResult = AppViewExtensionsKt.reflectResolveMethodStep3(appInfo,aClass, method.getProto(), method.getName());
                if (resolutionResult != null && resolutionResult.isSingleResolution() && !resolutionResult.asSingleResolution().getResolvedMethod().isAbstract()) {
                    // 找到一个非空实现
                } else {
                    DexType holder = method.getReference().holder;
                    String de = holder.descriptor.toSmaliString();
                    de = de.substring(0, de.length() - 1) + "$DefaultImpls;";
                    DexType d = appInfo.app().dexItemFactory.createType(de);
                    DexClass dexClass = appInfo.app().definitionFor(d);
                    if (dexClass != null) {
                        DexType[] targetV = method.getReference().getParameters().values;
                        DexEncodedMethod forwartMethod = dexClass.lookupDirectMethod(new Predicate<DexEncodedMethod>() {
                            @Override
                            public boolean test(DexEncodedMethod dexEncodedMethod) {
                                if (dexEncodedMethod.isStatic()) {
                                    DexType[] values = dexEncodedMethod.getReference().proto.parameters.values;
                                    if (values.length == targetV.length + 1 && dexEncodedMethod.getReference().name.equals(method.getReference().name)) {
                                        if (holder.equals(values[0])) {
                                            for (int i = 0; i < targetV.length; i++) {
                                                if (!targetV[i].equals(values[i + 1])) {
                                                    return false;
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                }
                                return false;
                            }
                        });
                        if (forwartMethod != null) {
                            System.out.println("add over method " + method.getReference().toSourceString() + " target class :" + d);
                            aClass.addVirtualMethod(createForwardingMethod(
                                    method, aClass, forwartMethod.getReference(), appInfo.app().dexItemFactory));
                        }
                    }
                }
            }
        }

    }

    public static DexEncodedMethod createForwardingMethod(
            DexEncodedMethod target, DexClass clazz, DexMethod forwardMethod, DexItemFactory factory) {

        DexMethod method = target.getReference();
        // New method will have the same name, proto, and also all the flags of the
        // default method, including bridge flag.
        DexMethod newMethod = factory.createMethod(clazz.type, method.proto, method.name);
        MethodAccessFlags newFlags = target.accessFlags.copy();
        // Some debuggers (like IntelliJ) automatically skip synthetic methods on single step.
        newFlags.setSynthetic();
        newFlags.unsetAbstract();
        ForwardMethodSourceCode.Builder forwardSourceCodeBuilder =
                ForwardMethodSourceCode.builder(newMethod);
        forwardSourceCodeBuilder
                .setReceiver(clazz.type)
                .setTarget(forwardMethod)
                .setInvokeType(Invoke.Type.STATIC)
                .setIsInterface(false); // Holder is companion class, or retarget method, not an interface.

        return DexEncodedMethod.builder().setMethod(newMethod).setAccessFlags(newFlags).setCode(new OverriderSynthesizedCode(new OverrideForward(clazz.type, newMethod, Position.SourcePosition.builder().setLine(0)
                .setMethod(target.getReference()).build(), forwardMethod, factory))).build();

    }

    private void getAllAbstractMethod(DexClass aClass, Set<DexEncodedMethod> methods) {
        DexType value = aClass.superType;
        DexClass dexClass = appInfo.definitionFor(value);
        while (dexClass != null) {
            if (!dexClass.accessFlags.isAbstract()) {
                return;
            }

            for (DexEncodedMethod virtualMethod : dexClass.virtualMethods()) {
                if (virtualMethod.accessFlags.isAbstract()) {
                    methods.add(virtualMethod);
                }
            }
            value = dexClass.superType;
            dexClass = appInfo.definitionFor(value);
        }

    }

    private void getAllInterfaceMethod(DexClass aClass, Set<DexEncodedMethod> methods) {
        for (DexType value : aClass.interfaces.values) {

            DexClass interfaceClass = appInfo.definitionFor(value);
            if (interfaceClass != null) {
                for (DexEncodedMethod virtualMethod : interfaceClass.virtualMethods()) {
                    if (virtualMethod.isAbstract()) {
                        methods.add(virtualMethod);
                    }
                }
                getAllInterfaceMethod(interfaceClass, methods);
            }
        }
    }

}
