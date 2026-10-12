package mod.brasfoot;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class InspectCb {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("c.b");
        System.out.println("Methods in c.b:");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println("  " + m.getName() + " -> " + m.getReturnType().getName() + " params: " + java.util.Arrays.toString(m.getParameterTypes()));
        }
        System.out.println("Fields in c.b:");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println("  " + f.getName() + " -> " + f.getType().getName());
        }
    }
}
