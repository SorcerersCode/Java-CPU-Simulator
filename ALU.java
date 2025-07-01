package Skeleton.src;

public class ALU {
    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);

    public void doInstruction() {
        // Decodes the 5-bit opcode from instruction bits 0-4.
        int opcode = 0;
        for (int i = 0; i < 5; i++) {
            opcode = opcode << 1;
            if (instruction.bits[i].getValue() == Bit.boolValues.TRUE) {
                opcode = opcode | 1;
            } else {
                opcode = opcode | 0;
            }
        }

        // ADD OPERATION
        if (opcode == 1) {
            Adder.add(op1, op2, result);

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // AND OPERATION
        } else if (opcode == 2) {
            for (int i = 0; i < 32; i++) {
                Bit.and(op1.bits[i], op2.bits[i], result.bits[i]);
            }

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // MULTIPLY OPERATION
        } else if (opcode == 3) {
            Multiplier.multiply(op1, op2, result);

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // LEFT SHIFT OPERATION: Shifts left by int value of op2
        } else if (opcode == 4) {
            int leftShiftValue = TestConverter.toInt(op2);

            // We need to use the lower 5 bits otherwise it won't work :(
            leftShiftValue = leftShiftValue & 0x1F;

            Shifter.LeftShift(op1, leftShiftValue, result);

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // SUBTRACTION OPERATION
        } else if (opcode == 5) {
            Adder.subtract(op1, op2, result);

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // OR OPERATION
        } else if (opcode == 6) {
            for (int i = 0; i < 32; i++) {
                Bit.or(op1.bits[i], op2.bits[i], result.bits[i]);
            }

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // RIGHT SHIFT OPERATION: Shifts right by int value of op2
        } else if (opcode == 7) {
            int rightShiftValue = TestConverter.toInt(op2);
            // Once again we need to use the lower 5 bits to work properly
            rightShiftValue = rightShiftValue & 0x1F;

            Shifter.RightShift(op1, rightShiftValue, result);

            less.assign(Bit.boolValues.FALSE);

            equal.assign(Bit.boolValues.FALSE);
            // COMPARE OPERATION: Need to set the flags depending on the result of the
            // comparison operations between the op1 and op2 values
        } else if (opcode == 11) {
            int intOp1 = TestConverter.toInt(op1);

            int intOp2 = TestConverter.toInt(op2);

            if (intOp1 < intOp2) {
                less.assign(Bit.boolValues.TRUE);
            } else {
                less.assign(Bit.boolValues.FALSE);
            }
            if (intOp1 == intOp2) {
                equal.assign(Bit.boolValues.TRUE);
            } else {
                equal.assign(Bit.boolValues.FALSE);
            }
            // Resets the result so we don't have any accidental data poisoning/leaks
            for (int i = 0; i < 32; i++) {
                result.bits[i].assign(Bit.boolValues.FALSE);
            }
        } else {
            throw new IllegalArgumentException("Error: Your opt code is bad: \n" + opcode);
        }
    }
}
