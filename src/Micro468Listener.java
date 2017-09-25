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

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext ctx) {

    }

    /**
     * This gets called when the parser enters the Program
     * */
    @Override
    public void exitPgm_body(MicroParser.Pgm_bodyContext ctx) {
        // Print the SymbolTable
    }

    /**
     * This gets called when the parser enters the Function
     * */
    @Override
    public void enterFunc_body(MicroParser.Func_bodyContext ctx) {

    }

    /**
     * This gets called when the parser exits the Function
     * */
    @Override
    public void exitFunc_body(MicroParser.Func_bodyContext ctx) {

    }

    /**
     * This gets called whenever the parser sees a variable declaration
     * */
    @Override
    public void enterDecl(MicroParser.DeclContext ctx) {

    }

    /**
     * This gets called whenever parser detects an IF Statement
     * */
    @Override public void enterIf_stmt(MicroParser.If_stmtContext ctx) {

    }

    /**
     * This gets called whenever parser detects an ELSE Statement
     * */
    @Override public void enterElse_part(MicroParser.Else_partContext ctx) {

    }

    /**
     * This gets called whenever parser detects an FOR Statement
     * */
    @Override public void enterFor_stmt(MicroParser.For_stmtContext ctx) {

    }
}