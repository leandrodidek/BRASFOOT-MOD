package mod.brasfoot;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TestInspection {
    public static void main(String[] args) {
        try {
            File jarFile = new File("C:/Brasfoot22-23/bf22-23_extracted.jar");
            URLClassLoader loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
            ZipFile zf = new ZipFile(jarFile);
            Enumeration<? extends ZipEntry> entries = zf.entries();
            Class<?> baseL = loader.loadClass("best.L");
            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                String name = ze.getName();
                if (name.endsWith(".class") && !name.contains("$")) {
                    String className = name.replace(".class", "").replace('/', '.');
                    try {
                        Class<?> c = loader.loadClass(className);
                        if (baseL.isAssignableFrom(c) && !c.equals(baseL)) {
                            System.out.println("Subclass of best.L: " + className);
                            for (Method m : c.getDeclaredMethods()) {
                                System.out.println("   Method: " + m.getName() + " -> " + m.getReturnType());
                            }
                        }
                    } catch (Throwable ignored) {}
                }
            }
            zf.close();
            loader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
