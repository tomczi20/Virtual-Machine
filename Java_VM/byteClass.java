/**
 * Class represents the byte of memory. Each byte contains an array of 8 bits
 */
public class byteClass {

    private bit[] bits = new bit[8];

    public byteClass(){
        bit spaceBit = new bit();
        for(int h = 0; h<8; h++){
            bits[h] = spaceBit;
        }
    }

    public void setByteBit(int index, bit g){
        bits[index] = g;
    }

    public bit getByteBit(int location){
        return bits[location];
    }

}
