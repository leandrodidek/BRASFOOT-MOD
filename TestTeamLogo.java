import java.io.*;
import java.util.*;
import javax.swing.ImageIcon;

public class TestTeamLogo {
    public static void main(String[] args) throws Exception {
        new best.M(true);
        File save = new File("C:/Brasfoot22-23/sav/teste var.s22");
        com.esotericsoftware.kryo.Kryo kryo = best.ak.gB();
        com.esotericsoftware.kryo.io.Input in = new com.esotericsoftware.kryo.io.Input(new FileInputStream(save));
        best.f game = (best.f) kryo.readObject(in, best.f.class);
        in.close();

        best.ah team = game.gA().get(0);
        System.out.println("Team name: " + team.getNome());
        ImageIcon icon = team.kP();
        System.out.println("team.kP(): " + icon);
        if (icon != null) {
            System.out.println("Icon size: " + icon.getIconWidth() + "x" + icon.getIconHeight());
        }
        ImageIcon iconX = team.x(40, 40);
        System.out.println("team.x(40,40): " + iconX);
    }
}
