package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class PrintBestIA {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("best/I.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                if ("a".equals(name) && desc.contains("best/A")) {
                    System.out.println("Found method a: " + desc);
                    return new MethodVisitor(Opcodes.ASM5) {
                        @Override
                        public void visitFieldInsn(int op, String owner, String name, String desc) {
                            if ("fN".equals(name)) System.out.println("  Field fN " + op);
                        }
                        @Override
                        public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                            if ("java/util/ArrayList".equals(owner)) System.out.println("  ArrayList." + name + " " + desc);
                        }
                    };
                }
                return null;
            }
        }, 0);
    }
}
