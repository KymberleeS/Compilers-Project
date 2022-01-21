// imports
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileWriter;
import java.io.IOException;

// Driver Class
public class Driver {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromFileName(args[0]);

        // create a lexer that feeds off of input CharStream
        LittleLexer lexer = new LittleLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();      //since we do not have a parser we need to manually load the tokens

        // initialize variable that stores token type
        String tokenText = "";

        // writes program output to a file called ProgramOutput.txt and places it in the current directory
        try {
            FileWriter writeToFile = new FileWriter("ProgramOutput.txt");

            for(Token t : tokens.getTokens()) {
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
                    writeToFile.write("Token Type: " + tokenText + "\nValue: " + t.getText() + "\n");
                }
            }
            writeToFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}