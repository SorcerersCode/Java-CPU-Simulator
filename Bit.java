package Skeleton.src;

public class Bit {
    public enum boolValues { FALSE, TRUE }
    private boolValues value;

    public Bit(boolean value) {
        if(value){
            this.value = boolValues.TRUE;
        } else {
            this.value = boolValues.FALSE;
        }
    }

    public boolValues getValue() {
        return value;
    }

    public void assign(boolValues value) {
        this.value = value;
    }

    public void and(Bit b2, Bit result) {
        and(this, b2, result);
    }

    public static void and(Bit b1, Bit b2, Bit result) {
        if((b1.value == boolValues.TRUE) && (b2.value == boolValues.TRUE)){
            result.assign(boolValues.TRUE);
        } else {
            result.assign(boolValues.FALSE);
        }
    }

    public void or(Bit b2, Bit result) {
        or(this, b2, result);
    }

    public static void or(Bit b1, Bit b2, Bit result) {
        if((b1.value == boolValues.TRUE) || (b2.value == boolValues.TRUE)){
            result.assign(boolValues.TRUE);
        } else {
            result.assign(boolValues.FALSE);
        }
    }

    public void xor(Bit b2, Bit result) {
        xor(this, b2, result);
    }

    public static void xor(Bit b1, Bit b2, Bit result) {
        if((b1.value == boolValues.TRUE && b2.value == boolValues.FALSE)
        || (b1.value == boolValues.FALSE && b2.value == boolValues.TRUE)){
            result.assign(boolValues.TRUE);
        } else {
            result.assign(boolValues.FALSE);
        }
    }

    public void not(Bit result) {
        not(this, result);
    }

    public static void not(Bit b1, Bit result) {
        if(b1.value == boolValues.TRUE) {
            result.assign(boolValues.FALSE);
        } else {
            result.assign(boolValues.TRUE);
        }
    }

    public String toString() {
        if(value == boolValues.TRUE){
            return "t";
        } else {
            return "f";
        }
    }
}
