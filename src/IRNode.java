/**
 * Created by mgorantl on 11/21/17.
 */
public class IRNode {

    private String OpCode;
    private String firstOp;
    private String secondOp;
    private String thirdOp;

    public IRNode(String opCode) {
        this.OpCode = opCode.trim();
        this.firstOp = null;
        this.secondOp = null;
        this.thirdOp = null;
    }

    public IRNode(String opCode, String firstOp) {
        this.OpCode = opCode.trim();
        this.firstOp = firstOp.trim();
        this.secondOp = null;
        this.thirdOp = null;
    }

    public IRNode(String opCode, String firstOp, String secondOp) {
        this.OpCode = opCode.trim();
        this.firstOp = firstOp.trim();
        this.secondOp = secondOp.trim();
        this.thirdOp = null;
    }

    public IRNode(String opCode, String firstOp, String secondOp, String thirdOp) {
        this.OpCode = opCode.trim();
        this.firstOp = firstOp.trim();
        this.secondOp = secondOp.trim();
        this.thirdOp = thirdOp.trim();
    }

    public String getOpCode() {
        if(OpCode == null) {
            return "NULL";
        }
        return OpCode;
    }

    public String getFirstOp() {
        if(firstOp == null) {
            return "NULL";
        }
        return firstOp;
    }

    public String getSecondOp() {
        if(secondOp == null) {
            return "NULL";
        }
        return secondOp;
    }

    public String getThirdOp() {
        if(thirdOp == null) {
            return "NULL";
        }
        return thirdOp;
    }

    @Override
    public String toString() {

        if(firstOp == null) {
            return ";" + " " + this.OpCode;
        }

        if(secondOp == null) {
            return ";" + " " + this.OpCode + " " + this.firstOp;
        }

        if(thirdOp == null) {
            return ";" + " " + this.OpCode + " " + this.firstOp + " " + this.secondOp;
        }

        return ";" + " " + this.OpCode + " " + this.firstOp + " "+ this.secondOp + " "+ this.thirdOp;
    }
}
