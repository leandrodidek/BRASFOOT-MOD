package mod.brasfoot;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import java.io.File;
import java.io.FileInputStream;
import best.M;

public class TestLoadSave {
    public static void main(String[] args) {
        String savePath = "C:/Brasfoot22-23/sav/teste var.s22";
        System.out.println("Testing save loading on: " + savePath);
        File f = new File(savePath);
        System.out.println("Save exists: " + f.exists() + ", size: " + f.length());

        new M(true);
        System.out.println("M(true) initialized.");

        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);

        try {
            Input input = new Input(new FileInputStream(f));
            System.out.println("Reading object 1 (SR)...");
            Object obj1 = kryo.readClassAndObject(input);
            System.out.println("Object 1 read successfully: " + obj1.getClass().getName());

            System.out.println("Reading object 2 (afQ)...");
            Object obj2 = kryo.readClassAndObject(input);
            System.out.println("Object 2 read successfully: " + obj2.getClass().getName());
            input.close();
            System.out.println("SUCCESS! Save loaded without any errors!");
        } catch (Throwable t) {
            System.out.println("FAILED WITH ERROR: " + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace(System.out);
        }
    }
}
