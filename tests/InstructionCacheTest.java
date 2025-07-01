package Skeleton.src;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstructionCacheTest {
    private static Processor runProgram(String[] program) {
        var assembled = Assembler.assemble(program);
        var merged    = Assembler.finalOutput(assembled);
        var memory         = new Memory();
        memory.load(merged);
        var processor = new Processor(memory);
        processor.run();
        return processor;
    }

    @Test
    public void testSumArray() {
        String[] program = {
                // initialize
                "copy 1 r2",    // r2 = 1               // Address 0
                "copy 20 r1",   // r1 = 20 (counter)
                "copy 0 r0",    // r0 = 0 (sum)         // Address 1
                "copy 0 r0",    // Throw away instruction - used to keep addresses pretty
                // loop:
                "add r2 r0",    // r0 += r2             // Address 2
                "add 1 r2",     // r2++
                "subtract 1 r1",// r1--                 // Address 3
                "compare 0 r1", // compare r1 with 0
                "bne -2",       // if r1 != 0 goto loop start    // Address 4
                // done
                "syscall 0",
                "halt"                                  // Address 5
        };
        var p = runProgram(program);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) sb.append("f,");
        sb.append("t,t,f,t,f,f,t,f,");

        assertEquals("r0:" + sb, p.output.get(0));
    }

    @Test
    public void testSumArrayBackwards() {
        String[] program = {
                // initialize
                "copy 20 r2",   // r2 = 20                 Address 0
                "copy 20 r1",   // r1 = 20 (counter)
                "copy 0 r0",    // r0 = 0 (sum)            Address 1
                "copy 0 r0",    // Throw away instruction -- Used to organize instruction addresses
                // loop:
                "add r2 r0",    // r0 += r2                Address 2
                "subtract 1 r2",// r2--
                "subtract 1 r1",// r1--                    Address 3
                "compare 0 r1", // compare r1 with 0
                "bne -2",       // if r1 != 0 goto add     Address 4
                // done
                "syscall 0", //
                "halt" //                                  Address 5
        };
        var p = runProgram(program);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) sb.append("f,");
        sb.append("t,t,f,t,f,f,t,f,");  // 210

        assertEquals("r0:" + sb, p.output.get(0));
    }

    @Test
    public void testSumLinkedList() {
        String[] program = {
                // --- Initialize ---
                "copy 0 r0",        // r0 = 0 (always-zero)                             --0
                "copy 20 r1",       // r1 = 20 (counter for list creation)
                "copy 31 r5",       // r5 = start address for list                      --1
                "copy 31 r5",       // Throwaway instruction for bne math easier
                "leftshift 3 r5",   // Need to leftshift to get address to equal 250    --2
                "add 2 r5",         // Finally sets the address to equal 250

                // --- Fill list with values 1 to 20 ---
                "add 1 r4",         // r4 = value to store (starts at 1, increments)    --3
                "store r4 r5",      // store r4 at mem[r5]
                "add 1 r5",         // move to next memory cell                         --4
                "subtract 1 r1",    // decrement counter
                "compare r0 r1",    // done when r1 == 0                                --5
                "bne -2",           // loop back to add/store

                // --- Prepare to sum list ---
                "subtract 1 r5",    // r5 now points to last value (269)                --6
                "copy 0 r1",        // r1 = 0 (reset counter for sum loop)

                // --- Sum all 20 values ---
                "copy r5 r4",       // r4 = r5 (copy pointer)                           --7
                "copy r5 r4",       // Throwaway instruction to make bne jump easier
                "load 0 r4",        // r4 = mem[r4]                                     --8
                "add r4 r2",        // r2 += r4
                "subtract 1 r5",    // move pointer to previous memory cell             --9
                "add 1 r1",         // increment loop counter
                "compare 20 r1",    // repeat 20 times                                  --10
                "bne -3",           // loop back to load/add

                // --- Done ---
                "syscall 0",        // print                                            --11
                "halt"
        };
        var p = runProgram(program);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) sb.append("f,");
        sb.append("t,t,f,t,f,f,t,f,");  // 210

        assertEquals("r2:" + sb, p.output.get(2));
    }
}
