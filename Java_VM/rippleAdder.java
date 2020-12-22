public class rippleAdder {

    /**
     * Adds two longwords
     * @param a longword to be added
     * @param b longword to be added
     * @return resulting added longword
     */
    public static longword add(longword a, longword b) {

        if(a.getSigned() > 0 && b.getSigned() < 0 ) { //Deals with a positive a and negative b longword
            if(getMagnitude(a) > getMagnitude(b)) { //If the magnitude of a is larger than b, so the resulting longword must be positive. For example, 56 + -14 will have to yield a positive number
                longword result = new longword();
                bit zeroBit = new bit();
                zeroBit.set(0);
                result = addWords(a,getTwosComplement(b)); //To add a positive binary number to a negative binary number, we need to add it to b's twos complement
                result.setBit(0, zeroBit); //Makes sure the resulting longword is positive by changing the sign bit to 0
                return result;
            }else {
                return addWords(getTwosComplement(a), b); //If b has a larger magnitude than a, add a's twos complement with b For example, 56 + -57 will have to be negative
            }
        }else if(a.getSigned() < 0 && b.getSigned() > 0) { //Deals with a negative a and positive b longword
            if(getMagnitude(a) < getMagnitude(b)) {  //If the magnitude of a is smaller than b, so the resulting longword must be positive. For example, -56 + 57 will have to yield a positive number
                longword result = new longword();
                bit zeroBit = new bit();
                zeroBit.set(0);
                result = addWords(getTwosComplement(a), b); //Gets twos complement of a in order to add it with b
                result.setBit(0, zeroBit); //Makes sure result is positive by setting sign bit to 0
                return result;
            }else {
                return addWords(a, getTwosComplement(b)); //If a has a larger magnitude than b, add b's twos complement with a For example, 56 + -55 will have to be negative
            }

        }else if(a.getSigned() < 0 && b.getSigned() < 0 ) { //Deals with adding two negative numbers
            longword result = new longword();
            bit oneBit = new bit();
            oneBit.set();
            result = addWords(a,b); //Adds the two longwords
            result.setBit(0, oneBit); //Then change sign bit to 1 to represent a negative number since the sum of two negative numbers will always be negative
            return result;
        }
        return addWords(a,b); //Adds two positive numbers

    }

    public static longword subtract(longword a, longword b) {

        if(a.getSigned() > 0 && b.getSigned() > 0 ) { //Deals with a positive a and positive b
            if(getMagnitude(a) < getMagnitude(b)) {  //a has a smaller magnitude than b so the answer has to be negative. example(45 - 50 = -5)
                longword result = new longword();
                bit oneBit = new bit();
                oneBit.set(1);
                result = addWords(getTwosComplement(a),b); //Gets twos complement of a to add it with b, resulting in a minus b
                result.setBit(0, oneBit); //Makes sure result is negative by setting sign bit to 1
                return result;
            }
        }
        if(a.getSigned() > 0 && b.getSigned() < 0 ) { //Deals with positive a and negative b
            longword result = new longword();
            bit zeroBit = new bit();
            zeroBit.set(0);
            result = addWords(a, b);
            result.setBit(0, zeroBit); //Adds the two longwords disregarding their sign, then sets the value to be positive. example(56 - -1 = 57)
            return result;
        }else if(a.getSigned() < 0 && b.getSigned() > 0) { //Deals with negative a and positive b
            if(getMagnitude(a) > getMagnitude(b)) { //If magnitude of a is greater than b then answer must be negative. example(-60 - 30 = -90)
                longword result = new longword();
                bit oneBit = new bit();
                oneBit.set(1);
                result = addWords(a,b);
                result.setBit(0, oneBit); //Adds disregarding their sign then makes sure they are negative
                return result;
            }
        }else if(a.getSigned() < 0 && b.getSigned() < 0 ) { //Deals with negative a and negative b
            if(getMagnitude(a) > getMagnitude(b)) { //If magnitude of a is greater than b then answer must be negative. example(-60 - -30 = -30)
                longword result = new longword();
                bit oneBit = new bit();
                oneBit.set(1);
                result = addWords(a,getTwosComplement(b));
                result.setBit(0, oneBit);
                return result;
            }else { //If magnitude of a is less than b then answer must be positive. example(-30 - -60 = 30)
                longword result = new longword();
                bit zeroBit = new bit();
                zeroBit.set(0);
                result = addWords(getTwosComplement(a),b);
                result.setBit(0, zeroBit);
                return result;
            }
        }

        return addWords(a,getTwosComplement(b)); //Subtracts two positive numbers with a positive result

    }

    /**
     * Finds the twos complement of a longword
     * @param f longword to find twos complement
     * @return twos complement of f
     */
    public static longword getTwosComplement(longword f) {
        longword twosComplement = new longword();
        longword valueOne = new longword();
        valueOne.set(1); //Makes a longword of value one for addition
        twosComplement = f.not(); //Sets twosComplement to NOT of f
        twosComplement = addWords(twosComplement, valueOne); //Then adds one to get the true twos complement
        return twosComplement;
    }

    /**
     * Holds algorithm for adding two longwords
     * @param a longword to be added
     * @param b longword to be added
     * @return added longword
     */
    public static longword addWords(longword a, longword b) {
        longword added = new longword();
        int carryVal = 0;

        for(int i = 31; i > -1; i--) {
            if(a.getBit(i).getValue() + b.getBit(i).getValue() + carryVal == 0 ) { //If the bits of the longwords and the carry value add to 0, then set the resulting longword's bit at index[i] to 0 and carry value to 0
                bit zeroBit = new bit();
                zeroBit.clear();
                added.setBit(i,zeroBit);
                carryVal = 0;
            }else if(a.getBit(i).getValue() + b.getBit(i).getValue() + carryVal == 1 ) { //If the bits of the longwords and the carry value add to 1, then set the resulting longword's bit at index [i] to 1 and carry value to 0
                bit oneBit = new bit();
                oneBit.set();
                added.setBit(i,oneBit);
                carryVal = 0;
            }else if(a.getBit(i).getValue() + b.getBit(i).getValue() + carryVal == 2 ) { //If the bits of the longwords and the carry value add to 2, then set the resulting longword's bit at index [i] to 0 and carry value to 1
                bit zeroBit = new bit();
                zeroBit.clear();
                added.setBit(i,zeroBit);
                carryVal = 1;
            }else if(a.getBit(i).getValue() + b.getBit(i).getValue() + carryVal == 3 ) { //If the bits of the longwords and the carry value add to 3, then set the resulting longword's bit at index[i] to 1 and carry value to 1
                bit oneBit = new bit();
                oneBit.set();
                added.setBit(i,oneBit);
                carryVal = 1;
            }
        }
        return added;
    }
    /**
     * Gets the magnitude of a longword without sign bit. For example, getMagnitude(-17) is 17 and getMagnitude(16) is 16
     * This is used in determining what the sign of the resulting longword should be when adding and subtracting
     * @param t
     * @return
     */
    public static int getMagnitude(longword t) {
        double sum = 0;
        double power = 30;
        for(double h = 1; h <32; h++) { //Loops through the word[] array starting at the second element since the first is the sign bit
            if(t.getBit((int) h).getValue() == 1 ) {
                sum = sum + Math.pow(2, power); //If a bit of 1 is encountered, sum equals sum plus 2 to the power of what position the bit of one is in
            }
            power--;
        }
        return (int) sum;
    }
}
