
public class ALU {


    public static longword doOp(bit first, bit second, bit third, bit fourth, longword a, longword b) {

        longword opResult = new longword();

        //The series of if and else if statements matches the sequence of bits passed into the argument to their operation
        if (first.getValue() == 1 && second.getValue() == 0 && third.getValue() == 0 && fourth.getValue() == 0) {

            opResult = a.and(b);

        } else if (first.getValue() == 1 && second.getValue() == 0 && third.getValue() == 0 && fourth.getValue() == 1) {

            opResult = a.or(b);

        } else if (first.getValue() == 1 && second.getValue() == 0 && third.getValue() == 1 && fourth.getValue() == 0) {

            opResult = a.xor(b);

        } else if (first.getValue() == 1 && second.getValue() == 0 && third.getValue() == 1 && fourth.getValue() == 1) {

            opResult = a.not();

        } else if (first.getValue() == 1 && second.getValue() == 1 && third.getValue() == 0 && fourth.getValue() == 0) {

            opResult = a.leftShift(b.getSigned());

        } else if (first.getValue() == 1 && second.getValue() == 1 && third.getValue() == 0 && fourth.getValue() == 1) {

            opResult = a.rightShift(b.getSigned());

        } else if (first.getValue() == 1 && second.getValue() == 1 && third.getValue() == 1 && fourth.getValue() == 0) {

            opResult = rippleAdder.add(a, b);

        } else if (first.getValue() == 1 && second.getValue() == 1 && third.getValue() == 1 && fourth.getValue() == 1) {

            opResult = rippleAdder.subtract(a, b);

        } else if (first.getValue() == 0 && second.getValue() == 1 && third.getValue() == 1 && fourth.getValue() == 1) {

            opResult = multiplier.multiply(a, b);

        }else{ //Deals with an invalid sequence of bits that does not match an operation, For example 0011
            System.out.println("Invalid operation for bit inputs!");
            System.exit(0); //Terminates program if sequence of bits is not a valid operation
        }

        return opResult;

    }

}
