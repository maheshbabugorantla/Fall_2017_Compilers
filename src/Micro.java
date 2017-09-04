import org.antlr.v4.runtime.*;
import java.io.*;
import java.util.*;
import java.lang.Exception;

public class Micro {
    public static void main(String[] args) throws Exception {

        try {
            	ANTLRFileStream tokenStream = new ANTLRFileStream(args[0]);
            	MicroLexer microLexer = new MicroLexer(tokenStream);
	    	//ExpLexer expLexer = new ExpLexer(tokenStream);

            	CommonTokenStream commonTokenStream = new CommonTokenStream(microLexer);

            	commonTokenStream.fill();

            // MicroParser accepts the TokenStream which implemented by CommonTokenStream
            /**
             * TokenStream Documentation: http://www.antlr.org/api/Java/org/antlr/v4/runtime/TokenStream.html
            * */
            
	    	MicroParser microParser = new MicroParser(commonTokenStream);
	    	//microParser.removeErrorListeners();
		microParser.setErrorHandler(new BailErrorStrategy());
            	microParser.program();
		//microParser.addErrorListener(new ANTLRErrorStrategy);
	 	System.out.println("No.of Syntax Errors: " + Integer.toString(microParser.getNumberOfSyntaxErrors()));

	    /*String[] tokenNames = microLexer.getTokenNames();

//            for(String tokenName: tokenNames) {
//                System.out.println("Token Name: " + tokenName);
//            }

            List<Token> tokenList = commonTokenStream.getTokens();

            for(Token token: tokenList) {
                if (token.getType() != -1) {
                    System.out.println("Token Type: " + tokenNames[token.getType()]);
                    System.out.println("Value: " + token.getText());
                }
            }*/



        }
	
	/*catch (ParseCancellationException parseException) {
		System.out.println("Parse Exception");
		System.out.println(parseException.getMessage());
	}*/

        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
