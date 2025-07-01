package Skeleton.src;

import java.util.logging.Level;

public class InstructionCache {
    private final Memory mem;
    // Holds the cache's start memory address
    private final Word32 cacheStartingMemoryAddress = new Word32();
    // The memory of our cache block itself
    private final Word32[] block = new Word32[8];
    // Determines on whether our cache is full or not
    private boolean valid = false;

    // Allows the InstructionCache to access the LevelTwoCache Instance
    private LevelTwoCache L2Cache;

    // After every read, this variable will hold how many cycles that access took:
    // 10 on a cache hit, 350 on a miss
    public int lastAccessCycles;

    // Instantiates the main memory with our cache so it knows where to access the
    // other object when it needs to be accessed
    public InstructionCache(Memory mainMemory, LevelTwoCache L2Cache) {
        this.mem = mainMemory;
        // Sets the cache lines
        for (int i = 0; i < 8; i++) {
            block[i] = new Word32();
        }
        this.L2Cache = L2Cache;
    }

    public void read(Word32 programCounterAddress, Word32 instructionsToReturn) {
        // Converts programCounter back into an int for lookup in cache
        int programCounter = TestConverter.toInt(programCounterAddress);

        // Compute the start of the 8-word memory block containing the target address.
        // This helps check if the address is already loaded in cache.
        int baseMemoryAddress = (programCounter / 8) * 8;

        // Need to see if the cache has been used at all first
        // If it has been, then lets check to see if the "base"
        // instruction address is there
        if (valid && TestConverter.toInt(cacheStartingMemoryAddress) == baseMemoryAddress) {
            // Cache hit if you are here
            int offset = programCounter - baseMemoryAddress;
            block[offset].copy(instructionsToReturn);
            lastAccessCycles = 10;
        } else {
            // Cache Miss
            Word32 safeAddress = new Word32();
            programCounterAddress.copy(safeAddress);
            // The InstructionCache doesn't have the instructions, so instead we have
            // the L2 cache grab it for us and save it to the instructionsToReturn variable
            L2Cache.read(safeAddress, instructionsToReturn);

            // Now we need to save it to our InstructionCache for faster access next time
            for (int i = 0; i < 8; i++) {
                Word32 tempWord = new Word32();
                TestConverter.fromInt(baseMemoryAddress + i, tempWord);
                // Allows the InstructionCache to have the same 8 word instructions
                // that L2 does at that base memory address location
                L2Cache.read(tempWord, block[i]);
            }
            TestConverter.fromInt(baseMemoryAddress, cacheStartingMemoryAddress);
            // Set the values we need to set now that functionality is done
            valid = true;
            lastAccessCycles = L2Cache.lastAccessCycles + 50;
        }
    }
}
