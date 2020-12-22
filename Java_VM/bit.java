/**
 * Tomasz Jablonski 8/31/2020
 * bit implements the IBIt interface
 */
public class bit implements IBit{



    private int bitValue; // holds 0 or 1

    @Override
    /**
     * Sets the value of bitValue
     * Only allows for "0" and "1" to be used in setting the value of the bit otherwise, end program.
     */
    public void set(int value) {
        if (value == 1) {
            this.bitValue = value;
        }else if (value == 0){
            this.bitValue = value;
        }else{
            System.out.println("Bit set to invalid value.");
            System.exit(0); //If a bit is set to a value other than "0" or "1", the program terminates
        }
    }

    @Override
    /**
     * Switches bitValue to the opposite value ( 0 or 1 )
     */
    public void toggle() {
        if (this.bitValue == 1) {
            this.bitValue = 0;
        }else if(this.bitValue == 0){
            this.bitValue = 1;
        }
    }

    @Override
    /**
     * Sets the value of bitValue to "1"
     */
    public void set() {
        this.bitValue = 1;
    }

    @Override
    /**
     * Sets the value of bitValue to "0"
     */
    public void clear() {
        this.bitValue = 0;
    }

    @Override
    /**
     * Returns the value of bitValue
     */
    public int getValue() {
        return this.bitValue;
    }

    @Override
    /**
     *Executes the "AND" operation on two bits and returns the result as a new bit
     */
    public bit and(bit other) {
        bit aBit = new bit(); //creates new bit
        if (this.bitValue == 1) {
            if (other.getValue() == 1) {
                aBit.set(); //sets the bitValue of the new bit to "1" if both bits are "1"
            }
        } else {
            aBit.clear(); //Otherwise the value of the new bit is "0"
        }
        return aBit;
    }

    @Override
    /**
     *Executes the "OR" operation on two bits and returns the result as a new bit
     *Sets the new bit to "1" if there is at least one bit with the value "1"
     */
    public bit or(bit other) {
        bit nBit = new bit();//creates new bit
        if(this.bitValue == 1) {
            nBit.set();//if the value of the first bit is "1", set the new bit to "1"
        }else{
            if(other.getValue() == 1){
                nBit.set();//if the value of the other bit is "1" set the new bit to "1"
            }else{
                nBit.clear();//if both bits are "0" set the value of the new bit to "0"
            }
        }
        return nBit;
    }

    @Override
    /**
     * Performs "XOR" on two bits and returns the result as a new bit
     */
    public bit xor(bit other) {
        bit gBit = new bit();//creates new bit
        if(this.bitValue != other.getValue()){
            gBit.set();//if the values of both bits are not equal, set the new bit to "1"
        }else{
            gBit.clear();//if the values of both bits are equal, set the new bit to "0"
        }
        return gBit;
    }

    @Override
    /**
     * Performs "NOT" on one bit and returns the result as a new bit
     */
    public bit not() {
        bit hBit = new bit();
        if(this.bitValue == 0){
            hBit.set();//if the value of the bit is "0", set the value of the new bit to "1"
        }else{
            hBit.clear();//if the value of the bit is "1", set the value of the new bit to "0"
        }
        return hBit;
    }

    @Override
    /**
     * Returns the value of bitValue as a String
     */
    public String toString() {
        return Integer.toString(this.bitValue);
    }
}
