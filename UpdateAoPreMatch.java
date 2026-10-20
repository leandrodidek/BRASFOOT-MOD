import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UpdateAoPreMatch {
    public static void main(String[] args) throws Exception {
        File origFile = new File("C:/Brasfoot22-23/projeto/src_decompiled/sources/components/ao.java");
        File targetFile = new File("C:/Brasfoot22-23/projeto/src_full/sources/components/ao.java");

        // 0. Restaura arquivo limpo da descompilação
        Files.copy(origFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String content = new String(Files.readAllBytes(targetFile.toPath()), "UTF-8");
        // Normaliza quebras de linha para \n
        content = content.replace("\r\n", "\n");

        // 1. Atualiza coordenadas de RK (banco de reservas em jogo: coluna única vertical centrada)
        String oldRkBench = "new int[]{20, 25}, new int[]{115, 35}, new int[]{20, 125}, new int[]{115, best.aq.adb}, new int[]{20, best.aq.aer}, new int[]{115, best.aq.aeb}, new int[]{20, 305}, new int[]{115, 315}, new int[]{20, 400}, new int[]{115, 410}, new int[]{20, 495}";
        String newRkBench = "new int[]{6, 25}, new int[]{6, 77}, new int[]{6, 129}, new int[]{6, 181}, new int[]{6, 233}, new int[]{6, 285}, new int[]{6, 337}, new int[]{6, 389}, new int[]{6, 441}, new int[]{6, 493}, new int[]{6, 545}";
        if (content.contains(oldRkBench)) {
            content = content.replace(oldRkBench, newRkBench);
            System.out.println("[UpdateAo] 1. Replaced RK bench slots with vertical single-column coordinates.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find oldRkBench!");
        }

        // 2. Aumenta tamanho da camisa no campo e adiciona interpolação bilinear
        String oldDrawJersey = "graphics2DCreateGraphics.drawImage(image, i9, i10, (ImageObserver) null);";
        String newDrawJersey = "graphics2DCreateGraphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);\n"
            + "                graphics2DCreateGraphics.drawImage(image, i9 - 4, i10 - 4, 48, 48, (ImageObserver) null);";
        if (content.contains(oldDrawJersey)) {
            content = content.replace(oldDrawJersey, newDrawJersey);
            System.out.println("[UpdateAo] 2. Replaced jersey draw call.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find oldDrawJersey!");
        }

        // 3. Alinha o card do jogador no campo centralizado sob a camisa
        String oldDrawCard = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, \"\"), i9 - 52, i10 + 47, (ImageObserver) null);";
        String newDrawCard = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, \"\"), i9 - 55, i10 + 34, (ImageObserver) null);";
        if (content.contains(oldDrawCard)) {
            content = content.replace(oldDrawCard, newDrawCard);
            System.out.println("[UpdateAo] 3. Replaced player card draw call.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find oldDrawCard!");
        }

        // 4. Remove ícones legados de capitão, faltas, escanteios, falso 9 e cruz de contusão
        int idxInjury = content.indexOf("if (((ao) arrayList.get(i8)).x().fP()) {");
        int idxRoleEnd = content.indexOf("int i12 = 22;");
        if (idxInjury != -1 && idxRoleEnd != -1 && idxInjury < idxRoleEnd) {
            content = content.substring(0, idxInjury) 
                + "// Legacy role icons & injury cross removed (rendered by renderPlayerShirtCard)\n                    "
                + content.substring(idxRoleEnd);
            System.out.println("[UpdateAo] 4. Removed legacy role & injury icons.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find legacy role icons block!");
        }

        // 5. Substitui método a(best.F f2, ...) para o layout EA FC no campo
        int idxCardA = content.indexOf("public static Image a(best.F f2");
        int idxCardB = content.indexOf("public static void a(Graphics2D graphics2D");
        if (idxCardA != -1 && idxCardB != -1) {
            String newCardMethod = "public static Image a(best.F f2, int i, int i2, String str) {\n"
                + "        if (f2 == null) return null;\n"
                + "        return mod.brasfoot.EaFcUiHelper.renderPlayerShirtCard(f2, i, i2, str);\n"
                + "    }\n\n    ";
            content = content.substring(0, idxCardA) + newCardMethod + content.substring(idxCardB);
            System.out.println("[UpdateAo] 5. Replaced a(best.F, ...) with renderPlayerShirtCard.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find a(best.F, ...) method bounds!");
        }

        // 6. Atualiza método S(int i, int i2) com auto-magnetismo no campo e banco pré-jogo
        int idxSStart = content.indexOf("public static int S(int i, int i2) {");
        int idxSEnd = content.indexOf("public static int T(int i, int i2) {");
        if (idxSStart != -1 && idxSEnd != -1) {
            String newS = "public static int S(int i, int i2) {\n"
                + "        for (int i3 = 1; i3 <= 25; i3++) {\n"
                + "            if (i >= RJ[i3][0] - 25 && i <= RJ[i3][0] + 65 && i2 >= RJ[i3][1] - 15 && i2 <= RJ[i3][1] + 65) {\n"
                + "                return i3;\n"
                + "            }\n"
                + "        }\n"
                + "        int benchY = 30;\n"
                + "        int rowStep = 27;\n"
                + "        int benchSlots = mod.brasfoot.EaFcUiHelper.getActiveBenchSlots();\n"
                + "        if (i >= 435 && i <= 1025 && i2 >= benchY && i2 < benchY + benchSlots * rowStep) {\n"
                + "            int r = (i2 - benchY) / rowStep;\n"
                + "            if (r >= 0 && r < benchSlots) {\n"
                + "                return 26 + r;\n"
                + "            }\n"
                + "        }\n"
                + "        return -1;\n"
                + "    }\n\n    ";
            content = content.substring(0, idxSStart) + newS + content.substring(idxSEnd);
            System.out.println("[UpdateAo] 6. Updated method S(int, int) with auto magnetism.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find method S bounds!");
        }

        // 7. Atualiza método T(int i, int i2) com auto-magnetismo no campo e banco em jogo
        int idxTStart = content.indexOf("public static int T(int i, int i2) {");
        int idxTEnd = content.indexOf("public static int U(int i, int i2) {");
        if (idxTStart != -1 && idxTEnd != -1) {
            String newT = "public static int T(int i, int i2) {\n"
                + "        for (int i3 = 1; i3 <= 25; i3++) {\n"
                + "            int i4 = RK[i3][0] + 200;\n"
                + "            int i5 = RK[i3][1] + 10;\n"
                + "            if (i >= i4 - 25 && i <= i4 + 65 && i2 >= i5 - 15 && i2 <= i5 + 65) {\n"
                + "                return i3;\n"
                + "            }\n"
                + "        }\n"
                + "        for (int i3 = 26; i3 <= 36; i3++) {\n"
                + "            int bx = RK[i3][0];\n"
                + "            int by = RK[i3][1];\n"
                + "            if (i >= bx - 4 && i <= bx + 192 && i2 >= by - 2 && i2 <= by + 50) {\n"
                + "                return i3;\n"
                + "            }\n"
                + "        }\n"
                + "        return -1;\n"
                + "    }\n\n    ";
            content = content.substring(0, idxTStart) + newT + content.substring(idxTEnd);
            System.out.println("[UpdateAo] 7. Updated method T(int, int) with auto magnetism.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find method T bounds!");
        }

        // 8. Atualiza U, V, W e X
        int idxUStart = content.indexOf("public static int U(int i, int i2) {");
        int idxAStart = content.indexOf("public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon, boolean z");
        if (idxUStart != -1 && idxAStart != -1) {
            String newUVX = "public static int U(int i, int i2) {\n"
                + "        int availY = mod.brasfoot.EaFcUiHelper.getAvailListY();\n"
                + "        int rowStep = 27;\n"
                + "        int scroll = mod.brasfoot.EaFcUiHelper.getAvailScrollIndex();\n"
                + "        if (i >= 435 && i <= 1025 && i2 >= availY && i2 < availY + 380) {\n"
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
                + "            return i >= 435 && i <= 1025 && i2 >= 20 && i2 < availY;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n\n"
                + "    public static boolean W(int i, int i2) {\n"
                + "        if (i < 200 || i > 640 || i2 < 0 || i2 > 650) {\n"
                + "            return i >= 0 && i <= 200 && i2 >= 10 && i2 <= 650;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n\n"
                + "    public static boolean X(int i, int i2) {\n"
                + "        int availY = mod.brasfoot.EaFcUiHelper.getAvailY();\n"
                + "        return i >= 435 && i <= 1025 && i2 >= availY && i2 <= 630;\n"
                + "    }\n\n    ";
            content = content.substring(0, idxUStart) + newUVX + content.substring(idxAStart);
            System.out.println("[UpdateAo] 8. Updated methods U, V, W, X.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find method U bounds!");
        }

        // 9. Atualiza vi() e vj() para que o label AB fique dinamicamente alinhado abaixo de availHeader
        int idxViStart = content.indexOf("public static int vi() {");
        int idxVjEnd = content.indexOf("public static ImageIcon i(ImageIcon imageIcon) {");
        if (idxViStart != -1 && idxVjEnd != -1) {
            String newViVj = "public static int vi() {\n"
                + "        return 440;\n"
                + "    }\n\n"
                + "    public static int vj() {\n"
                + "        return mod.brasfoot.EaFcUiHelper.getAvailListY();\n"
                + "    }\n\n    ";
            content = content.substring(0, idxViStart) + newViVj + content.substring(idxVjEnd);
            System.out.println("[UpdateAo] 9. Updated vi() and vj() to dynamic EA FC bounds.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find vi() and vj() method bounds!");
        }

        // 10. Atualiza RD e RE
        content = content.replace("private static int RD = 435;\n    private static int RE = best.aq.aer;",
                                  "private static int RD = 440;\n    private static int RE = 250;");

        // 11. Ajusta tamanho da imagem de retorno do banco pré-jogo (i3 == 1)
        String oldBenchDim = "if (i3 == 1) {\n            i4 = 580;\n            i5 = 160;\n        }";
        String newBenchDim = "if (i3 == 1) {\n            i4 = 580;\n            i5 = 320;\n        }";
        if (content.contains(oldBenchDim)) {
            content = content.replace(oldBenchDim, newBenchDim);
            System.out.println("[UpdateAo] 11. Adjusted pre-match bench image height to 320.");
        } else {
            String oldBenchDim2 = "if (i3 == 1) {\n            i4 = 480;\n            i5 = 153;\n        }";
            if (content.contains(oldBenchDim2)) {
                content = content.replace(oldBenchDim2, newBenchDim);
                System.out.println("[UpdateAo] 11. Adjusted pre-match bench image height (alt) to 320.");
            }
        }

        // 12. Injeta desenho das linhas do banco na tela de pré-jogo (i3 == 1) e jogo (i3 == 3)
        String targetLoop = "for (int i8 = i6; i8 <= i7; i8++) {\n            if (((ao) arrayList.get(i8)).vf() != null && i8 != i) {";
        String replacementLoop = targetLoop + "\n"
            + "                if (i3 == 3) {\n"
            + "                    if (((ao) arrayList.get(i8)).x() != null) {\n"
            + "                        mod.brasfoot.EaFcUiHelper.drawBenchPlayerRow(graphics2DCreateGraphics, ((ao) arrayList.get(i8)).x(), i8, RK[i8][0], RK[i8][1], 188, 46);\n"
            + "                    } else {\n"
            + "                        graphics2DCreateGraphics.setColor(new Color(18, 24, 38, 120));\n"
            + "                        graphics2DCreateGraphics.fillRoundRect(RK[i8][0], RK[i8][1], 188, 46, 6, 6);\n"
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
        if (content.contains(targetLoop)) {
            content = content.replace(targetLoop, replacementLoop);
            System.out.println("[UpdateAo] 12. Injected bench row renderer for i3 == 1 and i3 == 3!");
        } else {
            System.err.println("[UpdateAo] ERROR: targetLoop not found!");
        }

        // 13. Atualiza método a(ArrayList arrayList, int i, ImageIcon imageIcon) para desenhar o elenco fora do banco no padrão unificado
        int idxAvailStart = content.indexOf("public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon) {");
        int idxAvailEnd = content.indexOf("public static Image a(best.F f2");
        if (idxAvailStart != -1 && idxAvailEnd != -1) {
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
            System.out.println("[UpdateAo] 13. Replaced available squad method with modern scrollable list.");
        } else {
            System.err.println("[UpdateAo] WARN: Could not find available squad method bounds!");
        }

        Files.write(targetFile.toPath(), content.getBytes("UTF-8"));
        System.out.println("SUCCESS: ao.java restored and updated cleanly!");
    }
}
