package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class FindCbBestIWrites {
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
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        if ("best/I".equals(owner)) {
                            System.out.println("c/b." + mName + " calls best/I." + name + desc);
                        }
                    }
                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        if ("best/I".equals(owner)) {
                            System.out.println("c/b." + mName + " accesses best/I." + name + desc);
                        }
                    }
                };
            }
        }, 0);
    }
}
