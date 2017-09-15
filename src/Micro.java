import org.antlr.v4.runtime.*;
import java.io.*;
import java.util.*;
import java.lang.Exception;

public class Micro {
    public static void main(String[] args) throws Exception {

        try {
            	ANTLRFileStream tokenStream = new ANTLRFileStream(args[0]);
            	MicroLexer microLexer = new MicroLexer(tokenStream);

            	CommonTokenStream commonTokenStream = new CommonTokenStream(microLexer);

            	commonTokenStream.fill();

            // MicroParser accepts the TokenStream which implemented by CommonTokenStream
            /**
             * TokenStream Documentation: http://www.antlr.org/api/Java/org/antlr/v4/runtime/TokenStream.html
            * */
            
	    	MicroParser microParser = new MicroParser(commonTokenStream);
           	microParser.program();

           	if(microParser.getNumberOfSyntaxErrors() < 1) {
                System.out.println("Accepted");
            }
            else {
                System.out.println("Not Accepted");
            }
        }
        catch(Exception e) {
            // System.out.println(e.getMessage());
            System.out.println("Not Accepted");
        }
    }
}
