// imports
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileWriter;
import java.io.IOException;

// Driver Class
public class Driver {
    public static void main(String[] args) throws Exception {
        int size = args.length;
		if(size != 1){
			System.out.println("\nERROR: Invalid number of arguments, only one argument accepted.\nNum of args supplied: " + size);
			System.exit(0);
		}
		
		// create a CharStream that reads from standard input
        CharStream input = CharStreams.fromFileName(args[0]);

        // create a lexer that feeds off of input CharStream
        Little lexer = new Little(input);
        
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();      //since we do not have a parser we need to manually load the tokens

        // initialize variable that stores token type
        String tokenText = "";

        // writes program output to a file called TokensOutput.txt and places it in the current directory
        try {
            FileWriter writeToFile = new FileWriter("TokensOutput.txt");

            for(Token t : tokens.getTokens()) {
                int tok_type = t.getType();
                if (tok_type != -1){
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
                        case 9:
                            // Stops the Lexer if an illegal token is encountered. TokensOutput.txt will be an empty file
                            System.out.println("\nLexer Error - Illegal Token was Found: " + t.getText());
                            System.exit(0);
                            break;
                    }
                    // Printing the output in addition to writing the tokens to a file. Printing halts when illegal token found
                    System.out.println("TokenType: " + tokenText);
                    System.out.println("Value: " + t.getText());

                    writeToFile.write("Token Type: " + tokenText + "\nValue: " + t.getText() + "\n");
                }
            }
            writeToFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}