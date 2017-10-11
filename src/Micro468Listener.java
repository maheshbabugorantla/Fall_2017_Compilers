import java.io.*;
import java.util.*;
import java.lang.Exception;

/**
 * This Listener Class implements the MicroBaseListener Interface to detect the path of the Parser Tree
 * */

public class Micro468Listener extends MicroBaseListener {

    private SymbolsTree parentTree;
    private int blockNumber;
    private int operationNumber;

    private SymbolsTable currentScope; // Used to refer to which functio is being parsed

    /**
     * Constructor to detect the Parser Actions.
     * REMEMBER: This gets called only once.
     * */
    public Micro468Listener() {
        this.parentTree = new SymbolsTree(); // Initializing the Symbol Tree to the GLOBAL Scope
        this.blockNumber = 1;
        operationNumber = 1;
    }

    private String getBlockNumber() {
        return "BLOCK " + blockNumber++;
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
                       Symbol newVariable = new Symbol(variable, "INT");
                       symbolsTable.addSymbol(newVariable);
                   }
               }

               // FLOAT
               else if (dataType.startsWith("FLOAT")) {
                   String[] variableNames = dataType.substring(5).split(",");

                   // Adding all the Variable Names
                   for(String variable: variableNames) {
                       Symbol newVariable = new Symbol(variable, "FLOAT");
                       symbolsTable.addSymbol(newVariable);

                   }
               }

               // STRING
               else if (dataType.startsWith("STRING")) {
                   String[] variableNames = dataType.substring(6).trim().split(":=");
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
        pushSymbol(ctx.getChild(0).getText(), parentTree.getParentScope());
    }


    /**
     * This gets called when the parser finds an assignment statement
     * */
    @Override
    public void enterAssign_stmt(MicroParser.Assign_stmtContext ctx) {
        String left = ctx.getChild(0).getChild(0).getText();
        String right = ctx.getChild(0).getChild(2).getText();

        String postfix = InfixToPostfix.convertStringToPostfix(right);

        System.out.println("\n\nPrinting postfix");
        System.out.println(postfix);

        parsePostfix(right, left, postfix);
    }

    private static boolean isOperator(String c)
    {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("(") || c.equals(")");
    }

    private void parsePostfix(String right, String left, String postfix) {
        System.out.println("Parse Postfix");

        Stack<String> stack = new Stack<String>();

        String[] words = postfix.split(" ");

        if (words.length == 1) {
            String location = "$T" + this.operationNumber;
            parseAssign_stmt(left, right, location);
            this.operationNumber += 1;
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
                    parentTree.getCurrentScope().addRegister(location, "INT");
                    stack.push(location);

                    System.out.println(";STOREI " + c + " " + location);
                }
                else {
                    parentTree.getCurrentScope().addRegister(location, "FLOAT");
                    stack.push(location);

                    System.out.println(";STOREF " + c + " " + location);
                }
            }

            if (isOperator(c)) {
                String val2 = stack.pop();
                String val1 = stack.pop();
                String location = "$T" + this.operationNumber;
                this.operationNumber += 1;

                String currentType = parentTree.getCurrentScope().variableMap.get(val1)[0];
                parentTree.getCurrentScope().addRegister(location, currentType);

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
                parentTree.getCurrentScope().addRegister(location, currentType);

                choose_operation(c, currentType, val1, val2, location);

                stack.push(location);
            }
            else {
                String currentType = parentTree.getCurrentScope().variableMap.get(c)[0];

                if (currentType.equals("INT")) {
                    System.out.println(";STOREI " + " " + c + " " + left);
                }
                else {
                    System.out.println(";STOREF " + " " + c + " " + left);
                }
            }
        }
    }

    private static void choose_operation(String symbol, String type, String str1, String str2, String location) {
        if (symbol.equals("+")) {
            operation_stmt("ADD", type, str1, str2, location);
        }
        else if (symbol.equals("*")) {
            operation_stmt("MULT", type, str1, str2, location);
        }
        else if (symbol.equals("/")) {
            operation_stmt("DIV", type, str1, str2, location);
        }
        else if (symbol.equals("-")) {
            operation_stmt("SUB", type, str1, str2, location);
        }
        else {
            System.out.println("None of the symbols...");
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



    private static void parseAssign_stmt(String left, String right, String location) {
        if (isNumeric(right) == false) {
            return;
        }

        if (isInteger(right)) {
            System.out.println(";STOREI " + right + " " + location);
            System.out.println(";STOREI " + location + " " + left);
        }
        else {
            System.out.println(";STOREF " + right + " " + location);
            System.out.println(";STOREF " + location + " " + left);
        }
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
    }

    /**
     * This gets called when the parser hits the Function Declaration
     * */
    @Override public void enterFunc_decl(MicroParser.Func_declContext ctx) {

        // Function Name
        // System.out.println("Function Name: " + ctx.getChild(2).getText());

        // Function Parameters
        // System.out.println("Function Parameters: " + ctx.getChild(4).getText()); // Function Paramters

        String functionParameters = ctx.getChild(4).getText();
        String functionName = ctx.getChild(2).getText();

        // Create a new SymbolsTable for the Function
        SymbolsTable functionSymbolsTable = new SymbolsTable(functionName);
        currentScope = functionSymbolsTable; // Setting the current Scope to the Function Scope



        // System.out.println(parentTree.getCurrentScope().variableMap.get("a")[0]);

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

    /**
     * This gets called whenever parser detects an IF Statement
     * */
    @Override public void enterIf_stmt(MicroParser.If_stmtContext ctx) {

        SymbolsTable ifBlock = new SymbolsTable(getBlockNumber());
        currentScope = ifBlock; // Setting the Scope to the IF Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(ifBlock);

        /**
         * TODO: Need to Check for Conditional Expr
         * */

        /**
         * TODO: Need to Check for Local Parameters
         * */
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
         * TODO: Need to Check for Conditional Expr
         * */

        /**
         * TODO: Need to Check for Local Parameters
         * */
    }

    /**
     * This gets called whenever parser detects an FOR Statement
     * */
    @Override public void enterFor_stmt(MicroParser.For_stmtContext ctx) {
        SymbolsTable forBlock = new SymbolsTable(getBlockNumber());
        currentScope = forBlock; // Setting the Scope to the FOR Block

        // Adding the new Block as a Child to the Program
        parentTree.getCurrentScope().addChild(forBlock);

        if(ctx.getChild(8) == null) {
            return;
        }

        pushSymbol(ctx.getChild(8).getText(), currentScope);
    }
}