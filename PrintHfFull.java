package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class PrintHfFull {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("best/I.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                if ("hF".equals(name)) {
                    System.out.println("Found method hF: " + desc);
                    return new MethodVisitor(Opcodes.ASM5) {
                        @Override
                        public void visitInsn(int op) { System.out.println("  INSN " + op); }
                        @Override
                        public void visitVarInsn(int op, int var) { System.out.println("  VAR " + op + " " + var); }
                        @Override
                        public void visitFieldInsn(int op, String owner, String name, String desc) { System.out.println("  FIELD " + op + " " + owner + "." + name + " " + desc); }
                        @Override
                        public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) { System.out.println("  INVOKE " + owner + "." + name + desc); }
                        @Override
                        public void visitJumpInsn(int op, Label label) { System.out.println("  JUMP " + op); }
                        @Override
                        public void visitIincInsn(int var, int inc) { System.out.println("  IINC " + var + " " + inc); }
                    };
                }
                return null;
            }
        }, 0);
    }
}
