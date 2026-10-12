import java.util.*;
import java.util.zip.*;

public class ListDvClasses {
    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile("C:/Brasfoot22-23/bf22-23_extracted.jar");
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            if (e.getName().contains("dV")) {
                System.out.println(e.getName());
            }
        }
        zip.close();
    }
}
