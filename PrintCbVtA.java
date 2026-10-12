package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class PrintCbVtA {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("c/b.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                if ("vT".equals(name)) {
                    return new MethodVisitor(Opcodes.ASM5) {
                        boolean logging = false;
                        int count = 0;
                        @Override
                        public void visitTypeInsn(int op, String type) {
                            if (op == Opcodes.NEW && "best/A".equals(type)) {
                                logging = true;
                            }
                            if (logging) System.out.println("NEW " + type);
                        }
                        @Override
                        public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                            if (logging) {
                                System.out.println("INVOKE " + owner + "." + name + desc);
                                count++;
                                if (count > 40) logging = false;
                            }
                        }
                        @Override
                        public void visitFieldInsn(int op, String owner, String name, String desc) {
                            if (logging) System.out.println("FIELD " + op + " " + owner + "." + name + " " + desc);
                        }
                        @Override
                        public void visitVarInsn(int op, int var) {
                            if (logging) System.out.println("VAR " + op + " " + var);
                        }
                        @Override
                        public void visitInsn(int op) {
                            if (logging) System.out.println("INSN " + op);
                        }
                        @Override
                        public void visitIntInsn(int op, int operand) {
                            if (logging) System.out.println("INTINSN " + op + " " + operand);
                        }
                    };
                }
                return null;
            }
        }, 0);
    }
}
