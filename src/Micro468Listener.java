//import com.sun.org.apache.xpath.internal.SourceTree;
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

    private String currentFunction = "";

    private int returnRegister;

    private SymbolsTree parentTree;
    private int blockNumber;
    private int operationNumber;
    private int labelNumber;
    private int labelNo;
    private static int tinyRegisterNumber;
    private SymbolsTable currentScope; // Used to refer to which functio is being parsed
    private SymbolsTable functionScope; // Used t
    private static ArrayList<TinyNode> tinyNodeArrayList = new ArrayList<TinyNode>();

    private Stack<String> labelStack1 = new Stack<String>();
    private Stack<String> labelStack2 = new Stack<String>();
    private Stack<Boolean> elsePresentStack = new Stack<Boolean>();

    private HashMap<String, Scope> symbolScope = new HashMap<String, Scope>();

    private boolean condFlag;

    private String incr_stmt;

    private int randomTiny = 20; // Used for the some statements where the tinyRegister is no more used in the subsequent Tiny instructions

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
        labelNo = 1;
        condFlag = false;
        tinyRegisterNumber = 0;
        incr_stmt = "";
        elsePresent = false;

    }

    private String getBlockNumber() {
        return "BLOCK " + blockNumber++;
    }

    public String getLabel() {
        String LabelNo = new String("label" + labelNo);
        labelNo += 1;
        return LabelNo;
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
                       //tinyNodeArrayList.add(new TinyNode(";var", variable));
                       Symbol newVariable = new Symbol(variable, "INT");
                       symbolsTable.addSymbol(newVariable);
                   }
               }

               // FLOAT
               else if (dataType.startsWith("FLOAT")) {
                   String[] variableNames = dataType.substring(5).split(",");

                   // Adding all the Variable Names
                   for(String variable: variableNames) {
                       //tinyNodeArrayList.add(new TinyNode(";var", variable));
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

//        System.out.println("Inside read Function parameters");

        // TODO: Need to add the functionParameters to the VariableMap

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

//        System.out.println("Exit readFunctionParameters");
    }

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext ctx) { // ParentScope or GLOBAL
        pushSymbol(ctx.getChild(0).getText(), parentTree.getParentScope());

        // Pushing the Symbols to the GLOBAL Symbol Table
        System.out.println("; IR code");
        System.out.println("; PUSH");
        tinyNodeArrayList.add(new TinyNode("push"));
        System.out.println("; JSR FUNC_id_main_L");
        tinyNodeArrayList.add(new TinyNode("jsr", "FUNC_id_main_L"));
        System.out.println("; HALT");
        tinyNodeArrayList.add(new TinyNode("sys", "halt"));
//        System.out.println("Global Declarations:");
//        System.out.println(ctx.getChild(0).getText());
    }

    @Override public void enterRead_stmt(MicroParser.Read_stmtContext ctx) {
        String right = ctx.getChild(2).getText();
        String[] words = right.split(",");
        //System.out.println("when reading: " + String.join(" ", words));

        for (int i = 0; i < words.length; i++) {

            String currentType = null;

            if(isVariableinGlobal(words[i])) {
                currentType = parentTree.getCurrentScope().variableMap.get(words[i])[0];
            }
            else {
                currentType = currentScope.variableMap.get(words[i])[0];
            }

            String word = words[i];
            if(symbolScope.containsKey(word)) {
                word = symbolScope.get(word).register;
            }

            if (currentType == "INT") {
                //System.out.println(";READI " + words[i]);
                printReadOperation("READI", words[i]);
                tinyNodeArrayList.add(new TinyNode("sys readi", word));
            }
            else if (currentType == "FLOAT") {
                //System.out.println(";READF " + words[i]);
                printReadOperation("READF", words[i]);
                tinyNodeArrayList.add(new TinyNode("sys readr", word));
            }
            else {
                System.out.println(";READS " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys reads", word));
            }
        }
    }

    public void printReadOperation(String read, String toRead) {
        if (symbolScope.containsKey(toRead)) {
            toRead = symbolScope.get(toRead).register;
        }
        System.out.println("; " + read + " " + toRead);
    }

    @Override public void enterWrite_stmt(MicroParser.Write_stmtContext ctx) {
        String right = ctx.getChild(2).getText();
        String[] words = right.split(",");
        //System.out.println("when writing: " + String.join(" ", words));

        for (int i = 0; i < words.length; i++) {

            String currentType = null;

            if(isVariableinGlobal(words[i])) {
                currentType = parentTree.getCurrentScope().variableMap.get(words[i])[0];
            }
            else {
                currentType = currentScope.variableMap.get(words[i])[0];
            }

            String word = words[i];
            if(symbolScope.containsKey(word)) {
                word = symbolScope.get(word).register;
            }

            if (currentType == "INT") {
                //System.out.println(";WRITEI " + words[i]);
                printWriteOperation("WRITEI", words[i]);
//                tinyNodeArrayList.add(new TinyNode("sys", "writei", words[i]));
                tinyNodeArrayList.add(new TinyNode("sys", "writei", word));
            }
            else if (currentType == "FLOAT") {
                //System.out.println(";WRITEF " + words[i]);
                printWriteOperation("WRITEF", words[i]);
//                tinyNodeArrayList.add(new TinyNode("sys", "writer", words[i]));
                tinyNodeArrayList.add(new TinyNode("sys", "writer", word));
            }
            else {
                System.out.println(";WRITES " + words[i]);
                tinyNodeArrayList.add(new TinyNode("sys", "writes", word));
            }
        }
    }


    public void printWriteOperation(String write, String toWrite) {
        if (symbolScope.containsKey(toWrite)) {
            toWrite = symbolScope.get(toWrite).register;
        }
        System.out.println("; " + write + " " + toWrite);
    }



    private boolean isVariableinGlobal(String variable) {
        return parentTree.getCurrentScope().variableMap.get(variable) != null;
    }

    /**
     * This gets called when the parser finds an assignment statement
     * */
    @Override
    public void enterAssign_stmt(MicroParser.Assign_stmtContext ctx) {
//        System.out.println("Enter Assign Stmt");
        String left = ctx.getChild(0).getChild(0).getText();
        String right = ctx.getChild(0).getChild(2).getText();

//        System.out.println("Left: " + left);
//        System.out.println("Right: " + right);

        if(isFunctionCall(right)) {
            // TODO: Handle function call
            //System.out.println("THIS IS A FUNCTION CALL: " + left + " " + right);
            //System.out.println(ctx.getChild(0).getChild(2).getChild(1).getText());
            //System.out.println(ctx.getChild(0).getChild(2).getChild(1).getText());
            //System.out.println(ctx.getChild(0).getChild(2).getChild(1).getText());
            return;
        }

        String postfix = InfixToPostfix.infixToPostfix(right);

        //System.out.println("PostFix: " + postfix);

        parsePostfix(right, left, postfix);

//        try {
//            parsePostfix(right, left, postfix);
//        }
//        catch (Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//        System.out.println("Exit Assign Stmt");
    }


    private boolean isSymbolScopeInteger(String name) {
        if (symbolScope.get(name).decl_type.equals("INT")) {
            return true;
        }
        return false;
    }

    private void parsingPostfixForReturnPush(String right, String left, String postfix) {

        parsePostfixForPush(right, left, postfix, false);
    }

    @Override public void enterCall_expr(MicroParser.Call_exprContext ctx) {
        String function_name = ctx.getChild(0).getText();

        String left = ctx.getParent().getParent().getParent().getParent().getChild(0).getText();
        String right = ctx.getParent().getParent().getParent().getParent().getChild(2).getText();

        // TODO: Not sure:

        String[] function_parameters = ctx.getChild(2).getText().split(",");
        System.out.println("; PUSH (return value)");
        tinyNodeArrayList.add(new TinyNode("push"));

        for (int i = 0; i < function_parameters.length; i++) {
//            System.out.println("param: " + function_parameters[i]);
            String postfix = InfixToPostfix.infixToPostfix(function_parameters[i]);
//            System.out.println("postfix: " + postfix);

            parsingPostfixForReturnPush(right, left, postfix);
            //System.out.println("; PUSH arguments");
        }

        System.out.println("; JSR FUNC_id_" + function_name + "_L");
        tinyNodeArrayList.add(new TinyNode("jsr", "FUNC_id_" + function_name + "_L"));

        for (int i = 0; i < function_parameters.length; i++) {
//            System.out.println("param: " + function_parameters[i]);
//            String postfix = InfixToPostfix.infixToPostfix(function_parameters[i]);
//            System.out.println("postfix: " + postfix);
            System.out.println("; POP");
            tinyNodeArrayList.add(new TinyNode("pop"));
        }


        //System.out.println("; POP the value returned");

        String location = "!T" + this.operationNumber;
        this.operationNumber += 1;

        if (isSymbolScopeInteger(left)) {

            parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
            tinyRegisterNumber = this.operationNumber - 2;

            System.out.println("; POP " +  location);
            tinyNodeArrayList.add(new TinyNode("pop", "r" + tinyRegisterNumber));
            add_reg_operation_stmt_2("move", "r" + tinyRegisterNumber, symbolScope.get(left).register);
            System.out.println("; STOREI " + location + " " + symbolScope.get(left).register);

            //add_reg_operation_stmt_2("move", c, location);
        }
        else {
            parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
            tinyRegisterNumber = this.operationNumber - 2;

            System.out.println("; POP " +  location);
            tinyNodeArrayList.add(new TinyNode("pop", "r" + tinyRegisterNumber));
            add_reg_operation_stmt_2("move", "r" + tinyRegisterNumber, symbolScope.get(left).register);
            System.out.println("; STOREF " + location + " " + symbolScope.get(left).register);
        }


        //System.out.println("function_name = " + function_name);
//        for (int i = 0; i < function_parameters.length; i++) {
//            System.out.println("param: " + function_parameters[i]);
//            String postfix = InfixToPostfix.infixToPostfix(function_parameters[i]);
//            System.out.println("postfix: " + postfix);
//        }
//
//        System.out.println("==========");
    }

    private static boolean isFunctionCall(String str) {
        if (str.trim().startsWith("\"") || (str.trim().startsWith("\'"))) {
            return false;
        }

        boolean val = str.contains(",");
        if (val) {
            return true;
        }

        String regex = "[a-zA-Z]+[a-zA-Z0-9_]*\\(.*\\)";
        Pattern funcPattern = Pattern.compile(regex);

        Matcher m = funcPattern.matcher(str);

        //System.out.println(Boolean.toString(m.matches()));

        return m.matches();
    }


    private static boolean isOperator(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("(") || c.equals(")");
    }

    private void parsePostfixForPush(String right, String left, String postfix, boolean push) {
        //System.out.println("Parse Postfix");

//        System.out.println("Left: " + left + " Right: " + right + " PostFix: " + postfix);

        Stack<String> stack = new Stack<String>();

        String[] words = postfix.split(" ");
        //System.out.println("split: " + Arrays.toString(words));
        if (words.length == 1) {
            System.out.println("; PUSH " + symbolScope.get(words[0]).register);
            tinyNodeArrayList.add(new TinyNode("push", symbolScope.get(words[0]).register));
            return;
        }

        //stack.push(words[0]);
        //stack.push(words[1]);

        for (int i = 0; i < words.length; i++) {
            String c = words[i];

            boolean foundNumber = false;

            if (isNumeric((c))) {

                // System.out.println("isNumeric");

                foundNumber = true;
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                if (isInteger(c)) {
                    //parentTree.getCurrentScope().addRegister(location, "INT");
//                    System.out.println("scopeName: " + parentTree.getCurrentScope().getBlockScope());
//                    System.out.println("Location: " + location);
                    parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    System.out.println(";STOREI " + c + " " + location);
                    add_reg_operation_stmt_2("move", c, location);
                }
                else {
                    System.out.println("scopeName: " + parentTree.getCurrentScope().getBlockScope());
                    System.out.println("Location: " + location);

                    //parentTree.getCurrentScope().addRegister(location, "FLOAT");
                    parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    //System.out.println(";STOREF " + c + " " + location);
                    printStoreOperation("STOREF", c, location);
                    add_reg_operation_stmt_2("move", c, location);
                }
            }

            if (isOperator(c)) {
                // System.out.println("isOperator");
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;
                // System.out.println("CurrentScope: " + currentScope.getBlockScope());
                //currentScope.printSymbolTable();
                //String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                String currentType = currentScope.variableMap.get(val1)[0];
                //System.out.println("currentType: " + currentType);
                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                //currentScope.addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else if (!foundNumber) {
                // System.out.println("foundNumber");
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            String c = stack.pop();

//            System.out.println("c: " + c);

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                //String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                String currentType = currentScope.variableMap.get(val1)[0];

                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                //currentScope.addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));

                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else {

               // String currentType;

//                if(isVariableinGlobal(c)) {
//                    currentType = parentTree.getCurrentScope().variableMap.get(c)[0];
//                }
//                else {
//                    currentType = currentScope.variableMap.get(c)[0];
//                }
//                if (left == null) {
//                    return;
//                }
                System.out.println("; PUSH " + c);
                add_reg_operation_stmt_3("push", c);
//                if (currentType.equals("INT")) {
//                    //System.out.println(";STOREI " + " " + c + " " + left);
//                    //printStoreOperation("PUSH", c, left);
//                    System.out.println("PUSH " + " " + c);
//                    //add_reg_operation_stmt_2("move", c, left);
//                }
//                else {
//                    //System.out.println(";STOREF " + " " + c + " " + left);
//                    printStoreOperation("STOREF", c, left);
//                    //add_reg_operation_stmt_2("move", c, left);
//                }
            }
        }
    }


    private void parsePostfix(String right, String left, String postfix) {
        //System.out.println("Parse Postfix");

//        System.out.println("Left: " + left + " Right: " + right + " PostFix: " + postfix);

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

                // System.out.println("isNumeric");

                foundNumber = true;
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                if (isInteger(c)) {
                    //parentTree.getCurrentScope().addRegister(location, "INT");
//                    System.out.println("scopeName: " + parentTree.getCurrentScope().getBlockScope());
//                    System.out.println("Location: " + location);
                    parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    System.out.println(";STOREI " + c + " " + location);
                    add_reg_operation_stmt_2("move", c, location);
                }
                else {
                    System.out.println("scopeName: " + parentTree.getCurrentScope().getBlockScope());
                    System.out.println("Location: " + location);

                    //parentTree.getCurrentScope().addRegister(location, "FLOAT");
                    parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    //System.out.println(";STOREF " + c + " " + location);
                    printStoreOperation("STOREF", c, location);
                    add_reg_operation_stmt_2("move", c, location);
                }
            }

            if (isOperator(c)) {
                // System.out.println("isOperator");
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;
                // System.out.println("CurrentScope: " + currentScope.getBlockScope());
                //currentScope.printSymbolTable();
                //String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                String currentType = currentScope.variableMap.get(val1)[0];
                //System.out.println("currentType: " + currentType);
                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                //currentScope.addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else if (!foundNumber) {
                // System.out.println("foundNumber");
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            String c = stack.pop();

//            System.out.println("c: " + c);

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                //String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                String currentType = currentScope.variableMap.get(val1)[0];

                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));
                //currentScope.addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));

                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else {

                String currentType;

                if(isVariableinGlobal(c)) {
                    currentType = parentTree.getCurrentScope().variableMap.get(c)[0];
                }
                else {
                    currentType = currentScope.variableMap.get(c)[0];
                }

                if (left == null) {
                    return;
                }
                if (currentType.equals("INT")) {
                    //System.out.println(";STOREI " + " " + c + " " + left);
                    printStoreOperation("STOREI", c, left);
                    add_reg_operation_stmt_2("move", c, left);
                }
                else {
                    //System.out.println(";STOREF " + " " + c + " " + left);
                    printStoreOperation("STOREF", c, left);
                    add_reg_operation_stmt_2("move", c, left);
                }
            }
        }
    }

    public void printStoreOperation(String store, String c, String left) {
        if (symbolScope.containsKey(c)) {
            c = symbolScope.get(c).register;
        }
        if (symbolScope.containsKey(left)) {
            left = symbolScope.get(left).register;
        }
        System.out.println("; " + store + " " + c + " " + left);
    }

    @Override public void enterReturn_stmt(MicroParser.Return_stmtContext ctx) {
        //System.out.println("++++++++++++++++++++++++++");
        //System.out.println(currentFunction);
        if (currentFunction.equals("main")) {
            this.returnRegister = 6;
        }
        String rightExpr = ctx.getChild(1).getText();
        String postfix = InfixToPostfix.infixToPostfix(rightExpr);


        if (isNumeric(postfix)) {

            if (isInteger(postfix)) {
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                System.out.println(";STOREI " + postfix + " " + location);
                parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
                System.out.println("; STOREI " + location + " $" + returnRegister);

                tinyRegisterNumber = this.operationNumber - 2;

                add_reg_operation_stmt_2("move", postfix, location);
                add_reg_operation_stmt_2("move", location,  "$" + returnRegister);

            }
            else {
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                System.out.println(";STOREF " + postfix + " " + location);
                parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                System.out.println("; STOREF " + location + " $" + returnRegister);

                tinyRegisterNumber = this.operationNumber - 2;

                add_reg_operation_stmt_2("move", postfix, location);
                add_reg_operation_stmt_2("move", location,  "$" + returnRegister);
            }

            System.out.println("; UNLINK");
            tinyNodeArrayList.add(new TinyNode("unlnk"));
            System.out.println("; RET");
            tinyNodeArrayList.add(new TinyNode("ret"));
            return;
        }

        parsePostfix(rightExpr, null, postfix);

        String[] words = postfix.split(" ");

        if (words.length == 1) {
            postfix = postfix.trim();
            String register = "";
            if (symbolScope.containsKey(postfix)) {
                register = symbolScope.get(postfix).register;
                if (symbolScope.get(postfix).decl_type.equals("INT")) {
                    System.out.println("; STOREI " + register + " $" + returnRegister);
                    add_reg_operation_stmt_2("move", register,  "$" + returnRegister);
                }
                else {
                    System.out.println("; STOREF " + register + " $" + returnRegister);
                    add_reg_operation_stmt_2("move", register,  "$" + returnRegister);

                }
            }
            else {
                System.out.println("POSTFIX = " + postfix);
                register = postfix;

                String currentType = "";
                if(isVariableinGlobal(postfix)) {
                    currentType = parentTree.getCurrentScope().variableMap.get(postfix)[0];
                }
                else {
                    currentType = currentScope.variableMap.get(postfix)[0];
                }

                if (currentType.equals("INT")) {
                    System.out.println("; STOREI " + register + " $" + returnRegister);
                    add_reg_operation_stmt_2("move", register,  "$" + returnRegister);

                }
                else {
                    System.out.println("; STOREF " + register + " $" + returnRegister);
                    add_reg_operation_stmt_2("move", register,  "$" + returnRegister);

                }
            }
        }
        else {
            String register = "!T" + Integer.toString(this.operationNumber - 1);
            String currentType = "";
            //System.out.println(register);
            //parentTree.getCurrentScope().printSymbolTable();
            //currentScope.printSymbolTable();

            if(isVariableinGlobal(register)) {
                currentType = parentTree.getCurrentScope().variableMap.get(register)[0];
            }
            else {
                currentType = currentScope.variableMap.get(register)[0];
            }
            if (currentType.equals("INT")) {
                tinyRegisterNumber = this.operationNumber - 2;

                System.out.println("; STOREI " + "!T" + Integer.toString(this.operationNumber - 1) + " $" + returnRegister);
                tinyNodeArrayList.add(new TinyNode("move", "r" + tinyRegisterNumber, " $" + returnRegister));
            }
            else {
                tinyRegisterNumber = this.operationNumber - 2;

                System.out.println("; STOREF " + "!T" +  Integer.toString(this.operationNumber - 1) + " $" + returnRegister);
                tinyNodeArrayList.add(new TinyNode("move", "r" + tinyRegisterNumber, " $" + returnRegister));
            }
        }


        System.out.println("; UNLINK");
        tinyNodeArrayList.add(new TinyNode("unlnk"));
        System.out.println("; RET");
        tinyNodeArrayList.add(new TinyNode("ret"));

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

    public void printMap(HashMap<String, String[]> mp) {
        List<String> keys = new ArrayList<String>(mp.keySet());
        for (int i = 0; i < keys.size(); i++) {
            System.out.println("key = " + keys.get(i));
            System.out.println("param 1 = " + mp.get(keys.get(i))[0]);
            System.out.println("param 2 = " + mp.get(keys.get(i))[1]);
        }
    }

    private void add_reg_operation_stmt(String operation, String str1, String location) {
        // parentTree.getCurrentScope()

//        System.out.println("str1: " + str1);
//        System.out.println("location: " + location);

        // TODO: Need to refactor this
        if (str1.trim().startsWith("!")) {
            if (parentTree.getCurrentScope().variableMap.get(str1)[1] == null) {
                if (symbolScope.containsKey(str1)) {
                    str1 = symbolScope.get(str1).register;
                }
                tinyNodeArrayList.add(new TinyNode(operation, str1, parentTree.getCurrentScope().variableMap.get(location)[1]));
            } else {
                tinyNodeArrayList.add(new TinyNode(operation, parentTree.getCurrentScope().variableMap.get(str1)[1],
                        parentTree.getCurrentScope().variableMap.get(location)[1]));
            }
        } else {
            if (currentScope.variableMap.get(str1)[1] == null) {
                if (symbolScope.containsKey(str1)) {
                    str1 = symbolScope.get(str1).register;
                }
                tinyNodeArrayList.add(new TinyNode(operation, str1, parentTree.getCurrentScope().variableMap.get(location)[1]));
            } else {
                tinyNodeArrayList.add(new TinyNode(operation, parentTree.getCurrentScope().variableMap.get(str1)[1],
                        parentTree.getCurrentScope().variableMap.get(location)[1]));
            }
        }
    }
    private void reg_operation_stmt(String operation, String type, String str1, String str2, String location) {

//        System.out.println("str1: " + str1);
//        System.out.println("str2: " + str2);
//        System.out.println("Location: " + location);

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

    private void operation_stmt(String operation, String type, String str1, String str2, String location) {
        if (symbolScope.containsKey(str1)) {
            str1 = symbolScope.get(str1).register;
        }
        if (symbolScope.containsKey(str2)) {
            str2 = symbolScope.get(str2).register;
        }
        if (type.equals("INT")) {
            System.out.println(";" + operation + "I " + str1 + " " + str2 + " " + location);
        }
        else {
            System.out.println(";" + operation + "F " + str1 + " " + str2 + " " + location);
        }
    }

    private void parseAssign_stmt(String left, String right) {
        if (isNumeric(right) == false) {
            // TODO: Might have to check for the GLOBAL Scope and also might have to change parentTree.getCurrentScope()
            if (parentTree.getCurrentScope().variableMap.containsKey(left) && parentTree.getCurrentScope().variableMap.containsKey(right)) {
                String dataType = parentTree.getCurrentScope().variableMap.get(left)[0];
                if (dataType.equals("INT")) {
                    //System.out.println(";STOREI " + right + " " + left);
                    printStoreOperation("STOREI", right, left);

                    tinyNodeArrayList.add(new TinyNode("move", right, "r" + randomTiny));
                    tinyNodeArrayList.add(new TinyNode("move", "r" + randomTiny, left));
                    randomTiny += 1;
                }
                else {
                    //System.out.println(";STOREF " + right + " " + left);
                    printStoreOperation("STOREF", right, left);

                    tinyNodeArrayList.add(new TinyNode("move", right, "r" + randomTiny));
                    tinyNodeArrayList.add(new TinyNode("move", "r" + randomTiny, left));
                    randomTiny += 1;
                }
            }
            return;
        }

        if (isInteger(right)) {
            String location = "!T" + this.operationNumber;
            this.operationNumber += 1;

            //System.out.println(";STOREI " + right + " " + location);
            printStoreOperation("STOREI", right, location);
            parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
            //System.out.println(";STOREI " + location + " " + left);
            printStoreOperation("STOREI", location, left);
            tinyRegisterNumber += 1;
            add_reg_operation_stmt_2("move", right, location);
            add_reg_operation_stmt_2("move", location, left);
        }
        else {
            String location = "!T" + this.operationNumber;
            this.operationNumber += 1;

            //System.out.println(";STOREF " + right + " " + location);
            printStoreOperation("STOREF", right, location);
            parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
            //System.out.println(";STOREF " + location + " " + left);
            printStoreOperation("STOREF", location, left);
            tinyRegisterNumber += 1;
            add_reg_operation_stmt_2("move", right, location);
            add_reg_operation_stmt_2("move", location, left);
        }
    }

    private void add_reg_operation_stmt_3(String operation, String str1) {
        String check1 = str1;
        if (parentTree.getCurrentScope().variableMap.containsKey(check1)) {
            if (parentTree.getCurrentScope().variableMap.get(check1)[1] != null) {
                check1 = parentTree.getCurrentScope().variableMap.get(check1)[1];
            }
        }

        if (symbolScope.containsKey(check1)) {
            check1 = symbolScope.get(check1).register;
        }

        tinyNodeArrayList.add(new TinyNode(operation, check1));
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

        if (symbolScope.containsKey(check1)) {
            check1 = symbolScope.get(check1).register;
        }


        if (symbolScope.containsKey(check2)) {
            check2 = symbolScope.get(check2).register;
        }

        if (check1.startsWith("$") && check2.startsWith("$") && operation.equals("move")) {
            String reg = "r" + Integer.toString(this.operationNumber - 2);
            tinyNodeArrayList.add(new TinyNode(operation, check1, reg));
            tinyNodeArrayList.add(new TinyNode(operation, reg, check2));

            return;
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
        System.out.println(";tiny code");
        printTinyNodeList(tinyNodeArrayList);
        //System.out.println("sys halt");

    }

    /**
     * This gets called when the parser enters the Function
     */
    @Override
    public void enterFunc_body(MicroParser.Func_bodyContext ctx) { // currentScope

        String functionName =  ctx.getParent().getChild(2).getText();
        currentFunction = functionName;

        System.out.println("; LABEL FUNC_id_" + functionName + "_L");
        tinyNodeArrayList.add(new TinyNode("label", "FUNC_id_" + functionName + "_L"));

        int totalRegisters = currentScope.variableMap.size();

        pushSymbol(ctx.getChild(0).getText(), currentScope);

        totalRegisters = currentScope.variableMap.size() - totalRegisters + 1;
        System.out.println("; LINK " + totalRegisters);
        tinyNodeArrayList.add(new TinyNode("link", Integer.toString(totalRegisters)));
    }

    /**
     * This gets called when the parser exits the Function
     */
//    @Override
//    public void exitFunc_body(MicroParser.Func_bodyContext ctx) {
//        //System.out.println("==================");
//        //System.out.println("; RETURNING " + ctx.getChild(1).getChild(1).getText());
//
//        /*
//            // For it seems ok just to return when we come
//        // across a return statement rather than the end of a function
//
//         */
//
//        System.out.println("; UNLINK");
//        tinyNodeArrayList.add(new TinyNode("unlnk"));
//        System.out.println("; RET");
//        tinyNodeArrayList.add(new TinyNode("ret"));
//    }

    /**
     * This gets called when the parser hits the Function Declaration
     * */
    @Override public void enterFunc_decl(MicroParser.Func_declContext ctx) {

//        // Function Name
//         System.out.println("Function Name: " + ctx.getChild(2).getText());
//
//        // Function Parameters
//         System.out.println("Function Parameters: " + ctx.getChild(4).getText()); // Function Parameters
    
        String functionParameters = ctx.getChild(4).getText();
        String functionName = ctx.getChild(2).getText();

        // TODO: Tiny
        //System.out.println(";LABEL " + functionName);

//        System.out.println("Function Name: " + functionName);
//        System.out.println("Function Parameters: " + functionParameters);

        // Create a new SymbolsTable for the Function
        SymbolsTable functionSymbolsTable = new SymbolsTable(functionName);
        currentScope = functionSymbolsTable; // Setting the current Scope to the Function Scope

        // Adding the New Function as a child to the Program
        parentTree.getCurrentScope().addChild(functionSymbolsTable);

        readFunctionParameters(functionParameters, functionSymbolsTable);

        //System.out.println("++++++++++++++++++++++++++++++++");
        String type = "ARGS";
        //System.out.println(functionParameters);
        addToSymbolScope(functionParameters, functionName, type);
        //printSymbolScope();
        //System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{");
        type = "LOCALS";
        //System.out.println(ctx.getChild(7).getChild(0).getText());
        addToSymbolScope(ctx.getChild(7).getChild(0).getText(), functionName, type);
        //printSymbolScope();

    }


    public void printSymbolScope() {
        List<String> keys = new ArrayList<String>(symbolScope.keySet());
        for (int i = 0; i < keys.size(); i++) {
            System.out.println("key = " + keys.get(i));
            symbolScope.get(keys.get(i)).print();
        }
    }

    public void addToSymbolScope(String parameters, String function, String type) {

//        System.out.println("Inside read Function parameters");

        // TODO: Need to add the functionParameters to the VariableMap

        if(parameters == null || parameters.equals("")) {
            return;
        }

        int count = 0;
        if (type.equals("ARGS")) {
            count = 2;
        }
        else {
            count = -1;
        }

        // Only INT and FLOAT are the datatypes of the Function Parameters
        String[] functionParameters = parameters.split(";");

        String lastType = "INT";

        for(String parameter: functionParameters) {
            String[] variables = parameter.split(",");
            for (String variable : variables) {
                if(variable.startsWith("INT")) {
                    lastType = "INT";
                    symbolScope.put(variable.substring(3),
                            new Scope(variable.substring(3), type, function, "INT", "$" + Integer.toString(count)));
                }
                else if (variable.startsWith("FLOAT")) {
                    lastType = "FLOAT";
                    symbolScope.put(variable.substring(5),
                            new Scope(variable.substring(5), type, function, "FLOAT", "$" + Integer.toString(count)));
                }
                else {
                    symbolScope.put(variable,
                            new Scope(variable, type, function, lastType, "$" + Integer.toString(count)));
                }
                if (type.equals("ARGS")) {
                    count += 1;
                }
                else {
                    count -= 1;
                }
            }
        }

        if (type.equals("ARGS")) {
            returnRegister = count;
        }

//        System.out.println("Exit readFunctionParameters");
    }

    /**
     * This gets called whenever the parser sees a variable declaration
     * */
    @Override
    public void enterDecl(MicroParser.DeclContext ctx) {
//         System.out.println(ctx.getChild(0).getText());
    }

    /**
     * This gets called whenever parser detects an IF Statement
     * */
    @Override public void enterIf_stmt(MicroParser.If_stmtContext ctx) {

        // System.out.println("enterIf");

        SymbolsTable ifBlock = new SymbolsTable(getBlockNumber());
        // currentScope = ifBlock; // Setting the Scope to the IF Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(ifBlock);

        condFlag = true;

        /**
         * TODO: Need to Check for Conditional Expressions
         * */
        String conditionExpr = ctx.getChild(2).getText();

        /**
         * TODO: Need to Check for Declarations and Statements
         * */
        String decl = ctx.getChild(4).getText();
        String stmt_list = ctx.getChild(5).getText();

        if(!ctx.getChild(6).getText().equals("")) {
            // System.out.println("ElsePresent: true");
            // printElsePresentStack(elsePresentStack);
            elsePresentStack.push(true);
        }
        else {
            // System.out.println("ElsePresent: false");
            elsePresentStack.push(false);
            // printElsePresentStack(elsePresentStack);
        }
    }

    @Override
    public void enterCond(MicroParser.CondContext ctx) {

        String leftExpr = ctx.getChild(0).getText();
        String compOp = ctx.getChild(1).getText();
        String rightExpr = ctx.getChild(2).getText();

//        System.out.println("leftExpr: " + leftExpr);
//        System.out.println("rightExpr: " + rightExpr);

//        currentScope.printSymbolTable();

        labelStack1.push(getLabel());

        if(condFlag) { // Entered If Statement
            labelStack2.push(getLabel());
        } // Enter If Statement

        if (isNumeric(rightExpr) || parentTree.getCurrentScope().variableMap.containsKey(rightExpr) || currentScope.variableMap.containsKey(rightExpr)) {
            // Right Operand is either a number(INT or FLOAT or just a variable)
            // System.out.println("enterCond If");
            checkCompOp(compOp, leftExpr, rightExpr);
        } else {
            // System.out.println("enterCond else");
            String postfix = InfixToPostfix.infixToPostfix(rightExpr);
            parsePostfix(rightExpr, null, postfix);
            checkCompOp(compOp, leftExpr, "!T" + Integer.toString(this.operationNumber - 1));
        }
    }

    /**
     * This gets called whenever parser exits an IF Statement
     * */
    @Override
    public void exitIf_stmt(MicroParser.If_stmtContext ctx) {
        // System.out.println("exitIf");
        // System.out.println("Exited the IF Part");
    }

    /**
     * This gets called whenever parser detects an ELSE Statement
     * */
    @Override public void enterElse_part(MicroParser.Else_partContext ctx) {

        // System.out.println("enterElse");

        SymbolsTable elseBlock = new SymbolsTable(getBlockNumber());
        // currentScope = elseBlock; // Setting the Scope to the ELSE Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(elseBlock);

        /**
         * TODO: Need to Check for Local Parameters
         * */
        if(ctx.getChild(1) != null) {
            String localDecl = ctx.getChild(1).getText();
            String localStmt = ctx.getChild(2).getText();
        }

        if(elsePresentStack.peek()) {
            // System.out.println("else Present");
            System.out.println(";JUMP " + labelStack2.peek());
            tinyNodeArrayList.add(new TinyNode("jmp", labelStack2.peek()));
            String labelName = labelStack1.pop();
            System.out.println(";LABEL " + labelName);
            tinyNodeArrayList.add(new TinyNode("label", labelName));
        }
    }

    /**
     * This gets called whenever parser exits an ELSE Part
     */
    @Override public void exitElse_part(MicroParser.Else_partContext ctx) {

        // System.out.println("exitElse");

        elsePresent = elsePresentStack.pop();

        String labelName;

        if(elsePresent) {
            labelName = labelStack2.pop();
            System.out.println(";LABEL " + labelName);
            tinyNodeArrayList.add(new TinyNode("label", labelName));
        } else {
            System.out.println(";JUMP " + labelStack2.peek());
            tinyNodeArrayList.add(new TinyNode("jmp", labelStack2.peek()));

            labelName = labelStack1.pop();
            System.out.println(";LABEL " + labelName);
            tinyNodeArrayList.add(new TinyNode("label", labelName));

            labelName = labelStack2.pop();
            System.out.println(";LABEL " + labelName);
            tinyNodeArrayList.add(new TinyNode("label", labelName));
        }

        // System.out.println("Exited the ELSE part");
    }

    /**
     * This gets called whenever parser detects an FOR Statement
     * */
    @Override public void enterFor_stmt(MicroParser.For_stmtContext ctx) {

        condFlag = false;

        SymbolsTable forBlock = new SymbolsTable(getBlockNumber());
        // currentScope = forBlock; // Setting the Scope to the FOR Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(forBlock);

        if(ctx.getChild(8) == null) {
            return;
        }

        pushSymbol(ctx.getChild(8).getText(), currentScope);

        /**
         * IR Code for init_stmt
         * */

        if(!ctx.getChild(2).getText().equals("")) {
            String init_stmt = ctx.getChild(2).getText();

            String[] operands = init_stmt.trim().split(":=");

            String left = operands[0].trim();
            String right = operands[1].trim();

            String postfix = InfixToPostfix.infixToPostfix(right);
            parsePostfix(right, left, postfix);
        }

        labelStack2.push(getLabel());
        System.out.println(";LABEL " + labelStack2.peek());
        tinyNodeArrayList.add(new TinyNode("label", labelStack2.peek()));

        /**
         * IR Code for incr_stmt
         * */
        incr_stmt = ctx.getChild(6).getText();
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

        String labelName = labelStack2.pop();
        System.out.println(";JUMP " + labelName);
        tinyNodeArrayList.add(new TinyNode("jmp", labelName));

        labelName = labelStack1.pop();
        System.out.println(";LABEL " + labelName);
        tinyNodeArrayList.add(new TinyNode("label", labelName));
    }

    private void printStack(Stack<String> labelStack) {
        System.out.println(Arrays.toString(labelStack.toArray()));
    }
    private void printElsePresentStack(Stack<Boolean> elsePresentStack) {
        System.out.println(Arrays.toString(elsePresentStack.toArray()));
    }

    public void checkCompOp(String compOp, String leftOp, String rightOp) {

//        System.out.println("LeftOp: " + leftOp);
//        System.out.println("RightOp: " + rightOp);

        String IRreg = "!T" + this.operationNumber;

        String tinyReg = "r" + (this.operationNumber - 1);


        //parentTree.getCurrentScope().printSymbolTable();

        //System.out.println(currentScope.variableMap.containsKey(rightOp));

        // If the rightOperand is either a variable or a register i.e. $Tn
        if (parentTree.getCurrentScope().variableMap.containsKey(rightOp)) {

            // if rightOp is a register
            if(rightOp.trim().startsWith("!")) {
                if(isVariableinGlobal(leftOp)) {
                    if (parentTree.getCurrentScope().variableMap.get(leftOp)[0].equals("FLOAT")) {
                        if (symbolScope.containsKey(leftOp)) {
                            leftOp = symbolScope.get(leftOp).register;
                        }

                        tinyNodeArrayList.add(new TinyNode("cmpr", leftOp, "r" + (this.operationNumber - 2)));
                        System.out.println("cmpr = " + leftOp + "1");
                    }
                    else {
                        if (symbolScope.containsKey(leftOp)) {
                            leftOp = symbolScope.get(leftOp).register;
                        }

                        tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, "r" + (this.operationNumber - 2)));
                        System.out.println("cmpi = " + leftOp + "2");

                    }
                }
                else {
                    if (currentScope.variableMap.get(leftOp)[0].equals("FLOAT")) {
                        if (symbolScope.containsKey(leftOp)) {
                            leftOp = symbolScope.get(leftOp).register;
                        }

                        tinyNodeArrayList.add(new TinyNode("cmpr", leftOp, "r" + (this.operationNumber - 2)));
                    }
                    else {
                        if (symbolScope.containsKey(leftOp)) {
                            leftOp = symbolScope.get(leftOp).register;
                        }

                        tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, "r" + (this.operationNumber - 2)));
                    }
                }
            }
            else {
                if (parentTree.getCurrentScope().variableMap.get(leftOp)[0].equals("FLOAT")) {
                    if (symbolScope.containsKey(leftOp)) {
                        leftOp = symbolScope.get(leftOp).register;
                    }
                    tinyNodeArrayList.add(new TinyNode("move", rightOp, "r" + randomTiny));
                    tinyNodeArrayList.add(new TinyNode("cmpr", leftOp, "r" + randomTiny));
                }
                else {

                    if (symbolScope.containsKey(leftOp)) {
                        leftOp = symbolScope.get(leftOp).register;
                    }
                    tinyNodeArrayList.add(new TinyNode("move", rightOp, "r" + randomTiny));
                    tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, "r" + randomTiny));
                }

                randomTiny += 1;
            }

            IRreg = rightOp;
        }
        else if(isInteger(leftOp)) {
            if (symbolScope.containsKey(leftOp)) {
                leftOp = symbolScope.get(leftOp).register;
            }

            printStoreOperation("STOREI", rightOp, IRreg);

            //System.out.println("-----------------------" +  tinyReg + " " + leftOp + " " + rightOp);

            add_reg_operation_stmt_2("move", rightOp, tinyReg);
            tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, tinyReg));
            this.operationNumber += 1;
        } else {


            if (currentScope.variableMap.containsKey(leftOp) && currentScope.variableMap.get(leftOp)[0].equals("INT")) {
                if (symbolScope.containsKey(leftOp)) {
                    leftOp = symbolScope.get(leftOp).register;
                }
                printStoreOperation("STOREI", rightOp, IRreg);
                add_reg_operation_stmt_2("move", rightOp, tinyReg);
                tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, tinyReg));
                this.operationNumber += 1;
            }
            else {
                if (symbolScope.containsKey(leftOp)) {
                    leftOp = symbolScope.get(leftOp).register;
                }

                printStoreOperation("STOREF", rightOp, IRreg);
                add_reg_operation_stmt_2("move", rightOp, tinyReg);
                tinyNodeArrayList.add(new TinyNode("cmpr", leftOp, tinyReg));
                this.operationNumber += 1;
            }

        }

        if (symbolScope.containsKey(leftOp)) {
            leftOp = symbolScope.get(leftOp).register;
        }

        switch(compOp) {

            case "<":
                System.out.println(";GE " + leftOp + " " + IRreg + " " + labelStack1.peek());
                tinyNodeArrayList.add(new TinyNode("jge", labelStack1.peek()));
                break;

            case ">":
                System.out.println(";LE " + leftOp + " " + IRreg + " " + labelStack1.peek());
                tinyNodeArrayList.add(new TinyNode("jle", labelStack1.peek()));
                break;

            case "<=":
                System.out.println(";GT " + leftOp + " " + IRreg + " " + labelStack1.peek());
                tinyNodeArrayList.add(new TinyNode("jgt", labelStack1.peek()));
                break;

            case ">=":
                System.out.println(";LT " + leftOp + " " + IRreg + " " + labelStack1.peek());
                tinyNodeArrayList.add(new TinyNode("jlt", labelStack1.peek()));
                break;

            case "=":
                System.out.println(";NE " + leftOp + " " + IRreg + " " + labelStack1.peek());
                tinyNodeArrayList.add(new TinyNode("jne", labelStack1.peek()));
                break;

            case "!=":
                System.out.println(";EQ " + leftOp + " " + IRreg + " " + labelStack1.peek());
                tinyNodeArrayList.add(new TinyNode("jeq", labelStack1.peek()));
                break;
        }
    }
}