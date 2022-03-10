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
}