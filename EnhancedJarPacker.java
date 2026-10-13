package mod.brasfoot;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class EnhancedJarPacker {
    public static void main(String[] args) throws Exception {
        File srcJar = new File("C:/Brasfoot22-23/bf22-23_extracted.jar");
        File targetJar = new File("C:/Brasfoot22-23/bf22-23_modded.jar");
        File buildDir = new File("C:/Brasfoot22-23/projeto/build_mod");

        Map<String, File> overrideFiles = new HashMap<>();
        collectFiles(buildDir, "", overrideFiles);
        System.out.println("Files to inject/override: " + overrideFiles.size());
        for (String k : overrideFiles.keySet()) {
            System.out.println(" -> " + k);
        }

        JarFile jarIn = new JarFile(srcJar);
        JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(targetJar));

        Enumeration<JarEntry> entries = jarIn.entries();
        byte[] buf = new byte[8192];
        Set<String> processed = new HashSet<>();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (overrideFiles.containsKey(name)) {
                File f = overrideFiles.get(name);
                JarEntry newEntry = new JarEntry(name);
                jarOut.putNextEntry(newEntry);
                FileInputStream fis = new FileInputStream(f);
                int r;
                while ((r = fis.read(buf)) != -1) {
                    jarOut.write(buf, 0, r);
                }
                fis.close();
                jarOut.closeEntry();
                processed.add(name);
                System.out.println("Overwritten in JAR: " + name);
            } else {
                jarOut.putNextEntry(new JarEntry(name));
                InputStream is = jarIn.getInputStream(entry);
                int r;
                while ((r = is.read(buf)) != -1) {
                    jarOut.write(buf, 0, r);
                }
                is.close();
                jarOut.closeEntry();
            }
        }

        for (Map.Entry<String, File> e : overrideFiles.entrySet()) {
            if (!processed.contains(e.getKey())) {
                JarEntry newEntry = new JarEntry(e.getKey());
                jarOut.putNextEntry(newEntry);
                FileInputStream fis = new FileInputStream(e.getValue());
                int r;
                while ((r = fis.read(buf)) != -1) {
                    jarOut.write(buf, 0, r);
                }
                fis.close();
                jarOut.closeEntry();
                System.out.println("Added new to JAR: " + e.getKey());
            }
        }

        jarOut.close();
        jarIn.close();
        System.out.println("SUCCESS! Modded JAR built: " + targetJar.getAbsolutePath() + " (" + targetJar.length() + " bytes)");
    }

    private static void collectFiles(File dir, String prefix, Map<String, File> map) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            String path = prefix.isEmpty() ? f.getName() : prefix + "/" + f.getName();
            if (f.isDirectory()) {
                collectFiles(f, path, map);
            } else if (!f.getName().endsWith(".java") 
                    && !f.getName().contains("JarPacker") 
                    && !f.getName().contains("InspectMethods") 
                    && !f.getName().contains("TestASM") 
                    && !f.getName().contains("ModInjector")
                    && !f.getName().contains("FullEaFcInjector")
                    && !f.getName().contains("VerifyClasses")
                    && !f.getName().contains("TestVisual")) {
                map.put(path, f);
            }
        }
    }
}
