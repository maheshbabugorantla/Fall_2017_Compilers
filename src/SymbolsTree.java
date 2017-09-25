import java.util.ArrayList;


/**
 * This is used to create relationship between different scopes in the Program
 * */
class SymbolsTree {

    private SymbolsTable parentScope;
    private SymbolsTable currentScope;
    private int scopeNumber = 1; // Used to number different Local Scopes

    public SymbolsTree() {
        parentScope = new SymbolsTable("GLOBAL");
        currentScope = parentScope; // Setting the currentScope to the GLOBAL scope
    }

    public SymbolsTable getParentScope() {
        return parentScope;
    }

    public SymbolsTable getCurrentScope() {
        return currentScope;
    }

    public void printWholeTree() {
        parentScope.printSymbolTable();
    }
}