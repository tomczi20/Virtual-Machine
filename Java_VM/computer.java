public class computer {

    private bit haltBit = new bit();
    private memory mainMemory = new memory();
    private longword PC = new longword();
    private longword currentInstruction = new longword();
    private longword[] registers = new longword[16];
    private longword op1 = new longword();
    private longword op2 = new longword();
    private longword result = new longword();
    private longword moveRegister = new longword(); //Used to hold the value of the register where a value will be moved to
    private longword moveValue = new longword(); //Used to hold the value of the number to be stored
    private longword addressForPreload = new longword(); //Holds the address of where the next instruction to be read should be stored into memory
    private longword jumpAddress = new longword();
    private bit compareBit1 = new bit(); //holds first bit from compare
    private bit compareBit2 = new bit(); //holds second bit from compare
    private boolean compareMatch = false;
    private longword addressForBranch = new longword();
    private longword SP = new longword();
    private int stackCount; //Used to set a longword equal to PC that is used in "call" function without actually using PC

    public computer(){
        haltBit.set(0);
        PC.set(0);
        SP.set(1020);
        stackCount = 0;
        for (int g = 0; g<16; g++){ //Fills registers with empty longwords to avoid null pointer exceptions
            longword d = new longword();
            registers[g] = d;
        }

    }

    public void run(){
        while(haltBit.getValue() == 0){
            fetch();
            decode();
            execute();
            store();
        }
    }

    public void fetch(){
        currentInstruction.copy(mainMemory.read(PC));
        longword incrementer = new longword();
        incrementer.getBit(30).set(1); //Sets incrementer longword to a value of 2 to be used in incrementing PC in the rippleAdder by 2
        PC.copy(rippleAdder.add(PC,incrementer));
        stackCount = stackCount+2; //stack count gets incremented by 2 since it represents PC and PC gets incremented by 2
    }

    public void decode(){
        bit oneBit = new bit();
        oneBit.set(1);

        if(isALUOperation(currentInstruction)) { //Only continue to retrieve op1 and op2 if the current instruction is an ALU operation, otherwise leave registers alone
            longword op1Source = new longword();
            longword op2Source = new longword();

            longword op1Mask = new longword();
            op1Mask.set(251658240); //Creates a longword: [00001111000000000000000000000000] that is later "anded" with currentInstruction to extract the bits that make up R1

            longword op2Mask = new longword();
            op2Mask.set(15728640); //Creates a longword: [00000000111100000000000000000000] that is later "anded" with currentInstruction to extract the bits that make up R2

            op1Source.copy(op1Mask.and(currentInstruction)); //The result of "anding" op1Mask and currentInstruction is copied into op1Source so it can be shifted
            op1Source.copy(op1Source.rightShift(24));
            op1.copy(registers[op1Source.getSigned()]);//The decimal value of op1Source is now the index of the longword in the register array that is copied into op1

            op2Source.copy(op2Mask.and(currentInstruction)); //The result of "anding" op2Mask and currentInstruction is copied into op2Source so it can be shifted
            op2Source.copy(op2Source.rightShift(20));
            op2.copy(registers[op2Source.getSigned()]); //The decimal value of op2Source is now the index of the longword in the register array that is copied into op2

        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 0 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 1) { //Inspects first four bits of instruction and proceeds of current Instruction is "move"

            moveRegister.setBit(28,currentInstruction.getBit(4)); //Extracts the register to which to move the value into as longword moveRegister
            moveRegister.setBit(29,currentInstruction.getBit(5));
            moveRegister.setBit(30,currentInstruction.getBit(6));
            moveRegister.setBit(31,currentInstruction.getBit(7));

            moveValue.setBit(24,currentInstruction.getBit(8)); //Extracts the value to be moved into a register from the current instruction
            moveValue.setBit(25,currentInstruction.getBit(9));
            moveValue.setBit(26,currentInstruction.getBit(10));
            moveValue.setBit(27,currentInstruction.getBit(11));
            moveValue.setBit(28,currentInstruction.getBit(12));
            moveValue.setBit(29,currentInstruction.getBit(13));
            moveValue.setBit(30,currentInstruction.getBit(14));
            moveValue.setBit(31,currentInstruction.getBit(15));
            //Note: Actually 'moving' the moveValue into the register indicated by moveRegister is done in the store() method

        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 0 && currentInstruction.getBit(2).getValue() == 1 && currentInstruction.getBit(3).getValue() == 1) { //Only proceeds if current Instruction is "jump"
            longword jumpMask = new longword();
            jumpMask.set(268369920); //Creates a longword "00001111111111110000000000000000" that is anded with currentInstruction and then right shifted 16. The resulting longword holds the value of the address to jump to
            jumpAddress.copy(jumpMask.and(currentInstruction));
            jumpAddress.copy(jumpAddress.rightShift(16));


        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 1 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 0){ //Proceeds if current Instruction is "compare" to extract the registers to be compared into op1 and op2
            longword op1Source = new longword();
            longword op2Source = new longword();

            longword op1Mask = new longword();
            op1Mask.set(15728640); //Creates a longword: [00000000111100000000000000000000] that is later "anded" with currentInstruction to extract the bits that make up the first register for comparison

            longword op2Mask = new longword();
            op2Mask.set(983040); //Creates a longword: [00000000000011110000000000000000] that is later "anded" with currentInstruction to extract the bits that make up the second register for comparison

            op1Source.copy(op1Mask.and(currentInstruction)); //The result of "anding" op1Mask and currentInstruction is copied into op1Source so it can be shifted
            op1Source.copy(op1Source.rightShift(20));
            op1.copy(registers[op1Source.getSigned()]); //op1 holds first register for comparison

            op2Source.copy(op2Mask.and(currentInstruction)); //The result of "anding" op2Mask and currentInstruction is copied into op2Source so it can be shifted
            op2Source.copy(op2Source.rightShift(16));
            op2.copy(registers[op2Source.getSigned()]); //op2 holds second register for comparison
        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 1 && currentInstruction.getBit(2).getValue() == 1 && currentInstruction.getBit(3).getValue() == 0){ //proceeds if pop, push, call, or return
            if(currentInstruction.getBit(4).getValue() == 0) { //proceed if push or pop
                op1.setBit(31, currentInstruction.getBit(15));
                op1.setBit(30, currentInstruction.getBit(14));
                op1.setBit(29, currentInstruction.getBit(13));
                op1.setBit(28, currentInstruction.getBit(12)); //Extracts the register from push or pop operation into op1
            }else{ //proceeds if call or return
                longword callMask = new longword();
                callMask.set(67043328); //Creates a longword: [00000011111111110000000000000000] that is later "anded" with currentInstruction to extract the address
                op1.copy(callMask.and(currentInstruction)); //The result of "anding" callMask and currentInstruction is copied into op1 and then shifted to be used as the address to jump to in "call"
                op1.copy(op1.rightShift(16));
            }
        }

    }

    public void execute() {
        if(isALUOperation(currentInstruction)){
            result.copy(ALU.doOp(currentInstruction.getBit(0), currentInstruction.getBit(1), currentInstruction.getBit(2), currentInstruction.getBit(3), op1, op2));

        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 0 && currentInstruction.getBit(2).getValue() == 1 && currentInstruction.getBit(3).getValue() == 0){
                if(currentInstruction.getBit(15).getValue() == 0){ //Inspects current Instruction to determine if it is a print register soft interrupt or a print memory.
                    printRegisters();
                }else{
                    printMemory();
                }
        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 0 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 0) { //If encountered "0000" then halt
                haltBit.set(1);
        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 1 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 0){ //If encountered "compare" then proceed
            longword compareResult = new longword();
            compareResult.copy(rippleAdder.subtract(op1,op2)); //compareResult holds the result of subtracting the two registers that are compared
            if(compareResult.getSigned() == 0){ //if compareResult is 0, then the two registers are equal, sets bits to represent "equal"
                compareBit1.set(0);
                compareBit2.set(1);
            }else if(compareResult.getSigned() < 0){ //if compareResult is less than 0, then second register is greater than the first, sets bits to represent "less than" and "not equal"
                compareBit1.set(0);
                compareBit2.set(0);
            }else if(compareResult.getSigned() > 0){ //if compareResult is greater than 0, then first register is greater than the second, sets bits to represent "greater than" and "not equal"
                compareBit1.set(1);
                compareBit2.set(0);
            }
        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 1 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 1) { //If encountered "Branch" then proceed
                    longword addressBranchMask = new longword();
                    addressBranchMask.set(33488896); //Creates a longword "00000001111111110000000000000000" that is anded with current instruction and shifted to extract the address to branch to
                    addressForBranch.copy(currentInstruction.and(addressBranchMask));
                    addressForBranch.copy(addressForBranch.rightShift(16));
                    if(currentInstruction.getBit(6).getValue() == 1){
                        longword negativeOne = new longword();
                        negativeOne.set(-1);
                        addressForBranch.copy(multiplier.multiply(addressForBranch,negativeOne)); //negates the address from branch instruction if the sign bit in the branch instruction is 1
                    }
                    if(currentInstruction.getBit(4).getValue() == 0 && currentInstruction.getBit(5).getValue() == 1){ //proceeds if condition code is 01 "equal"
                        if(compareBit2.getValue() == 1) {
                            compareMatch = true; //if compare bits signify equal values in the registers then sets compare match to true;
                        }

                    }else if(currentInstruction.getBit(4).getValue() == 1 && currentInstruction.getBit(5).getValue() == 0){ //proceeds if condition code is 10 "greater than"
                        if(compareBit1.getValue() == 1){
                            compareMatch = true; //if compare bits signify first register is greater than second then sets compare match to true;
                        }
                    }else if(currentInstruction.getBit(4).getValue() == 0 && currentInstruction.getBit(5).getValue() == 0){ //proceeds if condition code is 00 "less than"
                        if(compareBit2.getValue() == 0){
                            compareMatch = true; //if compare bits signify first register is less than second then sets compare match to true;
                        }
                    }else if(currentInstruction.getBit(4).getValue() == 1 && currentInstruction.getBit(5).getValue() == 1){ //proceeds if condition code is 11 "greater than or equal"
                        if(compareBit1.getValue() == 1 || compareBit2.getValue() == 1){
                            compareMatch = true; //if compare bits signify first register is greater than second or that they are equal, then sets compare match to true;
                        }
                    }
        }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 1 && currentInstruction.getBit(2).getValue() == 1 && currentInstruction.getBit(3).getValue() == 0){ //proceeds if pop, push, call, or return
            longword incrementby4 = new longword();
            incrementby4.set(4); //used in changing SP
            if(currentInstruction.getBit(4).getValue() == 0) { //proceed if push or pop
                if (currentInstruction.getBit(5).getValue() == 1) { //proceed if pop
                    SP.copy(rippleAdder.add(SP, incrementby4));
                    registers[op1.getSigned()].copy(mainMemory.read(SP)); //value read from memory at SP is copied into the register given by op1
                    mainMemory.write(SP, new longword()); //if a value is popped then an empty longword takes its place
                }else{
                    mainMemory.write(SP, registers[op1.getSigned()]); //writes the value from the register at op1 into memory at SP
                    SP.copy(rippleAdder.subtract(SP, incrementby4));
                }
            }else{
                if(currentInstruction.getBit(5).getValue() == 0) { //proceed if "call"
                    longword callAddress = new longword();
                    callAddress.set(stackCount); //callAdrress is set to the value of PC without using PC. it is also the address of the next instruction after the call
                    mainMemory.write(SP, callAddress);
                    SP.copy(rippleAdder.subtract(SP, incrementby4));
                    PC.copy(op1); //address given in call instruction is set into PC
                }else{
                    SP.copy(rippleAdder.add(SP, incrementby4));
                    PC.copy(mainMemory.read(SP));
                    mainMemory.write(SP, new longword()); //when the address is popped off the stack, then an empty longword takes its place
                }
            }
        }
    }

    public void store(){
        if(isALUOperation(currentInstruction)) { //Executes if current Instruction is an ALU operation otherwise leave registers alone.
            longword r3 = new longword();
            bit oneBit = new bit();
            oneBit.set(1);

            longword r3Mask = new longword();
            r3Mask.setBit(12, oneBit);
            r3Mask.setBit(13, oneBit);
            r3Mask.setBit(14, oneBit);
            r3Mask.setBit(15, oneBit); //Creates a longword: [00000000000011110000000000000000] that is later "anded" with currentInstruction to extract the bits that make up R3

            r3.copy(r3Mask.and(currentInstruction)); //The result of "anding" r3Mask and currentInstruction is copied into r3 so it can be shifted
            r3.copy(r3.rightShift(16));
            registers[r3.getSigned()].copy(result); //After shifting, the decimal value of r3 now represents the index in the register array that is meant to store the result

        }else{
            if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 0 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 1) { //Only proceeds if current Instruction is "move"
                registers[moveRegister.getSigned()].copy(moveValue); //move the value to be moved into the register at index moveRegister
            }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 0 && currentInstruction.getBit(2).getValue() == 1 && currentInstruction.getBit(3).getValue() == 1) { //Only proceeds if current Instruction is "jump"
                PC.copy(jumpAddress);
            }else if(currentInstruction.getBit(0).getValue() == 0 && currentInstruction.getBit(1).getValue() == 1 && currentInstruction.getBit(2).getValue() == 0 && currentInstruction.getBit(3).getValue() == 1) { //If encountered "Branch" then proceed
                if(compareMatch){
                    PC.copy(rippleAdder.add(PC,addressForBranch)); //If there is a match between the branch instruction and the comparison then proceeds to change PC
                    compareMatch = false;
                }

            }
        }
    }

    public void preload(String[] commands){
        longword addressIncrementer = new longword();
        addressIncrementer.set(4);
        int k = commands.length;
        for(int u = 0; u<commands.length; u++) { //Loops for every element in the String array
            longword instruction = new longword();
            String str = commands[u];
            str = str.replaceAll("\\s", ""); //Used to get rid of white space between groups of 4 bits
            if(k >1) { //proceeds as long as there are at least 2 instructions to read from the array
                for (int y = 0; y < 16; y++) { //Used to loop through the first string (str)
                    bit insertBit = new bit();
                    insertBit.set(Character.getNumericValue(str.charAt(y))); //Turns every character from the given string into an integer that sets the value of the bit that then is inserted into instruction.
                    instruction.setBit(y, insertBit);
                }for (int l = 0; l < 16; l++) { //Used to loop through the second string
                    bit insertBit = new bit();
                    insertBit.set(Character.getNumericValue(commands[u+1].replaceAll("\\s", "").charAt(l))); //Turns every character from the given string into an integer that sets the value of the bit that then is inserted into instruction.
                    instruction.setBit(l + 16, insertBit);
                }
                mainMemory.write(addressForPreload,instruction);
                addressForPreload.copy(rippleAdder.add(addressForPreload, addressIncrementer));
                k = k-2; //After 2 instructions are read from the instruction array, the remaining amount of instructions are represented by k-2
                u++; //increments u since commands[u+1] was already read into memory so it needs to be skipped
            }else if(k == 1){ //proceeds if there is only 1 element left in the array of instructions
                for (int y = 0; y < 16; y++) {
                    bit insertBit = new bit();
                    insertBit.set(Character.getNumericValue(str.charAt(y))); //Turns every character from the given string into an integer that sets the value of the bit that then is inserted into instruction.
                    instruction.setBit(y, insertBit);
                }
                mainMemory.write(addressForPreload,instruction);
                addressForPreload.copy(rippleAdder.add(addressForPreload, addressIncrementer));
            }
        }
    }

    public void printRegisters(){
        System.out.println("Registers:");
        for(int g = 0; g < 16; g++){
            System.out.println(registers[g].toString());
        }
    }

    public void printMemory(){ //Calls printBytes from memory class to print all bytes from memory to screen
        System.out.println("Memory:");
        mainMemory.printBytes();
    }

    public boolean isALUOperation(longword a){ //Inspects the first four bits of the passed longword and returns true if it is an ALU operation. This is used to protect the registers from any changes if we are not executing an alu operation
        if(a.getBit(0).getValue() == 1){
            return true;
        }else if( a.getBit(1).getValue() == 1 && a.getBit(2).getValue() == 1 && a.getBit(3).getValue() == 1){
            return true;
        }
        return false;
    }
}



