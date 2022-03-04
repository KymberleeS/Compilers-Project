public class Listener extends LittleBaseListener {
    public void enterProgram(LittleParser.ProgramContext ctx){
        String id = ctx.id().getText();
        String body = ctx.pgm_body().getText();
        System.out.println(id+"\n"+body);
    }
}