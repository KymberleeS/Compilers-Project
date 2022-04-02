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

public class Listener extends LittleBaseListener {
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

    ArrayList<String> symbolTableNames = new ArrayList<>();
    LinkedHashMap<String, Node> globalHT = new LinkedHashMap<>();
    Stack<LinkedHashMap<String, Node>> stackHT = new Stack<>();
    int scope_cnt = 0;

    public void enterString_decl(LittleParser.String_declContext ctx) {
        String[] list = ctx.getText().split("[a-z]+");
        String type = "";

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals("STRING")) {
                type = list[i];
            }
        }

        //create a node for the string variable
        Node stringNode = new Node(ctx.id().getText(), type, ctx.str().getText());

        //put the node into the symbol table who's scope the string variable was
        stackHT.peek().put(stringNode.name, stringNode);
    }

    public void enterPgm_body(LittleParser.Pgm_bodyContext ctx) {
        symbolTableNames.add("Symbol table GLOBAL");

        //add the global hash table to the stack
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

    public void printHashTableValues() {
        for (int i = 0; i < stackHT.size(); i++) {
            System.out.println(symbolTableNames.get(i));
            Iterator<Node> itr = stackHT.get(i).values().iterator();

            while(itr.hasNext()){
                System.out.println(itr.next().toString());
            }
            System.out.print("\n");
        }
    }








    ArrayList<ASTNode> astIRNodes = new ArrayList<>();
    int tempRegister = 1;

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
                 if (containsParentheses(buildAST) == true) {
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

    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        System.out.println(ctx.id_list().getText());
    }

    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        System.out.println(ctx.id_list().getText());
    }

    public void printIRNodes() {
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
    }

    private void parserHelper(String[] temp, ArrayList tempBuildAST) {
        for (int i = 0; i < temp.length; i++) {
            tempBuildAST.add(temp[i]);
        }
    }

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

    private void buildASTNode(ArrayList<ASTNode> astIRNodes, ArrayList<ASTNode> buildAST, int i) {
        String add = "ADDI";
        String sub = "SUBI";
        String mul = "MULTI";
        String div = "DIVI";
        String store = "STOREI";

        astIRNodes.add(new ASTNode(buildAST.get(i - 1).value, null, null, null));
        astIRNodes.add(new ASTNode(buildAST.get(i + 1).value, null, null, null));

        buildAST.get(i).leftChild = buildAST.get(i - 1);
        buildAST.get(i).rightChild = buildAST.get(i + 1);

        if (containsFloatNum(astIRNodes) == true) {
            add = "ADDF";
            sub = "SUBF";
            mul = "MULTF";
            div = "DIVF";
            store = "STOREF";
        }

        switch (buildAST.get(i).value) {
            case "+":
                astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1),
                        ";" + add + " " + buildAST.get(i).leftChild.value + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister));
                break;
            case "-":
                astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1),
                        ";" + sub + " " + buildAST.get(i).leftChild.value + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister));
                break;
            case "*":
                astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1),
                        ";" + mul + " " + buildAST.get(i).leftChild.value + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister));
                break;
            case "/":
                astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1),
                        ";" + div + " " + buildAST.get(i).leftChild.value + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister));
                break;
            case ":=":
                if (buildAST.get(i).rightChild.value.equals("+") || buildAST.get(i).rightChild.value.equals("-") ||
                        buildAST.get(i).rightChild.value.equals("*") || buildAST.get(i).rightChild.value.equals("/")) {
                    astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1),
                            ";" + store + " " + "$T" + tempRegister + " " + buildAST.get(i).leftChild.value));
                } else {
                    astIRNodes.add(new ASTNode(buildAST.get(i).value, buildAST.get(i - 1), buildAST.get(i + 1),
                            ";" + store + " " + buildAST.get(i).rightChild.value + " $T" + tempRegister + "\n" +
                                    ";" + store + " " + "$T" + tempRegister + " " + buildAST.get(i).leftChild.value));
                }
                tempRegister++;
                break;
        }
    }


}
