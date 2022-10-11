package com.android.tools.r8;

import com.android.tools.r8.errors.DexFileOverflowDiagnostic;
import com.android.tools.r8.graph.DexItemFactory;
import com.android.tools.r8.origin.Origin;
import com.android.tools.r8.shaking.ProguardConfiguration;
import com.android.tools.r8.shaking.ProguardConfigurationParser;
import com.android.tools.r8.shaking.ProguardConfigurationRule;
import com.android.tools.r8.shaking.ProguardConfigurationSource;
import com.android.tools.r8.shaking.ProguardConfigurationSourceFile;
import com.android.tools.r8.utils.AndroidApp;
import com.android.tools.r8.utils.InternalOptions;
import com.android.tools.r8.utils.Reporter;
import com.android.tools.r8.utils.StringDiagnostic;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class A8Command extends BaseCommand {


    boolean forceReflectionError;

    static class DefaultD8DiagnosticsHandler implements DiagnosticsHandler {

        @Override
        public void error(Diagnostic error) {
            if (error instanceof DexFileOverflowDiagnostic) {
                DexFileOverflowDiagnostic overflowDiagnostic = (DexFileOverflowDiagnostic) error;
                if (!overflowDiagnostic.hasMainDexSpecification()) {
                    DiagnosticsHandler.printDiagnosticToStream(new StringDiagnostic(overflowDiagnostic.
                                    getDiagnosticMessage() + ". Try supplying a main-dex list"),
                            "Error", System.err);
                    return;
                }
            }
            DiagnosticsHandler.printDiagnosticToStream(error, "Error", System.err);
        }

    }


    /**
     * Builder for constructing a A8Command.
     *
     * <p>A builder is obtained by calling {@link A8Command#builder}.
     */
    @Keep
    public static class Builder extends BaseCommand.Builder<A8Command, Builder> {

        private boolean forceReflectionError = false;
        private final List<ProguardConfigurationSource> mainDexRules = new ArrayList<>();

        private Builder() {
            this(new DefaultD8DiagnosticsHandler());
        }

        private Builder(DiagnosticsHandler diagnosticsHandler) {
            super(diagnosticsHandler);
        }

        /**
         * Add dex program-data.
         */
        public Builder addDexProgramData(byte[] data, Origin origin) {
            guard(() -> getAppBuilder().addDexProgramData(data, origin));
            return self();
        }

        /**
         * Add classpath file resources.
         */
        public Builder addClasspathFiles(Path... files) {
            guard(() -> Arrays.stream(files).forEach(this::addClasspathFile));
            return self();
        }

        public Builder setForceReflectionError(boolean forceReflectionError) {
            this.forceReflectionError = forceReflectionError;
            return self();
        }

        /**
         * Add classpath file resources.
         */
        public Builder addClasspathFiles(Collection<Path> files) {
            guard(() -> files.forEach(this::addClasspathFile));
            return self();
        }

        private void addClasspathFile(Path file) {
            guard(() -> getAppBuilder().addClasspathFile(file));
        }

        /**
         * Add classfile resources provider for class-path resources.
         */
        public Builder addClasspathResourceProvider(ClassFileResourceProvider provider) {
            guard(() -> getAppBuilder().addClasspathResourceProvider(provider));
            return self();
        }


        @Override
        Builder self() {
            return this;
        }


        @Override
        void validate() {
            Reporter reporter = getReporter();
        }

        int minApiLevel;

        public void setMinApiLevel(int minApiLevel) {
            this.minApiLevel = minApiLevel;
        }

        @Override
        A8Command makeCommand() {
            return new A8Command(
                    getAppBuilder().build(),
                    getReporter(),
                    minApiLevel,
                    mainDexRules,
                    forceReflectionError);
        }

        public void addA8Rule(Path path) {
            mainDexRules.add(new ProguardConfigurationSourceFile(path));
        }
    }

    static final String USAGE_MESSAGE = "";


    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DiagnosticsHandler diagnosticsHandler) {
        return new Builder(diagnosticsHandler);
    }

    private List<ProguardConfigurationSource> mainDexRules = new ArrayList<>();
    private int minApiLevel;
    Reporter reporter;

    private A8Command(
            AndroidApp inputApp,
            Reporter reporter,
            int minApiLevel,
            List<ProguardConfigurationSource> mainDexRules,
            boolean forceReflectionError
    ) {
        super(inputApp);
        this.forceReflectionError = forceReflectionError;
        this.reporter = reporter;
        this.minApiLevel = minApiLevel;
        this.mainDexRules = mainDexRules;
    }

    public Reporter getReporter() {
        return reporter;
    }

    @Override
    InternalOptions getInternalOptions() {
        DexItemFactory dexItemFactory = new DexItemFactory();
        ProguardConfigurationParser parser =
                new ProguardConfigurationParser(dexItemFactory, getReporter());
        parser.parse(mainDexRules);
        ProguardConfiguration.Builder configurationBuilder = parser.getConfigurationBuilder();

        ProguardConfiguration proguardConfiguration = configurationBuilder.build();
        List<ProguardConfigurationRule> mainDexKeepRules = parser.getConfig().getRules();
        InternalOptions internal = new InternalOptions(CompilationMode.DEBUG,proguardConfiguration, getReporter());
        assert !internal.debug;
        internal.debug = true;
        internal.programConsumer = ClassFileConsumer.emptyConsumer();
        internal.mainDexListConsumer = null;
        internal.minimalMainDex = true;
        //internal.minApiLevel = minApiLevel;
        internal.intermediate = false;
        // Assert and fixup defaults.
        assert !internal.passthroughDexCode;
        // Disable some of R8 optimizations.
     /*   assert internal.enableInlining;
        internal.enableInlining = false;*/
        assert internal.enableClassInlining;
        internal.enableClassInlining = false;
/*        assert internal.enableHorizontalClassMerging;
        internal.enableHorizontalClassMerging = false;*/
        assert internal.enableVerticalClassMerging;
        internal.enableVerticalClassMerging = false;
        assert internal.enableClassStaticizer;
        internal.enableClassStaticizer = false;
        assert internal.outline.enabled;
        internal.outline.enabled = false;
/*        assert internal.enableValuePropagation;
        internal.enableValuePropagation = false;*/
        //   internal.enableLambdaMerging = false;
        return internal;
    }
}
