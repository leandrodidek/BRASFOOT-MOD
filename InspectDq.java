package mod.brasfoot;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class InspectDq {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("a.dq");
        System.out.println("Superclass: " + clazz.getSuperclass().getName());
        System.out.println("Methods in a.dq:");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println("  " + m.getName() + " -> " + m.getReturnType().getName() + " params: " + java.util.Arrays.toString(m.getParameterTypes()));
        }
        System.out.println("Fields in a.dq:");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println("  " + f.getName() + " -> " + f.getType().getName());
        }
    }
}
