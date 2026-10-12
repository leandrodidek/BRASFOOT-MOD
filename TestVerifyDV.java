package mod.brasfoot;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class TestVerifyDV {
    public static void main(String[] args) {
        try {
            System.out.println("Testing loading a.dV class...");
            File buildDir = new File("C:/Brasfoot22-23/projeto/build_mod");
            File baseJar = new File("C:/Brasfoot22-23/bf22-23_extracted.jar");
            URLClassLoader cl = new URLClassLoader(new URL[]{
                buildDir.toURI().toURL(),
                baseJar.toURI().toURL()
            }, TestVerifyDV.class.getClassLoader());
            
            Class<?> dVClass = cl.loadClass("a.dV");
            System.out.println("SUCCESS! Loaded class: " + dVClass.getName());
            cl.close();
        } catch (Throwable t) {
            System.out.println("VERIFICATION FAILED: " + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace(System.out);
        }
    }
}
