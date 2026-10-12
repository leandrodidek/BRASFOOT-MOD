package mod.brasfoot;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class InspectComponentsS {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("components.s");
        System.out.println("Methods in components.s:");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println("  " + m.getName() + " -> " + m.getReturnType().getName() + " params: " + java.util.Arrays.toString(m.getParameterTypes()));
        }
        System.out.println("Fields in components.s:");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println("  " + f.getName() + " -> " + f.getType().getName());
        }
    }
}
