import java.io.*;
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
                return "name "+ name + " type " + type;
            }
            return "name " + name + " type " + type + " value " + value;
        }
    }

    ArrayList<String> symbolTableNames = new ArrayList<>();
    LinkedHashMap<String, Node> globalHT = new LinkedHashMap<>();
    Stack<LinkedHashMap<String, Node>> stackHT = new Stack<>();

    public void enterString_decl(LittleParser.String_declContext ctx) {
        String[] list = ctx.getText().split("[a-z]+");
        String type = "";

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals("STRING")) {
                type = list[i];
            }
        }

        Node stringNode = new Node(ctx.id().getText(), type, ctx.str().getText());
        stackHT.peek().put(stringNode.name, stringNode);
    }

    public void enterPgm_body(LittleParser.Pgm_bodyContext ctx) {
        symbolTableNames.add("Symbol table GLOBAL");
        stackHT.push(globalHT);
    }
    
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type    = ctx.var_type().getText();
        String name    = "placeholder";
        String id_list = ctx.id_list().getText();
        int    size    = id_list.length();
        String list[]  = id_list.split(",");
        LinkedHashMap<String, Node> current_table = stackHT.peek();

        for(String str : list){
            if(!current_table.containsKey(str)){
                Node var_decl = new Node(str, type, null);
                current_table.put(str, var_decl);
               // System.out.println("name "+str+" type "+type);
            }
            else{
               System.out.println("DECLARATION ERROR "+str);
            }
        }
    }

    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        LinkedHashMap<String, Node> functionTable  = new LinkedHashMap<>();
        stackHT.push(functionTable);

        String tableName = ctx.id().getText();
        symbolTableNames.add("Symbol table " + tableName);

        String paramRegex = "(INT|FLOAT)|,(INT|FLOAT)";

        String[] var_id = ctx.param_decl_list().getText().replaceFirst(paramRegex, "").split(paramRegex, 0);
        String[] type = ctx.param_decl_list().getText().split("([a-z]),|([a-z])");

        for(int i = 0; i < var_id.length; i++){
            if(!functionTable.containsKey(var_id[i])){
                if (var_id[i].equals("") || type[i].equals("")) {
                    break;
                }
                Node var_decl = new Node(var_id[i], type[i], null);
                functionTable.put(var_id[i], var_decl);
            } else {
                System.out.println("DECLARATION ERROR "+var_id[i]);
            }
        }
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