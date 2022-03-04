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
        System.out.println(ctx.var_type().getText());
        System.out.println(ctx.id_list().id().getText());
        System.out.println(ctx.id_list().id_tail().id().getText());
        System.out.println(ctx.id_list().id_tail().id_tail().id().getText());
        System.out.println(ctx.id_list().id_tail().id_tail().id_tail().id().getText());
    }

}