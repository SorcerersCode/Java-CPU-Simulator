package Skeleton.src;

public class TestConverter {
    public static void fromInt(int value, Word32 result) {
        // Changes the int value to its 32-bit two's complement
        // Fill the array so i=0 is the most sig bit
        for (int i = 31; i >= 0; i--) {
            int bitValue = value & 1;
            value = value >>> 1;  // Unsigned shift gets us the bit pattern
            if (bitValue == 1) {
                result.bits[i].assign(Bit.boolValues.TRUE);
            } else {
                result.bits[i].assign(Bit.boolValues.FALSE);
            }
        }
    }

    public static int toInt(Word32 value) {
        // Needed to change the Word32 in two's complement back to an int
        int resultingInteger = 0;
        for (int i = 0; i < 32; i++) {
            resultingInteger = (resultingInteger << 1) | (value.bits[i].getValue() == Bit.boolValues.TRUE ? 1 : 0);
        }
        return resultingInteger;
    }
}
