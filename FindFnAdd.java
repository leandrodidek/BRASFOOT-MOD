package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FindFnAdd {
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
                            public void visitMethodInsn(int op, String owner, String mName, String mDesc, boolean itf) {
                                if ("java/util/ArrayList".equals(owner) && "add".equals(mName)) {
                                    // check if called on fN
                                }
                            }
                            @Override
                            public void visitFieldInsn(int op, String owner, String fName, String fDesc) {
                                if ("best/I".equals(owner) && "fN".equals(fName)) {
                                    System.out.println("fN in " + className + "." + name);
                                }
                            }
                        };
                    }
                }, 0);
            }
        }
    }
}
