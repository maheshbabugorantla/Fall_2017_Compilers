import java.io.*;
import java.util.*;
import java.lang.Exception;

/**
 * This Listener Class implements the MicroBaseListener Interface to detect the path of the Parser Tree
 * */

public class Micro468Listener extends MicroBaseListener {

    private SymbolsTree parentTree;
    private int blockNumber;

    private SymbolsTable currentScope; // Used to refer to which functio is being parsed

    /**
     * Constructor to detect the Parser Actions.
     * REMEMBER: This gets called only once.
     * */
    public Micro468Listener() {
        this.parentTree = new SymbolsTree(); // Initializing the Symbol Tree to the GLOBAL Scope
        this.blockNumber = 1;
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
     * This gets called when the parser enters the Program
     * */
    @Override
    public void exitPgm_body(MicroParser.Pgm_bodyContext ctx) {
        // Print the SymbolTable
        parentTree.printWholeTree();
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