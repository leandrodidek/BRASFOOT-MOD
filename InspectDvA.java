package mod.brasfoot;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.util.Printer;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.util.Textifier;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.util.TraceMethodVisitor;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.zip.ZipFile;

public class InspectDvA {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        InputStream is = zip.getInputStream(zip.getEntry("a/dV.class"));
        ClassReader cr = new ClassReader(is);
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ("a".equals(name) && "(Lcomponents/v;Lbest/I;Lbest/A;I)V".equals(desc)) {
                    Printer p = new Textifier();
                    TraceMethodVisitor tmv = new TraceMethodVisitor(p);
                    return new MethodVisitor(Opcodes.ASM5, tmv) {
                        @Override
                        public void visitEnd() {
                            p.print(new PrintWriter(System.out));
                        }
                    };
                }
                return null;
            }
        }, 0);
    }
}
