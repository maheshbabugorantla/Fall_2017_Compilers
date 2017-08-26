import org.antlr.v4.runtime.*;
import java.io.*;
import java.util.*;
import java.lang.Exception;

public class Micro {
    public static void main(String[] args) throws Exception {

        System.out.println("Hello World");

        try {
            ANTLRFileStream tokenStream = new ANTLRFileStream(args[0]);
            MicroLexer microLexer = new MicroLexer(tokenStream);

            CommonTokenStream commonTokenStream = new CommonTokenStream(microLexer);

            commonTokenStream.fill();
            String[] tokenNames = microLexer.getTokenNames();

            for(String tokenName: tokenNames) {
                System.out.println("Token Name: " + tokenName);
            }

            List<Token> tokenList = commonTokenStream.getTokens();

            for(Token token: tokenList) {
                System.out.println(token.getType());
            }
        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }
  }
}
