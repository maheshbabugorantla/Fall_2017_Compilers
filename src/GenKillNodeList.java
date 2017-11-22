import java.util.ArrayList;

/**
 * Created by Mahesh Babu Gorantla (mgorantl) & Rahul Patni (rpatni) on Nov 21, 2017.
 */
public class GenKillNodeList {

    private ArrayList<GenKillNode> genKillNodeList;
    private IRNodeList irNodeList;

    public GenKillNodeList(IRNodeList irNodeList) {
        this.irNodeList = irNodeList;
        this.genKillNodeList = new ArrayList<GenKillNode>();
    }

    public void genKillNodeListInit(String[] globalVariables) {
        int i = 0;
        for (IRNode irNode : irNodeList.getIrNodeList()) {
            String opCode = irNode.getOpCode().trim();
            String firstOp = irNode.getFirstOp().trim();
            String secondOp = irNode.getSecondOp().trim();
            String thirdOp = irNode.getThirdOp().trim();

            GenKillNode currNode = new GenKillNode(i);


            if (opCode.equals("PUSH")) {
                if (!firstOp.equals("NULL")) {
                    currNode.addToGenSet(firstOp);
                }
            }
            else if (opCode.equals("POP")) {
                if (!firstOp.equals("NULL")) {
                    currNode.addToGenSet(firstOp);
                }
            }
            else if (opCode.startsWith("WRITE")) {
                currNode.addToGenSet(firstOp);
            }
            else if (opCode.startsWith("READ")) {
                currNode.addToKillSet(firstOp);
            }
            else if (opCode.equals("LABEL")) {

            }
            else if(opCode.equals("JSR")) {
                for(String var : globalVariables) {
                    currNode.addToGenSet(var);
                }
            }
            else if (opCode.startsWith("STORE")) {
                currNode.addToGenSet(firstOp);
                currNode.addToKillSet(secondOp);
            }
            else if (opCode.startsWith("ADD") || opCode.startsWith("SUB") ||
                    opCode.startsWith("MULT") || opCode.startsWith("DIV")) {
                //TODO: ADD $2 !T6 !T6
                currNode.addToGenSet(firstOp);
                currNode.addToGenSet(secondOp);
                currNode.addToKillSet(thirdOp);
            }
            else if (opCode.startsWith("LE") || opCode.startsWith("LT") ||
                    opCode.startsWith("GE") || opCode.startsWith("GT") ||
                    opCode.startsWith("EQ") || opCode.startsWith("NE")) {
                currNode.addToGenSet(firstOp);
                currNode.addToGenSet(secondOp);
            }
            else {
                // UNLINK, RET, HALT, LINK
            }

            genKillNodeList.add(currNode);

            i+= 1;
        }
    }

    public void printGenKillNodeList() {
        int i = 0;
        for (GenKillNode genKillNode : genKillNodeList) {
            System.out.println(irNodeList.getIrNodeList().get(i++).toString() + " => "  + genKillNode.toString());
        }
    }

}
