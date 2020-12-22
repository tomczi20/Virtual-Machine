import java.util.Arrays;

public class longword implements ILongword{

    private bit[] word = new bit[32];


    public longword(){
        for(int count = 0; count != 32; count++) {
            bit zeroBit = new bit();
            zeroBit.clear();
            word[count] = zeroBit;
        }
    }
    /**
     * Returns the bit at the given index
     */
    public bit getBit(int i) {
        if(i > 31 || i < 0) { //Checks for an input that could be outside the array
            System.out.println("Not a valid index");
            System.exit(0); //Program terminates if an invalid input is given
        }
        return word[i];
    }

    /**
     * Sets the Bit at a specific index
     */
    public void setBit(int i, bit value) {
        if(i > 31 || i < 0) { //Checks for an input that could be outside the array
            System.out.println("Not a valid index");
            System.exit(0); //Program terminates if an invalid input is given
        }
        word[i].set(value.getValue());
    }

    /**
     * And's two longwords and returns another longword set to the result
     */
    public longword and(longword other) {
        longword anded = new longword();
        for(int count = 0; count != 32; count++) { //Loop is used to loop through the arrays of the longwords being And'ed
            anded.setBit(count, this.getBit(count).and(other.getBit(count))); //Performs the AND operation on two bits of a given index (count) from the longwords being anded. The result is then set to the same index in the longword being returned.

        }
        return anded;
    }

    /**
     * Or's two longwords and returns another longword set to the result
     */
    public longword or(longword other) {
        longword ored = new longword();
        for(int count = 0; count != 32; count++) { //Loop is used to loop through the arrays of the longwords being Or'ed
            ored.setBit(count, this.getBit(count).or(other.getBit(count))); //Performs the OR operation on two bits of a given index (count) from the longwords being anded. The result is then set to the same index in the longword being returned.

        }
        return ored;
    }

    /**
     * Xor's two longwords and returns another longword set to the rsult
     */
    public longword xor(longword other) {
        longword xored = new longword();
        for(int count = 0; count != 32; count++) { //Loop is used to loop through the arrays of the longwords being XOr'ed
            xored.setBit(count, this.getBit(count).xor(other.getBit(count))); //Performs the XOR operation on two bits of a given index (count) from the longwords being anded. The result is then set to the same index in the longword being returned.

        }
        return xored;
    }

    /**
     * NOT's a longword and returns another set to the result
     */
    public longword not() {
        longword noted = new longword();
        for(int count = 0; count != 32; count++) {
            noted.setBit(count, this.getBit(count).not()); //Loops through the longword negating every bit as it goes, then sets that bit into the resulting longword

        }
        return noted;
    }

    /**
     * Right shifts a longword by (amount)
     */
    public longword rightShift(int amount) {
        longword rightShifted = new longword();
        if(amount < 0) { // If a negative number is entered stop, since it is the same as right shifting by that number
            int h = amount;
            h = h*-1;
            leftShift(h);
        }else if(amount == 0) { //If 0 is entered, then copy the same set of bits into the result longword since nothing is being shifted
            for(int s = 0; s < 32; s++) {
                rightShifted.setBit(s, word[s]);
            }
        }else {
            bit tempBit = new bit(); //Creates a bit with value 0
            tempBit.clear();

            longword tempWord = new longword();
            for(int s = 0; s < 32; s++) { //Copies this longword into a temporary longword
                tempWord.setBit(s, word[s]);
            }
            while(amount != 0) { //Repeats for the amount of bits to be shifted
                int index = 31;
                while(index !=0) { //Copies the bit from the original longword into one place ahead in the result longword(rightshift) For example, word[12] from the temporary longword becomes word[13] in the shifted longword
                    rightShifted.setBit(index, tempWord.getBit(index-1));

                    index--;
                }
                if(index == 0) {
                    rightShifted.setBit(0, tempBit); //After this, the bit at index [0] is null, so set this to a bit with value 0
                }
                for(int l = 0; l < 32; l++) {
                    tempWord.setBit(l, rightShifted.getBit(l)); // longword tempWord is then set to the right shifted longword so that the process can happen again if shifting more than one bit
                }
                amount--;
            }
        }
        return rightShifted;
    }

    public longword leftShift(int amount) {
        longword leftShifted = new longword();
        if(amount < 0) { // If a negative number is entered stop, since it is the same as left shifting by that number
            int h = amount;
            h = h*-1;
            rightShift(h);
        }else if(amount == 0) { //If 0 is entered, then copy the same set of bits into the result longword since nothing is being shifted
            for(int s = 0; s < 32; s++) {
                leftShifted.setBit(s, word[s]);
            }
        }else {
            bit tempBit = new bit();
            tempBit.clear();
            longword tempWord = new longword(); //Copies this longword into a temporary longword
            for(int s = 0; s < 32; s++) {
                tempWord.setBit(s, word[s]);
            }
            while(amount != 0) { //Repeats for the amount of bits to be shifted
                int index = 0;
                while(index !=31) { //Copies the bit from the original longword into one place behind in the result longword(rightshift) For example, word[17] from the temporary longword becomes word[16] in the shifted longword
                    leftShifted.setBit(index, tempWord.getBit(index+1));
                    index++;
                }
                if(index == 31 ) {
                    leftShifted.setBit(31, tempBit); //After this, the bit at index [31] is null, so set this to a bit with value 0
                }
                for(int l = 31; l > -1; l--) {
                    tempWord.setBit(l, leftShifted.getBit(l)); // longword tempWord is then set to the right shifted longword so that the process can happen again if shifting more than one bit
                }
                amount--;
            }
        }
        return leftShifted;
    }

    /**
     * Returns the value of the longword as an unsigned Long
     */
    public long getUnsigned() {
        double sum = 0;
        double power = 31;
        for(double h = 0; h <32; h++) { //Loops through the word[] array starting at the first position
            if(word[(int) h].getValue() == 1 ) { //If a bit of 1 is encountered, sum equals sum plus 2 to the power of what position the bit of one is in.
                //For Example, if the first bit is a 1 sum equals 2^31, if the third bit is 1, sum equals sum + 2^29
                sum = sum + Math.pow(2, power);
            }
            power--;
        }
        return (long) sum;
    }
    /**
     * Returns the value of the longword as a signed integer
     */
    public int getSigned() {
        double sum = 0;
        double power = 30;
        for(double h = 1; h <32; h++) { //Loops through the word[] array starting at the second element since the first is the sign
            if(word[(int) h].getValue() == 1 ) { //If a bit of 1 is encountered, sum equals sum plus 2 to the power of what position the bit of one is in
                sum = sum + Math.pow(2, power);
            }
            power--;
        }
        if(word[0].getValue() == 1) { //If the sign bit is 1, negate the result
            sum = sum * -1;
        }
        return (int) sum;
    }

    /**
     * Copies the bits from one longword into another
     */
    public void copy(longword other) {
        for(int s = 0; s < 32; s++) { //Loops through the (other) longword copying elements into this longword
            word[s].set(other.getBit(s).getValue());
        }

    }

    /**
     * Sets the value of the bits in this longword
     */
    public void set(int value) {
        Arrays.fill(word, null);//clears the array
        if(value >= 0){ //converts a positive integer to binary
            int i = 31;
            while (value > 0){ // this loop Divides the value by 2 until it is 0, and if there is a remainder, set the appropriate bit to 1, otherwise, set the bit to null, then repeat
                bit h = new bit();
                h.set(value % 2);
                word[i] = h;
                value = value / 2;
                i--;
            }
            for(int g = 0; g < 32; g++) { //this loop sets the remaining null bits in the array to 0
                if(word[g] == null) {
                    bit j = new bit();
                    j.clear();
                    word[g] = j;
                }
            }
        }else{ //converts a negative integer to binary
            value = value * -1; // Converts the negative number to positive and repeats the same steps above^
            int i = 31;
            while (value > 0){
                bit h = new bit();
                h.set(value % 2);
                word[i] = h;
                value = value / 2;
                i--;
            }
            for(int g = 0; g < 32; g++) {
                if(word[g] == null) {
                    bit j = new bit();
                    j.clear();
                    word[g] = j;
                }
            }
            bit signBit = new bit();
            signBit.set();
            word[0] = signBit; //adds "1" as the sign bit to signify a negative number

        }


    }
    /**
     * Returns the value of the bits in the longword as an array
     */
    public String toString() {

        return Arrays.toString(word);

    }
}
