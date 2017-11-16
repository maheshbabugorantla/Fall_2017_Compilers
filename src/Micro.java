import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import java.io.*;
import java.lang.Exception;

public class Micro {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream tokenStream = new ANTLRFileStream(args[0]);
        MicroLexer microLexer = new MicroLexer(tokenStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(microLexer);

        // commonTokenStream.fill();

        // MicroParser accepts the TokenStream which implemented by CommonTokenStream
        /**
         * TokenStream Documentation: http://www.antlr.org/api/Java/org/antlr/v4/runtime/TokenStream.html
         * */

        MicroParser microParser = new MicroParser(commonTokenStream);
        microParser.setErrorHandler(new BailErrorStrategy());

        try {
            ParseTree parseTree = microParser.program();
            ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
            Micro468Listener micro468Listener = new Micro468Listener();
            parseTreeWalker.walk(micro468Listener, parseTree);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
