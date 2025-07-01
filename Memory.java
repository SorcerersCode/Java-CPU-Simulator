package Skeleton.src;

public class Memory {
    public Word32 address = new Word32();
    public Word32 value = new Word32();

    private final Word32[] dram = new Word32[1000];

    public Memory() {
        // Loads each memory block into our version of "RAM"
        for (int i = 0; i < dram.length; i++) {
            dram[i] = new Word32();
        }
    }

    public int addressAsInt() {
        int valueToReturn = 0;
        // Changes the Word stored in the given address to an unsigned int from least sig to most
        for (int i = 0; i < 32; i++) {
            valueToReturn = (valueToReturn << 1) | (address.bits[i].getValue() == Bit.boolValues.TRUE ? 1 : 0);
        }
        return valueToReturn;
    }

    public void read() {
        // Needed to copy the word from dram[] to the given value
        int addressValue = addressAsInt();
        if (addressValue < 0 || addressValue >= dram.length) {
            throw new IllegalArgumentException("Error: Incorrect memory address\n" + addressValue);
        }
        // Saves the stored value to the memory address it needs to be at
        dram[addressValue].copy(value);
    }

    public void write() {
        int addressIntValue = addressAsInt();
        // This "writes" the value to dram at the given value dram[addressAsInt()]
        if (addressIntValue < 0 || addressIntValue >= dram.length) {
            throw new IllegalArgumentException("Error: Incorrect memory address\n" + addressIntValue);
        }
        // Officially writes the value into where it needs to go
        value.copy(dram[addressIntValue]);
    }

    public void load(String[] data) {
        // Prevents issues with the array size
        if (data.length > dram.length) {
            throw new IllegalArgumentException("Error: Data size is too big for memory");
        }

        // Verifies that there are only true and false characters ('t' & 'f')
        // in string and creates the bits accordingly based on value
        for (int i = 0; i < data.length; i++) {
            String line = data[i];
            if (line.length() != 32) {
                throw new IllegalArgumentException("Error: The data at " + i + " is not 32 characters\n");
            }
            // This is the section of code where the chars get turned into bits and saved
            Bit[] bits = new Bit[32];
            for (int j = 0; j < 32; j++) {
                char currentChar = line.charAt(j);
                if (currentChar == 't') {
                    bits[j] = new Bit(true);
                } else if (currentChar == 'f') {
                    bits[j] = new Bit(false);
                } else {
                    throw new IllegalArgumentException("Error: Erogenous character found: '" + currentChar +
                            "' at index " + j + " while parsing the data string at index " + i);
                }
            }
            // Stores new Word32 to DRAM to finish the method
            dram[i] = new Word32(bits);
        }
    }
}
