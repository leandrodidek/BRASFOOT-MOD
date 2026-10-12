package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InspectCoach {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        for (String cName : new String[]{"a/bz.class", "a/eg.class"}) {
            System.out.println("=== " + cName + " ===");
            ZipEntry entry = zip.getEntry(cName);
            InputStream is = zip.getInputStream(entry);
            ClassReader cr = new ClassReader(is);
            cr.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                    System.out.println(" Field: " + name + " : " + desc);
                    return null;
                }
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    System.out.println(" Method: " + name + " " + desc);
                    return null;
                }
            }, 0);
        }
        zip.close();
    }
}
