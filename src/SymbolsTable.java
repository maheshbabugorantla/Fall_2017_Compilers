import java.io.*;
import java.util.*;
import java.lang.Exception;

/**
 *  Symbols Table is a dataStructure that keeps information about non-keyword symbols that appear in source programs
 * */

public class SymbolsTable {

    /**
     * Variables for the Class
     * */

    // TODO: Have to think of how to implement the DataStructure

    private String symbolScope = null;

    public void SymbolsTable() {

    }

    public void setSymbolScope(String symbolScope) {
        this.symbolScope = symbolScope;
    }

    public String getSymbolScope() {
        return symbolScope;
    }
}