package mod.brasfoot;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;

public class FindEventRefs {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        Enumeration<? extends ZipEntry> en = zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            if (e.getName().endsWith(".class")) {
                InputStream is = zip.getInputStream(e);
                ClassReader cr = new ClassReader(is);
                cr.accept(new ClassVisitor(Opcodes.ASM5) {
                    String className;
                    @Override
                    public void visit(int v, int a, String name, String s, String sup, String[] ifaces) {
                        this.className = name;
                    }
                    @Override
                    public MethodVisitor visitMethod(int a, String name, String desc, String sig, String[] ex) {
                        return new MethodVisitor(Opcodes.ASM5) {
                            @Override
                            public void visitTypeInsn(int op, String type) {
                                if (op == Opcodes.NEW && "best/A".equals(type)) {
                                    System.out.println("NEW best/A in " + className + "." + name + desc);
                                }
                            }
                            @Override
                            public void visitMethodInsn(int op, String owner, String mName, String mDesc, boolean itf) {
                                if ("best/I".equals(owner) && mName.startsWith("h") && mDesc.contains("ArrayList")) {
                                    // lists in match engine
                                }
                            }
                        };
                    }
                }, 0);
            }
        }
    }
}
