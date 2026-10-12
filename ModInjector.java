package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModInjector {
    public static void main(String[] args) throws Exception {
        File outDir = new File("C:/Brasfoot22-23/projeto/build_mod/a");
        outDir.mkdirs();

        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");

        // ==========================================
        // 1. INJETAR a/cD.class (Desfazer Substituição)
        // Apenas no final de mJ() com parâmetros (Object, Object, Object, Object)
        // ==========================================
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/cD.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    // Hook no final de mJ() (criação da interface de pausa)
                    if ("mJ".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    // ModBridge.initUndoButton((Object)this, (Object)this.Db, (Object)this.DA, (Object)this.DF);
                                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Db", "Lcomponents/v;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "DA", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "DF", "Lcomponents/M;");
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/ModBridge", "initUndoButton", 
                                        "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(new File(outDir, "cD.class"));
            fos.write(b);
            fos.close();
            System.out.println("Patched a/cD.class written successfully (" + b.length + " bytes)");
        }

        // ==========================================
        // 2. INJETAR a/dV.class (VAR e Expulsão)
        // ==========================================
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/dV.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    // Hook no início de a(Lcomponents/v;Lbest/I;Lbest/A;I)V
                    if ("a".equals(name) && "(Lcomponents/v;Lbest/I;Lbest/A;I)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                // if (ModBridge.handleEventHook(c0378v, i, a2, i2, this)) return;
                                mv.visitVarInsn(Opcodes.ALOAD, 1); // c0378v
                                mv.visitVarInsn(Opcodes.ALOAD, 2); // i
                                mv.visitVarInsn(Opcodes.ALOAD, 3); // a2
                                mv.visitVarInsn(Opcodes.ILOAD, 4); // i2
                                mv.visitVarInsn(Opcodes.ALOAD, 0); // this (dV)
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/ModBridge", "handleEventHook",
                                    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;ILjava/lang/Object;)Z", false);
                                Label continueLbl = new Label();
                                mv.visitJumpInsn(Opcodes.IFEQ, continueLbl);
                                mv.visitInsn(Opcodes.RETURN);
                                mv.visitLabel(continueLbl);
                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(new File(outDir, "dV.class"));
            fos.write(b);
            fos.close();
            System.out.println("Patched a/dV.class written successfully (" + b.length + " bytes)");
        }

        zip.close();
        System.out.println("All patches injected successfully!");
    }
}
