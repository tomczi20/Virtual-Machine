public class multiplier {

    public static longword multiply(longword a, longword b) {
        longword aClone = new longword();
        aClone.copy(a);
        bit zeroBit = new bit();
        zeroBit.clear();
        bit oneBit = new bit();
        oneBit.set(1);   //Creates bits of value zero and one to use in setting the correct sign bit of the multiplied number
        aClone.setBit(0,zeroBit); //Sets the sign bit of the copy of longword a to zero in order to ensure that it gets multiplied correctly
        longword result = new longword();
        result.set(0); //result is set to 0, so that if longword a or b is zero and nothing is being added, the result will be zero.
        int shiftCount = 0;

        for(int k = 31; k > 0; k--){ //loop iterates over the bits in longword b and whenever it encounters a bit of value 1, it will shift longword a by shiftCount and add it to the result
            if(b.getBit(k).getValue() == 1){
                result = rippleAdder.add(result,aClone.leftShift(shiftCount)); //result is set to result plus a copy of longword a shifted to the left by shiftCount
                aClone.copy(a); //Resets longword aClone to longword a, so that it can be shifted by the appropriate amount on the next iteration
            }
            shiftCount++;
        }

        if(a.getSigned() < 0 && b.getSigned() > 0 || b.getSigned() < 0 && a.getSigned() > 0){
            result.setBit(0,oneBit); //If a and b have different signs, then their product must be negative so set sign bit to 1
        }else{
            result.setBit(0,zeroBit); //If a and b have the same sign their product must be positive so set sign bit to 0
        }
        return result;
    }
}
