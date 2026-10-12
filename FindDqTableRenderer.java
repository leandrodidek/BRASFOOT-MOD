package mod.brasfoot;

import java.io.InputStream;
import java.util.zip.ZipFile;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;

public class FindDqTableRenderer {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("a/dq.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            String mName;
            @Override
            public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                mName = name + desc;
                return new MethodVisitor(Opcodes.ASM5) {
                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        if ("a/dq".equals(owner) && "DG".equals(name)) {
                            System.out.println("a/dq." + mName + " accesses DG");
                        }
                    }
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        if ("best/I".equals(owner)) {
                            System.out.println("a/dq." + mName + " calls best/I." + name + desc);
                        }
                    }
                };
            }
        }, 0);
    }
}
