package com.android.tools.r8.a8;

import com.android.tools.r8.graph.DexClassAndMethod;
import com.android.tools.r8.graph.ProgramMethod;
import com.android.tools.r8.graph.UseRegistry;
import com.android.tools.r8.ir.code.Position;
import com.android.tools.r8.ir.conversion.SourceCode;
import com.android.tools.r8.ir.synthetic.SynthesizedCode;

import java.util.function.Consumer;


public class OverriderSynthesizedCode extends SynthesizedCode {

    OverrideForward overrideForward;

    public OverriderSynthesizedCode(OverrideForward overrideForward) {
        super(null);
        this.overrideForward = overrideForward;
    }

    @Override
    public SourceCodeProvider getSourceCodeProvider() {
        return (programMethod, position) -> overrideForward;
    }

    @Override
    public Consumer<UseRegistry> getRegistryCallback(DexClassAndMethod dexClassAndMethod) {
       return new Consumer<UseRegistry>() {
           @Override
           public void accept(UseRegistry useRegistry) {

           }
       };
    }

}
