// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {
    public static void main(String[] args) throws Exception {
        
        // create a CharStream that reads from standard input
        //ANTLRInputStream input = new ANTLRInputStream(System.in); -- Now deprecated use CharStreams
        CharStream input = CharStreams.fromFileName(args[0]);

        // create a lexer that feeds off of input CharStream
        LittleLexer lexer = new LittleLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println("program begins");
        tokens.fill(); //since we do not have a parser we need to manually load the tokens
        System.out.println("Num of tokens: " + tokens.getTokens().size());

        /*
            INTLITERAL=1
            FLOATLITERAL=2
            STRINGLITERAL=3
            COMMENT=4
            KEYWORD=5
            OPERATOR=6
            WS=7
            IDENTIFIER=8
        */

        for(Token t : tokens.getTokens()){
            System.out.println("Token Type: " + t.getType());
            System.out.println("Value: "+ t.getText());
        }
    }
}