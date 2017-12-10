
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
    private int labelNo;
    private static int tinyRegisterNumber;
    private SymbolsTable currentScope; // Used to refer to which functio is being parsed
    private static ArrayList<TinyNode> tinyNodeArrayList = new ArrayList<TinyNode>();

    private Stack<String> labelStack1 = new Stack<String>();
    private Stack<String> labelStack2 = new Stack<String>();
    private Stack<Boolean> elsePresentStack = new Stack<Boolean>();

    private HashMap<String, Scope> symbolScope = new HashMap<String, Scope>();

    private boolean condFlag;

    private String incr_stmt;

    private int randomTiny = 20; // Used for the some statements where the tinyRegister is no more used in the subsequent Tiny instructions

    private boolean elsePresent; // An Indicator to find if there is an else following the If Statement

    // IRNode List
    private IRNodeList irNodeList;

    private ArrayList<TinyNode> tinyNodes;

    private String[] globalVariables;

    /**
     * Constructor to detect the Parser Actions.
     * REMEMBER: This gets called only once.
     * */
    public Micro468Listener() {
        this.parentTree = new SymbolsTree(); // Initializing the Symbol Tree to the GLOBAL Scope
        this.blockNumber = 1;
        operationNumber = 1;
        labelNo = 1;
        condFlag = false;
        tinyRegisterNumber = 0;
        incr_stmt = "";
        elsePresent = false;
        irNodeList = new IRNodeList();
        tinyNodes = new ArrayList<TinyNode>();
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
     * This is used to push the Symbols to the Appropriate SymbolsTable
     * */
    public void pushSymbol(String parameters, SymbolsTable symbolsTable) {

        System.out.println(";" + parameters);

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
                       tinyNodes.add(new TinyNode("var", variable));
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
                       tinyNodes.add(new TinyNode("var", variable));
                       Symbol newVariable = new Symbol(variable, "FLOAT");
                       symbolsTable.addSymbol(newVariable);

                   }
               }

               // STRING
               else if (dataType.startsWith("STRING")) {
                   String[] variableNames = dataType.substring(6).trim().split(":=");
                   tinyNodeArrayList.add(new TinyNode("str", variableNames[0].trim(), variableNames[1].trim()));
                   tinyNodes.add(new TinyNode("str", variableNames[0].trim(), variableNames[1].trim()));
                   symbolsTable.addSymbol(new Symbol("STRING", variableNames[0].trim(), variableNames[1].trim()));
               }
           }
       }
   }

    public void readFunctionParameters(String parameters, SymbolsTable symbolsTable) {

//        // System.out.println("Inside read Function parameters");

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

//        // System.out.println("Exit readFunctionParameters");
    }

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext ctx) { // ParentScope or GLOBAL
        pushSymbol(ctx.getChild(0).getText(), parentTree.getParentScope());

        HashSet<String> symbolSet = parentTree.getCurrentScope().getSymbolSet();

        globalVariables = symbolSet.toArray(new String[symbolSet.size()]);

        // Pushing the Symbols to the GLOBAL Symbol Table
        System.out.println("; IR code");
        irNodeList.addNode(new IRNode("PUSH"));

        tinyNodeArrayList.add(new TinyNode("push"));

        irNodeList.addNode(new IRNode("JSR", "FUNC_id_main_L"));
        tinyNodeArrayList.add(new TinyNode("jsr", "FUNC_id_main_L"));

        irNodeList.addNode(new IRNode("HALT"));
        tinyNodeArrayList.add(new TinyNode("sys", "halt"));
    }

    @Override public void enterRead_stmt(MicroParser.Read_stmtContext ctx) {
        String right = ctx.getChild(2).getText();
        String[] words = right.split(",");

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
                printReadOperation("READI", words[i]);
                tinyNodeArrayList.add(new TinyNode("sys readi", word));
            }
            else if (currentType == "FLOAT") {
                printReadOperation("READF", words[i]);
                tinyNodeArrayList.add(new TinyNode("sys readr", word));
            }
            else {
                irNodeList.addNode(new IRNode("READS", words[i]));
                tinyNodeArrayList.add(new TinyNode("sys reads", word));
            }
        }
    }

    public void printReadOperation(String read, String toRead) {
        if (symbolScope.containsKey(toRead)) {
            toRead = symbolScope.get(toRead).register;
        }
        irNodeList.addNode(new IRNode(read, toRead));
    }

    @Override public void enterWrite_stmt(MicroParser.Write_stmtContext ctx) {
        String right = ctx.getChild(2).getText();
        String[] words = right.split(",");

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
                printWriteOperation("WRITEI", words[i]);
                tinyNodeArrayList.add(new TinyNode("sys", "writei", word));
            }
            else if (currentType == "FLOAT") {
                printWriteOperation("WRITEF", words[i]);
                tinyNodeArrayList.add(new TinyNode("sys", "writer", word));
            }
            else {
                irNodeList.addNode(new IRNode("WRITES", words[i]));
                tinyNodeArrayList.add(new TinyNode("sys", "writes", word));
            }
        }
    }

    public void printWriteOperation(String write, String toWrite) {
        if (symbolScope.containsKey(toWrite)) {
            toWrite = symbolScope.get(toWrite).register;
        }

        irNodeList.addNode(new IRNode(write, toWrite));
    }

    private boolean isVariableinGlobal(String variable) {
        return parentTree.getCurrentScope().variableMap.get(variable) != null;
    }

    /**
     * This gets called when the parser finds an assignment statement
     * */
    @Override
    public void enterAssign_stmt(MicroParser.Assign_stmtContext ctx) {
        String left = ctx.getChild(0).getChild(0).getText();
        String right = ctx.getChild(0).getChild(2).getText();

        //System.out.println("left: " + left + "right: " + right);

        if(isOnlyFunctionCall(right)) {
           // System.out.println("only function call: " + right);
            return;
        }

        int numberBefore = this.operationNumber;

        String postFixFunction = InfixToPostfix.infixToPostfixFunctions(right, this.operationNumber);
        int numberAfter = InfixToPostfix.registerNumber;

        if (numberAfter - numberBefore == 0) {

            System.out.println(";only one function call with operation: or no function call" + right);
            parsePostfix(right, left, postFixFunction);
        }
        else {
            parsePostfixFunctions(right, left, postFixFunction, numberBefore, numberAfter);
        }
    }

    private void parsePostfixFunctions(String right, String left, String postfix, int numberBefore, int numberAfter) {
        System.out.println(";=======================");
        System.out.println("Left: " + left + " right: " + right + " postfix: " + postfix);
        for (int i = numberBefore; i < numberAfter; i++) {
            String currentRegister = "!T" + i;
            String functionCall = InfixToPostfix.tempToFunctionMap.get(currentRegister);

            System.out.println(";current register = " + currentRegister + " current call " + functionCall);
            String functionName = InfixToPostfix.getFunctionName(functionCall);
            System.out.println("Function name = " + functionName);

            String[] functionParameters = InfixToPostfix.getFunctionParameters(functionCall);

            String parametersJoined = "";
            for (String f : functionParameters) {
                parametersJoined = parametersJoined + " " + f;
            }

            System.out.println("Function parameters = " + parametersJoined.trim());
        }
        System.out.printf("-----------------------");
    }


    private boolean isSymbolScopeInteger(String name) {
        System.out.printf(";name: " + name);
        if (symbolScope.get(name).decl_type.equals("INT")) {
            return true;
        }
        return false;
    }

    @Override public void enterCall_expr(MicroParser.Call_exprContext ctx) {
        boolean check = true;
        if (check) return;
        String function_name = ctx.getChild(0).getText();

//        String left = ctx.getParent().getParent().getParent().getParent().getChild(0).getText();
//        String right = ctx.getParent().getParent().getParent().getParent().getChild(2).getText();

        if (ctx.getParent().getParent().getParent().getParent().getChild(0).getText() == null) {
            //System.out.println(";left function call is wrong");
            return;
        }

        String left = ctx.getParent().getParent().getParent().getParent().getChild(0).getText();

        if  (ctx.getParent().getParent().getParent().getParent().getChild(2).getText() == null) {
            //System.out.println(";right function call is wrong");
            return;
        }

        String right = ctx.getParent().getParent().getParent().getParent().getChild(2).getText();

        // TODO: Not sure:

        String[] function_parameters = ctx.getChild(2).getText().split(",");
        irNodeList.addNode(new IRNode("PUSH", "r0"));
        irNodeList.addNode(new IRNode("PUSH", "r1"));
        irNodeList.addNode(new IRNode("PUSH", "r2"));
        irNodeList.addNode(new IRNode("PUSH", "r3"));

        irNodeList.addNode(new IRNode("PUSH", ";(return value)"));
        tinyNodeArrayList.add(new TinyNode("push"));


        for (int i = 0; i < function_parameters.length; i++) {
            // System.out.println("param: " + function_parameters[i]);
            String postfix = InfixToPostfix.infixToPostfix(function_parameters[i]);
            // System.out.println("postfix: " + postfix);

            parsePostfixForPush(right, left, postfix);
        }

        irNodeList.addNode(new IRNode("JSR", "FUNC_id_" + function_name + "_L"));
        tinyNodeArrayList.add(new TinyNode("jsr", "FUNC_id_" + function_name + "_L"));

        for (int i = 0; i < function_parameters.length; i++) {
            // System.out.println("param: " + function_parameters[i]);
            // String postfix = InfixToPostfix.infixToPostfix(function_parameters[i]);
            // System.out.println("postfix: " + postfix);

            irNodeList.addNode(new IRNode("POP"));
            tinyNodeArrayList.add(new TinyNode("pop"));
        }

        String location = "!T" + this.operationNumber;
        this.operationNumber += 1;

        if (isSymbolScopeInteger(left)) {

            parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
            tinyRegisterNumber = this.operationNumber - 2;

            irNodeList.addNode(new IRNode("POP", location));
            tinyNodeArrayList.add(new TinyNode("pop", "r" + tinyRegisterNumber));
            //add_reg_operation_stmt_2("move", "r" + tinyRegisterNumber, symbolScope.get(left).register);

            irNodeList.addNode(new IRNode("STOREI", location, symbolScope.get(left).register));
        }
        else {
            parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
            tinyRegisterNumber = this.operationNumber - 2;

            irNodeList.addNode(new IRNode("POP", location));
            tinyNodeArrayList.add(new TinyNode("pop", "r" + tinyRegisterNumber));
            //add_reg_operation_stmt_2("move", "r" + tinyRegisterNumber, symbolScope.get(left).register);

            irNodeList.addNode(new IRNode("STOREF", location, symbolScope.get(left).register));
        }

        irNodeList.addNode(new IRNode("POP", "r3"));
        irNodeList.addNode(new IRNode("POP", "r2"));
        irNodeList.addNode(new IRNode("POP", "r1"));
        irNodeList.addNode(new IRNode("POP", "r0"));
    }

   private void parsePostfixForPush(String right, String left, String postfix) {

        Stack<String> stack = new Stack<String>();

        String[] words = postfix.split(" ");
        //// System.out.println("split: " + Arrays.toString(words));
        if (words.length == 1) {

            irNodeList.addNode(new IRNode("PUSH", symbolScope.get(words[0]).register));
            tinyNodeArrayList.add(new TinyNode("push", symbolScope.get(words[0]).register));
            return;
        }

        for (int i = 0; i < words.length; i++) {
            String c = words[i];

            boolean foundNumber = false;

            if (isNumeric((c))) {

                // // System.out.println("isNumeric");

                foundNumber = true;
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                if (isInteger(c)) {
                    parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    irNodeList.addNode(new IRNode("STOREI", c, location));

                    //add_reg_operation_stmt_2("move", c, location);
                }
                else {

                    parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    printStoreOperation("STOREF", c, location);
                    //add_reg_operation_stmt_2("move", c, location);
                }
            }

            if (isOperator(c)) {

                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                String currentType = currentScope.variableMap.get(val1)[0];

                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));

                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else if (!foundNumber) {
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            String c = stack.pop();

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                String currentType = currentScope.variableMap.get(val1)[0];

                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));

                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else {
                irNodeList.addNode(new IRNode("PUSH", c));
                //add_reg_operation_stmt_3("push", c);
            }
        }
    }


    private void parsePostfix(String right, String left, String postfix) {

        Stack<String> stack = new Stack<String>();

        String[] words = postfix.split(" ");
        if (words.length == 1) {
            parseAssign_stmt(left, right);
            return;
        }

        for (int i = 0; i < words.length; i++) {
            String c = words[i];

            boolean foundNumber = false;

            if (isNumeric((c))) {

                foundNumber = true;
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                if (isInteger(c)) {
                    parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    irNodeList.addNode(new IRNode("STOREI", c, location));
                    //add_reg_operation_stmt_2("move", c, location);
                }
                else {

                    parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                    tinyRegisterNumber = this.operationNumber - 2;
                    stack.push(location);

                    printStoreOperation("STOREF", c, location);
                   // add_reg_operation_stmt_2("move", c, location);
                }
            }

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;
                //System.out.println("Error from this: " + val1 + " " + val2);

                String currentType;


                if(isVariableinGlobal(val1)) {
                    currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                }
                else {
                    currentType = currentScope.variableMap.get(val1)[0];
                }

                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));

                tinyRegisterNumber = this.operationNumber - 2;

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else if (!foundNumber) {
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            String c = stack.pop();

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                String currentType;


                if(isVariableinGlobal(val1)) {
                    currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                }
                else {
                    currentType = currentScope.variableMap.get(val1)[0];
                }

                parentTree.getCurrentScope().addRegister(location, currentType, "r" + Integer.toString(this.operationNumber - 2));

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
                    printStoreOperation("STOREI", c, left);
                    ///add_reg_operation_stmt_2("move", c, left);
                }
                else {
                    printStoreOperation("STOREF", c, left);
                    //add_reg_operation_stmt_2("move", c, left);
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
        System.out.println(";Print store: c " + c  + " left: " + left);
        irNodeList.addNode(new IRNode(store, c, left));
    }

    @Override public void enterReturn_stmt(MicroParser.Return_stmtContext ctx) {
        if (currentFunction.equals("main")) {
            this.returnRegister = 2;
        }
        String rightExpr = ctx.getChild(1).getText();
        String postfix = InfixToPostfix.infixToPostfix(rightExpr);

        if (isNumeric(postfix)) {

            if (isInteger(postfix)) {
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                irNodeList.addNode(new IRNode("STOREI", postfix, location));

                parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));

                // System.out.println("; STOREI " + location + " $" + returnRegister);
                irNodeList.addNode(new IRNode("STOREI", location, "$" + returnRegister));

                tinyRegisterNumber = this.operationNumber - 2;

                //add_reg_operation_stmt_2("move", postfix, location);
                //add_reg_operation_stmt_2("move", location,  "$" + returnRegister);

            }
            else {
                String location = "!T" + this.operationNumber;
                this.operationNumber += 1;

                // System.out.println(";STOREF " + postfix + " " + location);
                irNodeList.addNode(new IRNode("STOREF", postfix, location));

                parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));
                // System.out.println("; STOREF " + location + " $" + returnRegister);
                irNodeList.addNode(new IRNode("STOREF", location, "$" + returnRegister));

                tinyRegisterNumber = this.operationNumber - 2;

                //add_reg_operation_stmt_2("move", postfix, location);
                //add_reg_operation_stmt_2("move", location,  "$" + returnRegister);
            }

            // System.out.println("; UNLINK");
            irNodeList.addNode(new IRNode("UNLINK"));
            tinyNodeArrayList.add(new TinyNode("unlnk"));

            // System.out.println("; RET");
            irNodeList.addNode(new IRNode("RET"));
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
                    irNodeList.addNode(new IRNode("STOREI", register, "$" + returnRegister));
                    //add_reg_operation_stmt_2("move", register,  "$" + returnRegister);
                }
                else {
                    irNodeList.addNode(new IRNode("STOREF", register, "$" + returnRegister));
                    //add_reg_operation_stmt_2("move", register,  "$" + returnRegister);
                }
            }
            else {
                register = postfix;

                String currentType = "";
                if(isVariableinGlobal(postfix)) {
                    currentType = parentTree.getCurrentScope().variableMap.get(postfix)[0];
                }
                else {
                    currentType = currentScope.variableMap.get(postfix)[0];
                }

                if (currentType.equals("INT")) {
                    irNodeList.addNode(new IRNode("STOREI", register, "$" + returnRegister));
                    //add_reg_operation_stmt_2("move", register,  "$" + returnRegister);
                }
                else {
                    irNodeList.addNode(new IRNode("STOREF", register, "$" + returnRegister));
                    //add_reg_operation_stmt_2("move", register,  "$" + returnRegister);
                }
            }
        }
        else {
            String register = "!T" + Integer.toString(this.operationNumber - 1);
            String currentType = "";

            if(isVariableinGlobal(register)) {
                currentType = parentTree.getCurrentScope().variableMap.get(register)[0];
            }
            else {
                currentType = currentScope.variableMap.get(register)[0];
            }
            if (currentType.equals("INT")) {
                tinyRegisterNumber = this.operationNumber - 2;

                irNodeList.addNode(new IRNode("STOREI", "!T" + Integer.toString(this.operationNumber - 1), "$" + returnRegister));
                tinyNodeArrayList.add(new TinyNode("move", "r" + tinyRegisterNumber, " $" + returnRegister));
            }
            else {
                tinyRegisterNumber = this.operationNumber - 2;

                irNodeList.addNode(new IRNode("STOREF", "!T" + Integer.toString(this.operationNumber - 1), "$" + returnRegister));
                tinyNodeArrayList.add(new TinyNode("move", "r" + tinyRegisterNumber, " $" + returnRegister));
            }
        }

        irNodeList.addNode(new IRNode("UNLINK"));
        tinyNodeArrayList.add(new TinyNode("unlnk"));

        irNodeList.addNode(new IRNode("RET"));
        tinyNodeArrayList.add(new TinyNode("ret"));
    }

    private void choose_operation(String symbol, String type, String str1, String str2, String location) {
        if (symbol.equals("+")) {
            operation_stmt("ADD", type, str1, str2, location);
//            reg_operation_stmt("add", type, str1, str2, location);
        }
        else if (symbol.equals("*")) {
            operation_stmt("MULT", type, str1, str2, location);
//            reg_operation_stmt("mul", type, str1, str2, location);
        }
        else if (symbol.equals("/")) {
            operation_stmt("DIV", type, str1, str2, location);
//            reg_operation_stmt("div", type, str1, str2, location);
        }
        else if (symbol.equals("-")) {
            operation_stmt("SUB", type, str1, str2, location);
//            reg_operation_stmt("sub", type, str1, str2, location);
        }
        else {
            // System.out.println("None of the symbols...");
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



    private void operation_stmt(String operation, String type, String str1, String str2, String location) {
        if (symbolScope.containsKey(str1)) {
            str1 = symbolScope.get(str1).register;
        }
        if (symbolScope.containsKey(str2)) {
            str2 = symbolScope.get(str2).register;
        }
        if (type.equals("INT")) {
            irNodeList.addNode(new IRNode(operation + "I", str1, str2, location));
        }
        else {
            irNodeList.addNode(new IRNode(operation + "F", str1, str2, location));
        }
    }


    // TODO: Refactor this fucktard
    private void parseAssign_stmt(String left, String right) {
        if (left == null) {
            return;
        }
        if (right == null) {
            return;
        }
        if (!isNumeric(right)) {
            // TODO: Might have to check for the GLOBAL Scope and also might have to change parentTree.getCurrentScope()
            if (parentTree.getCurrentScope().variableMap.containsKey(left) && parentTree.getCurrentScope().variableMap.containsKey(right)) {
                String dataType = parentTree.getCurrentScope().variableMap.get(left)[0];
                if (dataType.equals("INT")) {
                    printStoreOperation("STOREI", right, left);

                    tinyNodeArrayList.add(new TinyNode("move", right, "r" + randomTiny));
                    tinyNodeArrayList.add(new TinyNode("move", "r" + randomTiny, left));
                    randomTiny += 1;
                }
                else {
                    printStoreOperation("STOREF", right, left);

                    tinyNodeArrayList.add(new TinyNode("move", right, "r" + randomTiny));
                    tinyNodeArrayList.add(new TinyNode("move", "r" + randomTiny, left));
                    randomTiny += 1;
                }
            }
            else {
                String currentType = "INT";
                if (currentScope.variableMap.containsKey(left)) {
                    currentType = currentScope.variableMap.get(left)[0];
                }
                else if (currentScope.variableMap.containsKey(right)) {
                    currentType = currentScope.variableMap.get(right)[0];
                }

                if (currentType.equals("INT")) {
                    printStoreOperation("STOREI", right, left);
                }
                else  {
                    printStoreOperation("STOREF", right, left);
                }
            }
            return;
        }

        if (isInteger(right)) {
            String location = "!T" + this.operationNumber;
            this.operationNumber += 1;

            printStoreOperation("STOREI", right, location);
            parentTree.getCurrentScope().addRegister(location,  "INT", "r" + Integer.toString(this.operationNumber - 2));

            printStoreOperation("STOREI", location, left);
            tinyRegisterNumber += 1;
            //add_reg_operation_stmt_2("move", right, location);
            //add_reg_operation_stmt_2("move", location, left);
        }
        else {
            String location = "!T" + this.operationNumber;
            this.operationNumber += 1;

            printStoreOperation("STOREF", right, location);
            parentTree.getCurrentScope().addRegister(location,  "FLOAT", "r" + Integer.toString(this.operationNumber - 2));

            printStoreOperation("STOREF", location, left);
            tinyRegisterNumber += 1;
            //add_reg_operation_stmt_2("move", right, location);
            //add_reg_operation_stmt_2("move", location, left);
        }
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isInteger(String str) {
        return str.matches("\\d+");
    }

    private static boolean isOnlyFunctionCall(String str) {
        if (str.trim().startsWith("\"") || (str.trim().startsWith("\'"))) {
            return false;
        }

//        boolean val = str.contains(",");
//        if (val) {
//            return true;
//        }

        //String regex = "^[a-zA-Z]+[a-zA-Z0-9_]*\\(.*\\)$";
        String regex = "^[a-zA-Z]+[a-zA-Z0-9_]*\\([a-zA-Z0-9_+/\\-*.,]*\\)$";
        Pattern funcPattern = Pattern.compile(regex);

        Matcher m = funcPattern.matcher(str);

        //System.out.println("; FUNCTION CALL: " + str + " " + str.matches(regex));

        return m.matches();
    }

    private static boolean isOperator(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("(") || c.equals(")");
    }

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void exitPgm_body(MicroParser.Pgm_bodyContext ctx) {

        // Print IR Code
        irNodeList.printIRNodeList();

//        System.out.println("; +++++++++++++");
//        System.out.println("; CUSTOM NODE LIST");
//        System.out.println("; +++++++++++++");
//        irNodeList.createCustomNodeList();
//        irNodeList.printCustomNodeList();

        //System.out.println(";tiny code");
        //printTinyNodeList(tinyNodeArrayList);
        //// System.out.println("sys halt");

//        System.out.println("; +++++++++++++");
//        System.out.println("; GEN KILL LIST");
//        System.out.println("; +++++++++++++");
//        GenKillNodeList genKillNodeList = new GenKillNodeList(irNodeList);
//        genKillNodeList.genKillNodeListInit(globalVariables);
//        genKillNodeList.printGenKillNodeList();

        convertIRtoTiny();
    }

    public String convertOperand(String op) {
        String check1 = op;

        if (parentTree.getCurrentScope().variableMap.containsKey(check1)) {
            if (parentTree.getCurrentScope().variableMap.get(check1)[1] != null) {
                check1 = parentTree.getCurrentScope().variableMap.get(check1)[1];
            }
        }

        if (symbolScope.containsKey(check1)) {
            check1 = symbolScope.get(check1).register;
        }

        return check1;
    }

    public void convertIRtoTiny() {
        int localVariables = 0;
        int currentTemporaryLocation = 1;
        ArrayList<IRNode> irNodes = irNodeList.getIrNodeList();

        HashMap<String, String> temporaries = new HashMap<String, String>();


        for(IRNode irNode: irNodes) {

            String opCode = irNode.getOpCode();
            String firstOp = irNode.getFirstOp();
            String secondOp = irNode.getSecondOp();
            String thirdOp = irNode.getThirdOp();

            //System.out.println(opCode + " " + firstOp + " " + secondOp + " " + thirdOp);
            // EQ, NE, LE, LT, GE, GT & cmpi/r
            if(opCode.equals("GT")) {
                // check first/second op is int/float
                // cmpi/r
                // check register, temporary, stack, etc. for first and second op

                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "cmp");

                tinyNodes.add(new TinyNode("jgt", thirdOp));
            }
            else if(opCode.equals("GE")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "cmp");

                tinyNodes.add(new TinyNode("jge", thirdOp));
            }
            else if(opCode.equals("LT")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "cmp");

                tinyNodes.add(new TinyNode("jlt", thirdOp));
            }
            else if(opCode.equals("LE")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "cmp");

                tinyNodes.add(new TinyNode("jle", thirdOp));
            }
            else if(opCode.equals("EQ")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "cmp");
                tinyNodes.add(new TinyNode("jeq", thirdOp));
            }
            else if(opCode.equals("NE")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "cmp");

                tinyNodes.add(new TinyNode("jne", thirdOp));
            }
            // LINK, UNLINK, RET, HALT
            else if(opCode.equals("LINK")) {
                localVariables = Integer.parseInt(firstOp);
                currentTemporaryLocation = localVariables;

                int totalSpace = localVariables + this.operationNumber;

                tinyNodes.add(new TinyNode("link", Integer.toString(totalSpace)));
            }
            else if (opCode.equals("UNLINK")) {
                tinyNodes.add(new TinyNode("unlnk"));
            }
            else if (opCode.equals("RET")) {
                tinyNodes.add(new TinyNode("ret"));
            }
            else if (opCode.equals("HALT")) {
                tinyNodes.add(new TinyNode("sys", "halt"));
            }
            // JSR, JUMP, LABEL
            else if (opCode.equals("JSR")) {
                tinyNodes.add(new TinyNode("jsr", firstOp));
            }
            else if (opCode.equals("JUMP")) {
                tinyNodes.add(new TinyNode("jmp", firstOp));
            }
            else if (opCode.equals("LABEL")) {
                tinyNodes.add(new TinyNode("label", firstOp));
            }
            // PUSH
            // POP
            else if (opCode.equals("PUSH")) {
                // DONE
                if (firstOp.equals("NULL")) {
                    tinyNodes.add(new TinyNode("push"));
                }
                else {
                    if (firstOp.startsWith("!T")) {
                        //tinyNodes.add(new TinyNode("push using temporary " + firstOp));
                        String updated = getTemporary(temporaries, firstOp, currentTemporaryLocation);
                        currentTemporaryLocation = updateTemporaryCount(firstOp, updated, currentTemporaryLocation);

                        tinyNodes.add(new TinyNode("push", updated, ";using temp"));
                    }
                    else {
                        tinyNodes.add(new TinyNode("push", convertOperand(firstOp)));
                    }
                }
            }
            else if (opCode.equals("POP")) {
                // DONE
                if (firstOp.equals("NULL")) {
                    tinyNodes.add(new TinyNode("pop"));
                }
                else {
                    if (firstOp.startsWith("!T")) {
                        //tinyNodes.add(new TinyNode("pop using temporary " + firstOp));
                        String updated = getTemporary(temporaries, firstOp, currentTemporaryLocation);
                        currentTemporaryLocation = updateTemporaryCount(firstOp, updated, currentTemporaryLocation);

                        tinyNodes.add(new TinyNode("pop", updated, ";using temp"));
                    }
                    else {
                        tinyNodes.add(new TinyNode("pop", convertOperand(firstOp)));
                    }
                }
            }
            // STOREI/F/S, READI/F, WRITEI/F/S
            else if (opCode.startsWith("STORE")) {
                String updated = getTemporary(temporaries, firstOp, currentTemporaryLocation);
                currentTemporaryLocation = updateTemporaryCount(firstOp, updated, currentTemporaryLocation);

                String updated1 = getTemporary(temporaries, secondOp, currentTemporaryLocation);
                currentTemporaryLocation = updateTemporaryCount(secondOp, updated1, currentTemporaryLocation);

                boolean check = parentTree.getCurrentScope().variableMap.containsKey(updated);
                boolean check1 = parentTree.getCurrentScope().variableMap.containsKey(updated1);

                tinyNodes.add(new TinyNode(";" + updated + " " + updated1 + " " + check + " " + check1));

                if ((updated.startsWith("$") && updated1.startsWith("$")) || (check && check1) || (updated.startsWith("$") && check1) || (updated1.startsWith("$") && check) ) {
                    tinyNodes.add(new TinyNode("move", updated, "r0"));
                    tinyNodes.add(new TinyNode("move", "r0", updated1));
                }
                else {
                    tinyNodes.add(new TinyNode("move", updated, updated1));
                }
            }
            else if (opCode.startsWith("READ")) {
                if (opCode.equals("READI")) {
                    tinyNodes.add(new TinyNode("sys", "readi", firstOp));
                }
                else if (opCode.equals("READF")) {
                    tinyNodes.add(new TinyNode("sys", "readr", firstOp));
                }
            }
            else if (opCode.startsWith("WRITE")) {
                if (opCode.equals("WRITEI")) {
                    tinyNodes.add(new TinyNode("sys", "writei", firstOp));
                }
                else if (opCode.equals("WRITEF")) {
                    tinyNodes.add(new TinyNode("sys", "writer", firstOp));
                }
                else if (opCode.equals("WRITES")) {
                    tinyNodes.add(new TinyNode("sys", "writes", firstOp));
                }
            }

            // ADD, SUB, MULT, DIV
            else if (opCode.startsWith("ADD")) {
                // TODO: Check if store handles
//                tinyNodes.add(new TinyNode("move", firstOp, "r0"));
//                tinyNodes.add(new TinyNode("move", secondOp, "r1"));
//                if (opCode.equals("ADDI")) {
//                    tinyNodes.add(new TinyNode("addi", "r0", "r1"));
//
//                }
//                else if (opCode.equals("ADDF")) {
//                    tinyNodes.add(new TinyNode("addr", "r0", "r1"));
//                }

                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "add");

                String updated = getTemporary(temporaries, thirdOp, currentTemporaryLocation);
                currentTemporaryLocation = updateTemporaryCount(thirdOp, updated, currentTemporaryLocation);

                tinyNodes.add(new TinyNode("move", "r1", updated + " ; + STORE_HANDLED"));
            }
            else if (opCode.startsWith("MULT")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, firstOp, secondOp, "mul");

                String updated = getTemporary(temporaries, thirdOp, currentTemporaryLocation);
                currentTemporaryLocation = updateTemporaryCount(thirdOp, updated, currentTemporaryLocation);

                tinyNodes.add(new TinyNode("move", "r1", updated + " ; * STORE_HANDLED"));
            }
            else if (opCode.startsWith("SUB")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, secondOp, firstOp, "sub");

                String updated = getTemporary(temporaries, thirdOp, currentTemporaryLocation);
                currentTemporaryLocation = updateTemporaryCount(thirdOp, updated, currentTemporaryLocation);

                tinyNodes.add(new TinyNode("move", "r1", updated + " ; - STORE_HANDLED"));
            }
            else if (opCode.startsWith("DIV")) {
                currentTemporaryLocation = getCompOpTiny(temporaries, currentTemporaryLocation, secondOp, firstOp, "div");

                String updated = getTemporary(temporaries, thirdOp, currentTemporaryLocation);
                currentTemporaryLocation = updateTemporaryCount(thirdOp, updated, currentTemporaryLocation);

                tinyNodes.add(new TinyNode("move", "r1", updated + " ; / STORE_HANDLED"));
            }
        }

        printTinyNodes();
    }

    private int updateTemporaryCount(String res, String converted, int currentTemporaryLocation) {
        if (res.equals(converted)) {
            return currentTemporaryLocation;
        }
        return currentTemporaryLocation + 1;
    }

    private String getTemporary(HashMap<String, String> temporaries, String op, int currentTemporaryLocation) {
        if(op.startsWith("!T")) {
            if (temporaries.containsKey(op)) {
                op = temporaries.get(op);
            }
            else {
                temporaries.put(op, "$-" + currentTemporaryLocation);
                op = "$-" + currentTemporaryLocation;
            }
        }
        return op;
    }

    private int getCompOpTiny(HashMap<String, String> temporaries, int currentTemporaryLocation, String firstOp, String secondOp, String operation) {
        boolean f = false;
        if(isVariableinGlobal(firstOp)) {
            if (parentTree.getCurrentScope().variableMap.get(firstOp)[0].equals("FLOAT")) {
                f = true;
            }
        }

        if(isVariableinGlobal(secondOp)) {
            if (parentTree.getCurrentScope().variableMap.get(secondOp)[0].equals("FLOAT")) {
                f = true;
            }
        }

        if(firstOp.startsWith("!T")) {
            if (temporaries.containsKey(firstOp)) {
                firstOp = temporaries.get(firstOp);
            }
            else {
                temporaries.put(firstOp, "$-" + currentTemporaryLocation);
                firstOp = "$-" + currentTemporaryLocation;
                currentTemporaryLocation += 1;
            }
        }

        if (secondOp.startsWith("!T")) {
            if (temporaries.containsKey(secondOp)) {
                secondOp = temporaries.get(secondOp);
            }
            else {
                temporaries.put(secondOp, "$-" + currentTemporaryLocation);
                secondOp = "$-" + currentTemporaryLocation;
                currentTemporaryLocation += 1;
            }
       }

        tinyNodes.add(new TinyNode("; handled?"));
        tinyNodes.add(new TinyNode("move", firstOp, "r0")); // check if temp
        tinyNodes.add(new TinyNode("; handled2?"));
        tinyNodes.add(new TinyNode("move", secondOp, "r1")); // check if temp

        if (f) {
            tinyNodes.add(new TinyNode(operation + "r", "r0", "r1")); // check if temp
        }
        else {
            tinyNodes.add(new TinyNode(operation + "i", "r0", "r1")); // check if temp
        }

        return currentTemporaryLocation;
    }

    public void printTinyNodes() {
        for (TinyNode tinyNode : tinyNodes) {
            System.out.println(tinyNode.toString());
        }
    }


    /**
     * This gets called when the parser enters the Function
     */
    @Override
    public void enterFunc_body(MicroParser.Func_bodyContext ctx) { // currentScope

        String functionName =  ctx.getParent().getChild(2).getText();
        currentFunction = functionName;

        irNodeList.addNode(new IRNode("LABEL", "FUNC_id_" + functionName + "_L"));
        tinyNodeArrayList.add(new TinyNode("label", "FUNC_id_" + functionName + "_L"));

        int totalRegisters = currentScope.variableMap.size();

        pushSymbol(ctx.getChild(0).getText(), currentScope);

        totalRegisters = currentScope.variableMap.size() - totalRegisters + 1;

        irNodeList.addNode(new IRNode("LINK", Integer.toString(totalRegisters)));
        tinyNodeArrayList.add(new TinyNode("link", Integer.toString(totalRegisters)));
    }

    /**
     * This gets called when the parser exits the Function
     */
    @Override
    public void exitFunc_body(MicroParser.Func_bodyContext ctx) {
        //System.out.println("==================");

        int lastIndex = tinyNodeArrayList.size() - 1;

        //System.out.println(tinyNodeArrayList.get(lastIndex).toString());
        if (!tinyNodeArrayList.get(lastIndex).toString().startsWith("ret")) {
            irNodeList.addNode(new IRNode("UNLINK"));
            irNodeList.addNode(new IRNode("RET"));
            tinyNodeArrayList.add(new TinyNode("unlnk"));
            tinyNodeArrayList.add(new TinyNode("ret"));
        }
    }

    /**
     * This gets called when the parser hits the Function Declaration
     * */
    @Override public void enterFunc_decl(MicroParser.Func_declContext ctx) {

        // Function Parameters
        String functionParameters = ctx.getChild(4).getText();
        // Function Name
        String functionName = ctx.getChild(2).getText();

        // System.out.println("Function Name: " + functionName);
        // System.out.println("Function Parameters: " + functionParameters);

        // Create a new SymbolsTable for the Function
        SymbolsTable functionSymbolsTable = new SymbolsTable(functionName);
        currentScope = functionSymbolsTable; // Setting the current Scope to the Function Scope

        // Adding the New Function as a child to the Program
        parentTree.getCurrentScope().addChild(functionSymbolsTable);

        readFunctionParameters(functionParameters, functionSymbolsTable);

        String type = "ARGS";
        addToSymbolScope(functionParameters, functionName, type);
        type = "LOCALS";
        addToSymbolScope(ctx.getChild(7).getChild(0).getText(), functionName, type);
    }

    private void printTinyNodeList(ArrayList<TinyNode> tinyNodeArrayList) {
        for(TinyNode tinyNode: tinyNodeArrayList) {
            System.out.println(tinyNode.toString());
        }
    }


    public void printSymbolScope() {
        List<String> keys = new ArrayList<String>(symbolScope.keySet());
        for (int i = 0; i < keys.size(); i++) {
             System.out.println("key = " + keys.get(i));
            symbolScope.get(keys.get(i)).print();
        }
    }

    public void addToSymbolScope(String parameters, String function, String type) {

        // System.out.println("Inside read Function parameters");

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

        // System.out.println("Exit readFunctionParameters");
    }

    /**
     * This gets called whenever the parser sees a variable declaration
     * */
    @Override
    public void enterDecl(MicroParser.DeclContext ctx) {
         // System.out.println(ctx.getChild(0).getText());
    }

    /**
     * This gets called whenever parser detects an IF Statement
     * */
    @Override public void enterIf_stmt(MicroParser.If_stmtContext ctx) {

         // System.out.println("enterIf");

        SymbolsTable ifBlock = new SymbolsTable(getBlockNumber());

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(ifBlock);

        condFlag = true;

/*
        String conditionExpr = ctx.getChild(2).getText();

        String decl = ctx.getChild(4).getText();
        String stmt_list = ctx.getChild(5).getText();
*/

        if(!ctx.getChild(6).getText().equals("")) {
            elsePresentStack.push(true);
        }
        else {
            elsePresentStack.push(false);
        }
    }

    @Override
    public void enterCond(MicroParser.CondContext ctx) {

        String leftExpr = ctx.getChild(0).getText();
        String compOp = ctx.getChild(1).getText();
        String rightExpr = ctx.getChild(2).getText();

        labelStack1.push(getLabel());

        if(condFlag) { // Entered If Statement
            labelStack2.push(getLabel());
        } // Enter If Statement

        if (isNumeric(rightExpr) || parentTree.getCurrentScope().variableMap.containsKey(rightExpr) || currentScope.variableMap.containsKey(rightExpr)) {
            // Right Operand is either a number(INT or FLOAT or just a variable)
            checkCompOp(compOp, leftExpr, rightExpr);
        } else {
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

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(elseBlock);

/*        if(ctx.getChild(1) != null) {
            String localDecl = ctx.getChild(1).getText();
            String localStmt = ctx.getChild(2).getText();
        }*/

        if(elsePresentStack.peek()) {
            irNodeList.addNode(new IRNode("JUMP",labelStack2.peek()));
            tinyNodeArrayList.add(new TinyNode("jmp", labelStack2.peek()));

            String labelName = labelStack1.pop();
            irNodeList.addNode(new IRNode("LABEL", labelName));
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

            irNodeList.addNode(new IRNode("LABEL", labelName));
            tinyNodeArrayList.add(new TinyNode("label", labelName));
        } else {

            irNodeList.addNode(new IRNode("JUMP",labelStack2.peek()));
            tinyNodeArrayList.add(new TinyNode("jmp", labelStack2.peek()));

            labelName = labelStack1.pop();

            irNodeList.addNode(new IRNode("LABEL", labelName));
            tinyNodeArrayList.add(new TinyNode("label", labelName));

            labelName = labelStack2.pop();

            irNodeList.addNode(new IRNode("LABEL", labelName));
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

        irNodeList.addNode(new IRNode("LABEL", labelStack2.peek()));
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

        irNodeList.addNode(new IRNode("JUMP", labelName));
        tinyNodeArrayList.add(new TinyNode("jmp", labelName));

        labelName = labelStack1.pop();

        irNodeList.addNode(new IRNode("LABEL", labelName));
        tinyNodeArrayList.add(new TinyNode("label", labelName));
    }

    private void printStack(Stack<String> labelStack) {
         System.out.println(Arrays.toString(labelStack.toArray()));
    }

    private void printElsePresentStack(Stack<Boolean> elsePresentStack) {
         System.out.println(Arrays.toString(elsePresentStack.toArray()));
    }

    // TODO: Refactor
    public void checkCompOp(String compOp, String leftOp, String rightOp) {
        // System.out.println("compOp " + compOp + " leftOp " + leftOp + " rightOp " + rightOp);

        String IRreg = "!T" + this.operationNumber;

        String tinyReg = "r" + (this.operationNumber - 1);

        // TODO: Refactor logic

        if (parentTree.getCurrentScope().variableMap.containsKey(rightOp)) {

            // if rightOp is a register
            if(rightOp.trim().startsWith("!")) {
                if(isVariableinGlobal(leftOp)) {
                    if (parentTree.getCurrentScope().variableMap.get(leftOp)[0].equals("FLOAT")) {
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

//            add_reg_operation_stmt_2("move", rightOp, tinyReg);
            tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, tinyReg));
            this.operationNumber += 1;
        } else {
            if (currentScope.variableMap.containsKey(leftOp) && currentScope.variableMap.get(leftOp)[0].equals("INT")) {
                if (symbolScope.containsKey(leftOp)) {
                    leftOp = symbolScope.get(leftOp).register;
                }
                printStoreOperation("STOREI", rightOp, IRreg);
//                add_reg_operation_stmt_2("move", rightOp, tinyReg);
                tinyNodeArrayList.add(new TinyNode("cmpi", leftOp, tinyReg));
                this.operationNumber += 1;
            }
            else {
                if (symbolScope.containsKey(leftOp)) {
                    leftOp = symbolScope.get(leftOp).register;
                }

                printStoreOperation("STOREF", rightOp, IRreg);
//                add_reg_operation_stmt_2("move", rightOp, tinyReg);
                tinyNodeArrayList.add(new TinyNode("cmpr", leftOp, tinyReg));
                this.operationNumber += 1;
            }
        }

        if (symbolScope.containsKey(leftOp)) {
            leftOp = symbolScope.get(leftOp).register;
        }

        processCompOp(compOp, leftOp, IRreg);
    }

    private void processCompOp(String compOp, String leftOp, String IRreg) {
        switch(compOp) {

            case "<":
                irNodeList.addNode(new IRNode("GE", leftOp, IRreg, labelStack1.peek()));
                tinyNodeArrayList.add(new TinyNode("jge", labelStack1.peek()));
                break;

            case ">":
                irNodeList.addNode(new IRNode("LE", leftOp, IRreg, labelStack1.peek()));
                tinyNodeArrayList.add(new TinyNode("jle", labelStack1.peek()));
                break;

            case "<=":
                irNodeList.addNode(new IRNode("GT", leftOp, IRreg, labelStack1.peek()));
                tinyNodeArrayList.add(new TinyNode("jgt", labelStack1.peek()));
                break;

            case ">=":
                irNodeList.addNode(new IRNode("LT", leftOp, IRreg, labelStack1.peek()));
                tinyNodeArrayList.add(new TinyNode("jlt", labelStack1.peek()));
                break;

            case "=":
                irNodeList.addNode(new IRNode("NE", leftOp, IRreg, labelStack1.peek()));
                tinyNodeArrayList.add(new TinyNode("jne", labelStack1.peek()));
                break;

            case "!=":
                irNodeList.addNode(new IRNode("EQ", leftOp, IRreg, labelStack1.peek()));
                tinyNodeArrayList.add(new TinyNode("jeq", labelStack1.peek()));
                break;
        }
    }
}

/*
    private void add_reg_operation_stmt_3(String operation, String str1) {
//        String check1 = str1;
//        if (parentTree.getCurrentScope().variableMap.containsKey(check1)) {
//            if (parentTree.getCurrentScope().variableMap.get(check1)[1] != null) {
//                check1 = parentTree.getCurrentScope().variableMap.get(check1)[1];
//            }
//        }
//
//        if (symbolScope.containsKey(check1)) {
//            check1 = symbolScope.get(check1).register;
//        }
//
//        tinyNodeArrayList.add(new TinyNode(operation, check1));
    }

    private void add_reg_operation_stmt(String operation, String str1, String location) {
        // TODO: Need to refactor this
//        if (str1.trim().startsWith("!")) {
//            if (parentTree.getCurrentScope().variableMap.get(str1)[1] == null) {
//                if (symbolScope.containsKey(str1)) {
//                    str1 = symbolScope.get(str1).register;
//                }
//                tinyNodeArrayList.add(new TinyNode(operation, str1, parentTree.getCurrentScope().variableMap.get(location)[1]));
//            } else {
//                tinyNodeArrayList.add(new TinyNode(operation, parentTree.getCurrentScope().variableMap.get(str1)[1],
//                        parentTree.getCurrentScope().variableMap.get(location)[1]));
//            }
//        } else {
//            if (parentTree.getCurrentScope().variableMap.get(str1)[1] == null) {
//            //if (currentScope.variableMap.get(str1)[1] == null) { // TODO: Crashing in Here again
//                if (symbolScope.containsKey(str1)) {
//                    str1 = symbolScope.get(str1).register;
//                }
//                tinyNodeArrayList.add(new TinyNode(operation, str1, parentTree.getCurrentScope().variableMap.get(location)[1]));
//            } else {
//                tinyNodeArrayList.add(new TinyNode(operation, parentTree.getCurrentScope().variableMap.get(str1)[1],
//                        parentTree.getCurrentScope().variableMap.get(location)[1]));
//            }
//        }
    }
    private void add_reg_operation_stmt_2(String operation, String str1, String location) {
//        String check1 = str1;
//        String check2 = location;
//        if (parentTree.getCurrentScope().variableMap.containsKey(check1)) {
//            if (parentTree.getCurrentScope().variableMap.get(check1)[1] != null) {
//               check1 = parentTree.getCurrentScope().variableMap.get(check1)[1];
//            }
//        }
//        if (parentTree.getCurrentScope().variableMap.containsKey(check2)) {
//            if (parentTree.getCurrentScope().variableMap.get(check2)[1] != null) {
//                check2 = parentTree.getCurrentScope().variableMap.get(check2)[1];
//            }
//        }
//
//        if (symbolScope.containsKey(check1)) {
//            check1 = symbolScope.get(check1).register;
//        }
//
//
//        if (symbolScope.containsKey(check2)) {
//            check2 = symbolScope.get(check2).register;
//        }
//
//        if (check1.startsWith("$") && check2.startsWith("$") && operation.equals("move")) {
//            String reg = "r" + Integer.toString(this.operationNumber - 2);
//            tinyNodeArrayList.add(new TinyNode(operation, check1, reg));
//            tinyNodeArrayList.add(new TinyNode(operation, reg, check2));
//
//            return;
//        }
//
//        tinyNodeArrayList.add(new TinyNode(operation, check1, check2));
    }
*/

//    private void reg_operation_stmt(String operation, String type, String str1, String str2, String location) {
//
//        //add_reg_operation_stmt("move", str1, location);
//        if (type.equals("INT")) {
//            //add_reg_operation_stmt(operation + "i", str2, location);
//        }
//        else {
//            //add_reg_operation_stmt(operation + "r", str2, location);
//        }
//    }
