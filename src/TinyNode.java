/**
 * Created by mgorantl on 10/17/17.
 */
public class TinyNode {

    private String opCode;
    private String register1;
    private String register2;

    public TinyNode(String opCode, String register1) {
        this.opCode = opCode;
        this.register1 = register1;
        this.register2 = null;
    }

    public TinyNode(String opCode, String register1, String register2) {
        this.opCode = opCode;
        this.register1 = register1;
        this.register2 = register2;
    }

    public String getOpCode() {
        return this.opCode;
    }

    public String getRegister1() {
        return this.register1;
    }

    public String getRegister2() {
        return this.register2;
    }

    @Override
    public String toString() {

        /* register2 is null for opCode for only Variable declaration 'var' and 'sys halt' */
        if (register2 == null) {
            return this.opCode + " " + this.register1;
        }

        return this.opCode + " " + this.register1 + " " + this.register2;
    }
}
