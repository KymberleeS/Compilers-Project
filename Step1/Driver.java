// imports
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromFileName(args[0]);
        //CharStream input = CharStreams.fromFileName("./Step1-TestCases/inputs/fibonacci.micro");

        // create a lexer that feeds off of input CharStream
        LittleLexer lexer = new LittleLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        //System.out.println("program begins");
        tokens.fill(); //since we do not have a parser we need to manually load the tokens
        //System.out.println("Num of tokens: " + tokens.getTokens().size());

        // initialize variable that
        String tokenText = "";

        for(Token t : tokens.getTokens()){
            int tok_type = t.getType();
            if(tok_type != -1){
                switch(tok_type) {
                    case 1:
                        tokenText = "INTLITERAL";
                        break;
                    case 2:
                        tokenText = "FLOATLITERAL";
                        break;
                    case 3:
                        tokenText = "STRINGLITERAL";
                        break;
                    case 4:
                        tokenText = "COMMENT";
                        break;
                    case 5:
                        tokenText = "KEYWORD";
                        break;
                    case 6:
                        tokenText = "OPERATOR";
                        break;
                    case 7:
                        tokenText = "WS";
                        break;
                    case 8:
                        tokenText = "IDENTIFIER";
                        break;
                }
            
                System.out.println("Token Type: " + tokenText);
                System.out.println("Value: "+ t.getText());
            }
        }
    }
}