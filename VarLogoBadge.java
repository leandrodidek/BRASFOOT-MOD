package mod.brasfoot;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class VarLogoBadge {
    private static ImageIcon cachedIcon = null;

    public static synchronized ImageIcon getVarLogoIcon() {
        if (cachedIcon != null) return cachedIcon;
        try {
            File imgFile = new File("img/var_logo.png");
            if (!imgFile.exists()) {
                File dir = imgFile.getParentFile();
                if (dir != null && !dir.exists()) dir.mkdirs();
                BufferedImage bi = renderLogoImage();
                ImageIO.write(bi, "png", imgFile);
            }
            if (imgFile.exists()) {
                cachedIcon = new ImageIcon(imgFile.getAbsolutePath());
                return cachedIcon;
            }
        } catch (Throwable ignored) {}
        
        // Fallback em mem?ria
        try {
            BufferedImage bi = renderLogoImage();
            cachedIcon = new ImageIcon(bi);
            return cachedIcon;
        } catch (Throwable t) {
            return null;
        }
    }

    public static BufferedImage renderLogoImage() {
        int width = 74;
        int height = 28;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fundo em gradiente suave broadcast EA FC
        RoundRectangle2D bg = new RoundRectangle2D.Float(1, 1, width - 2, height - 2, 8, 8);
        GradientPaint gp = new GradientPaint(0, 0, new Color(2, 132, 199), width, 0, new Color(14, 116, 144));
        g2.setPaint(gp);
        g2.fill(bg);

        // Borda sutil de neon ciano
        g2.setColor(new Color(56, 189, 248, 220));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(bg);

        // Monitor TV
        int monX = 7;
        int monY = 6;
        int monW = 20;
        int monH = 13;
        
        RoundRectangle2D monitor = new RoundRectangle2D.Float(monX, monY, monW, monH, 3, 3);
        g2.setColor(new Color(11, 19, 36));
        g2.fill(monitor);
        g2.setColor(new Color(56, 189, 248));
        g2.setStroke(new BasicStroke(1.1f));
        g2.draw(monitor);

        // Base da TV
        g2.drawLine(monX + monW/2, monY + monH, monX + monW/2, monY + monH + 2);
        g2.drawLine(monX + 5, monY + monH + 2, monX + monW - 5, monY + monH + 2);

        // Led REC vermelho
        g2.setColor(new Color(239, 68, 68));
        g2.fillOval(monX + 3, monY + 3, 3, 3);

        // Linhas de v?deo
        g2.setColor(new Color(56, 189, 248, 140));
        g2.drawLine(monX + 8, monY + 4, monX + monW - 3, monY + 4);
        g2.drawLine(monX + 8, monY + 7, monX + monW - 3, monY + 7);
        g2.drawLine(monX + 4, monY + 10, monX + monW - 3, monY + 10);

        // Texto VAR em bold Segoe UI
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString("VAR", 33, 20);

        g2.dispose();
        return img;
    }
}