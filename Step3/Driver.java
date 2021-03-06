/**
 * The Driver.java program utilizes a lexer and parser generated from the
 * Little.g4 via ANTLR4. In this step a listener class was created to walk through
 * provided programs and create symbol tables for the various scopes of a program,
 * as well as check to make sure there are no duplicate variables within the same
 * scope.
 * <p>
 * The listener, Listener.java, utilizes the listeners classes already generated by
 * ANTLR4 and creates symbol tables for all relevant scopes
 * <p>
 * The program is to be run with the provided Micro.sh script and takes a .micro program
 * file and either outputs a declaration error or all the symbol tables and their variable
 * declarations. As of current push program works with Micro.sh script in the format of ./Micro.sh <filename> 
 * 
 * @author  Kymberlee Sables, Harrison Wine
 * @version step3
 * @since   3/08/2022
 */
 
// imports
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

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
        Listener listener = new Listener();

        // parsing through program with listener
        new ParseTreeWalker().walk(listener, littleParser.program());

        // printing values from hash tables within the stack
        listener.printHashTableValues();
    }
}

