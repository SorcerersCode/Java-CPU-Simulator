package Skeleton.src;

public class Shifter {
    public static void LeftShift(Word32 source, int amount, Word32 result) {
        // Shifting left moves bits toward the most sig end
        // For each index i, if i+amount is within bounds, copy that bit; otherwise, set to false.
        for (int i = 0; i < 32; i++) {
            if (i + amount < 32) {
                result.bits[i].assign(source.bits[i + amount].getValue());
            } else {
                result.bits[i].assign(Bit.boolValues.FALSE);
            }
        }
    }

    public static void RightShift(Word32 source, int amount, Word32 result) {
        // Moves bits toward the least sig end
        // Follows the same logic as leftshift for the loop
        for (int i = 31; i >= 0; i--) {
            if (i - amount >= 0) {
                result.bits[i].assign(source.bits[i - amount].getValue());
            } else {
                result.bits[i].assign(Bit.boolValues.FALSE);
            }
        }
    }
}
