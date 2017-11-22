import java.util.ArrayList;

/**
 * Created by Mahesh Babu Gorantla (mgorantl) & Rahul Patni (rpatni) on Nov 21, 2017.
 */

public class IRNodeList {

    private ArrayList<IRNode> irNodeList;
    private ArrayList<CustomNode> customNodes;

    public IRNodeList() {
        irNodeList = new ArrayList<IRNode>();
        customNodes = new ArrayList<CustomNode>();
    }

    public void addNode(IRNode irNode) {
        irNodeList.add(irNode);
    }

    public void createCustomNodeList() {
        for (int i = 0; i < irNodeList.size(); i++) {
            addCustomNode(irNodeList.get(i), i);
        }
    }

    public ArrayList<IRNode> getIrNodeList() {
        return irNodeList;
    }

    private void addCustomNode(IRNode irNode, int index) {

        int maxIndex = irNodeList.size();

        if(checkIfCompOp(irNode.getOpCode().trim())) { // {IF/ELSE, FOR} (Conditional)

            int tempIndex = index + 1; // Start searching bottom up

            while(tempIndex < maxIndex) {

                IRNode irNode1 = irNodeList.get(tempIndex);

                // Setting the Second Successor for the Conditional Jump IR Nodes
                if(irNode1.getOpCode().equals("LABEL") && irNode.getThirdOp().equals(irNode1.getFirstOp())) {
                    customNodes.add(new CustomNode(index, irNode, index + 1, tempIndex, index - 1)); // Index, Content, Succ1, Succ2, Pred
                    break;
                }
                tempIndex += 1;
            }
        }
        else if(irNode.getOpCode().trim().equals("JUMP")) { // {JUMP} (UnConditional)

            int tempIndex = 0;

            while(tempIndex < maxIndex) {

                IRNode irNode1 = irNodeList.get(tempIndex);

                if(irNode1.getOpCode().equals("LABEL") && irNode1.getFirstOp().equals(irNode.getFirstOp().trim())) {
                    customNodes.add(new CustomNode(index, irNode, tempIndex, -1, index - 1));
                    break;
                }
                tempIndex += 1;
            }
        }
        else if(irNode.getOpCode().equals("JSR")) { // JSR (Function Call)
            customNodes.add(new CustomNode(index, irNode, index + 1, -1, index - 1));
        }
        // Handle RET/HALT & PUSH/POP (With no firstOp)
        else if(irNode.getOpCode().equals("RET") || irNode.getOpCode().equals("HALT")) {
            customNodes.add(new CustomNode(index, irNode, -1, -1, index - 1));
        }
        else { // Else
            customNodes.add(new CustomNode(index, irNode, index + 1, -1, index - 1));
        }
        /*        else if ((irNode.getOpCode().equals("PUSH") && irNode.getFirstOp().equals("NULL"))
                || (irNode.getOpCode().equals("POP") && irNode.getFirstOp().equals("NULL"))) {


        }*/
    }

    public void printCustomNodeList() {

        for(CustomNode customNode: customNodes) {
            System.out.println(customNode.toString());
        }
    }

    public void printIRNodeList() {
        for(IRNode irNode: irNodeList) {
            System.out.println(irNode.toString());
        }
    }

    private boolean checkIfCompOp(String compOp) {

        switch (compOp) {

            case "LT":
                return true;

            case "GT":
                return true;

            case "LE":
                return true;

            case "GE":
                return true;

            case "EQ":
                return true;

            case "NE":
                return true;
        }

        return false;
    }

    private class CustomNode {

        int index;
        IRNode irNode;
        int successor1;
        int successor2;
        int predecessor;

        public CustomNode(int index, IRNode irNode, int successor1, int predecessor) {
            this.index = index;
            this.irNode = irNode;
            this.successor1 = successor1; // When it is RET == -1
            this.predecessor = predecessor;
        }

        public CustomNode(int index, IRNode irNode, int successor1, int successor2, int predecessor) {
            this.index = index;
            this.irNode = irNode;
            this.successor1 = successor1;
            this.successor2 = successor2;
            this.predecessor = predecessor;
        }

        @Override
        public String toString() {
            return "; " + Integer.toString(index) + " " + irNode.toString() + " " + Integer.toString(successor1)
                    + " " + Integer.toString(successor2) + " " + Integer.toString(predecessor);
        }
    }
}
