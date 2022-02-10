/**
 * The Driver.java program utilizes the lexer generated from the Little.g4 via ANTLR4.
 * It is to be run with the provided Micro.sh script and creates a CharStream from the System.in stream.
 * From there the CharStream input is passed to the lexer which is then passed to the CommonTokenStream,
 * which holds all the tokens generated by the lexer. From there the tokens are determined via their type
 * and outputted both to the terminal and an output file called "ProgramOutput.txt".
 * <p>
 * As of current push program works with Micro.sh script in the format of ./Micro.sh <filename> 
 * 
 * @author  Kymberlee Sables, Harrison Wine
 * @version step1
 * @since   1/24/2022
 */
 
// imports
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

// Driver Class
/*public class Driver {
    public static void main(String[] args) throws Exception {
		// create a CharStream that reads from standard input
        CharStream input = CharStreams.fromStream(System.in);

        // create a lexer that feeds off of input CharStream
        Little lexer = new Little(input);
        
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();      //since we do not have a parser we need to manually load the tokens

        // initialize variable that stores token type
        String tokenText = "";

        // writes program output to a file called TokensOutput.txt and places it in the current directory
        try {
            PrintStream writeToFile = new PrintStream("ProgramOutput.txt");

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
                            // Stops the Lexer if an illegal token is encountered. TokensOutput.txt will be an EMPTY file
                            System.out.println("\nLexer Error - Illegal Token was Found: " + t.getText());
                            System.exit(0);
                            break;
                    }
                    // Printing the output in addition to writing the tokens to a file. Printing halts when illegal token found
                    String output = "Token Type: " + tokenText + "\nValue: " + t.getText() + "\n";

                    // Piping console output to a file
                    System.setOut(writeToFile);
                    System.out.print(output);

                    PrintStream consoleOutput = new PrintStream(new FileOutputStream(FileDescriptor.out));

                    System.setOut(consoleOutput);
                    System.out.print(output);

                    System.out.flush();
                }
            }
            writeToFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/

public class Driver {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromStream(System.in);

        // create a lexer that feeds off of input CharStream
        LittleLexer littleLexer = new LittleLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(littleLexer);

        // generate parser by creating parser object
        LittleParser littleParser = new LittleParser(tokens);

        // removing original error listeners to clear console
        littleParser.removeErrorListeners();

        // starting the parser at the start rule named "program"
        littleParser.program();

        // string to store program output
        String programOutput = "";

        if (littleParser.getNumberOfSyntaxErrors() > 0) {
            programOutput = "Not accepted\n";
        } else {
            programOutput = "Accepted\n";
        }

        // piping console output to a file
        try {
            PrintStream consoleOutput = new PrintStream(new FileOutputStream(FileDescriptor.out));

            System.setOut(consoleOutput);
            System.out.print(programOutput);

            PrintStream writeToFile = new PrintStream("ProgramOutput.txt");

            System.setOut(writeToFile);
            System.out.print(programOutput);

            System.out.flush();
            writeToFile.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

