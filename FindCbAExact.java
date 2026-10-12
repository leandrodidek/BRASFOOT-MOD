package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class FindCbAExact {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("c/b.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            String mName;
            @Override
            public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                mName = name + desc;
                return new MethodVisitor(Opcodes.ASM5) {
                    @Override
                    public void visitTypeInsn(int op, String type) {
                        if (op == Opcodes.NEW && "best/A".equals(type)) {
                            System.out.println("NEW best/A in c/b." + mName);
                        }
                    }
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        if ("best/A".equals(owner) && "<init>".equals(name)) {
                            System.out.println("  invokespecial best/A.<init> " + desc + " in c/b." + mName);
                        }
                    }
                };
            }
        }, 0);
    }
}
