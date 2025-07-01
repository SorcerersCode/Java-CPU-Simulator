package Skeleton.src;

public class LevelTwoCache {
    private final Memory mem;
    // For each of our 4 blocks we need to remember its starting (base) address
    private Word32[] blockBases = new Word32[4];
    // Used to hold the 8 words in each block
    private Word32[][] blocks = new Word32[4][8];
    // Valid bits for each block on whether we saved memory to it or not
    private boolean[] valid = new boolean[4];
    // Holds the amount of clock cycles it took to perform an action
    public int lastAccessCycles;

    // Instantiates the main memory with our cache mechanism so it
    // can access the memory for when it doesn't exist
    public LevelTwoCache(Memory mainMemory) {
        this.mem = mainMemory;
        for (int i = 0; i < 4; i++) {
            blockBases[i] = new Word32();
            valid[i] = false;
            for (int j = 0; j < 8; j++) {
                blocks[i][j] = new Word32();
            }
        }
    }

    public void read(Word32 programCounterAddress, Word32 instructionsToReturn) {
        int addressInt = TestConverter.toInt(programCounterAddress);

        // Compute the block’s base (multiple of 8)
        int base = (addressInt / 8) * 8;
        // Finds which of our 4 cache blocks to use
        int index = (base / 8) % 4;
        // Checks to see if this block ever been loaded
        if (valid[index] && TestConverter.toInt(blockBases[index]) == base) {
            // L2 cache hit
            int offset = addressInt - base;
            blocks[index][offset].copy(instructionsToReturn);
            lastAccessCycles = 20;
        } else {
            // L2 cache miss
            // Save the old address that the memory was working on before pointing it in a different direction
            Word32 savedInstructionPointer = new Word32();
            mem.address.copy(savedInstructionPointer);

            // Grab the instructions at the requested address from main memory
            // Set the main memory's pointer to the instruction location
            programCounterAddress.copy(mem.address);
            // Read the instruction at that location to get its value
            mem.read();
            // Grab and save the raw bites at that location into cache and output
            mem.value.copy(instructionsToReturn);

            // Now that we dealt with handing over the right instructions from memory, lets
            // save the instruction as well as it's next set of instructions to cache memory
            // Here the loop saves the next 8 instructions into cache for that memory block
            for (int i = 0; i < 8; i++) {
                int currentCacheAddress = base + i;
                // point main memory
                Word32 tempWord = new Word32();
                TestConverter.fromInt(currentCacheAddress, tempWord);
                tempWord.copy(mem.address);
                mem.read();
                // Save the instructions into our cache block
                mem.value.copy(blocks[index][i]);
            }
            // Update the cache block with a "used" tag so we know we have
            // instructions there that can be referenced and accessed
            TestConverter.fromInt(base, blockBases[index]);
            valid[index] = true;

            // Set the memory instruction pointer back to its original location
            savedInstructionPointer.copy(mem.address);
            // Make sure to reset the value as well now that we fixed the pointer
            mem.read();
            // Paying the cost for this excursion
            lastAccessCycles = 350;
        }
    }

    public void load(Word32 addressPointer, Word32 valueToReturn){
        // Point the memory unit to the pointer's address
        addressPointer.copy(mem.address);

        // Read the data located at that pointer's address
        mem.read();

        // Now we can store the value at that memory address to the designated variable
        mem.value.copy(valueToReturn);
    }

    public void write(Word32 addressPointer, Word32 valueToSave){
        // Set the memory pointer to the requested location
        addressPointer.copy(mem.address);

        // Set the memory's value to be equal to the requested one
        valueToSave.copy(mem.value);

        // Save the requested value to that memory address with write
        mem.write();
    }
}
