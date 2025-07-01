package Skeleton.src;

public class Multiplier {
    public static void multiply(Word32 a, Word32 b, Word32 result) {
        // Makes sure to zero it out before we perform calculations
        for (int i = 0; i < 32; i++) {
            result.bits[i].assign(Bit.boolValues.FALSE);
        }
        // Temporary words to hold intermediate results.
        Word32 temporaryWord = new Word32();
        Word32 shiftedValue = new Word32();

        // The least sig bit is in position 31
        for (int i = 0; i < 32; i++) {
            int bitIndex = 31 - i;
            if (b.bits[bitIndex].getValue() == Bit.boolValues.TRUE) {
                // shifts a << i
                Shifter.LeftShift(a, i, shiftedValue);
                // The result is the new shifted value
                Adder.add(result, shiftedValue, temporaryWord);
                temporaryWord.copy(result);
            }
        }
    }
}
