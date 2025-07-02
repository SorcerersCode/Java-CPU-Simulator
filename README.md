# Java-CPU-Simulator
A Java implementation of a simple processor architecture (similar to ARM) complete with an ALU, multi‐level caches for faster repetitive instruction access, "virtual" memory where assembly code is stored and executed, assembler and a small suite of unit tests.

## Bit Representation

At the core of this project, individual bits are handled using a custom `Bit` class. Instead of standard booleans or raw integers, each bit is represented as an enum—either `FALSE` or `TRUE`.

Whenever the program needs to print out or display these bits (like in `Word16`, `Word32`, or anywhere else that outputs to the console), the bits are shown as single characters for readability:

* `'t'` → represents a `1` bit (TRUE)
* `'f'` → represents a `0` bit (FALSE)

This makes it easy to visually track what’s going on inside the processor’s memory and registers.

---

## What This Program Does

At a high level, this project simulates a simple processor that can read, write, and execute custom ARM-like assembly code.

The assembly programs get loaded into a "memory bank"—which, is just a large array of nullified bits that the processor can freely modify during execution.

---

## Performance Optimization via Caching

To make execution time more realistic (and introduce some basic CPU performance concepts), the processor doesn’t just fetch every instruction directly from main memory on every cycle.

Instead, it uses a two-level caching system:

* **L1 Instruction Cache** (fastest, smallest, closest to the processor)
* **L2 Cache** (larger, slower than L1 but still faster than main memory)

This caching system stores frequently accessed instructions (like loops) so they can be executed in fewer clock cycles.

---

## Instruction Format and Opcodes

### Instruction Encoding

Each instruction in this processor is 16 bits wide and follows one of two formats:

1. **Register-Register / Register-Immediate Format**

```
[5 bits] [1 bit] [5 bits] [5 bits]
[Opcode] [FormatID] [LeftReg] [RightReg/Immediate]
```

* **Opcode (5 bits)** → Determines the operation
* **Format Identifier (6th bit)** →

  * If `false`: Instruction uses **register-to-register format**
  * If `true`: Instruction uses **immediate-to-register format** (5-bit immediate value)

2. **Immediate-Only Format**

```
[5 bits] [11 bits]
[Opcode] [Immediate Value]
```

For operations that require a full 11-bit immediate, the remaining 11 bits are reserved for that value.

---

### Opcode Reference Table

| Opcode | Instruction | Notes                                  |
| ------ | ----------- | ---------------------------------------|
| 1      | ADD         | Register/Immediate add                 |
| 2      | AND         | Logical AND                            |
| 3      | MULTIPLY    | Integer multiply                       |
| 4      | LEFTSHIFT   | Logical left shift                     |
| 5      | SUBTRACT    | Integer subtraction                    |
| 6      | OR          | Logical OR                             |
| 7      | RIGHTSHIFT  | Logical right shift                    |
| 8      | SYSCALL     | Prints out memory based on given value |
| 9      | CALL        | Function call (subroutine)             |
| 10     | RETURN      | Return from subroutine                 |
| 11     | COMPARE     | Sets flags for conditional branches    |
| 12     | BLE         | Branch if Less or Equal                |
| 13     | BLT         | Branch if Less Than                    |
| 14     | BGE         | Branch if Greater or Equal             |
| 15     | BGT         | Branch if Greater Than                 |
| 16     | BEQ         | Branch if Equal                        |
| 17     | BNE         | Branch if Not Equal                    |
| 18     | LOAD        | Load value from memory                 |
| 19     | STORE       | Store value to memory                  |
| 20     | COPY        | Copy between registers/memory          |

---

## Example Assembly Code

If you want to see what the custom assembly language looks like in action, check out the `InstructionCacheTest` file. There’s example assembly code in there that shows how instructions are loaded, stored, and executed by the processor.

---

## Testing & Validation

Every major component of this processor (ALU, memory, caches, assembler, etc.) has its own dedicated test class to validate functionality.

Some examples:

* `ProcessorTest.java` → Tests the overall processor execution loop
* `MemoryTest.java` → Tests memory read/write behavior
* `InstructionCacheTest.java` → Tests cache hit/miss behavior with example assembly

---

## Running the Program

If you want to run the project yourself:

1. **Download all the `.java` files** and make sure they all sit in the same directory on your machine.
2. **Compile everything with:**

   ```bash
   javac *.java
   ```
3. **Run the InstructionCacheTest to execute the example assembly:**

   ```bash
   java InstructionCacheTest
   ```

That will load the example assembly program, execute it through the processor, and show output including cache behavior.

---
