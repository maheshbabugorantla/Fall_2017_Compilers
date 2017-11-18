public class Scope {
    public String name;
    public String loc_type;
    public String function;
    public String decl_type;
    public String register;

    public Scope(String name, String loc_type, String function, String decl_type) {
        this.name = name;
        this.loc_type = loc_type;
        this.function = function;
        this.decl_type = decl_type;
        this.register = null;
    }

    public Scope(String name, String loc_type, String function, String decl_type, String register) {
        this.name = name;
        this.loc_type = loc_type;
        this.function = function;
        this.decl_type = decl_type;
        this.register = register;
    }


    public void print() {
        System.out.printf("name = " + name);
        System.out.printf(" loc = " + loc_type);
        System.out.printf(" func = " + function);
        System.out.printf(" dec = " + decl_type);
        if (register == null) {
            System.out.printf(" name = null\n");
        }
        else {
            System.out.printf(" name = " + register + "\n");
        }

    }

}
