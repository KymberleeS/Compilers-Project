public class Listener extends LittleBaseListener {
    /*public void enterProgram(LittleParser.ProgramContext ctx){
        String id = ctx.id().getText();
        String body = ctx.pgm_body().getText();
        System.out.println(body+"\n");
    }*/

    public void enterString_decl(LittleParser.String_declContext ctx) {
        System.out.println(ctx.id().getText());
        System.out.println(ctx.str().getText());
    }

    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type    = ctx.var_type().getText();
        String name    = "placeholder";
        String id_list = ctx.id_list().getText();
        int size       = id_list.length();
        char list[]    = id_list.toCharArray();

        for(int i = 0; i < size; i++){
            if(list[i] == ','){
                continue;
            }
            else{
                System.out.println("name "+list[i]+" type "+type);
            }
        }

        /*System.out.println(list[0]);
        System.out.println("name "+name+" type "+type);
        System.out.println(ctx.id_list());
        System.out.println(ctx.id_list().getText());
        System.out.println(ctx.id_list().id().getText());
        System.out.println(ctx.id_list().id_tail().id().getText());
        System.out.println(ctx.id_list().id_tail().id_tail().id().getText());
        System.out.println(ctx.id_list().id_tail().id_tail().id_tail().id().getText());*/
    }

    public void enterId_list(LittleParser.Id_listContext ctx) {
        System.out.println(ctx.getText());
    }
}