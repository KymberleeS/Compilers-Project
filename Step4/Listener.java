/**
 * The Listener.java extends the LittleBaseListener and implements several of
 * the empty methods pregenerated from ANTLR4. This listener creates a hash table
 * for each scope of the program and pushes them onto the stack. Within each scope
 * any variables are added to the scopes hash table, which acts as a symbol table.
 * The values inserted into the table are stored as nodes which contains three
 * string variables: name, type, value. The tables are then able to be printed
 * with the printHashTableValues() method.
 * 
 * @author  Kymberlee Sables, Harrison Wine
 * @version step3
 * @since   3/08/2022
 */

import java.util.*;
import java.util.regex.*;

public class Listener extends LittleBaseListener {
    // (step 3) - class that holds name, type, and value attributes for variables
    class Node {
        String name;
        String type;
        String value;

        Node(String str1, String str2, String str3){
            name  = str1;
            type  = str2;
            value = str3;
        }

        public String toString() {
            if (value == null) {
                return "name " + name + " type " + type;
            }
            return "name " + name + " type " + type + " value " + value;
        }
    }

    // (step 4) - class for assisting in building ASTs for simple expressions
    class ASTNode {
        String value;
        ASTNode leftChild;
        ASTNode rightChild;
        String irCode;

        ASTNode(String value, ASTNode leftChild, ASTNode rightChild, String irCode) {
            this.value = value;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.irCode = irCode;
        }
    }

    // step 3 variables
    ArrayList<String> symbolTableNames = new ArrayList<>();
    LinkedHashMap<String, Node> globalHT = new LinkedHashMap<>();
    Stack<LinkedHashMap<String, Node>> stackHT = new Stack<>();
    int scope_cnt = 0;

    // step 4 variables
    ArrayList<ASTNode> astIRNodes = new ArrayList<>();
    ArrayList<String[]> threeAddressCode = new ArrayList<>();
    ArrayList<String> tinyAssemblyCode = new ArrayList<>();
    ArrayList<String> trackRegister = new ArrayList<>();
    ArrayList<String> trackVariables = new ArrayList<>();
    int tempRegister = 1;
    int register = 0;


    public void enterString_decl(LittleParser.String_declContext ctx) {
        String[] list = ctx.getText().split("[a-z]+");
        String type = "";

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals("STRING")) {
                type = list[i];
            }
        }

        // create a node for the string variable
        Node stringNode = new Node(ctx.id().getText(), type, ctx.str().getText());

        // put the node into the symbol table who's scope the string variable was
        stackHT.peek().put(stringNode.name, stringNode);
    }

    public void enterPgm_body(LittleParser.Pgm_bodyContext ctx) {
        symbolTableNames.add("Symbol table GLOBAL");

        // add the global hash table to the stack
        stackHT.push(globalHT);
    }

    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type    = ctx.var_type().getText();
        String id_list = ctx.id_list().getText();
        String[] list  = id_list.split(",");
        LinkedHashMap<String, Node> current_table = stackHT.peek();

        for(String str : list){
            //if the hash table does not have the variable already, add it
            //otherwise throw an error
            if(!current_table.containsKey(str)){
                Node var_decl = new Node(str, type, null);
                current_table.put(str, var_decl);
            }
            else{
               System.out.println("DECLARATION ERROR "+str);
               System.exit(0);
            }
        }
    }

    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        LinkedHashMap<String, Node> functionTable  = new LinkedHashMap<>();
        stackHT.push(functionTable);

        String tableName = ctx.id().getText();
        symbolTableNames.add("Symbol table " + tableName);
        astIRNodes.add(new ASTNode(";LABEL " + tableName, null, null, null));

        String idRegex = "(INT|FLOAT|STRING)|,(INT|FLOAT|STRING)";

        // parsing the parameter declaration list based on regular expressions and storing them into temporary arrays
        String[] tempVar_id = ctx.param_decl_list().getText().replaceFirst(idRegex, "").split(idRegex, 0);
        String[] tempType = ctx.param_decl_list().getText().split("([a-z]),|([a-z])");

        // array to hold needed elements from the temporary arrays
        ArrayList<String> var_id = new ArrayList<>();
        ArrayList<String> type = new ArrayList<>();

        // if a non-empty string, add the variable id to var_id array
        for (int i = 0; i < tempVar_id.length; i++) {
            if(!(tempVar_id[i].equals(""))) {
                var_id.add(tempVar_id[i]);
            }
        }

        // if a non-empty string, add the variable type to type array
        for (int i = 0; i < tempType.length; i++) {
            if(!(tempType[i].equals(""))) {
                type.add(tempType[i]);
            }
        }

        for(int i = 0; i < var_id.size(); i++){
            // check to see if variable is already declared; throw error if so
            if(!functionTable.containsKey(var_id.get(i))){
                Node var_decl = new Node(var_id.get(i), type.get(i), null);
                functionTable.put(var_id.get(i), var_decl);
            } else {
                System.out.println("DECLARATION ERROR "+var_id.get(i));
                System.exit(0);
            }
        }
    }

    public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        LinkedHashMap<String, Node> ifTable  = new LinkedHashMap<>();
        stackHT.push(ifTable);
        symbolTableNames.add("Symbol table BLOCK " + (++scope_cnt));
     }

    public void enterElse_part(LittleParser.Else_partContext ctx) {
        // check to see if there is not an else statement present; if there is add a new BLOCK scope
        if(ctx.decl() != null){
            LinkedHashMap<String, Node> elseTable  = new LinkedHashMap<>();
            stackHT.push(elseTable);
            symbolTableNames.add("Symbol table BLOCK " + (++scope_cnt));
        }
     }

    public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        LinkedHashMap<String, Node> whileTable  = new LinkedHashMap<>();
        stackHT.push(whileTable);
        symbolTableNames.add("Symbol table BLOCK " + (++scope_cnt));
     }

  /*  public void printHashTableVariables() {
        for (int i = 0; i < stackHT.size(); i++) {
            System.out.println(symbolTableNames.get(i));
            Iterator<Node> itr = stackHT.get(i).values().iterator();

            while(itr.hasNext()){
                System.out.println(itr.next().toString());
            }
            System.out.print("\n");
        }
    } */

    // function that extracts all expressions found in Little Source Code
    public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
        ArrayList<String> tempBuildAST = new ArrayList<>();
        ArrayList<ASTNode> buildAST = new ArrayList<>();

        String[] temp;
        String exprSplitter = "(?<=\\\\d)|(?=\\+)|(?<=\\+)|(?=\\/)|(?<=\\/)|(?=\\*)|(?<=\\*)|(?=\\-)|(?<=\\-)|(?=\\))|((?<=\\)))|(?=\\()|(?<=\\()";

        tempBuildAST.add(ctx.id().getText());
        tempBuildAST.add(":=");

        temp = ctx.expr().expr_prefix().getText().split(exprSplitter);
        parserHelper(temp, tempBuildAST);

        temp = ctx.expr().factor().factor_prefix().getText().split(exprSplitter);
        parserHelper(temp, tempBuildAST);

        temp = ctx.expr().factor().postfix_expr().getText().split(exprSplitter);
        parserHelper(temp, tempBuildAST);

        for (int i = 0; i < tempBuildAST.size(); i++) {
            if (tempBuildAST.get(i) != "") {
                buildAST.add(new ASTNode(tempBuildAST.get(i), null, null, null));
            }
        }

         while (!(buildAST.isEmpty())) {
             for (int i = 0; i < buildAST.size(); i++) {
                 if (containsParentheses(buildAST)) {
                     if (buildAST.get(i).value.equals("+") || buildAST.get(i).value.equals("-") || buildAST.get(i).value.equals("*") ||
                         buildAST.get(i).value.equals("/")) {
                         if (!(buildAST.get(i + 1).value.equals(")")) && !(buildAST.get(i - 1).value.equals("("))) {
                             buildASTNode(astIRNodes, buildAST, i);

                             buildAST.remove(i + 1);
                             buildAST.remove(i - 1);
                         } else if ((buildAST.get(i + 1).value.equals(")")) && (buildAST.get(i - 1).value.equals("("))) {
                             buildAST.remove(i + 1);
                             buildAST.remove(i - 1);
                         }
                     }
                 } else {
                     if (buildAST.get(i).value.equals("+") || buildAST.get(i).value.equals("-") || buildAST.get(i).value.equals("*") ||
                         buildAST.get(i).value.equals("/")) {
                         buildASTNode(astIRNodes, buildAST, i);

                         buildAST.remove(i + 1);
                         buildAST.remove(i - 1);
                     } else if (buildAST.size() == 3 && buildAST.get(i).value.equals(":=")) {
                                buildASTNode(astIRNodes, buildAST, i);

                         buildAST.remove(i + 1);
                         buildAST.remove(i - 1);
                     }
                 }
             }

             for (int i = 0; i < buildAST.size(); i++) {
                 if (buildAST.size() == 1 && buildAST.get(i).value.equals(":=")) {
                     buildAST.remove(i);
                 }
             }
        }
    }

    // function that extracts variables from Read statements
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        ArrayList<String> tempReadAST = new ArrayList<>();

        String[] temp = ctx.id_list().getText().split(",");
        parserHelper(temp, tempReadAST);

       readWriteStmts(tempReadAST, "READ");
    }

    // functions that extracts variables from Write statements
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        ArrayList<String> tempWriteAST = new ArrayList<>();

        String[] temp = ctx.id_list().getText().split(",");
        parserHelper(temp, tempWriteAST);

        readWriteStmts(tempWriteAST, "WRITE");
    }

    // function that converts existing 3AC to Tiny Assembly Codes
    public void tinyAssemblyConversion() {
        for (Map.Entry<String, Node> entry : globalHT.entrySet()) {
            if (entry.getValue().type.equals("STRING")) {
                tinyAssemblyCode.add("str " + entry.getValue().name + " " + entry.getValue().value);
            } else {
                tinyAssemblyCode.add("var " + entry.getValue().name);
            }
        }

        for (int i = 0; i < astIRNodes.size(); i++) {
            if (astIRNodes.get(i).irCode != null) {
               threeAddressCode.add(astIRNodes.get(i).irCode.split(" "));
            }
        }

        for (int i = 0; i < threeAddressCode.size(); i++) {
            // converting read/write statements
            convertReadWriteAddressCode(threeAddressCode, i);

            String expr;
            switch (threeAddressCode.get(i)[0]) {
                case ";ADDF":
                case ";ADDI":
                    if (threeAddressCode.get(i)[0].equals(";ADDI")) {
                        expr = "addi ";
                    } else {
                        expr = "addr ";
                    }

                    if (trackVariables.contains(threeAddressCode.get(i)[1]) && trackVariables.contains(threeAddressCode.get(i)[2])) {
                        tinyAssemblyCode.add(expr + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])) + " " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[2])));
                        tinyAssemblyCode.add("move " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[2])) + " " + threeAddressCode.get(i + 1)[2]);
                    } else if (trackVariables.contains(threeAddressCode.get(i)[1])) {
                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[2] + " " + "r" + register);

                        trackRegister.add("r" + register);
                        trackVariables.add(threeAddressCode.get(i)[2]);

                        tinyAssemblyCode.add(expr + "r" + register + " " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])));
                        tinyAssemblyCode.add("move " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])) + " " + threeAddressCode.get(i + 1)[2]);
                    }

                    if (!trackRegister.contains("r" + register) && !trackVariables.contains(threeAddressCode.get(i + 1)[2])) {
                        trackRegister.add("r" + register);
                        trackVariables.add(threeAddressCode.get(i + 1)[2]);
                    }
                    register++;
                    break;
                case ";SUBF":
                case ";SUBI":
                    if (threeAddressCode.get(i)[0].equals(";SUBI")) {
                        expr = "subi ";
                    } else {
                        expr = "subr ";
                    }

                    if (trackVariables.contains(threeAddressCode.get(i)[1]) && trackVariables.contains(threeAddressCode.get(i)[2])) {
                        tinyAssemblyCode.add(expr + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])) + " " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[2])));
                        tinyAssemblyCode.add("move " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[2])) + " " + threeAddressCode.get(i + 1)[2]);
                    } else {
                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[1] + " " + "r" + register);
                        tinyAssemblyCode.add(expr + threeAddressCode.get(i)[2] + " " + "r" + register);
                        tinyAssemblyCode.add("move " + "r" + register + " " + threeAddressCode.get(i + 1)[2]);

                    }

                    if (!trackRegister.contains("r" + register) && !trackVariables.contains(threeAddressCode.get(i + 1)[2])) {
                        trackRegister.add("r" + register);
                        trackVariables.add(threeAddressCode.get(i + 1)[2]);
                    }
                    register++;
                    break;
                case ";MULTF":
                case ";MULTI":
                    if (threeAddressCode.get(i)[0].equals(";MULTI")) {
                        expr = "muli ";
                    } else {
                        expr = "mulr ";
                    }

                    if (trackVariables.contains(threeAddressCode.get(i)[1]) && trackVariables.contains(threeAddressCode.get(i)[2])) {
                        tinyAssemblyCode.add(expr + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])) + " " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[2])));
                        tinyAssemblyCode.add("move " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[2])) + " " + threeAddressCode.get(i + 1)[2]);

                        trackVariables.set(trackVariables.indexOf(threeAddressCode.get(i)[2]) ,threeAddressCode.get(i + 1)[2]);
                    } else {
                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[1] + " " + "r" + register);
                        tinyAssemblyCode.add(expr + threeAddressCode.get(i)[2] + " " + "r" + register);
                        tinyAssemblyCode.add("move " + "r" + register + " " + threeAddressCode.get(i + 1)[2]);
                    }

                    if (!trackRegister.contains("r" + register) && !trackVariables.contains(threeAddressCode.get(i + 1)[2])) {
                        trackRegister.add("r" + register);
                        trackVariables.add(threeAddressCode.get(i + 1)[2]);
                    } else if (trackRegister.contains("r" + register)) {
                        trackVariables.set(trackRegister.indexOf("r" + register), threeAddressCode.get(i + 1)[2]);
                    }
                    register++;
                    break;
                case ";DIVF":
                case ";DIVI":
                    if (threeAddressCode.get(i)[0].equals(";DIVI")) {
                        expr = "divi ";
                    } else {
                        expr = "divr ";
                    }

                    if (trackVariables.contains(threeAddressCode.get(i)[1])) {
                        tinyAssemblyCode.add(expr + threeAddressCode.get(i)[2] + " " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])));
                        tinyAssemblyCode.add("move " + trackRegister.get(trackVariables.indexOf(threeAddressCode.get(i)[1])) + " " + threeAddressCode.get(i + 1)[2]);
                    } else {
                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[1] + " " + "r" + register);
                        tinyAssemblyCode.add(expr + threeAddressCode.get(i)[2] + " " + "r" + register);
                        tinyAssemblyCode.add("move " + "r" + register + " " + threeAddressCode.get(i + 1)[2]);
                    }

                    if (!trackRegister.contains("r" + register) && !trackVariables.contains(threeAddressCode.get(i + 1)[2])) {
                        trackRegister.add("r" + register);
                        trackVariables.add(threeAddressCode.get(i + 1)[2]);
                    } else if (trackRegister.contains("r" + register)) {
                        trackVariables.set(trackRegister.indexOf("r" + register), threeAddressCode.get(i + 1)[2]);
                    }
                    register++;
                    break;
                case ";STOREF":
                case ";STOREI":
                    if (threeAddressCode.get(i).length == 5) {
                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[1] + " " + threeAddressCode.get(i)[4]);
                    } else if (threeAddressCode.get(i).length > 5) {
                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[1] + " " + "r" + register);
                        register++;

                        if (threeAddressCode.get(i)[2].contains(";DIVF")) {
                            expr = "divr ";
                        } else {
                            expr = "divi ";
                        }

                        tinyAssemblyCode.add("move " + threeAddressCode.get(i)[3] + " " + "r" + register);
                        tinyAssemblyCode.add(expr + "r" + (register - 1) + " " + "r" + register);
                        tinyAssemblyCode.add("move " + "r" + register + " " + threeAddressCode.get(i + 1)[2]);

                        if (!trackRegister.contains("r" + register) && !trackVariables.contains(threeAddressCode.get(i + 1)[2])) {
                            trackRegister.add("r" + register);
                            trackVariables.add(threeAddressCode.get(i + 1)[2]);
                        }

                        register++;
                    }
                    break;
            }
        }
    }

    // functions that prints out code generation
    public void printGeneratedCode() {
        System.out.println(";IR code");
        System.out.println(astIRNodes.get(0).value);
        System.out.println(";LINK");
        for (int i = 0; i < astIRNodes.size(); i++) {
            if (astIRNodes.get(i).irCode != null) {
                System.out.println(astIRNodes.get(i).irCode);
            }
        }
        System.out.println(";RET");
        System.out.println(";tiny code");

        for (int i = 0; i < tinyAssemblyCode.size(); i++) {
            System.out.println(tinyAssemblyCode.get(i));
        }

        System.out.println("sys halt");
    }

    // helper function that transfers final parsed simple expressions into an arraylist
    private void parserHelper(String[] temp, ArrayList<String> tempBuildAST) {
        for (int i = 0; i < temp.length; i++) {
            tempBuildAST.add(temp[i]);
        }
    }

    // helper function that assembles AST nodes and inserts into the "astIRNodes" arraylist
    private void buildASTNode(ArrayList<ASTNode> astIRNodes, ArrayList<ASTNode> buildAST, int i) {
        String add = "ADDI";
        String sub = "SUBI";
        String mul = "MULTI";
        String div = "DIVI";
        String store = "STOREI";

        String irCode;

        astIRNodes.add(new ASTNode(buildAST.get(i - 1).value, null, null, null));
        astIRNodes.add(new ASTNode(buildAST.get(i + 1).value, null, null, null));

        buildAST.get(i).leftChild = buildAST.get(i - 1);
        buildAST.get(i).rightChild = buildAST.get(i + 1);

        if (containsFloatNum(astIRNodes)) {
            add = "ADDF";
            sub = "SUBF";
            mul = "MULTF";
            div = "DIVF";
            store = "STOREF";
        }

        switch (buildAST.get(i).value) {
            case "+":
                nodeCases(buildAST, store, add, i);
                break;
            case "-":
                nodeCases(buildAST, store, sub, i);
                break;
            case "*":
                nodeCases(buildAST, store, mul, i);
                break;
            case "/":
                nodeCases(buildAST, store, div, i);
                break;
            case ":=":
                if (buildAST.get(i).rightChild.value.equals("+") || buildAST.get(i).rightChild.value.equals("-") ||
                    buildAST.get(i).rightChild.value.equals("*") || buildAST.get(i).rightChild.value.equals("/")) {
                    irCode = ";" + store + " " + "$T" + tempRegister + " " + buildAST.get(i).leftChild.value;

                    astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1), irCode));
                } else {
                    irCode = ";" + store + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister + "\n" +
                             ";" + store + " " + "$T" + tempRegister + " " + buildAST.get(i).leftChild.value;

                    astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1), irCode));
                }
                tempRegister++;
                break;
        }
    }

    // helper function that adjusts 3AC depending on if the value of a left/right child is a constant or not
    private void nodeCases(ArrayList<ASTNode> buildAST, String updateOp, String op, int i) {
        String irCode;

        if (checkNumConstant(buildAST.get(i).rightChild.value)) {
            irCode = updateRightIRCode(buildAST, updateOp, i) + ";" + op + " " + buildAST.get(i).leftChild.value + " " + "$T" + (tempRegister - 1) + " $T" + tempRegister;
            astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1), irCode));
        } else if (checkNumConstant(buildAST.get(i).leftChild.value)) {
            irCode = updateLeftIRCode(buildAST, updateOp, i) + ";" + op + " " + "$T" + (tempRegister - 1) + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister;
            astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1), irCode));
        } else {
            irCode = ";" + op + " " + buildAST.get(i).leftChild.value + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister;
            astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1), irCode));
        }
    }

    // helper functions that checks if a parsed simple expression contains parentheses
    private boolean containsParentheses(ArrayList<ASTNode> buildAST) {
        int parenthesesCount = 0;

        for (int i = 0; i < buildAST.size(); i++) {
            if (buildAST.get(i).value.equals("(") || buildAST.get(i).value.equals(")")) {
                parenthesesCount++;
            }

            if (parenthesesCount > 0) {
                return true;
            }
        }
        return false;
    }

    // helper function that checks if a value contains a "." for FLOAT types
    private boolean containsFloatNum(ArrayList<ASTNode> buildAST) {
        int decimalCount = 0;

        for (int i = 0; i < buildAST.size(); i++) {
            if (buildAST.get(i).value.contains(".")) {
                decimalCount++;
            }

            if (decimalCount > 0) {
                return true;
            }
        }
        return false;
    }

    // helper function that checks if a left/right child is a numerical value; if so, value needs to be stored in a temporary register before computation
    private boolean checkNumConstant(String value) {
        Pattern pattern = Pattern.compile("(\\.)?[0-9]\\d*(\\.\\d+)?");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            return true;
        }
        return false;
    }

    // helper function that updates IRCode of right child if value is a constant
    private String updateRightIRCode(ArrayList<ASTNode> buildAST, String store, int i) {
        int previousRegister = tempRegister;
        tempRegister++;
        return ";" + store + " " + buildAST.get(i).rightChild.value + " $T" + previousRegister + "\n";
    }

    // helper function that updates IRCode of left child if value is a constant
    private String updateLeftIRCode(ArrayList<ASTNode> buildAST, String store, int i) {
        int previousRegister = tempRegister;
        tempRegister++;
        return ";" + store + " " + buildAST.get(i).leftChild.value + " $T" + previousRegister + "\n";
    }

    // helper function that handles the proper "READ" or "WRITE" command based on the variable's type
    private void readWriteStmts(ArrayList<String> tempWriteAST, String stmtType) {
        for (int i = 0; i < tempWriteAST.size(); i++) {
            for (Map.Entry<String, Node> entry : globalHT.entrySet()) {
                if (tempWriteAST.get(i).equals(entry.getValue().name)) {
                    if (entry.getValue().type.equals("INT")) {
                        astIRNodes.add(new ASTNode(tempWriteAST.get(i), null, null,   ";" + stmtType + "I " + tempWriteAST.get(i)));
                    } else if (entry.getValue().type.equals("FLOAT")) {
                        astIRNodes.add(new ASTNode(tempWriteAST.get(i), null, null, ";" + stmtType + "F " + tempWriteAST.get(i)));
                    } else if (entry.getValue().type.equals("STRING")) {
                        astIRNodes.add(new ASTNode(tempWriteAST.get(i), null, null, ";" + stmtType + "S " + tempWriteAST.get(i)));
                    }
                }
            }
        }
    }

    // helper function that converts read/write 3AC to tiny assembly code
    private void convertReadWriteAddressCode(ArrayList<String[]> threeAddressCode, int i) {
        switch(threeAddressCode.get(i)[0]) {
            case ";WRITEI":
                tinyAssemblyCode.add("sys writei " + threeAddressCode.get(i)[1]);
                break;
            case ";WRITEF":
                tinyAssemblyCode.add("sys writer " + threeAddressCode.get(i)[1]);
                break;
            case ";WRITES":
                tinyAssemblyCode.add("sys writes " + threeAddressCode.get(i)[1]);
                break;
            case ";READI":
                tinyAssemblyCode.add("sys readi " + threeAddressCode.get(i)[1]);
                break;
            case ";READF":
                tinyAssemblyCode.add("sys readr " + threeAddressCode.get(i)[1]);
                break;
            case ";READS":
                tinyAssemblyCode.add("sys reads " + threeAddressCode.get(i)[1]);
                break;
        }
    }

}
