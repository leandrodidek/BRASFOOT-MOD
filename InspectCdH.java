package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InspectCdH {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        ZipEntry entry = zip.getEntry("a/cD.class");
        InputStream is = zip.getInputStream(entry);
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ("H".equals(name) || "pb".equals(name)) {
                    System.out.println("=== a/cD." + name + " " + desc + " ===");
                    return new MethodVisitor(Opcodes.ASM5) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            System.out.println("  METHOD: " + owner + "." + name + " " + desc);
                        }
                    };
                }
                return null;
            }
        }, 0);
        zip.close();
    }
}
