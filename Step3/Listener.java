public class Listener extends LittleBaseListener {
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

    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type    = ctx.var_type().getText();
        String name    = "placeholder";
        String id_list = ctx.id_list().getText();
        int    size    = id_list.length();
        String list[]  = id_list.split(",");

        for(String str : list){
            System.out.println("name " + str + " type " + type);
        }
    }

}