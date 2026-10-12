import java.io.*;
import java.util.*;
import java.util.zip.*;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;

public class FindCaller {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry e = entries.nextElement();
            if (e.getName().endsWith(".class")) {
                InputStream is = zip.getInputStream(e);
                ClassReader cr = new ClassReader(is);
                cr.accept(new ClassVisitor(Opcodes.ASM5) {
                    String className = e.getName();
                    @Override
                    public MethodVisitor visitMethod(int access, final String mName, final String mDesc, String sig, String[] ex) {
                        return new MethodVisitor(Opcodes.ASM5) {
                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if ("a".equals(name) && "(Lcomponents/v;Lbest/I;Lbest/A;I)V".equals(desc)) {
                                    System.out.println("CALLER: " + className + " -> " + mName + mDesc + " calls " + owner + "." + name + desc);
                                }
                            }
                        };
                    }
                }, 0);
                is.close();
            }
        }
        zip.close();
    }
}
