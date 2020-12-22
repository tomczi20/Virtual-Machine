public class memory {

    private byteClass[] mem = new byteClass[1024];

    public memory(){ //Constructor Fills byte array of memory with empty bytes to avoid null pointer exception
        for(int b = 0; b < 1024; b++){
            byteClass a = new byteClass();
            mem[b] = a;
        }
    }

    public longword read(longword address){
        if(address.getSigned() > 1023 || address.getSigned() < 0 ){ //Terminates program if invalid address is passed
            System.out.println("Invalid address to read from!");
            System.exit(0);
        }
        longword readWord = new longword();
        int index = address.getSigned();
        int end = index + 4; //end is the index of where the last byte is to store all 32 bits of longword
        int pos = 0;
        while(index < end && index != 1024){ //ensures the proper amount of Bytes are read (no longer than 4 bytes)
            for (int g = 0; g < 8; g++) { //Repeats over the 8 bits in every of the 4 bytes being read
                readWord.setBit(pos, mem[index].getByteBit(g)); //Sets the resultant bit of the longword to the proper position from one of the bytes bits.
                pos++;
            }
            index++;
        }
        return readWord;
    }

    public void write(longword address, longword value){
        if(address.getSigned() > 1020 || address.getSigned() < 0){ //Terminates program if invalid address is passed
            System.out.println("Invalid address to write value to.");
            System.exit(0);
        }
        int index = address.getSigned();
        int end = index + 4; //end is the index of where the last byte is to store all 32 bits of longword
        int pos = 0;
        while(index < end){ //Repeats over no more than 4 bytes in the array
            for (int g = 0; g < 8; g++) {
                mem[index].setByteBit(g, value.getBit(pos)); //Sets 8 bits of each byte to the to the bits that match longword
                pos++;
            }
            index++;
        }
    }

    public void printBytes(){
        longword loc = new longword();
        longword value4 = new longword();
        value4.set(4); //used in incrementing loc
        while(loc.getSigned() < 1024){ //Keeps loc in bounds of memory
            System.out.println(read(loc).toString());
            loc.copy(rippleAdder.add(loc, value4)); //since one byte contains 8 bits, increment longword loc by 4 to read the next 32 bits in memory
        }

    }

}



