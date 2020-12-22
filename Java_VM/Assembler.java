import java.util.StringTokenizer;

public class Assembler {

    private static boolean jumpVal = false;
    private static boolean branchVal = false;
    private static boolean callVal = false;

    public static String[] assemble(String[] words) throws Exception {
        String[] parsedCommands = new String[words.length]; //creates a new string array that is the same size of the array passed into the method
        int indexCount = 0;
        String firstCommand = "";
        for(int g = 0; g<words.length; g++) {
            StringTokenizer st = new StringTokenizer(words[g], " "); //Dedicated lexical analyzer splits every element of passed string array into tokens
            firstCommand = st.nextToken(); //gets first command
            if(firstCommand.equals("move")){
                parsedCommands[indexCount] = commandToString(firstCommand) + registerToString(st.nextToken()) + moveValToString(st.nextToken()); //"move" command is made up of three tokens: "move", the register to move into, and the value being moved. We get the bit representation of each token as a string, concatenate them and place them into the output array
                indexCount++;
            }else if(firstCommand.equals("interrupt")){
                parsedCommands[indexCount] = commandToString(firstCommand) + moveValToString(st.nextToken()); //"interrupt" is made up of two tokens: "interrupt" and a 1 or 0. We get the bit representation of each token as a string, concatenate them and place them into the output array
                indexCount++;
            }else if(firstCommand.equals("not")){
                parsedCommands[indexCount] = commandToString(firstCommand) +  registerToString(st.nextToken()) + "0000 " + registerToString(st.nextToken()); //"not" is made up of three tokens since it only acts on one register and then stores into a register. Gets bits of each token as a string, concatenates them, and places into output array
                indexCount++;
            }else if(firstCommand.equals("jump") || firstCommand.equals("call")){
                if(firstCommand.equals("jump")) {
                    jumpVal = true; //jumpVal is set to true so that when moveValToString is called it returns the bit representation of the jump value in the correct form for the jump instruction
                    parsedCommands[indexCount] = commandToString(firstCommand) + moveValToString(st.nextToken()); //"jump" is made up of two tokens: "jump" and the address to jump two
                    indexCount++;
                }else{
                    callVal = true; //callVal is set to true so that when moveValToString is called it returns the bit representation of the call value in the correct form for the call instruction
                    parsedCommands[indexCount] = commandToString(firstCommand) + moveValToString(st.nextToken()); //"call" is made up of two tokens: "call" and the address to jump to
                    indexCount++;
                }
            }else if(firstCommand.equals("compare")){
                parsedCommands[indexCount] = commandToString(firstCommand) + "0000 " + registerToString(st.nextToken()) + registerToString(st.nextToken()); //"compare" is made up of three tokens: "compare" and two registers
                indexCount++;
            }else if(firstCommand.equals("branchIfEqual") || firstCommand.equals("branchIfNotEqual") || firstCommand.equals("branchIfGreaterThan") || firstCommand.equals("branchIfGreaterThanOrEqual")){
                branchVal = true; //branchVal is set to true so that when moveValToString is called it returns the bit representation of the branch value in the correct form for the branch instruction
                String branchInstruction = "";
                branchInstruction = commandToString(firstCommand);
                String branchAddress = st.nextToken(); //value to branch to is stored in branchAddress
                if(branchAddress.charAt(0) == '-'){
                    branchAddress = branchAddress.replace("-",""); //gets rid of "-" in the branchAddress leaving just the value
                    branchInstruction = branchInstruction + "1"; //adds a "1" as the sign bit to the instruction if the value to branch to is negative
                }else{
                    branchInstruction = branchInstruction + "0"; //adds a "0" as the sign bit to the instruction if the value to branch to is positive
                }
                branchInstruction = branchInstruction + moveValToString(branchAddress);
                parsedCommands[indexCount] = branchInstruction;
                indexCount++;
            }else if(firstCommand.equals("halt") || firstCommand.equals("return")){
                if(firstCommand.equals("halt")) {
                    parsedCommands[indexCount] = "0000000000000000";
                    indexCount++;
                }else{
                    parsedCommands[indexCount] = "0110110000000000";
                    indexCount++;
                }
            }else if(firstCommand.equals("push") || firstCommand.equals("pop")){
                parsedCommands[indexCount] = commandToString(firstCommand) + registerToString(st.nextToken()); //"push" and "pop" are made up of the command and a register
                indexCount++;
            }else{
                parsedCommands[indexCount] = commandToString(firstCommand) + registerToString(st.nextToken()) + registerToString(st.nextToken()) + registerToString(st.nextToken()); //Every other operation is made up of 4 tokens: the operation, and three registers.
                indexCount++;
            }
        }
        return parsedCommands;
    }


    public static String commandToString(String operation){ //Converts operation commands into their bit strings
        return switch (operation) {
            case "interrupt" -> "0010 0000 ";
            case "add" -> "1110 ";
            case "subtract" -> "1111 ";
            case "or" -> "1001 ";
            case "and" -> "1000 ";
            case "leftshift" -> "1100 ";
            case "rightshift" -> "1101 ";
            case "xor" -> "1010 ";
            case "not" -> "1011 ";
            case "multiply" -> "0111 ";
            case "move" -> "0001 ";
            case "jump" -> "0011 ";
            case "compare" -> "0100 ";
            case "branchIfEqual" -> "0101 01";
            case "branchIfNotEqual" -> "0101 00";
            case "branchIfGreaterThan" -> "0101 10";
            case "branchIfGreaterThanOrEqual" -> "0101 11";
            case "push" -> "0110 0000 0000 ";
            case "pop" -> "0110 0100 0000 ";
            case "call" -> "0110 10";
            default -> null;
        };
    }

    public static String registerToString(String register) throws Exception { //Converts register commands such as "R5" and "R10" to their bit strings
        String bitsFromRegister = "";
        longword regWord = new longword();
        register = register.replace("R",""); //gets rid of "R" in front of value leaving just the value so that it can be made into an integer
        int regValue = Integer.parseInt(register);

        if(regValue < 0 || regValue > 15){ //checks for a valid register value
            throw new Exception("Invalid register index!");
        }

        regWord.set(regValue); //sets a longword to the register value which converts the value into binary

        for(int k = 28; k<32; k++){
            bitsFromRegister = bitsFromRegister.concat(Integer.toString(regWord.getBit(k).getValue())); //loops through the first 4 bits of the longword saving their values into a string
        }

        bitsFromRegister = bitsFromRegister + " ";
        return bitsFromRegister;
    }

    public static String moveValToString(String val) throws Exception { //converts values that are moved into registers into their bit strings
        String valInBits = "";
        longword valueMoved = new longword();

        if(val.charAt(0) == '-'){ //continues if value being passed is negative
            val = val.replace("-",""); //gets rid of  "-" in front of value so that it can be made into an integer
            if(Integer.parseInt(val) <= 128) { //checks for a valid negative value
                valueMoved.set(Integer.parseInt(val)); //sets the longword to the positive binary representation of the value being moved
                valueMoved.copy(rippleAdder.getTwosComplement(valueMoved)); //sets the longword to its twos complement form using the getTwosComplement method in rippleAdder. The resulting longword is the negative binary representation of the value being moved
            }else{
                throw new Exception("Invalid move Value!");
            }
        }else {
            if(Integer.parseInt(val) <= 127 ) { //checks for a valid positive value
                valueMoved.set(Integer.parseInt(val));
            }else{
                throw new Exception("Invalid move Value!");
            }
        }

        if(!jumpVal && !branchVal && !callVal) { //proceeds if not creating a string for a "jump" or "branch" or "call" instruction, used for "move" instruction specifically
            for (int k = 24; k < 32; k++) {
                if (k == 28) {
                    valInBits = valInBits + " "; //creates a space in the string every 4 bits
                }
                valInBits = valInBits.concat(Integer.toString(valueMoved.getBit(k).getValue())); //loops through the first 8 bits of the longword that represents the value being moved, saving their values into a string
            }
        }else if(jumpVal){ //proceeds to create a string for the passed value for a "jump" instruction
            for (int k = 20; k < 32; k++) {
                if (k == 28 || k==24) {
                    valInBits = valInBits + " "; //creates a space in the string every 4 bits
                }
                valInBits = valInBits.concat(Integer.toString(valueMoved.getBit(k).getValue())); //loops through the first 12 bits of the longword that represents the value to jump to, saving their values into a string
            }
            jumpVal = false;
        }else if(branchVal){ //proceeds to create a string for the passed value for a "branch" instruction
            for (int k = 23; k < 32; k++) {
                if (k == 28 || k==24) {
                    valInBits = valInBits + " "; //creates a space in the string every 4 bits
                }
                valInBits = valInBits.concat(Integer.toString(valueMoved.getBit(k).getValue())); //loops through the first 9 bits of the longword that represents the value to branch to, saving their values into a string
            }
            branchVal = false;
        }else{ //proceeds to create a string for the passed value for a "call" instruction
            for (int k = 22; k < 32; k++) {
                if (k == 28 || k==24) {
                    valInBits = valInBits + " "; //creates a space in the string every 4 bits
                }
                valInBits = valInBits.concat(Integer.toString(valueMoved.getBit(k).getValue())); //loops through the first 10 bits of the longword that represents the value to branch to, saving their values into a string
            }
            callVal = false;
        }
        return valInBits;
    }

}
