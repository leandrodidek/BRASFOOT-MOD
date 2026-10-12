package mod.brasfoot;

import java.io.InputStream;
import java.util.zip.ZipFile;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;

public class InspectBestIArrays {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("best/I.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            String mName;
            @Override
            public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                mName = name + desc;
                return new MethodVisitor(Opcodes.ASM5) {
                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        if ("best/I".equals(owner) && desc.equals("[I")) {
                            System.out.println("best/I." + mName + " " + (op == Opcodes.PUTFIELD ? "PUT" : "GET") + " Field " + name);
                        }
                    }
                };
            }
        }, 0);
    }
}
