import org.antlr.v4.runtime.misc.NotNull;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Exception;

/**
 * This Listener Class implements the MicroBaseListener Interface to detect the path of the Parser Tree
 * */

public class Micro468Listener extends MicroBaseListener {

    private SymbolsTree parentTree;
    private int blockNumber;
    private int operationNumber;
    private int labelNumber;
    private static int tinyRegisterNumber;
    private SymbolsTable currentScope; // Used to refer to which functio is being parsed
    private static ArrayList<TinyNode> tinyNodeArrayList = new ArrayList<TinyNode>();
    private Stack<String> labelStack = new Stack<String>();

    private boolean condFlag;

    private String incr_stmt;

    private boolean elsePresent; // An Indicator to find if there is an else following the If Statement

    /**
     * Constructor to detect the Parser Actions.
     * REMEMBER: This gets called only once.
     * */
    public Micro468Listener() {
        this.parentTree = new SymbolsTree(); // Initializing the Symbol Tree to the GLOBAL Scope
        this.blockNumber = 1;
        operationNumber = 1;
        labelNumber = 1;
        condFlag = false;
        tinyRegisterNumber = 0;
        incr_stmt = "";
        elsePresent = false;
    }

    private String getBlockNumber() {
        return "BLOCK " + blockNumber++;
    }

    /**
     * This is a helper method to print all the tinyNodes
     * */
    private void printTinyNodeList(ArrayList<TinyNode> tinyNodeArrayList) {

        for(TinyNode tinyNode: tinyNodeArrayList) {
            System.out.println(tinyNode.toString());
        }
    }

    /**
     * This is used to push the Symbols to the Appropriate SymbolsTable
     * */
    public void pushSymbol(String parameters, SymbolsTable symbolsTable) {

       // test1.micro Where there are no Global and Local Variables/Symbols
       if(parameters == null || parameters.equals("")) {
            return;
       }
       else {

           String[] newVariableTypes = parameters.split(";");

           for(String dataType: newVariableTypes) {

               // Check for the Symbol DataType
               // INT
               if (dataType.startsWith("INT")) {
                   String[] variableNames = dataType.substring(3).split(",");

                   // Adding all the Variable Names
                   for(String variable: variableNames) {
                       tinyNodeArrayList.add(new TinyNode("var", variable));
                       Symbol newVariable = new Symbol(variable, "INT");
                       symbolsTable.addSymbol(newVariable);
                   }
               }

               // FLOAT
               else if (dataType.startsWith("FLOAT")) {
                   String[] variableNames = dataType.substring(5).split(",");

                   // Adding all the Variable Names
                   for(String variable: variableNames) {
                       tinyNodeArrayList.add(new TinyNode("var", variable));
                       Symbol newVariable = new Symbol(variable, "FLOAT");
                       symbolsTable.addSymbol(newVariable);

                   }
               }

               // STRING
               else if (dataType.startsWith("STRING")) {
                   String[] variableNames = dataType.substring(6).trim().split(":=");
                   tinyNodeArrayList.add(new TinyNode("str", variableNames[0].trim(), variableNames[1].trim()));
                   symbolsTable.addSymbol(new Symbol("STRING", variableNames[0].trim(), variableNames[1].trim()));
               }
           }
       }
   }

    public void readFunctionParameters(String parameters, SymbolsTable symbolsTable) {

       if(parameters == null || parameters.equals("")) {
           return;
       }

       // Only INT and FLOAT are the datatypes of the Function Parameters
       String[] functionParameters = parameters.split(",");

       for(String parameter: functionParameters) {

           // INT
           if(parameter.startsWith("INT")) {
                symbolsTable.addSymbol(new Symbol(parameter.substring(3), "INT"));
           }

           // FLOAT
           else if(parameter.startsWith("FLOAT")) {
               symbolsTable.addSymbol(new Symbol(parameter.substring(5), "FLOAT"));
           }
       }
    }

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext ctx) { // ParentScope or GLOBAL
        // Pushing the Symbols to the GLOBAL Symbol Table
        System.out.println(";IR code");
        pushSymbol(ctx.getChild(0).getText(), parentTree.getParentScope());
    }

    @Override public void enterRead_stmt(MicroParser.Read_stmtContext ctx) {
        String right = ctx.getChild(2).getText();
        String[] words = right.split(",");
        //System.out.println("when reading: " + String.join(" ", words));

        for (int i = 0; i < words.length; i++) {
            String currentType = parentTree.getCurrentScope().variableMap.get(words[i])[0];

            if (currentType == "INT") {
                System.out.println(";READI " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys readi", words[i]));
            }
            else if (currentType == "FLOAT") {
                System.out.println(";READF " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys readf", words[i]));
            }
            else {
                System.out.println(";READS " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys reads", words[i]));
            }
        }
    }



    @Override public void enterWrite_stmt(MicroParser.Write_stmtContext ctx) {
        String right = ctx.getChild(2).getText();
        String[] words = right.split(",");
        //System.out.println("when writing: " + String.join(" ", words));

        for (int i = 0; i < words.length; i++) {
            String currentType = parentTree.getCurrentScope().variableMap.get(words[i])[0];

            if (currentType == "INT") {
                System.out.println(";WRITEI " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys", "writei", words[i]));
            }
            else if (currentType == "FLOAT") {
                System.out.println(";WRITEF " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys", "writer", words[i]));
            }
            else {
                System.out.println(";WRITES " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys", "writes", words[i]));
            }
        }
    }


    /**
     * This gets called when the parser finds an assignment statement
     * */
    @Override
    public void enterAssign_stmt(MicroParser.Assign_stmtContext ctx) {
        String left = ctx.getChild(0).getChild(0).getText();
        String right = ctx.getChild(0).getChild(2).getText();

        String postfix = InfixToPostfix.infixToPostfix(right);

        parsePostfix(right, left, postfix);
    }

    private static boolean isOperator(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("(") || c.equals(")");
    }

    private void parsePostfix(String right, String left, String postfix) {
        //System.out.println("Parse Postfix");

        Stack<String> stack = new Stack<String>();

        String[] words = postfix.split(" ");
        //System.out.println("split: " + Arrays.toString(words));
        if (words.length == 1) {
            parseAssign_stmt(left, right);
            return;
        }

        //stack.push(words[0]);
        //stack.push(words[1]);

        for (int i = 0; i < words.length; i++) {
            String c = words[i];

            boolean foundNumber = false;

            if (isNumeric((c))) {
                foundNumber = true;
                String location = "$T" + this.operationNumber;
                this.operationNumber += 1;

                if (isInteger(c)) {
                    //parentTree.getCurrentScope().addRegister(location, "INT");
                    parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    System.out.println(";STOREI " + c + " " + location);
                    add_reg_operation_stmt_2("move", c, location);
                }
                else {
                    //parentTree.getCurrentScope().addRegister(location, "FLOAT");
                    parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    System.out.println(";STOREF " + c + " " + location);
                    add_reg_operation_stmt_2("move", c, location);
                }
            }

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "$T" + this.operationNumber;
                this.operationNumber += 1;

                String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                //parentTree.getCurrentScope().addRegister(location, currentType);
                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else if (!foundNumber){
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            String c = stack.pop();

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "$T" + this.operationNumber;
                this.operationNumber += 1;

                String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                //parentTree.getCurrentScope().addRegister(location, currentType);
                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                tinyRegisterNumber = this.operationNumber - 2;


                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else {
                String currentType = parentTree.getCurrentScope().variableMap.get(c)[0];

                if (currentType.equals("INT")) {
                    System.out.println(";STOREI " + " " + c + " " + left);
                    add_reg_operation_stmt_2("move", c, left);
                }
                else {
                    System.out.println(";STOREF " + " " + c + " " + left);
                    add_reg_operation_stmt_2("move", c, left);
                }
            }
        }
    }

    private void choose_operation(String symbol, String type, String str1, String str2, String location) {
        if (symbol.equals("+")) {
            operation_stmt("ADD", type, str1, str2, location);
            reg_operation_stmt("add", type, str1, str2, location);
        }
        else if (symbol.equals("*")) {
            operation_stmt("MULT", type, str1, str2, location);
            reg_operation_stmt("mul", type, str1, str2, location);
        }
        else if (symbol.equals("/")) {
            operation_stmt("DIV", type, str1, str2, location);
            reg_operation_stmt("div", type, str1, str2, location);
        }
        else if (symbol.equals("-")) {
            operation_stmt("SUB", type, str1, str2, location);
            reg_operation_stmt("sub", type, str1, str2, location);
        }
        else {
            System.out.println("None of the symbols...");
        }
    }

    private void add_reg_operation_stmt(String operation, String str1, String location) {
        if (parentTree.getCurrentScope().variableMap.get(str1)[1] == null) {
            tinyNodeArrayList.add(new TinyNode(operation, str1, parentTree.getCurrentScope().variableMap.get(location)[1]));
        }
        else {
            tinyNodeArrayList.add(new TinyNode(operation, parentTree.getCurrentScope().variableMap.get(str1)[1],
                    parentTree.getCurrentScope().variableMap.get(location)[1]));
        }
    }

    private void reg_operation_stmt(String operation, String type, String str1, String str2, String location) {
        add_reg_operation_stmt("move", str1, location);
        if (type.equals("INT")) {
            add_reg_operation_stmt(operation + "i", str2, location);
        }
        else {
            add_reg_operation_stmt(operation + "r", str2, location);
        }
    }

    private static void registerOperation_stmt(String operation, String type, String str1, String str2) {

        if (type.equals("INT")) {

            switch (operation) {

                case "ADD":
                    // Tiny code for Add Operation

                    break;

                case "SUB":
                    // Tiny code for Subtract Operation
                    break;

                case "MULT":
                    // Tiny code for Multiply Operation
                    break;

                case "DIV":
                    // Tiny code for Division Operation
                    break;
            }
        }
        else { // FLOAT

            switch (operation) {

                case "ADD":
                    // Tiny code for Add Operation
                    break;

                case "SUB":
                    // Tiny code for Subtract Operation
                    break;

                case "MULT":
                    // Tiny code for Multiply Operation
                    break;

                case "DIV":
                    // Tiny code for Division Operation
                    break;
            }
        }
    }

    private static void operation_stmt(String operation, String type, String str1, String str2, String location) {
        if (type.equals("INT")) {
            System.out.println(";" + operation + "I " + str1 + " " + str2 + " " + location);
        }
        else {
            System.out.println(";" + operation + "F " + str1 + " " + str2 + " " + location);
        }
    }

    private void parseAssign_stmt(String left, String right) {
        if (isNumeric(right) == false) {
            if (parentTree.getCurrentScope().variableMap.containsKey(left) && parentTree.getCurrentScope().variableMap.containsKey(right)) {
                String dataType = parentTree.getCurrentScope().variableMap.get(left)[0];
                if (dataType.equals("INT")) {
                    System.out.println(";STOREI " + right + " " + left);
                    //tinyNodeArrayList.add(new TinyNode("STOREI", left, right));
                }
                else {
                    System.out.println(";STOREF " + right + " " + left);
                    //tinyNodeArrayList.add(new TinyNode("STOREF", left, right));
                }
            }
            return;
        }

        if (isInteger(right)) {
            String location = "$T" + this.operationNumber;
            this.operationNumber += 1;

            System.out.println(";STOREI " + right + " " + location);
            parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
            //tinyNodeArrayList.add(new TinyNode("move", right, "r" + Integer.toString(tinyRegisterNumber)));
            //add_reg_operation_stmt("move", right, location);
            System.out.println(";STOREI " + location + " " + left);
            //tinyNodeArrayList.add(new TinyNode("move", "r" + Integer.toString(tinyRegisterNumber), left));
            //add_reg_operation_stmt("move", location, left);
            tinyRegisterNumber += 1;
            add_reg_operation_stmt_2("move", right, location);
            add_reg_operation_stmt_2("move", location, left);
        }
        else {
            String location = "$T" + this.operationNumber;
            this.operationNumber += 1;

            System.out.println(";STOREF " + right + " " + location);
            parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
            //tinyNodeArrayList.add(new TinyNode("move", right, "r" + Integer.toString(tinyRegisterNumber)));
            //add_reg_operation_stmt("move", right, location);
            System.out.println(";STOREF " + location + " " + left);
            //tinyNodeArrayList.add(new TinyNode("move", "r" + Integer.toString(tinyRegisterNumber), left));
            //add_reg_operation_stmt("move", location, left);
            tinyRegisterNumber += 1;
            add_reg_operation_stmt_2("move", right, location);
            add_reg_operation_stmt_2("move", location, left);
        }
    }


    private void add_reg_operation_stmt_2(String operation, String str1, String location) {
        String check1 = str1;
        String check2 = location;
        if (parentTree.getCurrentScope().variableMap.containsKey(check1)) {
            if (parentTree.getCurrentScope().variableMap.get(check1)[1] != null) {
               check1 = parentTree.getCurrentScope().variableMap.get(check1)[1];
            }
        }
        if (parentTree.getCurrentScope().variableMap.containsKey(check2)) {
            if (parentTree.getCurrentScope().variableMap.get(check2)[1] != null) {
                check2 = parentTree.getCurrentScope().variableMap.get(check2)[1];
            }
        }

        tinyNodeArrayList.add(new TinyNode(operation, check1, check2));
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isInteger(String str) {
        return str.matches("\\d+");
    }

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void exitPgm_body(MicroParser.Pgm_bodyContext ctx) {
        // Print the SymbolTable
        //parentTree.printWholeTree();
        //System.out.print("\n");
        // System.out.println(ctx.getChild(0).getText());
        printTinyNodeList(tinyNodeArrayList);
        System.out.println("sys halt");

    }

    /**
     * This gets called when the parser enters the Function
     */
    @Override
    public void enterFunc_body(MicroParser.Func_bodyContext ctx) { // currentScope
        pushSymbol(ctx.getChild(0).getText(), currentScope);
    }

    /**
     * This gets called when the parser exits the Function
     */
    @Override
    public void exitFunc_body(MicroParser.Func_bodyContext ctx) {
        // System.out.println(ctx.getChild(0).getText());
        System.out.println(";RET");
        System.out.println(";tiny code");
    }

    /**
     * This gets called when the parser hits the Function Declaration
     * */
    @Override public void enterFunc_decl(MicroParser.Func_declContext ctx) {

        // Function Name
//         System.out.println("Function Name: " + ctx.getChild(2).getText());

        // Function Parameters
//         System.out.println("Function Parameters: " + ctx.getChild(4).getText()); // Function Paramters

        String functionParameters = ctx.getChild(4).getText();
        String functionName = ctx.getChild(2).getText();

//        System.out.println("Function Name: " + functionName);
//        System.out.println("Function Parameters: " + functionParameters);

        // Create a new SymbolsTable for the Function
        SymbolsTable functionSymbolsTable = new SymbolsTable(functionName);
        currentScope = functionSymbolsTable; // Setting the current Scope to the Function Scope

        // Adding the New Function as a child to the Program
        parentTree.getCurrentScope().addChild(functionSymbolsTable);

        readFunctionParameters(functionParameters, functionSymbolsTable);
    }

    /**
     * This gets called whenever the parser sees a variable declaration
     * */
    @Override
    public void enterDecl(MicroParser.DeclContext ctx) {
        // System.out.println(ctx.getChild(0).getText());
    }

    private void printConditionalIR(String leftOp, String compOp, String rightOp) {

        String IRreg = "$T" + this.operationNumber;
        this.operationNumber += 1;

        // Finding if the rightOperand is an INT or FLOAT
        if(isInteger(rightOp)) { // rightOp is an INT
            System.out.println(";STOREI " + rightOp + " " + IRreg);
        } else {
            System.out.println(";STOREF " + rightOp + " " + IRreg);
        }

        // Getting the Label
        String labelName = "label" + this.labelNumber;
        labelStack.push(labelName);
        this.labelNumber += 1;

        switch (compOp) {

            case ">": // LE
                System.out.println(";LE " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "<": // GE
                System.out.println(";GE " + leftOp + " " + IRreg + " " + labelName);
                break;

            case ">=": // LT
                System.out.println(";LT " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "<=": // GT
                System.out.println(";GT " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "=": // EQ
                System.out.println(";NE " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "!=": // NE
                System.out.println(";EQ " + leftOp + " " + IRreg + " " + labelName);
                break;
        }

        // Only used for IF Statement
        if(condFlag) {
            labelName = "label" + labelNumber;
            this.labelNumber += 1;
            labelStack.push(labelName);
        }
    }

    /**
     * This gets called whenever parser detects an IF Statement
     * */
    @Override public void enterIf_stmt(MicroParser.If_stmtContext ctx) {

        SymbolsTable ifBlock = new SymbolsTable(getBlockNumber());
        currentScope = ifBlock; // Setting the Scope to the IF Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(ifBlock);

        condFlag = true;

        /**
         * TODO: Need to Check for Conditional Expressions
         * */
        String conditionExpr = ctx.getChild(2).getText();

/*        // Regex to detect comparison operator in the condition expression
        String regex = "([a-zA-Z]+)(<|>|=|!=|<=|>=)([0-9]+|[0-9]+.[0-9]+|[a-zA-Z]+)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conditionExpr);

        while (matcher.find()) {
            String leftOp = matcher.group(1);
            String compOp = matcher.group(2);
            String rightOp = matcher.group(3);

            System.out.println("LeftOp: " + leftOp);
            System.out.println("CompOp: " + compOp);
            System.out.println("RightOp: " + rightOp);

            printConditionalIR(leftOp, compOp, rightOp); // Prints the appropriate IR Code for the Conditional Expression IF and FOR
        }*/

        /**
         * TODO: Need to Check for Declarations and Statements
         * */
        String decl = ctx.getChild(4).getText();
        String stmt_list = ctx.getChild(5).getText();

        if(ctx.getChild(6) != null) {
            elsePresent = true;
        }
        else {
            elsePresent = false;
        }
    }

    @Override
    public void enterCond(MicroParser.CondContext ctx) {

        String leftExpr = ctx.getChild(0).getText();
        String compOp = ctx.getChild(1).getText();
        String rightExpr = ctx.getChild(2).getText();

//        System.out.println("LeftOp: " + leftExpr);
//        System.out.println("CompOp: " + compOp);
//        System.out.println("RightOp: " + rightExpr);

        printConditionalIR(leftExpr, compOp, rightExpr); // Prints the appropriate IR Code for the Conditional Expression IF and FOR
    }

    /**
     * This gets called whenever parser exits an IF Statement
     * */
    @Override
    public void exitIf_stmt(MicroParser.If_stmtContext ctx) {

        String label2 = "";
        String label1 = "";

        if(!labelStack.empty()) {
            label2 = labelStack.pop();
        }

        if(!labelStack.empty()) {
            label1 = labelStack.pop();

            if(elsePresent) {
                System.out.println(";JUMP " + label2);
                System.out.println(";LABEL " + label1);
                labelStack.push(label2);
            } else {
                System.out.println(";JUMP " + label2);
                System.out.println(";LABEL " + label1);
                System.out.println(";LABEL " + label2);
            }

            return;
        }

        if(label1.isEmpty() && !label2.isEmpty()) {
            System.out.println(";JUMP " + label2);
            System.out.println(";LABEL " + label2);
        }

//        System.out.println("Exited the IF Part");
    }

    /**
     * This gets called whenever parser detects an ELSE Statement
     * */
    @Override public void enterElse_part(MicroParser.Else_partContext ctx) {

        SymbolsTable elseBlock = new SymbolsTable(getBlockNumber());
        currentScope = elseBlock; // Setting the Scope to the ELSE Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(elseBlock);

        /**
         * TODO: Need to Check for Local Parameters
         * */
        if(ctx.getChild(1) != null) {
            String localDecl = ctx.getChild(1).getText();
            String localStmt = ctx.getChild(2).getText();
        }
    }

    /**
     * This gets called whenever parser exits an ELSE Part
     */
    @Override public void exitElse_part(MicroParser.Else_partContext ctx) {

        String label2 = "";
        String label1 = "";

        if(!labelStack.empty()) {
            label2 = labelStack.pop();
        }

        if(!labelStack.empty()) {
            label1 = labelStack.pop();
            System.out.println(";JUMP " + label2);
            System.out.println(";LABEL " + label1);
            System.out.println(";LABEL " + label2);
        }

//        System.out.println("Exited the ELSE part");
    }

    /**
     * This gets called whenever parser detects an FOR Statement
     * */
    @Override public void enterFor_stmt(MicroParser.For_stmtContext ctx) {

        condFlag = true;

        SymbolsTable forBlock = new SymbolsTable(getBlockNumber());
        currentScope = forBlock; // Setting the Scope to the FOR Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(forBlock);

        if(ctx.getChild(8) == null) {
            return;
        }

        pushSymbol(ctx.getChild(8).getText(), currentScope);

        String init_stmt = ctx.getChild(2).getText();

        /**
         * IR Code for init_stmt
         * */

        String[] operands = init_stmt.trim().split(":=");

        String left = operands[0].trim();
        String right = operands[1].trim();

        String postfix = InfixToPostfix.infixToPostfix(right);
        parsePostfix(right, left, postfix);

        /**
         * IR Code for cond_stmt
         * */
/*        String cond_stmt = ctx.getChild(4).getText(); */

        String labelName1 = "label" + this.labelNumber;
        this.labelNumber += 1;

        System.out.println(";LABEL " + labelName1);

/*
        // Regex to detect comparison operator in the condition expression
        String regex = "([a-zA-Z]+)(<|>|=|!=|<=|>=)([0-9])";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cond_stmt);

        while (matcher.find()) {
            String leftOp = matcher.group(1);
            String compOp = matcher.group(2);
            String rightOp = matcher.group(3);

            printForIR(leftOp, compOp, rightOp); // Prints the appropriate IR Code for the Conditional Expression IF and FOR
        }
*/

        /**
         * IR Code for incr_stmt
         * */
        incr_stmt = ctx.getChild(6).getText();

        labelStack.push(labelName1);
    }

    private void printForIR(String leftOp, String compOp, String rightOp) {
        String IRreg = "$T" + this.operationNumber;
        this.operationNumber += 1;

        // Finding if the rightOperand is an INT or FLOAT
        if(isInteger(rightOp)) { // rightOp is an INT
            System.out.println(";STOREI " + rightOp + " " + IRreg);
        } else {
            System.out.println(";STOREF " + rightOp + " " + IRreg);
        }

        // Getting the Label
        String labelName = "label" + this.labelNumber;
        labelStack.push(labelName);
        this.labelNumber += 1;

        switch (compOp) {

            case ">": // LE
                System.out.println(";LE " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "<": // GE
                System.out.println(";GE " + leftOp + " " + IRreg + " " + labelName);
                break;

            case ">=": // LT
                System.out.println(";LT " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "<=": // GT
                System.out.println(";GT " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "=": // EQ
                System.out.println(";NE " + leftOp + " " + IRreg + " " + labelName);
                break;

            case "!=": // NE
                System.out.println(";EQ " + leftOp + " " + IRreg + " " + labelName);
                break;
        }
    }

    @Override
    public void exitFor_stmt(MicroParser.For_stmtContext ctx) {

        if(!incr_stmt.isEmpty()) {
            String[] operands = incr_stmt.split(":=");

            String left = operands[0].trim();
            String right = operands[1].trim();
            String postfix = InfixToPostfix.infixToPostfix(right);
            parsePostfix(right, left, postfix);
        }

        String label1 = "";
        String label2 = "";

        if(!labelStack.empty()) {
            label1 = labelStack.pop();
        }

        if(!labelStack.empty()) {
            label2 = labelStack.pop();

            System.out.println(";JUMP " + label1);
            System.out.println(";LABEL " + label2);
        }
    }
}