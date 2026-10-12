package mod.brasfoot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InspectBestIScoreMethods {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("best.I");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals("hu") || m.getName().equals("hw") || m.getName().equals("hv") || m.getName().equals("hx") || m.getName().equals("hF")) {
                System.out.println("Method: " + m.getName() + " -> " + m.getReturnType() + " params: " + java.util.Arrays.toString(m.getParameterTypes()));
            }
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equals("fB") || f.getName().equals("fC") || f.getName().equals("fO") || f.getName().equals("fP") || f.getName().equals("fZ") || f.getName().equals("ga")) {
                System.out.println("Field: " + f.getName() + " type: " + f.getType());
            }
        }
    }
}
