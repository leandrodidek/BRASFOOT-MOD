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
        // 1. PATCH a/eg.class (Expandir Janela de Escalação de 1024 para 1580x756)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/eg.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if ("qZ".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitIntInsn(int opcode, int operand) {
                                if (opcode == Opcodes.SIPUSH && operand == 1024) {
                                    super.visitIntInsn(Opcodes.SIPUSH, 1580);
                                    return;
                                }
                                if (opcode == Opcodes.SIPUSH && operand == 748) {
                                    super.visitIntInsn(Opcodes.SIPUSH, 756);
                                    return;
                                }
                                super.visitIntInsn(opcode, operand);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("a/eg.class", b);
            System.out.println("PATCHED a/eg.class written: " + b.length + " bytes (Dialog expandido para 1580x756)");
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

                    if ("a".equals(name) && "(Lcomponents/v;Lbest/I;Lbest/A;I)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                mv.visitVarInsn(Opcodes.ALOAD, 1);
                                mv.visitVarInsn(Opcodes.ALOAD, 2);
                                mv.visitVarInsn(Opcodes.ALOAD, 3);
                                mv.visitVarInsn(Opcodes.ILOAD, 4);
                                mv.visitVarInsn(Opcodes.ALOAD, 0);
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/ModBridge", "handleEventHook",
                                    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;ILjava/lang/Object;)Z", false);
                                Label continueLbl = new Label();
                                mv.visitJumpInsn(Opcodes.IFEQ, continueLbl);
                                mv.visitInsn(Opcodes.RETURN);
                                mv.visitLabel(continueLbl);
                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String mName, String mDesc, boolean itf) {
                                if (opcode == Opcodes.INVOKEVIRTUAL && "a/dV".equals(owner) && "dk".equals(mName) && "(I)V".equals(mDesc)) {
                                    // Redireciona a/dV.dk(soundId) para ModBridge.playGameSound para suprimir duplicidade de gol pós-VAR
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/ModBridge", "playGameSound",
                                        "(Ljava/lang/Object;I)V", false);
                                    return;
                                }
                                super.visitMethodInsn(opcode, owner, mName, mDesc, itf);
                            }
                        };
                    }

                    if ("e".equals(name) && "(IZ)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitIntInsn(int opcode, int operand) {
                                if (opcode == Opcodes.SIPUSH) {
                                    if (operand == 966 || operand == 1024) {
                                        super.visitIntInsn(opcode, 1740);
                                        return;
                                    }
                                    if (operand == 726 || operand == 663) {
                                        super.visitIntInsn(opcode, 880);
                                        return;
                                    }
                                }
                                super.visitIntInsn(opcode, operand);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("a/dV.class", b);
            System.out.println("PATCHED a/dV.class (VAR Event Hook) written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 3. PATCH a/bz.class (Expandir Bp para 1580, aplicar EaFcUiHelper e liberar Juniores)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/bz.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    // mJ: Layout e inicialização da tela de escalação
                    if ("mJ".equals(name) && "()V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitIntInsn(int opcode, int operand) {
                                if (opcode == Opcodes.SIPUSH && operand == 1024) {
                                    super.visitIntInsn(Opcodes.SIPUSH, 1580);
                                    return;
                                }
                                if (opcode == Opcodes.SIPUSH && operand == 748) {
                                    super.visitIntInsn(Opcodes.SIPUSH, 756);
                                    return;
                                }
                                super.visitIntInsn(opcode, operand);
                            }

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



                    // oj(): Abertura da Janela de Táticas Salvas interna estilo EA FC
                    if ("oj".equals(name) && "()V".equals(desc)) {
                        mv.visitCode();
                        mv.visitVarInsn(Opcodes.ALOAD, 0); // this (a/bz)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "openTacticsDialog",
                            "(Ljavax/swing/JPanel;)V", false);
                        mv.visitInsn(Opcodes.RETURN);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();
                        return new MethodVisitor(Opcodes.ASM5, null) {};
                    }

                    // a(MouseEvent): Atualização do posicionamento do card arrastado
                    if ("a".equals(name) && "(Ljava/awt/event/MouseEvent;)V".equals(desc)) {
                        mv.visitCode();
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, "a/bz", "AC", "Ljavax/swing/JLabel;");
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "updateDragLocation",
                            "(Ljavax/swing/JLabel;Ljava/awt/event/MouseEvent;)V", false);
                        mv.visitInsn(Opcodes.RETURN);
                        mv.visitMaxs(2, 2);
                        mv.visitEnd();
                        return new MethodVisitor(Opcodes.ASM5, null) {};
                    }

                    // b(MouseEvent): Início do arrasto (Drag & Drop) com o card completo (camisa + subcard)
                    if ("b".equals(name) && "(Ljava/awt/event/MouseEvent;)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            private boolean afterGetX = false;
                            private boolean afterGetY = false;
                            private int fiftyCount = 0;

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                afterGetX = ("getX".equals(name) && "()I".equals(desc));
                                afterGetY = ("getY".equals(name) && "()I".equals(desc));

                                if (opcode == Opcodes.INVOKEVIRTUAL && "a/bz".equals(owner) && "a".equals(name) && "(ILbest/F;)Ljavax/swing/ImageIcon;".equals(desc)) {
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "createDragPlayerIcon",
                                        "(La/bz;ILbest/F;)Ljavax/swing/ImageIcon;", false);
                                    return;
                                }
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }

                            @Override
                            public void visitIntInsn(int opcode, int operand) {
                                if (opcode == Opcodes.BIPUSH) {
                                    if (afterGetX && operand == 25) {
                                        afterGetX = false;
                                        super.visitIntInsn(Opcodes.BIPUSH, 75);
                                        return;
                                    }
                                    if (afterGetY && operand == -25) {
                                        afterGetY = false;
                                        super.visitIntInsn(Opcodes.BIPUSH, -24);
                                        return;
                                    }
                                    if (afterGetY && operand == 25) {
                                        afterGetY = false;
                                        super.visitIntInsn(Opcodes.BIPUSH, 24);
                                        return;
                                    }
                                    if (operand == 50) {
                                        fiftyCount++;
                                        if (fiftyCount % 2 == 1) {
                                            super.visitIntInsn(Opcodes.SIPUSH, 150);
                                        } else {
                                            super.visitIntInsn(Opcodes.SIPUSH, 102);
                                        }
                                        return;
                                    }
                                }
                                super.visitIntInsn(opcode, operand);
                            }
                        };
                    }

                    return new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            if (opcode == Opcodes.INVOKESTATIC && "best/ah".equals(owner) && "a".equals(name) && "(Ljava/util/ArrayList;IZZ)Lbest/F;".equals(desc)) {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/PositionPenaltyHelper", "selectBestPlayerForSlot", desc, false);
                                return;
                            }
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    };
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("a/bz.class", b);
            System.out.println("PATCHED a/bz.class written: " + b.length + " bytes (Painel expandido + Juniores livres)");
        }

        // -------------------------------------------------------------
        // 4. PATCH a/cD.class (Substituições Livres durante a partida)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/cD.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if ("<init>".equals(name)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/SubstitutionManager", "onPauseOpen",
                                        "(Ljava/lang/Object;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    if ("H".equals(name) && "(II)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if ((opcode == Opcodes.INVOKESPECIAL || opcode == Opcodes.INVOKEVIRTUAL)
                                        && "a/cD".equals(owner) && "K".equals(name) && "(II)V".equals(desc)) {
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/SubstitutionManager", "handleSwap",
                                        "(Ljava/lang/Object;II)V", false);
                                    return;
                                }
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }
                        };
                    }

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

                    if ("paintComponent".equals(name) && "(Ljava/awt/Graphics;)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                mv.visitVarInsn(Opcodes.ALOAD, 0);
                                mv.visitVarInsn(Opcodes.ALOAD, 1);
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "paintModernInMatchBackground",
                                    "(Ljavax/swing/JPanel;Ljava/awt/Graphics;)V", false);
                                mv.visitInsn(Opcodes.RETURN);
                            }
                        };
                    }

                    if ("cS".equals(name) && "(I)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "onInMatchTeamRefreshed",
                                        "(Ljava/lang/Object;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("a/cD.class", b);
            System.out.println("PATCHED a/cD.class written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 5. PATCH components/ao.class (Minichips PES/FIFA + Badges de Funções)
        // -------------------------------------------------------------
        {
            File compDir = new File("C:/Brasfoot22-23/projeto/build_mod/components");
            compDir.mkdirs();

            InputStream is = zip.getInputStream(zip.getEntry("components/ao.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    // Substitui renderização de texto antigo pelo layout 3 linhas compactas (h=26)
                    if ("a".equals(name) && "(Lbest/F;IILjava/lang/String;)Ljava/awt/Image;".equals(desc)) {
                        MethodVisitor customMv = cv.visitMethod(access, name, desc, signature, exceptions);
                        customMv.visitCode();
                        customMv.visitVarInsn(Opcodes.ALOAD, 0); // f2
                        customMv.visitVarInsn(Opcodes.ILOAD, 1); // i
                        customMv.visitVarInsn(Opcodes.ILOAD, 2); // i2
                        customMv.visitVarInsn(Opcodes.ALOAD, 3); // str
                        customMv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "renderPlayerShirtCard",
                            "(Lbest/F;IILjava/lang/String;)Ljava/awt/Image;", false);
                        customMv.visitInsn(Opcodes.ARETURN);
                        customMv.visitMaxs(4, 4);
                        customMv.visitEnd();
                        return null;
                    }

                    // Patch no método principal do campo/banco:
                    // 1. Desloca o card para cima: 47 -> 41 (elimina sobreposição no banco)
                    // 2. Desativa o drawImage legado da cruz de contusão que ficava colado na barra de energia
                    if ("a".equals(name) && "(Ljava/util/ArrayList;ILjavax/swing/ImageIcon;ZILbest/ah;I)Ljavax/swing/ImageIcon;".equals(desc)) {
                        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitIntInsn(int opcode, int operand) {
                                if (opcode == Opcodes.BIPUSH && operand == 47) {
                                    super.visitIntInsn(Opcodes.BIPUSH, 41);
                                    return;
                                }
                                super.visitIntInsn(opcode, operand);
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String mName, String mDesc, boolean itf) {
                                if ("best/F".equals(owner) && "fP".equals(mName) && "()Z".equals(mDesc)) {
                                    // Pula o desenho legado da cruz: o IFEQ subsequente saltará o drawImage legado,
                                    // sendo substituído pela renderização limpa e alinhada ao lado esquerdo do nome em EaFcUiHelper.
                                    super.visitMethodInsn(opcode, owner, mName, mDesc, itf);
                                    super.visitInsn(Opcodes.POP);
                                    super.visitInsn(Opcodes.ICONST_0);
                                    return;
                                }
                                super.visitMethodInsn(opcode, owner, mName, mDesc, itf);
                            }
                        };
                    }

                    // Patch na renderização do elenco disponível:
                    // 1. Ao arrastar um atleta, pula o desenho do card no fundo (salta direto para registro de hitbox)
                    // 2. Elimina todas as letras e números legados desenhados com drawString
                    if ("a".equals(name) && "(Ljava/util/ArrayList;ILjavax/swing/ImageIcon;)Ljavax/swing/ImageIcon;".equals(desc)) {
                        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            private Label skipToHitboxLabel = new Label();
                            private boolean replacedJump = false;

                            @Override
                            public void visitJumpInsn(int opcode, Label label) {
                                if (opcode == Opcodes.IF_ICMPEQ && !replacedJump) {
                                    replacedJump = true;
                                    super.visitJumpInsn(Opcodes.IF_ICMPEQ, skipToHitboxLabel);
                                    return;
                                }
                                super.visitJumpInsn(opcode, label);
                            }

                            @Override
                            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                                if (replacedJump && skipToHitboxLabel != null && opcode == Opcodes.GETSTATIC && "components/ao".equals(owner) && "RH".equals(name)) {
                                    super.visitLabel(skipToHitboxLabel);
                                    skipToHitboxLabel = null;
                                }
                                super.visitFieldInsn(opcode, owner, name, desc);
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if ("drawString".equals(name) && "(Ljava/lang/String;II)V".equals(desc)) {
                                    // Pula todos os drawString legados (letras de posição e números de camisa)
                                    super.visitInsn(Opcodes.POP2); // int y, int x
                                    super.visitInsn(Opcodes.POP2); // String text, Graphics2D g
                                    return;
                                }
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }
                        };
                    }

                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("components/ao.class", b);
            System.out.println("PATCHED components/ao.class (Minichips + Role Badges) written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 6. PATCH a/il.class (Modernização da Janela de Táticas Salvas)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("a/il.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                    if ("<init>".equals(name) && "(La/bz;Ljavax/swing/JDialog;Lbest/ah;)V".equals(desc)) {
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/EaFcUiHelper", "modernizeTacticsPanel",
                                        "(Ljavax/swing/JPanel;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }

                    return mv;
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("a/il.class", b);
            System.out.println("PATCHED a/il.class (Táticas Salvas EA FC UI) written: " + b.length + " bytes");
        }




        // -------------------------------------------------------------
        // 8. PATCH c/b.class (Penalidades de Posição & Lateralidade na Simulação do Jogo)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("c/b.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    // Renomeia o método original B para orig_B para manter sua StackMapTable 100% intacta
                    if ("B".equals(name) && "(Lbest/F;)D".equals(desc)) {
                        return super.visitMethod(access, "orig_B", desc, signature, exceptions);
                    }
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }

                @Override
                public void visitEnd() {
                    // Gera o método público wrapper B(best.F) com invocação limpa e sem conflitos de registradores
                    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "B", "(Lbest/F;)D", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                    mv.visitVarInsn(Opcodes.ALOAD, 1); // player
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "c/b", "orig_B", "(Lbest/F;)D", false); // double base
                    mv.visitVarInsn(Opcodes.ALOAD, 1); // player
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/PositionPenaltyHelper",
                        "applyMatchPenalty", "(DLbest/F;)D", false);
                    mv.visitInsn(Opcodes.DRETURN);
                    mv.visitMaxs(4, 2);
                    mv.visitEnd();

                    super.visitEnd();
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("c/b.class", b);
            System.out.println("PATCHED c/b.class (Position Penalty Match Engine Wrapper) written: " + b.length + " bytes");
        }

        // -------------------------------------------------------------
        // 9. PATCH best/ah.class (Seleção Inteligente de Atletas para CPU e Substituições)
        // -------------------------------------------------------------
        {
            InputStream is = zip.getInputStream(zip.getEntry("best/ah.class"));
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    // Renomeia o método original a(ArrayList, int, boolean, boolean) para orig_a
                    if ("a".equals(name) && "(Ljava/util/ArrayList;IZZ)Lbest/F;".equals(desc)) {
                        return super.visitMethod(access, "orig_a", desc, signature, exceptions);
                    }
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }

                @Override
                public void visitEnd() {
                    // Gera o método público estático a(ArrayList, int, boolean, boolean) delegando para PositionPenaltyHelper.selectBestPlayerForSlot
                    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "a", "(Ljava/util/ArrayList;IZZ)Lbest/F;", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(Opcodes.ALOAD, 0); // available
                    mv.visitVarInsn(Opcodes.ILOAD, 1); // slot
                    mv.visitVarInsn(Opcodes.ILOAD, 2); // isBench
                    mv.visitVarInsn(Opcodes.ILOAD, 3); // disallowKeeper
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mod/brasfoot/PositionPenaltyHelper",
                        "selectBestPlayerForSlot", "(Ljava/util/ArrayList;IZZ)Lbest/F;", false);
                    mv.visitInsn(Opcodes.ARETURN);
                    mv.visitMaxs(4, 4);
                    mv.visitEnd();

                    super.visitEnd();
                }
            }, 0);

            byte[] b = cw.toByteArray();
            saveClass("best/ah.class", b);
            System.out.println("PATCHED best/ah.class (Smart Lineup Selection for CPU & Match) written: " + b.length + " bytes");
        }

        zip.close();
        System.out.println("All patches injected successfully!");
    }

    private static void saveClass(String relativePath, byte[] b) throws IOException {
        File f1 = new File("C:/Brasfoot22-23/projeto/build_mod/" + relativePath);
        f1.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f1)) {
            fos.write(b);
        }
        File f2 = new File("C:/Users/Leandro/.gemini/antigravity/scratch/BRASFOOT-MOD/base_patches/" + relativePath);
        f2.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f2)) {
            fos.write(b);
        }
    }
}
