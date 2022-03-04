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

    Hashtable<Integer, Node> globalHT = new Hashtable<>();
    Stack<Hashtable> stackHT = new Stack<>();

    /*public void enterProgram(LittleParser.ProgramContext ctx){
        /*String id = ctx.id().getText();
        String body = ctx.pgm_body().getText();
        System.out.println(body+"\n");*/
    //*/}

    public void enterPgm_body(LittleParser.Pgm_bodyContext ctx) {
        System.out.println("Symbol table GLOBAL");
    }
    
    public void enterString_decl(LittleParser.String_declContext ctx) {
        System.out.println(ctx.id().getText());
        System.out.println(ctx.str().getText());
    }

    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type    = ctx.var_type().getText();
        String name    = "placeholder";
        String id_list = ctx.id_list().getText();
        int    size    = id_list.length();
        String list[]  = id_list.split(",");

        for(String str : list){
            System.out.println("name "+str+" type "+type);
        }
    }

    public void enterId_list(LittleParser.Id_listContext ctx) {
        //System.out.println(ctx.getText());
    }
}