package mod.brasfoot;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class InspectBestI {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("best.I");
        System.out.println("Methods in best.I:");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getReturnType() == ArrayList.class || m.getName().equals("V") || m.getName().startsWith("h")) {
                System.out.println("  " + m.getName() + " -> " + m.getReturnType().getName() + " params: " + java.util.Arrays.toString(m.getParameterTypes()));
            }
        }
        System.out.println("Fields in best.I:");
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType() == ArrayList.class || f.getType() == int.class) {
                System.out.println("  " + f.getName() + " -> " + f.getType().getName());
            }
        }
    }
}
