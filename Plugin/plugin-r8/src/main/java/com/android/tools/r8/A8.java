package com.android.tools.r8;

import com.android.tools.r8.dex.ApplicationReader;
import com.android.tools.r8.dex.IndexedItemCollection;
import com.android.tools.r8.graph.AppInfo;
import com.android.tools.r8.graph.AppInfoWithClassHierarchy;
import com.android.tools.r8.graph.AppServices;
import com.android.tools.r8.graph.AppView;
import com.android.tools.r8.graph.DexAnnotation;
import com.android.tools.r8.graph.DexCallSite;
import com.android.tools.r8.graph.DexClass;
import com.android.tools.r8.graph.DexEncodedField;
import com.android.tools.r8.graph.DexEncodedMethod;
import com.android.tools.r8.graph.DexField;
import com.android.tools.r8.graph.DexMethod;
import com.android.tools.r8.graph.DexMethodHandle;
import com.android.tools.r8.graph.DexProgramClass;
import com.android.tools.r8.graph.DexProto;
import com.android.tools.r8.graph.DexReference;
import com.android.tools.r8.graph.DexString;
import com.android.tools.r8.graph.DexType;
import com.android.tools.r8.graph.DirectMappedDexApplication;
import com.android.tools.r8.graph.FieldResolutionResult;
import com.android.tools.r8.graph.MethodResolutionResult;
import com.android.tools.r8.graph.ProgramMethod;
import com.android.tools.r8.graph.UseRegistry;
import com.android.tools.r8.reflect.AppViewExtensionsKt;
import com.android.tools.r8.shaking.MainDexInfo;
import com.android.tools.r8.shaking.ProguardClassFilter;
import com.android.tools.r8.utils.AndroidApp;
import com.android.tools.r8.utils.InternalOptions;
import com.android.tools.r8.utils.StringDiagnostic;
import com.android.tools.r8.utils.ThreadUtils;
import com.android.tools.r8.utils.Timing;
import com.android.tools.r8.utils.collections.ProgramMethodSet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class A8 {

    private final Timing timing = new Timing("A8");
    private final InternalOptions options;
    private final ProguardClassFilter dontCheckPatterns;
    private AppInfoWithClassHierarchy appInfo;
    private AppView<AppInfoWithClassHierarchy> appView;
    private boolean forceReflectionError = false;
    private final ProgramMethodSet pendingReflectiveUses = ProgramMethodSet.createLinked();

    private A8(InternalOptions options, boolean forceReflectionError) {
        this.options = options;
        dontCheckPatterns = options.getProguardConfiguration().getDontNotePatterns();
        this.forceReflectionError = forceReflectionError;
    }

    public static void run(A8Command command) throws Throwable {
        AndroidApp app = command.getInputApp();
        InternalOptions options = command.getInternalOptions();
        ExecutorService executor = ThreadUtils.getExecutorService(options);
        new A8(options, command.forceReflectionError).run(app, executor, false);
    }

    public static void run(A8Command command, boolean forceOverWrite) throws Throwable {
        AndroidApp app = command.getInputApp();
        InternalOptions options = command.getInternalOptions();
        ExecutorService executor = ThreadUtils.getExecutorService(options);
        new A8(options, command.forceReflectionError).run(app, executor, forceOverWrite);
    }

    public static void run(A8Command command, int threads) throws Throwable {
        AndroidApp app = command.getInputApp();
        InternalOptions options = command.getInternalOptions();
        ExecutorService executor = ThreadUtils.getExecutorService(threads);
        new A8(options, command.forceReflectionError).run(app, executor, false);
    }

    private void run(AndroidApp inputApp, ExecutorService executorService, boolean forceOverWrite) throws IOException, ExecutionException {
        ApplicationReader applicationReader = new ApplicationReader(inputApp, options, timing);
        DirectMappedDexApplication application = applicationReader.read(executorService).toDirect();
        MainDexInfo mainDexClasses = applicationReader.readMainDexClasses(application);

        // Now that the dex-application is fully loaded, close any internal archive providers.
        inputApp.closeInternalArchiveProviders();

        appView = AppView.createForR8(application, mainDexClasses);
        appView.setAppServices(AppServices.builder(appView).build());
        appInfo = appView.appInfo();
        for (DexProgramClass aClass : appView.appInfo().classes()) {
            checkType(aClass.superType, aClass.type, appView.appInfo());
            for (DexType dexType : aClass.interfaces.values) {
                checkInterface(dexType, aClass.type, appView.appInfo());
            }
            for (DexEncodedField field : aClass.fields()) {
//                checkField(field.field, aClass.type, appView.appInfo());
                checkType(field.getReference().type, aClass.type, appView.appInfo());
            }
            aClass.forEachProgramMethod(new Consumer<ProgramMethod>() {
                @Override
                public void accept(ProgramMethod method) {
                    for (DexType parameterType : method.getReference().proto.parameters.values) {
                        checkType(parameterType, method.getReference(), appView.appInfo());
                    }
                    checkType(method.getReference().proto.returnType, method.getReference(), appView.appInfo());
                    method.registerCodeReferences(new AUseRegistry(appView, appView.appInfo(), method));
                }
            });
            forAnnotation(aClass, appView.appInfo());
            // 检查过于粗暴
            checkOverWriteMethod(aClass, forceOverWrite);
        }
        //  当前版本存在dex 解析问题。 所以故关闭该检查
//        pendingReflectiveUses.forEach(this::handleReflectiveBehavior);
    }

    private void checkOverWriteMethod(DexProgramClass aClass, boolean forceOverWrite) {
        if (donotCheckOn(aClass.type)) {
            return;
        }
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
                    reportMissingOverWriteMethod(method.getReference(), aClass.type, "method is Abstract");
                }
            } else {
                resolutionResult = AppViewExtensionsKt.reflectResolveMethodStep3(appInfo,aClass, method.getProto(), method.getName());
                if (resolutionResult != null && resolutionResult.isSingleResolution() && !resolutionResult.asSingleResolution().getResolvedMethod().isAbstract()) {

                    // 找到一个非空实现
                } else {
                    if (!forceOverWrite) {
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
                                continue;
                            }
                        }
                    }
                    reportMissingOverWriteMethod(method.getReference(), aClass.type, "method not overwrite");
                }
            }
        }

    }

    private void getAllAbstractMethod(DexClass aClass, Set<DexEncodedMethod> methods) {
        DexType value = aClass.superType;
        DexClass dexClass = appInfo.definitionFor(value);
        while (dexClass != null) {
            if (!dexClass.accessFlags.isAbstract()) {
                return;
            }
            if (donotCheckOn(dexClass.type)) {
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
            if (donotCheckOn(value)) {
                return;
            }
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

   /* private void handleReflectiveBehavior(ProgramMethod method) {
        try {
            DexType originHolder = method.getReference().holder;
            IRCode code = method.buildIR(appView);
            Iterator<Instruction> iterator = code.instructionIterator();
            while (iterator.hasNext()) {
                Instruction instruction = iterator.next();
                handleReflectiveBehavior(method, instruction);
            }
        } catch (Throwable throwable) {
            options.reporter.warning(new StringDiagnostic("handle method  " + method.toSourceString() + " failure :ignore : " + throwable.getMessage()));
        }
    }

    private void handleReflectiveBehavior(ProgramMethod method, Instruction instruction) {
        if (!instruction.isInvokeMethod()) {
            return;
        }
        InvokeMethod invoke = instruction.asInvokeMethod();
        DexMethod invokedMethod = invoke.getInvokedMethod();

        if (!isReflectionMethod(appInfo.dexItemFactory(), invokedMethod)) {
            return;
        }
        DexReference identifierItem = identifyIdentifier(invoke, appView);
        if (identifierItem == null) {
            return;
        }
        if (identifierItem.isDexType()) {
            DexClass clazz = appInfo.definitionFor(identifierItem.asDexType());
            if (clazz == null) {
                reportMissingClass(identifierItem.asDexType(), method.getReference(), true);
            }
        } else if (identifierItem.isDexField()) {
            if (appInfo.resolveField(identifierItem.asDexField()).isFailedOrUnknownResolution()) {
                reportMissingField(identifierItem.asDexField(), method.getReference(), true);
            }
        } else {
            assert identifierItem.isDexMethod();
            DexMethod dexMethod = identifierItem.asDexMethod();
            DexClass clazz = appInfo.definitionFor(dexMethod.holder);
            if (clazz == null) {
                reportMissingClass(identifierItem.asDexType(), method.getReference(), true);
            } else {
                if (appInfo.resolveMethodOn(clazz, dexMethod).isFailedResolution()) {
                    reportMissingMethod(identifierItem.asDexMethod(), method.getReference(), true);
                }
            }

        }
    }*/

    private void forAnnotation(DexProgramClass aClass, AppInfoWithClassHierarchy appInfo) {
        for (DexAnnotation annotation : aClass.annotations().annotations) {
            checkType(annotation.annotation.type, aClass.type, appInfo);
            annotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, aClass.type));
        }
        for (DexEncodedMethod method : aClass.directMethods()) {
            for (DexAnnotation annotation : method.annotations().annotations) {
                checkType(annotation.annotation.type, method.getReference(), appInfo);
                annotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, method.getReference()));
            }
            method.parameterAnnotationsList.forEachAnnotation(new Consumer<DexAnnotation>() {
                @Override
                public void accept(DexAnnotation dexAnnotation) {
                    checkType(dexAnnotation.annotation.type, method.getReference(), appInfo);
                    dexAnnotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, method.getReference()));
                }
            });
        }
        for (DexEncodedMethod method : aClass.virtualMethods()) {
            for (DexAnnotation annotation : method.annotations().annotations) {
                checkType(annotation.annotation.type, method.getReference(), appInfo);
                annotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, method.getReference()));
            }
            method.parameterAnnotationsList.forEachAnnotation(new Consumer<DexAnnotation>() {
                @Override
                public void accept(DexAnnotation dexAnnotation) {
                    checkType(dexAnnotation.annotation.type, method.getReference(), appInfo);
                    dexAnnotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, method.getReference()));
                }
            });
        }
        for (DexEncodedField field : aClass.instanceFields()) {
            for (DexAnnotation annotation : field.annotations().annotations) {
                checkType(annotation.annotation.type, field.getReference(), appInfo);
                annotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, field.getReference()));
            }
        }
        for (DexEncodedField field : aClass.staticFields()) {
            for (DexAnnotation annotation : field.annotations().annotations) {
                checkType(annotation.annotation.type, field.getReference(), appInfo);
                annotation.collectIndexedItems(new AAnnotationReferenceMarker(appInfo, field.getReference()));
            }
        }
    }

    private void checkInterface(DexType dexType, DexReference from, AppInfoWithClassHierarchy appInfo) {
//        Inliner.ConstraintWithTarget.deriveConstraint(context, holderType, holder.accessFlags, appInfo);
        checkType(dexType, from, appInfo);
    }


    class AAnnotationReferenceMarker implements IndexedItemCollection {
        private AppInfoWithClassHierarchy appInfo;
        private DexReference ref;

        public AAnnotationReferenceMarker(AppInfoWithClassHierarchy appInfo, DexReference ref) {
            this.appInfo = appInfo;
            this.ref = ref;
        }

        @Override
        public boolean addClass(DexProgramClass dexProgramClass) {
            return false;
        }

        @Override
        public boolean addField(DexField field) {
            checkType(field.holder, ref, appInfo);
            checkField(field, ref, appInfo);
            return false;
        }

        @Override
        public boolean addMethod(DexMethod method) {
//            checkMethod(method, appInfo);
            return false;
        }

        @Override
        public boolean addString(DexString string) {
            return false;
        }

        @Override
        public boolean addProto(DexProto proto) {
            return false;
        }

        @Override
        public boolean addType(DexType type) {
            return false;
        }

        @Override
        public boolean addCallSite(DexCallSite callSite) {
            return false;
        }

        @Override
        public boolean addMethodHandle(DexMethodHandle methodHandle) {
            return false;
        }
    }

    private void checkField(DexField field, DexReference form, AppInfoWithClassHierarchy appInfoWithSubtyping) {
        if (checkType(field.type, form, appInfoWithSubtyping)) {
            FieldResolutionResult encodedField = appInfoWithSubtyping.resolveFieldOn(field.holder, field);
            if (encodedField.isFailedOrUnknownResolution()) {
                reportMissingField(field, form, false);
            }
        }
    }

    class AUseRegistry extends UseRegistry {

        private final AppInfoWithClassHierarchy appInfoWithSubtyping;
        private ProgramMethod method;

        public AUseRegistry(AppView<?> appView, AppInfoWithClassHierarchy appInfoWithSubtyping, ProgramMethod method) {
            super(appView, method);
            this.appInfoWithSubtyping = appInfoWithSubtyping;
            this.method = method;
        }

        @Override
        public void registerInitClass(DexType type) {

        }

        @Override
        public void registerInvokeVirtual(DexMethod method) {
            checkVMethod(method, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerInvokeDirect(DexMethod method) {
            checkVMethod(method, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerInvokeStatic(DexMethod method) {
            checkVMethod(method, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerInvokeInterface(DexMethod method) {
            checkVMethod(method, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerInvokeSuper(DexMethod method) {
            checkVMethod(method, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerInstanceFieldWrite(DexField field) {
            checkField(field, this.method.getReference(), appInfoWithSubtyping);
        }


        @Override
        public void registerInstanceFieldRead(DexField field) {
            checkField(field, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerNewInstance(DexType type) {
            checkType(type, method.getReference(), appInfoWithSubtyping);
        }


        @Override
        public void registerStaticFieldRead(DexField field) {
            checkField(field, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerStaticFieldWrite(DexField field) {
            checkField(field, this.method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerTypeReference(DexType type) {
            checkType(type, method.getReference(), appInfoWithSubtyping);
        }

        @Override
        public void registerInstanceOf(DexType type) {

        }


        private void checkVMethod(DexMethod method, DexReference fromMethod, AppInfoWithClassHierarchy appInfo) {
            if (method == appView.dexItemFactory().classMethods.newInstance || method == appView.dexItemFactory().constructorMethods.newInstance) {
                pendingReflectiveUses.add(this.method);
            } else if (appView.dexItemFactory().classMethods.isReflectiveMemberLookup(method)) {
                pendingReflectiveUses.add(this.method);
            }
            if (checkType(method.holder, fromMethod, appInfo)) {
                if (method.holder.isArrayType()) {
                    return;
                }
                DexClass dexClass = appInfo.definitionFor(method.holder);
                if (dexClass != null) {
                    DexEncodedMethod topTarget = appInfo.resolveMethodOn(dexClass, method).getSingleTarget();
                    if (topTarget == null) {
                        reportMissingMethod(method, fromMethod, false);
                    }
                } else {
                    reportMissingClass(method.holder, fromMethod, false);
                }
            }

        }

    }


    public DexType getSoruceDexType(DexType type, AppInfo appInfo) {
        DexType type1 = type;
        while (type1.isArrayType()) {
            type1 = type1.toArrayElementType(appInfo.dexItemFactory());
        }
        return type1;
    }

    private boolean checkType(DexType type, DexReference from, AppInfoWithClassHierarchy appInfo) {
        DexType type1 = getSoruceDexType(type, appInfo);
        if (type1.isPrimitiveType() || type1.isPrimitiveArrayType() || type1.isVoidType()) {
            return true;
        }
        DexClass dexClass = appInfo.definitionFor(type1);
        if (dexClass == null) {
            reportMissingClass(type1, from, false);
            return false;
        }
        return true;
    }

    boolean donotCheckOn(DexReference dexReference) {
        DexType dexType = null;
        if (dexReference.isDexType()) {
            dexType = getSoruceDexType(dexReference.asDexType(), appInfo);
        } else if (dexReference.isDexMethod()) {
            dexType = getSoruceDexType(dexReference.asDexMethod().holder, appInfo);
        } else if (dexReference.isDexField()) {
            dexType = getSoruceDexType(dexReference.asDexField().holder, appInfo);
        }
        if (dexType.isPrimitiveType()) {
            return true;
        }
        return dontCheckPatterns.matches(dexType);
    }


    HashSet<String> happend = new HashSet<>();

    private boolean printenable(Object clazz, Object dexType) {
        if (happend.contains(clazz.toString() + dexType.toString())) {
            return false;
        }
        happend.add(clazz.toString() + dexType.toString());
        return true;
    }

    private void reportMissingClass(DexType clazz, DexReference dexType, boolean reflection) {

        if (!donotCheckOn(clazz) && !donotCheckOn(dexType) && printenable(clazz, dexType)) {
            if (reflection) {
                if (forceReflectionError) {
                    options.reporter.error("not found Class " + clazz.toSourceString() + " reflection from " + dexType.toSourceString());
                } else {
                    options.reporter.warning(new StringDiagnostic("not found Class " + clazz.toSourceString() + " reflection from " + dexType.toSourceString()));
                }
            } else {
                options.reporter.error("not found Class " + clazz.toSourceString() + " from " + dexType.toSourceString());
            }
        }
    }

    public void findAllMethod(DexClass dexClass, DexString methodname, Set<DexClass> vist, Set<DexMethod> vMethods, Set<DexMethod> dMethods) {
        if (dexClass == null) {
            return;
        }
        if (vist.contains(dexClass)) {
            return;
        }
        vist.add(dexClass);
        for (DexEncodedMethod virtualMethod : dexClass.virtualMethods()) {
            if (virtualMethod.getReference().name.equals(methodname)) {
                boolean found = false;
                for (DexMethod vMethod : vMethods) {
                    if (vMethod.proto.equals(virtualMethod.getReference().proto)) {
                        found = true;
                    }
                }
                if (!found) {
                    vMethods.add(virtualMethod.getReference());
                }
            }
        }
        for (DexEncodedMethod mt : dexClass.directMethods()) {
            if (mt.getReference().name.equals(methodname)) {
                dMethods.add(mt.getReference());
            }
        }
        if (!methodname.toString().equals("<init>")) {
            DexType superType = dexClass.superType;
            if (superType != null) {
                DexClass superClass = appInfo.definitionFor(superType);
                findAllMethod(superClass, methodname, vist, vMethods, dMethods);
            }
            for (DexType value : dexClass.interfaces.values) {
                DexClass inteDex = appInfo.definitionFor(value);
                findAllMethod(inteDex, methodname, vist, vMethods, dMethods);
            }
        }
    }

    private void reportMissingOverWriteMethod(DexMethod method, DexReference dexType, String res) {
        if (!donotCheckOn(method.holder) && !donotCheckOn(dexType) && printenable(method, dexType)) {
            options.reporter.error(res + " " + method.toSourceString() + " from " + dexType.toSourceString());
        }
    }

    private void reportMissingMethod(DexMethod method, DexReference dexType, boolean reflection) {
        if (!donotCheckOn(method.holder) && !donotCheckOn(dexType) && printenable(method, dexType)) {
            DexClass dexClass = appInfo.definitionFor(method.holder);

            if (dexClass == null) {
                reportMissingClass(method.holder, dexType, reflection);
                return;
            }
            Set<DexClass> vit = new HashSet<>();
            Set<DexMethod> vMethods = new HashSet<>();
            Set<DexMethod> dMethods = new HashSet<>();
            findAllMethod(dexClass, method.name, vit, vMethods, dMethods);

            StringBuffer stringBuffer = new StringBuffer();
            if (vMethods.size() != 0 || dMethods.size() != 0) {
                stringBuffer.append("\n");
                if (vMethods.size() > 0) {
                    stringBuffer.append("  found similar virtual Method:").append("\n");
                    for (DexMethod mt : vMethods) {
                        stringBuffer.append("    ").append(mt.toSourceString()).append("\n");
                    }
                }
                if (dMethods.size() > 0) {
                    stringBuffer.append("  found similar directMethod:").append("\n");
                    for (DexMethod mt : dMethods) {
                        stringBuffer.append("    ").append(mt.toSourceString()).append("\n");
                    }
                }
            }
            if (reflection) {
                if (forceReflectionError) {
                    options.reporter.error("not found Method " + method.toSourceString() + " reflection from " + dexType.toSourceString() + stringBuffer.toString()

                    );
                } else {
                    options.reporter.warning(new StringDiagnostic("not found Method " + method.toSourceString() + " reflection from " + dexType.toSourceString() + stringBuffer.toString()));
                }
            } else {
                options.reporter.error("not found Method " + method.toSourceString() + " from " + dexType.toSourceString() + stringBuffer.toString());
            }
        }
    }

    private void reportMissingField(DexField field, DexReference dexType, boolean reflection) {
        DexClass dexClass = appInfo.definitionFor(field.holder);
        if (dexClass == null) {
            reportMissingClass(field.holder, dexType, reflection);
            return;
        }
        if (!donotCheckOn(field.holder) && !donotCheckOn(dexType) && printenable(field, dexType)) {
            if (reflection) {
                if (forceReflectionError) {
                    options.reporter.error("not found Field " + field.toSourceString() + " reflection from " + dexType.toSourceString());
                } else {
                    options.reporter.warning(new StringDiagnostic("not found Field " + field.toSourceString() + " reflection from " + dexType.toSourceString()));
                }
            } else {
                options.reporter.error("not found Field " + field.toSourceString() + " from " + dexType.toSourceString());
            }
        }
    }
}
