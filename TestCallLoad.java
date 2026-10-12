package mod.brasfoot;

import best.M;

public class TestCallLoad {
    public static void main(String[] args) {
        try {
            System.out.println("Current dir: " + System.getProperty("user.dir"));
            new M(true);
            System.out.println("Calling c.a.c(\"teste var\", false)...");
            boolean result = c.a.c("teste var", false);
            System.out.println("Result: " + result);
        } catch (Throwable t) {
            System.out.println("Caught exception in TestCallLoad:");
            t.printStackTrace(System.out);
        }
    }
}
