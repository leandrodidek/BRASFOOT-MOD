package mod.brasfoot;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class TestVerifyBoth {
    public static void main(String[] args) {
        try {
            System.out.println("Testing verification of a.cD and a.dV on Java " + System.getProperty("java.version") + "...");
            File buildDir = new File("C:/Brasfoot22-23/projeto/build_mod");
            File baseJar = new File("C:/Brasfoot22-23/bf22-23_extracted.jar");
            URLClassLoader cl = new URLClassLoader(new URL[]{
                buildDir.toURI().toURL(),
                baseJar.toURI().toURL()
            }, TestVerifyBoth.class.getClassLoader());
            
            Class<?> cDClass = cl.loadClass("a.cD");
            System.out.println("SUCCESS! Loaded class: " + cDClass.getName());

            Class<?> dVClass = cl.loadClass("a.dV");
            System.out.println("SUCCESS! Loaded class: " + dVClass.getName());

            cl.close();
            System.out.println("ALL CLASSES VERIFIED CLEANLY WITH 0 ERRORS!");
        } catch (Throwable t) {
            System.out.println("VERIFICATION FAILED: " + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace(System.out);
        }
    }
}
