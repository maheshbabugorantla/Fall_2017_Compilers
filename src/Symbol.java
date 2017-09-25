/**
 * This is Class to define the symbol name, type and value of the Micro Language
 * */

import java.io.*;
import java.util.*;
import java.lang.Exception;

public class Symbol {

    private String symbolName;
    private String symbolType;
    private String symbolValue;

    // Constructors

    // This constructor is used for the INT and FLOAT Data Types
    public Symbol(String symbolName, String symbolType) {
        setSymbolName(symbolName);
        setSymbolType(symbolType);
        setSymbolValue(null);
    }

    // This Constructor is only used for STRING Data Type
    public Symbol(String symbolType, String symbolName, String symbolValue) {
        setSymbolName(symbolName);
        setSymbolType(symbolType);
        setSymbolValue(symbolValue);
    }

    /**
     * Get and Set Methods
     * */

    public String getSymbolName() {
        return symbolName;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public String getSymbolValue() {
        return symbolValue;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }

    public void setSymbolValue(String symbolValue) {
        this.symbolValue = symbolValue;
    }

    // Used to print the details of each Symbol
    @Override
    public String toString() {

        // If the Symbol type is STRING
        if(getSymbolType().equals("STRING")) {
            return("name " + this.getSymbolName() + " type " + this.getSymbolType() + " value \"" + this.getSymbolValue() + "\"");
        }
        else {
            return("name " + this.getSymbolName() + " type " + this.getSymbolType());
        }
    }
}