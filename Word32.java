package Skeleton.src;

public class Word32 {

    Bit[] bits;

    public Word32() {
        this.bits = new Bit[32];
        for(int i = 0; i < 32; i++){
            this.bits[i] = new Bit(false);
        }
    }

    public Word32(Bit[] in) {
        if(in.length != 32){
            throw new IllegalArgumentException("Error: Word allocation must be 32 bites\n");
        }
        this.bits = in;
    }

    public void getTopHalf(Word16 result) {
        // sets result = bits 0-15 of this word. use bit.assign
        for(int i = 0; i < 16; i++){
            result.setBitN(i, this.bits[i]);
        }
    }

    public void getBottomHalf(Word16 result) {
        // sets result = bits 16-31 of this word. use bit.assign
        for(int i = 16; i < 32; i++){
            result.setBitN(i - 16, this.bits[i]);
        }
    }

    public void copy(Word32 result) {
        // sets result's bit to be the same as this. use bit.assign
        for(int i = 0; i < 32; i++){
            result.bits[i].assign(this.bits[i].getValue());
        }
    }

    public boolean equals(Word32 other) {
        return equals(this, other);
    }

    public static boolean equals(Word32 a, Word32 b) {
        for(int i = 0; i < 32; i++){
            if(b.bits[i].getValue() != a.bits[i].getValue()){
                return false;
            }
        }
        return true;
    }

    public void getBitN(int n, Bit result) {
        // use bit.assign
        result.assign(this.bits[n].getValue());
    }

    public void setBitN(int n, Bit source) {
        //  use bit.assign
        source.assign(this.bits[n].getValue());
    }

    public void and(Word32 other, Word32 result) {
        and(this, other, result);
    }

    public static void and(Word32 a, Word32 b, Word32 result) {
        for(int i = 0; i < 32; i++){
            Bit.and(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void or(Word32 other, Word32 result) {
        or(this, other, result);
    }

    public static void or(Word32 a, Word32 b, Word32 result) {
        for(int i = 0; i < 32; i++){
            Bit.or(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void xor(Word32 other, Word32 result) {
        xor(this, other, result);
    }

    public static void xor(Word32 a, Word32 b, Word32 result) {
        for(int i = 0; i < 32; i++){
            Bit.xor(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void not( Word32 result) {
        not(this, result);
    }

    public static void not(Word32 a, Word32 result) {
        for(int i = 0; i < 32; i++){
            Bit.not(a.bits[i], result.bits[i]);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bit bit : bits) {
            sb.append(bit.toString());
            sb.append(",");
        }
        // Again, offset of 1 at end is to remove trailing ',' char
        return sb.substring(0, sb.length() - 1);
    }
}
