import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UpdateAoPreMatch {
    public static void main(String[] args) throws Exception {
        File origFile = new File("C:/Brasfoot22-23/projeto/src_decompiled/sources/components/ao.java");
        File targetFile = new File("C:/Brasfoot22-23/projeto/src_full/sources/components/ao.java");

        // Restaura arquivo limpo
        Files.copy(origFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String content = new String(Files.readAllBytes(targetFile.toPath()), "UTF-8");

        // 1. Aumenta tamanho da camisa no campo
        content = content.replace(
            "graphics2DCreateGraphics.drawImage(image, i9, i10, (ImageObserver) null);",
            "graphics2DCreateGraphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);\n"
            + "                graphics2DCreateGraphics.drawImage(image, i9 - 4, i10 - 4, 48, 48, (ImageObserver) null);"
        );

        content = content.replace(
            "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, \"\"), i9 - 52, i10 + 47, (ImageObserver) null);",
            "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, \"\"), i9 - 55, i10 + 34, (ImageObserver) null);"
        );

        // 2. Substitui método a(best.F f2, ...) para o layout EA FC no campo
        int idxCardA = content.indexOf("public static Image a(best.F f2, int i, int i2, String str) {");
        int idxCardB = content.indexOf("public static void a(Graphics2D graphics2D, String str, int i, int i2, int i3, int i4) {");
        if (idxCardA != -1 && idxCardB != -1) {
            String newCardMethod = "public static Image a(best.F f2, int i, int i2, String str) {\n"
                + "        if (f2 == null) return null;\n"
                + "        return mod.brasfoot.EaFcUiHelper.renderPlayerShirtCard(f2, i, i2, str);\n"
                + "    }\n\n    ";
            content = content.substring(0, idxCardA) + newCardMethod + content.substring(idxCardB);
        }

        // 3. Atualiza método S(int i, int i2)
        int idxSStart = content.indexOf("public static int S(int i, int i2) {");
        int idxSEnd = content.indexOf("public static int T(int i, int i2) {");
        String newS = "public static int S(int i, int i2) {\n"
            + "        for (int i3 = 1; i3 <= 25; i3++) {\n"
            + "            if (i >= RJ[i3][0] && i <= RJ[i3][0] + 60 && i2 >= RJ[i3][1] && i2 <= RJ[i3][1] + 60) {\n"
            + "                return i3;\n"
            + "            }\n"
            + "        }\n"
            + "        int benchY = 30;\n"
            + "        int rowStep = 27;\n"
            + "        int benchSlots = mod.brasfoot.EaFcUiHelper.getActiveBenchSlots();\n"
            + "        if (i >= 440 && i <= 1018 && i2 >= benchY && i2 < benchY + benchSlots * rowStep) {\n"
            + "            int r = (i2 - benchY) / rowStep;\n"
            + "            if (r >= 0 && r < benchSlots) {\n"
            + "                return 26 + r;\n"
            + "            }\n"
            + "        }\n"
            + "        return -1;\n"
            + "    }\n\n    ";
        content = content.substring(0, idxSStart) + newS + content.substring(idxSEnd);

        // 4. Atualiza U, V e X
        int idxUStart = content.indexOf("public static int U(int i, int i2) {");
        int idxAStart = content.indexOf("public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon, boolean z");
        String newUVX = "public static int U(int i, int i2) {\n"
            + "        int availY = mod.brasfoot.EaFcUiHelper.getAvailListY();\n"
            + "        int rowStep = 27;\n"
            + "        int scroll = mod.brasfoot.EaFcUiHelper.getAvailScrollIndex();\n"
            + "        if (i >= 440 && i <= 1018 && i2 >= availY && i2 < availY + 380) {\n"
            + "            int visibleRow = (i2 - availY) / rowStep;\n"
            + "            int totalAvail = 0;\n"
            + "            if (RM != null) totalAvail = RM.length;\n"
            + "            int playerSlot = 37 + scroll + visibleRow;\n"
            + "            if (visibleRow >= 0 && (scroll + visibleRow) < totalAvail) {\n"
            + "                return playerSlot;\n"
            + "            }\n"
            + "        }\n"
            + "        return -1;\n"
            + "    }\n\n"
            + "    public static boolean V(int i, int i2) {\n"
            + "        if (i < 0 || i > 434 || i2 < 0 || i2 > 640) {\n"
            + "            int availY = mod.brasfoot.EaFcUiHelper.getAvailY();\n"
            + "            return i >= 440 && i <= 1018 && i2 >= 20 && i2 < availY;\n"
            + "        }\n"
            + "        return true;\n"
            + "    }\n\n"
            + "    public static boolean W(int i, int i2) {\n"
            + "        if (i < 200 || i > 640 || i2 < 0 || i2 > 650) {\n"
            + "            return i >= 10 && i <= 200 && i2 >= 20 && i2 <= 650;\n"
            + "        }\n"
            + "        return true;\n"
            + "    }\n\n"
            + "    public static boolean X(int i, int i2) {\n"
            + "        int availY = mod.brasfoot.EaFcUiHelper.getAvailY();\n"
            + "        return i >= 440 && i <= 1018 && i2 >= availY && i2 <= 625;\n"
            + "    }\n\n    ";
        content = content.substring(0, idxUStart) + newUVX + content.substring(idxAStart);

        // 5. Ajusta tamanho da imagem de retorno de banco
        content = content.replace("if (i3 == 1) {\n            i4 = 480;\n            i5 = 153;\n        }",
                                  "if (i3 == 1) {\n            i4 = 580;\n            i5 = 320;\n        }");
        content = content.replace("if (i3 == 1) {\n            i4 = 580;\n            i5 = 160;\n        }",
                                  "if (i3 == 1) {\n            i4 = 580;\n            i5 = 320;\n        }");

        // 6. Injeta desenho das linhas do banco na tela de pré-jogo (i3 == 1) e jogo (i3 == 3)
        String targetLoop = "for (int i8 = i6; i8 <= i7; i8++) {\n            if (((ao) arrayList.get(i8)).vf() != null && i8 != i) {";
        String replacementLoop = targetLoop + "\n"
            + "                if (i3 == 3) {\n"
            + "                    if (((ao) arrayList.get(i8)).x() != null) {\n"
            + "                        mod.brasfoot.EaFcUiHelper.drawBenchPlayerRow(graphics2DCreateGraphics, ((ao) arrayList.get(i8)).x(), i8, RK[i8][0], RK[i8][1], 188, 44);\n"
            + "                    } else {\n"
            + "                        graphics2DCreateGraphics.setColor(new Color(18, 24, 38, 120));\n"
            + "                        graphics2DCreateGraphics.fillRoundRect(RK[i8][0], RK[i8][1], 188, 44, 6, 6);\n"
            + "                    }\n"
            + "                    continue;\n"
            + "                }\n"
            + "                if (i3 == 1) {\n"
            + "                    int colW = 574;\n"
            + "                    int rowH = 25;\n"
            + "                    int rowStep = 27;\n"
            + "                    int row = i8 - 26;\n"
            + "                    int rx = 0;\n"
            + "                    int ry = row * rowStep;\n"
            + "                    if (((ao) arrayList.get(i8)).x() != null) {\n"
            + "                        mod.brasfoot.EaFcUiHelper.drawPreMatchBenchPlayerRow(graphics2DCreateGraphics, ((ao) arrayList.get(i8)).x(), i8, rx, ry, colW, rowH);\n"
            + "                    } else {\n"
            + "                        graphics2DCreateGraphics.setColor(new Color(18, 24, 38, 120));\n"
            + "                        graphics2DCreateGraphics.fillRoundRect(rx, ry, colW, rowH, 6, 6);\n"
            + "                    }\n"
            + "                    continue;\n"
            + "                }";
        content = content.replace(targetLoop, replacementLoop);

        // 7. Atualiza método a(ArrayList arrayList, int i, ImageIcon imageIcon) para desenhar o elenco fora do banco no padrão unificado
        int idxAvailStart = content.indexOf("public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon) {");
        int idxAvailEnd = content.indexOf("public static Image a(best.F f2, int i, int i2, String str) {");
        String newAvail = "public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon) {\n"
            + "        int w = 580;\n"
            + "        int h = 380;\n"
            + "        BufferedImage bufferedImage = new BufferedImage(w, h, 2);\n"
            + "        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();\n"
            + "        graphics2DCreateGraphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);\n"
            + "        graphics2DCreateGraphics.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);\n"
            + "        RH.clear();\n"
            + "        RI.clear();\n\n"
            + "        ArrayList availSlots = new ArrayList();\n"
            + "        for (int i5 = 37; i5 < arrayList.size(); i5++) {\n"
            + "            if (((ao) arrayList.get(i5)).vf() != null) {\n"
            + "                availSlots.add(Integer.valueOf(i5));\n"
            + "            }\n"
            + "        }\n\n"
            + "        int totalAvail = availSlots.size();\n"
            + "        int rowH = 25;\n"
            + "        int rowStep = 27;\n"
            + "        int visibleRows = h / rowStep;\n"
            + "        int maxScroll = Math.max(0, totalAvail - visibleRows);\n"
            + "        int scroll = Math.max(0, Math.min(maxScroll, mod.brasfoot.EaFcUiHelper.getAvailScrollIndex()));\n"
            + "        mod.brasfoot.EaFcUiHelper.setAvailMaxScroll(maxScroll);\n\n"
            + "        boolean hasScroll = (maxScroll > 0);\n"
            + "        int colW = hasScroll ? 562 : 574;\n\n"
            + "        for (int vi = 0; vi < visibleRows && (scroll + vi) < totalAvail; vi++) {\n"
            + "            int slotIdx = ((Integer) availSlots.get(scroll + vi)).intValue();\n"
            + "            int ry = vi * rowStep;\n"
            + "            if (slotIdx != i) {\n"
            + "                best.F player = ((ao) arrayList.get(slotIdx)).x();\n"
            + "                if (player != null) {\n"
            + "                    mod.brasfoot.EaFcUiHelper.drawPreMatchBenchPlayerRow(graphics2DCreateGraphics, player, slotIdx, 0, ry, colW, rowH);\n"
            + "                }\n"
            + "            }\n"
            + "            int absX = 440;\n"
            + "            int absY = mod.brasfoot.EaFcUiHelper.getAvailListY() + ry;\n"
            + "            RH.add(Integer.valueOf(absX));\n"
            + "            RI.add(Integer.valueOf(absY));\n"
            + "        }\n\n"
            + "        if (hasScroll) {\n"
            + "            int sbX = 568;\n"
            + "            int sbY = 2;\n"
            + "            int sbW = 6;\n"
            + "            int sbH = h - 4;\n"
            + "            graphics2DCreateGraphics.setColor(new Color(24, 33, 52, 160));\n"
            + "            graphics2DCreateGraphics.fillRoundRect(sbX, sbY, sbW, sbH, 3, 3);\n\n"
            + "            int thumbH = Math.max(20, (int) ((visibleRows / (float) totalAvail) * sbH));\n"
            + "            int thumbY = sbY + (int) ((scroll / (float) maxScroll) * (sbH - thumbH));\n"
            + "            graphics2DCreateGraphics.setColor(new Color(71, 85, 105, 220));\n"
            + "            graphics2DCreateGraphics.fillRoundRect(sbX, thumbY, sbW, thumbH, 3, 3);\n"
            + "        }\n\n"
            + "        int[][] iArr = new int[availSlots.size()][2];\n"
            + "        for (int i8 = 0; i8 < availSlots.size(); i8++) {\n"
            + "            iArr[i8][0] = 440;\n"
            + "            iArr[i8][1] = mod.brasfoot.EaFcUiHelper.getAvailListY() + (i8 - scroll) * rowStep;\n"
            + "        }\n"
            + "        RM = iArr;\n"
            + "        graphics2DCreateGraphics.dispose();\n"
            + "        return new ImageIcon(bufferedImage);\n"
            + "    }\n\n    ";
        content = content.substring(0, idxAvailStart) + newAvail + content.substring(idxAvailEnd);

        Files.write(targetFile.toPath(), content.getBytes("UTF-8"));
        System.out.println("SUCCESS: ao.java restored and updated cleanly!");
    }
}
