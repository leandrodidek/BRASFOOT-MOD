package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.*;
import java.util.zip.ZipFile;

public class FullEaFcInjector {
    public static void main(String[] args) throws Exception {
        File outDir = new File("C:/Brasfoot22-23/projeto/build_mod/a");
        outDir.mkdirs();

        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");

        // -------------------------------------------------------------
        // 1. PATCH a/eg.class (Tela de Treinador: manter original 1024x748)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/eg.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cr.accept(cw, 0);

            byte[] b = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(new File(outDir, "eg.class"));
            fos.write(b);
            fos.close();
            System.out.println("PATCHED a/eg.class written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 2. PATCH a/dV.class (VAR para Gols e Vermelhos)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/dV.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    // Hook no evento principal: a(Lcomponents/v;Lbest/I;Lbest/A;I)V
                    if ("a".equals(name) && "(Lcomponents/v;Lbest/I;Lbest/A;I)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
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
            System.out.println("PATCHED a/dV.class (VAR Event Hook) written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 3. PATCH a/bz.class (C0053bz - Estilizacao EA FC 27)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/bz.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if ("mJ".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bp", "Lcomponents/K;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bl", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "we", "Ljavax/swing/JPanel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "vd", "Ljavax/swing/JPanel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "vA", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "vy", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bd", "Ljavax/swing/JCheckBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bn", "Ljavax/swing/JButton;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bo", "Ljavax/swing/JButton;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "vb", "Ljavax/swing/JButton;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bf", "Ljavax/swing/JComboBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bg", "Ljavax/swing/JComboBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bh", "Ljavax/swing/JComboBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "Bi", "Ljavax/swing/JComboBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "ug", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "uh", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "a_", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "ur", "Ljavax/swing/JLabel;");
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "applyPreMatchLayout",
                                        "(Ljavax/swing/JPanel;Ljavax/swing/JLayeredPane;Ljavax/swing/JLabel;Ljavax/swing/JPanel;Ljavax/swing/JPanel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JCheckBox;Ljavax/swing/JButton;Ljavax/swing/JButton;Ljavax/swing/JButton;Ljavax/swing/JComboBox;Ljavax/swing/JComboBox;Ljavax/swing/JComboBox;Ljavax/swing/JComboBox;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(new File(outDir, "bz.class"));
            fos.write(b);
            fos.close();
            System.out.println("PATCHED a/bz.class written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 4. PATCH a/cD.class (EA FC / PES Free Swap Substitutions)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/cD.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    // 1. Hook no final do construtor <init>: grava os titulares iniciais
                    if ("<init>".equals(name)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/SubstitutionManager", "onPauseOpen",
                                        "(Ljava/lang/Object;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    // 2. Intercepta K(II)V (INVOKESPECIAL ou INVOKEVIRTUAL) dentro de H(II)V para troca livre!
                    if ("H".equals(name) && "(II)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if ((opcode == Opcodes.INVOKESPECIAL || opcode == Opcodes.INVOKEVIRTUAL)
                                        && "a/cD".equals(owner) && "K".equals(name) && "(II)V".equals(desc)) {
                                    // Na pilha ja estao [this, i, i2]
                                    // Redireciona diretamente para handleSwap(Object, int, int)V
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/SubstitutionManager", "handleSwap",
                                        "(Ljava/lang/Object;II)V", false);
                                    return;
                                }
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }
                        };
                    }

                    // 3. Hook no inicio de pb()V (ao clicar em Voltar ao Jogo): efetiva as substituicoes finais!
                    if ("pb".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                mv.visitVarInsn(Opcodes.ALOAD, 0);
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/SubstitutionManager", "onResumeGame",
                                    "(Ljava/lang/Object;)V", false);
                            }
                        };
                    }

                    // 4. Hook de layout EA FC no final de mJ()V
                    if ("mJ".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "DF", "Lcomponents/M;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Ds", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dq", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dw", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dx", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "zO", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "zP", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dv", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "DE", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "xI", "Ljavax/swing/JLabel;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dm", "Ljavax/swing/JButton;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Do", "Ljavax/swing/JComboBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dp", "Ljavax/swing/JComboBox;");
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/cD", "Dn", "Ljavax/swing/JComboBox;");
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "applyInMatchLayout",
                                        "(Ljavax/swing/JPanel;Ljavax/swing/JLayeredPane;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JLabel;Ljavax/swing/JButton;Ljavax/swing/JComboBox;Ljavax/swing/JComboBox;Ljavax/swing/JComboBox;)V", false);
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
            System.out.println("PATCHED a/cD.class (EA FC / PES Free Subs) written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 5. PATCH a/fB.class (Menu Opcoes: VAR Automatico Sim/Nao)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/fB.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if ("mJ".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this (fB)
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitFieldInsn(Opcodes.GETFIELD, "a/fB", "vd", "Ljavax/swing/JPanel;");
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/VarOptionsHelper", "injectVarOption",
                                        "(Ljava/lang/Object;Ljavax/swing/JPanel;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(new File(outDir, "fB.class"));
            fos.write(b);
            fos.close();
            System.out.println("PATCHED a/fB.class (Menu Opcoes VAR) written: " + b.length + " bytes");
        }

        zip.close();
        System.out.println("ALL 5 CLASSES SUCCESSFULLY INJECTED!");
    }
}
