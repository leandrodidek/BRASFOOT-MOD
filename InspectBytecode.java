package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InspectBytecode {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        ZipEntry entry = zip.getEntry("a/bz.class");
        InputStream is = zip.getInputStream(entry);
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ("mJ".equals(name)) {
                    System.out.println("=== a/bz.mJ ===");
                    return new MethodVisitor(Opcodes.ASM5) {
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            System.out.println("  FIELD: " + opcode + " " + owner + "." + name + " " + desc);
                        }
                        @Override
                        public void visitIntInsn(int opcode, int operand) {
                            System.out.println("  INT_INSN: " + opcode + " operand=" + operand);
                        }
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            if (name.contains("add") || name.contains("Dimension") || name.contains("setSize") || name.contains("<init>")) {
                                System.out.println("  METHOD: " + owner + "." + name + " " + desc);
                            }
                        }
                    };
                }
                return null;
            }
        }, 0);
        zip.close();
    }
}
