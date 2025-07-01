package Skeleton.src;

public class Adder {
    public static void subtract(Word32 a, Word32 b, Word32 result) {
        // Used to calculate (-b) for later subtraction process
        Word32 negB = new Word32();
        Word32.not(b, negB);

        // The word will represent our carrying value if needed
        Word32 carryingOne = new Word32();
        // Need to set everything to false except for the last one at i=31
        for (int i = 0; i < 32; i++) {
            carryingOne.bits[i].assign(Bit.boolValues.FALSE);
        }
        carryingOne.bits[31].assign(Bit.boolValues.TRUE);

        // Setting the complement to perform ~b + 1
        Word32 complement = new Word32();
        add(negB, carryingOne, complement);
        add(a, complement, result);
    }

    public static void add(Word32 a, Word32 b, Word32 result) {
        // Adds all the bits from the least sig to the most sig
        Bit carry = new Bit(false);
        for (int i = 31; i >= 0; i--) {
            Bit bitA = a.bits[i];
            Bit bitB = b.bits[i];

            // Needed to perform the XOR operation without losing data
            Bit temporaryBit = new Bit(false);
            Bit.xor(bitA, bitB, temporaryBit);
            Bit sumBit = new Bit(false);
            Bit.xor(temporaryBit, carry, sumBit);

            // Performs: a AND b OR carry AND (a XOR b))
            Bit temporarySecondBit = new Bit(false);
            Bit.and(bitA, bitB, temporarySecondBit);
            Bit temp3 = new Bit(false);
            Bit.and(carry, temporaryBit, temp3);
            Bit newCarry = new Bit(false);
            Bit.or(temporarySecondBit, temp3, newCarry);

            // Need to save it after calculating it
            result.bits[i].assign(sumBit.getValue());
            // Updates the value with the new carr
            carry.assign(newCarry.getValue());
        }
    }
}
