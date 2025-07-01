package Skeleton.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Assembler {

    private static String convertToBits(int value, int bitCount) {
        StringBuilder binaryRepresentation = new StringBuilder();
        boolean isNegativeNumber = (value < 0);
        int index = bitCount;

        if (isNegativeNumber) {
            value += 1; // Prepare for inverted representation
        }

        while (value != 0 && index != 0) {
            if (!isNegativeNumber && (value % 2 == 0)) {
                binaryRepresentation.append('f');
            } else if (!isNegativeNumber && (value % 2 != 0)) {
                binaryRepresentation.append('t');
            } else if (isNegativeNumber && (value % 2 == 0)) {
                binaryRepresentation.append('t');
            } else {
                binaryRepresentation.append('f');
            }
            index--;
            value /= 2;
        }

        // Add padding if needed
        int len = bitCount - binaryRepresentation.length();
        char padChar = isNegativeNumber ? 't' : 'f';
        binaryRepresentation.append(String.valueOf(padChar).repeat(Math.max(0, len)));

        // Fixes the bits by flipping them
        binaryRepresentation.reverse();

        // Adds padding for the values
        if(binaryRepresentation.length() < bitCount){
            while(binaryRepresentation.length() < bitCount){
                binaryRepresentation.insert(0, 'f');
            }
        }

        return binaryRepresentation.toString();
    }

    // Converts a register to its binary value by stripping the r and passes
    // the remaining integer to convertToBits
    private static String registerToBits(String register) {
        // Gets rid of "r" in register variable so the value can be converted
        int regularNumber = Integer.parseInt(register.substring(1));

        return convertToBits(regularNumber, 5);  // Convert adjusted value to 5-bit binary
    }

    private static HashMap<String, Integer> generateOpcodeHashMap() {
        HashMap<String, Integer> opcodes = new HashMap<>();
        opcodes.put("halt", 0);
        opcodes.put("add", 1);
        opcodes.put("and", 2);
        opcodes.put("multiply", 3);
        opcodes.put("leftshift", 4);
        opcodes.put("subtract", 5);
        opcodes.put("or", 6);
        opcodes.put("rightshift", 7);
        opcodes.put("syscall", 8);
        opcodes.put("call", 9);
        opcodes.put("return", 10);
        opcodes.put("compare", 11);
        opcodes.put("ble", 12);
        opcodes.put("blt", 13);
        opcodes.put("bge", 14);
        opcodes.put("bgt", 15);
        opcodes.put("beq", 16);
        opcodes.put("bne", 17);
        opcodes.put("load", 18);
        opcodes.put("store", 19);
        opcodes.put("copy", 20);
        return opcodes;
    }

    public static String[] assemble(String[] input) {
        // Creates the opcode's mapping
        HashMap <String, Integer> opcodes = generateOpcodeHashMap();

        ArrayList<String> output = new ArrayList<>();
        //String[] op = new String[input.length];
        // Process each instruction line
        for (String line : input) {
            if (line.trim().equals("")) continue;

            // Allows us to split the lines of assembly into parts for processing
            String lowerCaseLine = line.trim().toLowerCase();
            String[] currentAssemblyLine = lowerCaseLine.split("\\s+");

            // Grabs the mnemonic so it can be processed into its binary representation
            String mnemonic = currentAssemblyLine[0];
            int opcodeValue = opcodes.get(mnemonic);
            // Converts opcode to binary so we can continue processing the rest of the satement
            String opcodeBits = convertToBits(opcodeValue, 5);
            // This variable will hold the final product of our line of assembly code
            String assembled = "";

            // Case: no operand (e.g. halt, return)
            if (currentAssemblyLine.length == 1) {
                // Fill remaining 11 bits with 'f'
                assembled = opcodeBits + convertToBits(0, 11);
            }
            // Case: one operand (immediate-only instructions: syscall, call, branches)
            else if (currentAssemblyLine.length == 2) {
                // Parse operand as a number
                int immediateValue = Integer.parseInt(currentAssemblyLine[1]);
                // Immediate field uses 11 bits
                String immediateBits = convertToBits(immediateValue, 11);
                assembled = opcodeBits + immediateBits;
            }
            // Case: two operands
            else if (currentAssemblyLine.length == 3) {
                // Determine if the first operand is a register
                boolean firstIsRegister = currentAssemblyLine[1].startsWith("r");
                if (firstIsRegister) {
                    // 2R format: both operands are registers
                    // Remove the 'r' and parse as integer
                    String register1Bits = registerToBits(currentAssemblyLine[1]);
                    String register2Bits = registerToBits(currentAssemblyLine[2]);
                    // Unused bit at end is false
                    assembled = opcodeBits + "f" + register1Bits + register2Bits;
                } else {
                    // Immediate-register format: first operand is a number, second operand is a register
                    int immediateValue = Integer.parseInt(currentAssemblyLine[1]);
                    // Immediate field in this format is 5 bits
                    String immediateBits = convertToBits(immediateValue, 5);
                    // Second operand should be a register so we check for that here
                    if (!currentAssemblyLine[2].startsWith("r")) {
                        throw new IllegalArgumentException("Error: Expected register for second operand: " + currentAssemblyLine[2]);
                    }
                    int registerTwo = Integer.parseInt(currentAssemblyLine[2].substring(1));
                    String destinationRegister = convertToBits(registerTwo, 5);
                    // Unused bit at end
                    assembled = opcodeBits + "t" + immediateBits + destinationRegister;
                }
            } else {
                throw new IllegalArgumentException("Error: Invalid instruction format: " + line);
            }
            // Ensure the assembled instruction is 16 characters.
            if (assembled.length() != 16) {
                throw new IllegalStateException("Assembled instruction not 16 bits: " + assembled);
            }

            output.add(assembled);
        }
        return output.toArray(new String[input.length]);
    }

    public static String[] finalOutput(String[] input) {
        // If the number of 16-bit instructions is odd, append a halt instruction.
        ArrayList<String> instructions = new ArrayList<>();
        Collections.addAll(instructions, input);
        if (instructions.size() % 2 != 0) {
            // Assemble a halt instruction. (halt's opcode is 0; format: 5 bits opcode + 11 false bits)
            String haltInstr = convertToBits(0, 5) + convertToBits(0, 11);
            instructions.add(haltInstr);
        }
        ArrayList<String> output = new ArrayList<>();
        // Merge instructions pairwise
        for (int i = 0; i < instructions.size(); i += 2) {
            String merged = instructions.get(i) + instructions.get(i + 1);
            output.add(merged);
        }
        return output.toArray(new String[0]);
    }
}
