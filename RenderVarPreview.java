package mod.brasfoot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class RenderVarPreview {
    public static void main(String[] args) {
        try {
            // Render Step 0: Falta no lance
            JFrame f0 = new JFrame();
            f0.setUndecorated(true);
            VarDialog dlg0 = new VarDialog(null, VarDialog.EVENT_GOL, VarDialog.RESULT_GOL_LEGAL, VarDialog.MOTIVO_FALTA, "Corinthians", "Lucas Piton", 19, null, null);
            f0.setContentPane(dlg0);
            f0.pack();
            f0.setSize(750, 390);
            f0.addNotify();
            f0.validate();

            BufferedImage img0 = new BufferedImage(750, 390, BufferedImage.TYPE_INT_RGB);
            Graphics2D g0 = img0.createGraphics();
            dlg0.printAll(g0);
            g0.dispose();
            ImageIO.write(img0, "png", new File("C:/Brasfoot22-23/projeto/var_step0_preview.png"));
            f0.dispose();
            System.out.println("Step 0 preview generated successfully!");

            // Render Step 1: Falta no lance -> Gol Confirmado (Legal por falta)
            JFrame f1 = new JFrame();
            f1.setUndecorated(true);
            VarDialog dlg1 = new VarDialog(null, VarDialog.EVENT_GOL, VarDialog.RESULT_GOL_LEGAL, VarDialog.MOTIVO_FALTA, "Corinthians", "Lucas Piton", 19, null, null);
            java.lang.reflect.Method m = VarDialog.class.getDeclaredMethod("setStep1");
            m.setAccessible(true);
            m.invoke(dlg1);
            f1.setContentPane(dlg1);
            f1.pack();
            f1.setSize(750, 390);
            f1.addNotify();
            f1.validate();

            BufferedImage img1 = new BufferedImage(750, 390, BufferedImage.TYPE_INT_RGB);
            Graphics2D g1 = img1.createGraphics();
            dlg1.printAll(g1);
            g1.dispose();
            ImageIO.write(img1, "png", new File("C:/Brasfoot22-23/projeto/var_step1_preview.png"));
            f1.dispose();
            System.out.println("Step 1 preview generated successfully!");

            // Render Step 1 (Impedimento Anulado)
            JFrame f2 = new JFrame();
            f2.setUndecorated(true);
            VarDialog dlg2 = new VarDialog(null, VarDialog.EVENT_GOL, VarDialog.RESULT_GOL_ANULADO_IMP, VarDialog.MOTIVO_IMPEDIMENTO, "Palmeiras", "Rony", 34, null, null);
            m.invoke(dlg2);
            f2.setContentPane(dlg2);
            f2.pack();
            f2.setSize(750, 390);
            f2.addNotify();
            f2.validate();

            BufferedImage img2 = new BufferedImage(750, 390, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img2.createGraphics();
            dlg2.printAll(g2);
            g2.dispose();
            ImageIO.write(img2, "png", new File("C:/Brasfoot22-23/projeto/var_step1_imp_anulado.png"));
            f2.dispose();
            System.out.println("Step 1 Imp Anulado preview generated successfully!");

            // Render Step 0 e Step 1: Gesto Obsceno
            JFrame f3 = new JFrame();
            f3.setUndecorated(true);
            VarDialog dlg3 = new VarDialog(null, VarDialog.EVENT_VERMELHO, VarDialog.RESULT_VERMELHO_MANTIDO,
                VarDialog.SUB_CONDUTA_OBSCENA, "Flamengo", "Gabigol", 74, null, null);
            f3.setContentPane(dlg3);
            f3.pack();
            f3.setSize(750, 390);
            f3.addNotify();
            f3.validate();

            BufferedImage img3 = new BufferedImage(750, 390, BufferedImage.TYPE_INT_RGB);
            Graphics2D g3 = img3.createGraphics();
            dlg3.printAll(g3);
            g3.dispose();
            ImageIO.write(img3, "png", new File("C:/Brasfoot22-23/projeto/var_step0_obsceno_preview.png"));
            f3.dispose();

            JFrame f4 = new JFrame();
            f4.setUndecorated(true);
            VarDialog dlg4 = new VarDialog(null, VarDialog.EVENT_VERMELHO, VarDialog.RESULT_VERMELHO_MANTIDO,
                VarDialog.SUB_CONDUTA_OBSCENA, "Flamengo", "Gabigol", 74, null, null);
            m.invoke(dlg4);
            f4.setContentPane(dlg4);
            f4.pack();
            f4.setSize(750, 390);
            f4.addNotify();
            f4.validate();

            BufferedImage img4 = new BufferedImage(750, 390, BufferedImage.TYPE_INT_RGB);
            Graphics2D g4 = img4.createGraphics();
            dlg4.printAll(g4);
            g4.dispose();
            ImageIO.write(img4, "png", new File("C:/Brasfoot22-23/projeto/var_step1_obsceno_preview.png"));
            f4.dispose();
            System.out.println("Obscene gesture previews generated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
