import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
//import java.lang.Exception;
import java.lang.IllegalArgumentException;

/**
 *  Symbols Table is a dataStructure that keeps information about non-keyword symbols that appear in source programs
 * */

public class SymbolsTable {

    /**
     * Variables for the Class
     * */

    // TODO: Have to think of how to implement the DataStructure

    private int printFlag = 0;
    private String blockScope; // Used to store the scope Name such as "GLOBAL" or "Local Scope No."
    private SymbolsTable globalParent; // This is used to refer to the parent of a child
    private ArrayList<SymbolsTable> childList; // List of child Scopes
    private ArrayList<Symbol> symbolsList; // List of all the Symbols
    // Search for the Symbols in the Local Scope in O(1). Usually used to check if
    private HashSet<String> symbolSet;

    public HashMap<String, String[]> variableMap;

    /**
     * Constructor to create the SymbolsTable
     * @param scope_name: Name of the Scope
     *                  example: 'GLOBAL' or FunctionName or BlockNumber (IF, ELSE, FOR)
     * */
    public SymbolsTable(String scope_name) {
        this.blockScope = scope_name;
        this.globalParent = null;
        this.childList = new ArrayList<SymbolsTable>();
        this.symbolsList = new ArrayList<Symbol>();
        this.symbolSet = new HashSet<String>();
        this.variableMap = new HashMap<String, String[]>();
    }

    /**
     * Helper Function to add the Symbol to the SymbolTable
     * @param symbol: This is the symbol that needs to be added to the SymbolTable
     * */
    public void addSymbol(Symbol symbol) throws IllegalArgumentException {

        // If the symbol already exists in the symbolSet then it is illegal to declare the variable again
        String variableName = symbol.getSymbolName();

        // Checks to see if the symbol already exists in the current Scope
        if(symbolSet.contains(variableName)) {
            throw new IllegalArgumentException("DECLARATION ERROR " + variableName);
        }
        else {
            // First Checks if the symbol exists in the Parent(s) scope.
            // If yes, then prints a Shadow Variable warning message
            checkShadowVariable(variableName);
            symbolSet.add(variableName); // Adding to the Set of Unique Symbol Names
            symbolsList.add(symbol); // Adding to the List of Symbols for the currentScope

            variableMap.put(variableName, new String[]{symbol.getSymbolType(), null} );
            //System.out.println("name: " + variableName + " symbol: " + symbol.getSymbolType() + " " + variableMap.get(variableName)[0]);
        }
    }

    public void addRegister(String name, String type) {
        variableMap.put(name, new String[]{type, null} );
    }

    public void addRegister(String name, String type, String reg) {
        variableMap.put(name, new String[]{type, reg} );
    }

//    public String getTypeFromTable(String name) {
//        System.out.println("name: " + name);
//        System.out.println("keys = " + this.variableMap.keySet());
//
//        return variableMap.get(name)[0];
//    }

    /**
     * Helper Function to check if the given variable is a shadow variable
     * @param variableName: Name of the Variable that is already a shadow Variable
     * */
    public void checkShadowVariable(String variableName) {

        // Get the ParentScope of the Current Scope
        SymbolsTable parentScope = globalParent;

        // First check if the parentScope is Null. It it is null then we have reached the end of the search
        // This is top-to-bottom search until we reach the 'GLOBAL' Scope
        while(parentScope != null) {

            // This works similar to 'gcc -Wshadow -Werror -g -Wall hello.c -o hello', -Wshadow flag0
            if(parentScope.getSymbolSet().contains(variableName)) {
                System.out.println("WARNING: Shadow Variable " + variableName);
                return;
            }

            parentScope = globalParent.getParentScope();
        }

        return;
    }

    public void addChild(SymbolsTable child) {
        childList.add(child);
    }

    public SymbolsTable getParentScope() {
        return globalParent;
    }

    public HashSet<String> getSymbolSet() {
        return symbolSet;
    }

    public void setSymbolScope(String symbolScope) {
        this.blockScope = symbolScope;
    }

    public String getSymbolScope() {
        return blockScope;
    }

    public void printSymbolTable() {

        if(blockScope == "GLOBAL") {
            System.out.println("Symbol table " + blockScope);
        }
        else {
            System.out.println("\nSymbol table " + blockScope);
        }

        for(Symbol symbol: symbolsList) {
            System.out.println(symbol);
        }

        for(SymbolsTable child: childList) {
            child.printSymbolTable();
        }
    }
}
