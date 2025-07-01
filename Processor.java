package Skeleton.src;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Processor {
    private Memory mem;
    private final Word32[] registers = new Word32[32];
    private final Stack<Integer> callStack = new Stack<>();
    private int programCounter = 0;
    private boolean fetchHigh = true;
    public final List<String> output = new LinkedList<>();
    private boolean isImmediateForm;
    private Word16 half16;
    private int currentClockCycle = 0;

    private Bit less = new Bit(false);
    private Bit equal = new Bit(false);


    // Needed to keep track of parts of memory
    private int opcode;
    // For immediate values of size 11 bits
    private int immediate11BitValue;
    // Used to store immediate values of size 5 bits
    private int immediate5BitValue;
    // Used to store the registers from memory
    private int registerA, registerB;

    // Cache variables
    private InstructionCache L1InstructionCache;
    private LevelTwoCache L2Cache;

    // Builds the processor class so we have a memory stack
    public Processor(Memory m) {
        mem = m;
        for (int i = 0; i < 32; i++)
            registers[i] = new Word32();
        L2Cache = new LevelTwoCache(mem);
        L1InstructionCache = new InstructionCache(mem, L2Cache);
    }

    // Runs the entire program until it reaches/detects an opcode halt value
    public void run() {
        while (true) {
            fetch();
            decode();
            if (opcode == 0) { // halt
                break;
            }

            // Since we didn't get a halt, we can continue to execute and save to output
            Word32 aluOut = execute();
            store(aluOut);
        }
    }

    private void fetch() {
        // Tells where to grab the next program based on the program counter
        Word32 programCounterAddress = new Word32();
        TestConverter.fromInt(programCounter, programCounterAddress);

        // This will hold the full32 instructions that need to be executed at the programCounter's location
        Word32 loadedBits32 = new Word32();

        // L1Instruction will get us the executable code at the given memory address
        // whether that is from its own memory or main doesn't matter to us here
        L1InstructionCache.read(programCounterAddress, loadedBits32);
        currentClockCycle += L1InstructionCache.lastAccessCycles;

        // Splits the word in 16 bit parts for the two instruction sets
        half16 = new Word16();
        if (fetchHigh) {
            loadedBits32.getTopHalf(half16);
        } else {
            loadedBits32.getBottomHalf(half16);
            // We are done processing that 32bit instruction set so we advance
            programCounter++;
        }
        // Reset the fetch requests since we already processed the first 16 of the 32
        fetchHigh = !fetchHigh;
    }

    private void decode() {
        // Parse for the opcode: bits[0..4]
        opcode = 0;
        // Binary to digit
        for (int i = 0; i < 5; i++) {
            // Grab the opcode and save it to the variable
            opcode <<= 1;
            if (half16.bits[i].getValue() == Bit.boolValues.TRUE)
                opcode |= 1;
        }

        // decode formats
        if (opcode == 0) {
            // halt
            // There is no code in this block because after the if statement
            // executes the code will jump back out where the break will kick in at run()
            // We do however need to print out the clock cycles variable
            System.out.println("Clock Cycles: " + currentClockCycle);

            // The opcode values from 8-10 and 13-17 are Call/Return format
        } else if ((opcode >= 8 && opcode <=10) || (opcode >= 12 && opcode <= 17)) {
            // Needed to reset the value to ensure clean data
            immediate11BitValue = 0;
            for (int i = 5; i < 16; i++) {
                int bitValue;
                if (half16.bits[i].getValue() == Bit.boolValues.TRUE) {
                    bitValue = 1;
                } else {
                    bitValue = 0;
                }
                immediate11BitValue = (immediate11BitValue << 1) | bitValue;
            }
            // Takes care of negative values
            immediate11BitValue = signExtend(immediate11BitValue, 11);
            // Now that we have the 11BitValue we can continue in the execution() method
        } else {
            // two‑operand: Determines if it is register-register or imm5-register
            // Look at the 6th bit: Based on our assembly convention, if
            // the 6th bit is false, then it's reg-reg, otherwise it's imm5-register
            if (half16.bits[5].getValue() == Bit.boolValues.FALSE) {
                // If you are here then you are a register-register format
                isImmediateForm = false;
                registerA = 0;
                for (int i = 6; i < 11; i++) {
                    registerA = registerA << 1;
                    if (half16.bits[i].getValue() == Bit.boolValues.TRUE) {
                        registerA |= 1;
                    } else {
                        registerA |= 0;
                    }
                }
                registerB = 0;
                for (int i = 11; i < 16; i++) {
                    registerB = registerB << 1;
                    if (half16.bits[i].getValue() == Bit.boolValues.TRUE) {
                        registerB |= 1;
                    } else {
                        registerB |= 0;
                    }
                }
            } else {
                // If you are here then you are immediate-register format
                isImmediateForm = true;
                immediate5BitValue = 0;

                // Grab the digit value and save it to a variable
                // The digit's value is the next 5 bits after the
                for (int i = 6; i < 11; i++) {
                    immediate5BitValue = immediate5BitValue << 1;
                    if (half16.bits[i].getValue() == Bit.boolValues.TRUE) {
                        immediate5BitValue |= 1;
                    } else {
                        immediate5BitValue |= 0;
                    }
                }

                // Used to store the register data
                registerB = 0;
                for (int i = 11; i < 16; i++) {
                    registerB = registerB << 1;
                    if (half16.bits[i].getValue() == Bit.boolValues.TRUE) {
                        registerB |= 1;
                    } else {
                        registerB |= 0;
                    }
                }
            }
        }
    }

    private Word32 execute() {
        Word32 out = new Word32();
        switch (opcode) {
            case 1: // add
                if (isImmediateForm) {
                    // build immediate word
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);
                    Adder.add(immediateWord5BitRepresentation, registers[registerB], out);
                } else {
                    Adder.add(registers[registerA], registers[registerB], out);
                }
                break;
            case 2: // and
                if (isImmediateForm) {
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);
                    Word32.and(immediateWord5BitRepresentation, registers[registerB], out);
                } else {
                    Word32.and(registers[registerA], registers[registerB], out);
                }
                break;
            case 3: // multiply
                if (isImmediateForm) {
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);
                    Multiplier.multiply(immediateWord5BitRepresentation, registers[registerB], out);
                } else {
                    Multiplier.multiply(registers[registerA], registers[registerB], out);
                }
                break;
            case 4: // leftshift
                Shifter.LeftShift(registers[registerB], immediate5BitValue, out);
                break;
            case 5: // subtract
                if (isImmediateForm) {
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);
                    // Gotta make sure to have a - b work properly. Before it was b - a
                    Adder.subtract(registers[registerB], immediateWord5BitRepresentation, out);
                } else {
                    Adder.subtract(registers[registerA], registers[registerB], out);
                }
                break;
            case 6: // or
                if (isImmediateForm) {
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);
                    Word32.or(immediateWord5BitRepresentation, registers[registerB], out);
                } else {
                    Word32.or(registers[registerA], registers[registerB], out);
                }
                break;
            case 7: // rightshift
                Shifter.RightShift(registers[registerB], immediate5BitValue, out);
                break;
            case 8: // syscall
                if (immediate11BitValue == 0) {
                    // print regs
                    for (int i = 0; i < 32; i++) {
                        StringBuilder sb = new StringBuilder();
                        for (Bit b : registers[i].bits) sb.append(b.toString()).append(",");
                        output.add("r" + i + ":" + sb);
                    }
                } else {
                    // print mem
                    for (int i = 0; i < 1000; i++) {
                        Word32 a = new Word32();
                        TestConverter.fromInt(i, a);
                        mem.address = a;
                        mem.read();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Bit b : mem.value.bits) stringBuilder.append(b.toString()).append(",");
                        output.add(i + ":" + stringBuilder);
                    }
                }
                break;
            case 9: // call
                // To make sure we come back to the next instruction set after this call
                // we just push the current value onto the stack
                callStack.push(programCounter);
                // Now we make the jump to the location where they requested -1 since we didn't finish executing
                // the address location 1
                programCounter += immediate11BitValue - 1;
                // This is a new instruction set so we reset the fetch
                fetchHigh = true;
                break;
            case 10: // return
                programCounter = callStack.pop();
                fetchHigh = true;
                break;
            case 11: // compare
                int valueA = 0;
                int valueB = 0;

                if (isImmediateForm) {
                    valueA = immediate5BitValue;
                    valueB = TestConverter.toInt(registers[registerB]);
                } else {
                    valueA = TestConverter.toInt(registers[registerA]);
                    valueB = TestConverter.toInt(registers[registerB]);
                }

                if (valueA < valueB) {
                    less.assign(Bit.boolValues.TRUE);
                } else {
                    less.assign(Bit.boolValues.FALSE);
                }

                if (valueA == valueB) {
                    equal.assign(Bit.boolValues.TRUE);
                } else {
                    equal.assign(Bit.boolValues.FALSE);
                }
                break;
            case 12: // ble
                if(less.getValue() == Bit.boolValues.TRUE || equal.getValue() == Bit.boolValues.TRUE) {
                    if(fetchHigh){
                        // Gotta account for the program counter moving forward 1
                        // since this code is considered executed already by doing -1
                        programCounter += immediate11BitValue - 1;
                    } else {
                        programCounter += immediate11BitValue;
                        fetchHigh = true;
                    }
                }
                break;
            case 13: // blt
                if (less.getValue() == Bit.boolValues.TRUE) {
                    if(fetchHigh){
                        // Gotta account for the program counter moving forward 1
                        // since this code is considered executed already by doing -1
                        programCounter += immediate11BitValue - 1;
                    } else {
                        programCounter += immediate11BitValue;
                        fetchHigh = true;
                    }
                }
                break;
            case 14: // bge
                if (less.getValue() == Bit.boolValues.FALSE) {
                    if(fetchHigh){
                        // Gotta account for the program counter moving forward 1
                        // since this code is considered executed already by doing -1
                        programCounter += immediate11BitValue - 1;
                    } else {
                        programCounter += immediate11BitValue;
                        fetchHigh = true;
                    }
                }
                break;
            case 15: // bgt
                Adder.subtract(registers[registerA], registers[registerB], out);
                if (less.getValue() == Bit.boolValues.FALSE && equal.getValue() == Bit.boolValues.FALSE) {
                    if(fetchHigh){
                        // Gotta account for the program counter moving forward 1
                        // since this code is considered executed already by doing -1
                        programCounter += immediate11BitValue - 1;
                    } else {
                        programCounter += immediate11BitValue;
                        fetchHigh = true;
                    }
                }
                break;
            case 16: // beq
                if (equal.getValue() == Bit.boolValues.TRUE) {
                    if(fetchHigh){
                        // Gotta account for the program counter moving forward 1
                        // since this code is considered executed already by doing -1
                        programCounter += immediate11BitValue - 1;
                    } else {
                        programCounter += immediate11BitValue;
                        fetchHigh = true;
                    }
                }
                break;
            case 17: // bne
                if (equal.getValue() == Bit.boolValues.FALSE) {
                    if(fetchHigh){
                        // Gotta account for the program counter moving forward 1
                        // since this code is considered executed already by doing -1
                        programCounter += immediate11BitValue - 1;
                    } else {
                        programCounter += immediate11BitValue;
                        fetchHigh = true;
                    }
                }
                break;
            case 18: // load
                if (isImmediateForm) {
                    // This is the immediate + destination-register form
                    // Used to store the immediate value in word32 format
                    Word32 immediateWord5BitRepresentation = new Word32();

                    // Need to convert the negative value properly
                    immediate5BitValue = signExtend(immediate5BitValue, 5);

                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);

                    // Saves the current mem address
                    Word32 currentWorkingMemAddress = new Word32();
                    mem.address.copy(currentWorkingMemAddress);

                    // Used to grab the address of where the register points to
                    Word32 temporaryAddress = new Word32();
                    // We need to perform the calculation first from the immediate value
                    // to make sure that we hit the right address from the base value
                    registers[registerB].copy(temporaryAddress);
                    Adder.add(temporaryAddress, immediateWord5BitRepresentation, temporaryAddress);

                    // Need to grab from L2 cache instead of main memory
                    L2Cache.load(temporaryAddress, out);

                    // This cache grab (lol get it) costs 50 cycles
                    currentClockCycle += 50;

                    // Now to reset the memory address to its original position
                    currentWorkingMemAddress.copy(mem.address);
                    // Used to reset the value we were working with
                    mem.read();
                } else {
                    // Save the current mem address
                    Word32 currentWorkingMemAddress = new Word32();
                    mem.address.copy(currentWorkingMemAddress);

                    // Used to grab the address of where the register points to
                    Word32 temporaryAddress = new Word32();
                    // We need to perform the calculation first from the immediate value
                    // to make sure that we hit the right address from the base value
                    registers[registerB].copy(temporaryAddress);
                    Adder.add(temporaryAddress, registers[registerA], temporaryAddress);

                    // Have L2 grab it rather than direct memory access
                    L2Cache.load(temporaryAddress, out);

                    // This cache grab (lol get it) costs 50 cycles
                    currentClockCycle += 50;

                    // Now to reset the memory address to its original position
                    currentWorkingMemAddress.copy(mem.address);
                    // Used to reset the value we were working with
                    mem.read();
                }
                break;
            case 19: // store
                // Determine if we are storing an immediate value or register to another register first
                if(isImmediateForm){
                    // Immediate + register format code
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);

                    // Save the current mem address
                    Word32 currentWorkingMemAddress = new Word32();
                    mem.address.copy(currentWorkingMemAddress);

                    // Have L2 take care of the "main memory" writing
                    L2Cache.write(registers[registerB], immediateWord5BitRepresentation);
                    // That L2 cache memory writing costed us clock cycles
                    currentClockCycle += 50;

                    // Now that the data is written there, we can go back to our old memory location
                    currentWorkingMemAddress.copy(mem.address);
                    // Used to reset the value we were working with
                    mem.read();
                } else {
                    // This is the register - register domain
                    // Save the current working memory address so we don't lose it
                    Word32 currentWorkingMemAddress = new Word32();
                    mem.address.copy(currentWorkingMemAddress);

                    // Have the L2 cache take care of the "main memory" touching
                    L2Cache.write(registers[registerB], registers[registerA]);

                    // That L2 cache memory touching costed us clock cycles
                    currentClockCycle += 50;

                    // Now that the data is written there, we can go back to our old memory location
                    currentWorkingMemAddress.copy(mem.address);
                    // Used to reset the value we were working with
                    mem.read();
                }
                break;
            case 20: // copy
                if (isImmediateForm) {
                    Word32 immediateWord5BitRepresentation = new Word32();
                    TestConverter.fromInt(immediate5BitValue, immediateWord5BitRepresentation);
                    immediateWord5BitRepresentation.copy(out);
                } else {
                    registers[registerA].copy(out);
                }
                break;
        }
        return out;
    }

    private void store(Word32 result) {
        switch (opcode) {
            // ALU operations
            case 1:  // add
                result.copy(registers[registerB]);
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 2:  // and
                result.copy(registers[registerB]);
                // Add in the clock cycle for operation
                currentClockCycle += 2;
                break;
            case 3:  // multiply
                result.copy(registers[registerB]);
                // Add in the clock cycle for action
                currentClockCycle += 10;
                break;
            case 4:  // leftshift
                result.copy(registers[registerB]);
                // Add in the clock cycle for operation
                currentClockCycle += 2;
                break;
            case 5:  // subtract
                result.copy(registers[registerB]);
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 6:  // or
                result.copy(registers[registerB]);
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 7:  // rightshift
                result.copy(registers[registerB]);
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 8: // Syscall
                // Updates the number of clock cycles for operation
                currentClockCycle += 300;
                break;
            case 9: // Call
                // Updates the number of clock cycles for operation
                currentClockCycle += 300;
                break;
            case 10: // Return
                // Updates the number of clock cycles for operation
                currentClockCycle += 300;
                break;
            case 11: // Compare
                // We don't do anything here, logic is implemented in execute()
                // Also If statements don't write data to memory so...
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 12: // BLE
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 13: // BLT
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 14: // BGE
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 15: // BGT
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 16: // BEQ
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            case 17: // BNE
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            // Load always writes into registerB
            case 18: // Load
                result.copy(registers[registerB]);
                // Updates the number of clock cycles for operation
                currentClockCycle += 300;
                break;
            case 19: // Store
                // Implementation already done in execute()
                // Updates the number of clock cycles for operation
                currentClockCycle += 300;
                break;
            // Copy always writes into registerB
            case 20: // Copy
                // Saves the value stored in result to the memory address located at said register
                result.copy(registers[registerB]);
                // Updates the number of clock cycles for operation
                currentClockCycle += 2;
                break;
            // Everything should be taken care of by now
            default:
                break;
        }
    }
    int signExtend(int value, int bitWidth) {
        int signBit = 1 << (bitWidth - 1);
        int mask = (1 << bitWidth) - 1;

        value &= mask; // Make sure the value is within bitWidth
        if ((value & signBit) != 0) {
            value |= ~mask;
        }
        return value;
    }

}
