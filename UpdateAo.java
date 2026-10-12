import java.io.File;
import java.nio.file.Files;

public class UpdateAo {
    public static void main(String[] args) throws Exception {
        File file = new File("C:/Brasfoot22-23/projeto/src_full/sources/components/ao.java");
        String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");

        // 1. Aumenta o tamanho da camisa e ajusta o offset de desenho da barra
        content = content.replace(
            "graphics2DCreateGraphics.drawImage(image, i9, i10, (ImageObserver) null);",
            "graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);\n"
            + "                graphics2DCreateGraphics.drawImage(image, i9 - 4, i10 - 4, 48, 48, (ImageObserver) null);"
        );

        content = content.replace(
            "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, \"\"), i9 - 52, i10 + 47, (ImageObserver) null);",
            "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, \"\"), i9 - 55, i10 + 34, (ImageObserver) null);"
        );

        // 2. Substitui o método a(best.F f2, ...) pelo layout EA FC com sombra e alinhamento perfeito
        int idx1 = content.indexOf("public static Image a(best.F f2");
        int idx2 = content.indexOf("public static void a(Graphics2D graphics2D");

        if (idx1 != -1 && idx2 != -1) {
            String newMethod = "public static Image a(best.F f2, int i, int i2, String str) {\n"
                + "        if (f2 == null) {\n"
                + "            return null;\n"
                + "        }\n"
                + "        int w = 150;\n"
                + "        int h = 34;\n"
                + "        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);\n"
                + "        java.awt.Graphics2D g = bufferedImage.createGraphics();\n"
                + "        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);\n"
                + "        g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);\n\n"
                + "        int forca = f2.fi();\n"
                + "        int energia = f2.fp();\n"
                + "        if (energia < 0) energia = 0;\n"
                + "        if (energia > 100) energia = 100;\n\n"
                + "        String nome = f2.getNome();\n"
                + "        if (nome == null) nome = \"\";\n"
                + "        if (nome.length() > 12) {\n"
                + "            nome = nome.substring(0, 11) + \".\";\n"
                + "        }\n\n"
                + "        java.awt.Font font = new java.awt.Font(\"Arial\", java.awt.Font.BOLD, 11);\n"
                + "        g.setFont(font);\n"
                + "        java.awt.FontMetrics fm = g.getFontMetrics(font);\n\n"
                + "        // Linha 1: Forca + Barra de Energia sobreposta na camisa\n"
                + "        String forcaStr = String.valueOf(forca);\n"
                + "        int forcaW = fm.stringWidth(forcaStr);\n"
                + "        int barW = 46;\n"
                + "        int gap = 5;\n"
                + "        int row1W = forcaW + gap + barW;\n"
                + "        int startX = 75 - (row1W / 2);\n\n"
                + "        // Forca com sombra escura\n"
                + "        g.setColor(new java.awt.Color(0, 0, 0, 240));\n"
                + "        g.drawString(forcaStr, startX + 1, 13);\n"
                + "        g.setColor(java.awt.Color.WHITE);\n"
                + "        g.drawString(forcaStr, startX, 12);\n\n"
                + "        // Barra de energia\n"
                + "        int barX = startX + forcaW + gap;\n"
                + "        int barY = 5;\n"
                + "        int barH = 5;\n\n"
                + "        g.setColor(new java.awt.Color(30, 41, 59, 235));\n"
                + "        g.fillRoundRect(barX, barY, barW, barH, 2, 2);\n"
                + "        g.setColor(new java.awt.Color(0, 0, 0, 160));\n"
                + "        g.drawRoundRect(barX, barY, barW, barH, 2, 2);\n\n"
                + "        java.awt.Color staminaColor;\n"
                + "        if (energia >= 70) {\n"
                + "            staminaColor = new java.awt.Color(34, 197, 94); // Verde\n"
                + "        } else if (energia >= 45) {\n"
                + "            staminaColor = new java.awt.Color(234, 179, 8); // Amarelo\n"
                + "        } else {\n"
                + "            staminaColor = new java.awt.Color(239, 68, 68); // Vermelho\n"
                + "        }\n\n"
                + "        int fillW = (int) Math.round((energia / 100.0) * barW);\n"
                + "        if (fillW > barW) fillW = barW;\n"
                + "        if (fillW > 0) {\n"
                + "            g.setColor(staminaColor);\n"
                + "            g.fillRoundRect(barX, barY, fillW, barH, 2, 2);\n"
                + "        }\n\n"
                + "        // Linha 2: Nome do jogador centralizado abaixo da camisa\n"
                + "        int nameW = fm.stringWidth(nome);\n"
                + "        int nameX = 75 - (nameW / 2);\n"
                + "        int nameY = 25;\n\n"
                + "        g.setColor(new java.awt.Color(0, 0, 0, 240));\n"
                + "        g.drawString(nome, nameX + 1, nameY + 1);\n"
                + "        g.setColor(java.awt.Color.WHITE);\n"
                + "        g.drawString(nome, nameX, nameY);\n\n"
                + "        g.dispose();\n"
                + "        return bufferedImage;\n"
                + "    }\n\n    ";

            String result = content.substring(0, idx1) + newMethod + content.substring(idx2);
            Files.write(file.toPath(), result.getBytes("UTF-8"));
            System.out.println("SUCCESSFULLY APPLIED EA FC LAYOUT TO ao.java!");
        } else {
            System.out.println("Could not find method indices!");
        }
    }
}
