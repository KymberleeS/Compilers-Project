/**
 * The Driver.java program utilizes the parser generated from the Little.g4 via ANTLR4.
 * It is to be run with the provided Micro.sh script and creates a CharStream from the System.in stream.
 * From there the CharStream input is passed to the lexer which is then passed to the CommonTokenStream,
 * which holds all the tokens generated by the lexer. From there the tokens are passed to the parser 
 * which checks to see if the program follows the grammar's parse rules. If there are no errors
 * "Accepted" is printed to the screen, while if there is an error "Not accepted" is printed.
 * <p>
 * As of current push program works with Micro.sh script in the format of ./Micro.sh <filename> 
 * 
 * @author  Kymberlee Sables, Harrison Wine
 * @version step2
 * @since   2/26/2022
 */
 
// imports
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

public class Driver {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromStream(System.in);
        //CharStream input = CharStreams.fromFileName("./Step3-TestCases/inputs/test21.micro");

        // create a lexer that feeds off of input CharStream
        LittleLexer littleLexer = new LittleLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(littleLexer);

        // generate parser by creating parser object
        LittleParser littleParser = new LittleParser(tokens);

        // removing original error listeners to clear console
        littleParser.removeErrorListeners();

        // starting the parser at the start rule named "program"
        Listener listener = new Listener();

        new ParseTreeWalker().walk(listener, littleParser.program());

        listener.printHashTableValues();

        // string to store program output
     /*   String programOutput = "";

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
        } */
    }
}

