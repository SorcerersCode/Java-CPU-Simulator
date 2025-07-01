package Skeleton.src;

public class Word16 {

    Bit[] bits;

    public Word16() {
        this.bits = new Bit[16];
        for(int i = 0; i < 16; i++){
            this.bits[i] = new Bit(false);
        }
    }

    public Word16(Bit[] in) {
        if(in.length != 16){
            throw new IllegalArgumentException("Error: Short allocation must have 16 bits\n");
        }
        this.bits = in;
    }

    public void copy(Word16 result) {
        // sets the values in "result" to be the same as the values in this instance; use "bit.assign"
        for(int i = 0; i < 16; i++){
            result.bits[i].assign(this.bits[i].getValue());
        }
    }

    public void setBitN(int n, Bit source) {
        // sets the nth bit of this word to "source"
        this.bits[n].assign(source.getValue());
    }

    public void getBitN(int n, Bit result) {
        // sets result to be the same value as the nth bit of this word
        result.assign(this.bits[n].getValue());
    }

    public boolean equals(Word16 other) { // is other equal to this
        return equals(this, other);
    }

    public static boolean equals(Word16 a, Word16 b) {
        // Runs through the bit array and checks their values
        for(int i = 0; i < 16; i++){
            if(a.bits[i].getValue() != b.bits[i].getValue()){
                return false;
            }
        }
        return true;
    }

    public void and(Word16 other, Word16 result) {
        and(this, other, result);
    }

    public static void and(Word16 a, Word16 b, Word16 result) {
        for(int i = 0; i < 16; i++){
            Bit.and(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void or(Word16 other, Word16 result) {
        or(this, other, result);
    }

    public static void or(Word16 a, Word16 b, Word16 result) {
        for(int i = 0; i < 16; i++){
            Bit.or(a.bits[i], b.bits[i], result.bits[i]);
        }
    }

    public void xor(Word16 other, Word16 result) {
        xor(this, other, result);
    }

    public static void xor(Word16 a, Word16 b, Word16 result) {
        for(int i = 0; i < 16; i++){
            Bit.xor(a.bits[i],b.bits[i],result.bits[i]);
        }
    }

    public void not( Word16 result) {
        not(this,result);
    }

    public static void not(Word16 a, Word16 result) {
        for(int i = 0; i < 16; i++){
            Bit.not(a.bits[i], result.bits[i]);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bit bit : bits) {
            sb.append(bit.toString()).append(",");
        }
        // The -1 is to get rid of the last ',' char
        return sb.substring(0, sb.length() - 1);
    }
}