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
    }

    Hashtable<String, Node> globalHT = new Hashtable<>();
    Stack<Hashtable<String, Node>> stackHT = new Stack<>();

    public void enterString_decl(LittleParser.String_declContext ctx) {
        String[] list = ctx.getText().split("[a-z]+");
        String type = "";

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals("STRING")) {
                type = list[i];
            }
        }

        System.out.println("name " + ctx.id().getText() + " type " + type + " value " + ctx.str().getText());
    }

    public void enterPgm_body(LittleParser.Pgm_bodyContext ctx) {
        System.out.println("Symbol table GLOBAL");
        stackHT.push(globalHT);
    }
    
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type    = ctx.var_type().getText();
        String name    = "placeholder";
        String id_list = ctx.id_list().getText();
        int    size    = id_list.length();
        String list[]  = id_list.split(",");
        Hashtable<String, Node> current_table = stackHT.peek();

        for(String str : list){
            if(!current_table.containsKey(str)){
                Node var_decl = new Node(str, type, null);
                current_table.put(str, var_decl);
                System.out.println("name "+str+" type "+type);
            }
            else{
               System.out.println("DECLARATION ERROR "+str);
            }
        }
    }
}