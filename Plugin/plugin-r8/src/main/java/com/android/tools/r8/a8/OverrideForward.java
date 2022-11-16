package com.android.tools.r8.a8;

import com.android.tools.r8.graph.DexItemFactory;
import com.android.tools.r8.graph.DexMethod;
import com.android.tools.r8.graph.DexType;
import com.android.tools.r8.ir.code.Invoke;
import com.android.tools.r8.ir.code.Position;
import com.android.tools.r8.ir.code.ValueType;
import com.android.tools.r8.ir.conversion.IRBuilder;
import com.android.tools.r8.ir.synthetic.SyntheticSourceCode;

import java.util.ArrayList;
import java.util.List;

public class OverrideForward extends SyntheticSourceCode {

    private DexMethod forwardMethod;
    private DexItemFactory dexItemFactory;


    public OverrideForward(DexType receiver, DexMethod method, Position callerPosition, DexMethod forwardMethod,
                           DexItemFactory dexItemFactory) {
        super(receiver, method, callerPosition);
        this.forwardMethod = forwardMethod;
        this.dexItemFactory = dexItemFactory;
    }

    @Override
    protected void prepareInstructions() {
        // Method call to the main functional interface method.

        DexType[] currentParams = proto.parameters.values;
        DexType[] enforcedParams = forwardMethod.proto.parameters.values;

        // Prepare call arguments.
        List<ValueType> argValueTypes = new ArrayList<>();
        List<Integer> argRegisters = new ArrayList<>();

        // Always add a receiver representing 'this' of the lambda class.
        argValueTypes.add(ValueType.fromDexType(enforcedParams[0]));
        argRegisters.add(getReceiverRegister());

        // Prepare arguments
        for (int i = 0; i < currentParams.length; i++) {
            DexType expectedParamType = currentParams[i];
            argValueTypes.add(ValueType.fromDexType(expectedParamType));
            argRegisters.add(getParamRegister(i));
        }
        add(
                builder ->
                        builder.addInvoke(
                                Invoke.Type.STATIC,
                                this.forwardMethod,
                                this.forwardMethod.proto,
                                argValueTypes,
                                argRegisters,
                                false /* isInterface */));

        // Does the method have return value?
        if (proto.returnType == dexItemFactory.voidType) {
            add(IRBuilder::addReturn);
        } else {
            ValueType valueType = ValueType.fromDexType(proto.returnType);
            int tempValue = nextRegister(valueType);
            add(builder -> builder.addMoveResult(tempValue));
            // We lack precise sub-type information, but there should not be a need to cast to object.
            if (proto.returnType != forwardMethod.proto.returnType
                    && proto.returnType != dexItemFactory.objectType) {
                add(builder -> builder.addCheckCast(tempValue, proto.returnType));
            }
            add(builder -> builder.addReturn(tempValue));
        }
    }
}
