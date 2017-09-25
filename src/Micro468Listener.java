import java.io.*;
import java.util.*;
import java.lang.Exception;

/**
 * This Listener Class implements the MicroBaseListener Interface to detect the path of the Parser Tree
 * */

public class Micro468Listener extends MicroBaseListener {

    private SymbolsTree parentTree;
    private int blockNumber;

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
     * */
    @Override
    public void enterFunc_body(MicroParser.Func_bodyContext ctx) { // currentScope
        System.out.println(ctx.getChild(0).getText());
    }

    /**
     * This gets called when the parser exits the Function
     * */
    @Override
    public void exitFunc_body(MicroParser.Func_bodyContext ctx) {
        // System.out.println(ctx.getChild(0).getText());
    }

    /**
     * This gets called when the parser hits the Function Declaration
     * */
    @Override public void enterFunc_decl(MicroParser.Func_declContext ctx) {
        System.out.println("Function Name: " + ctx.getChild(2).getText()); // Function Name

        // Function Parameters
        System.out.println("Function Parameters: " + ctx.getChild(4).getText()); // Function Paramters
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
        System.out.println(ctx.getChild(0).getText());
    }

    /**
     * This gets called whenever parser detects an ELSE Statement
     * */
    @Override public void enterElse_part(MicroParser.Else_partContext ctx) {
        System.out.println(ctx.getChild(0).getText());
    }

    /**
     * This gets called whenever parser detects an FOR Statement
     * */
    @Override public void enterFor_stmt(MicroParser.For_stmtContext ctx) {
        System.out.println(ctx.getChild(0).getText());
    }
}