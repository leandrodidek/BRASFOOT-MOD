import java.io.File;
import java.nio.file.Files;

public class ApplyBenchPatch {
    public static void main(String[] args) throws Exception {
        File aoFile = new File("C:/Brasfoot22-23/projeto/src_full/sources/components/ao.java");
        String content = new String(Files.readAllBytes(aoFile.toPath()), "UTF-8");

        // 1. Add RenderingHints import
        if (!content.contains("import java.awt.RenderingHints;")) {
            content = content.replace(
                "import java.awt.image.ImageObserver;",
                "import java.awt.image.ImageObserver;\nimport java.awt.RenderingHints;"
            );
        }

        // 2. Replace RK bench coordinates (slots 26..36)
        String oldBenchRK = "new int[]{20, 25}, new int[]{115, 35}, new int[]{20, 125}, new int[]{115, best.aq.adb}, new int[]{20, best.aq.aer}, new int[]{115, best.aq.aeb}, new int[]{20, 305}, new int[]{115, 315}, new int[]{20, 400}, new int[]{115, 410}, new int[]{20, 495}";
        String newBenchRK = "new int[]{6, 30}, new int[]{6, 82}, new int[]{6, 134}, new int[]{6, 186}, new int[]{6, 238}, new int[]{6, 290}, new int[]{6, 342}, new int[]{6, 394}, new int[]{6, 446}, new int[]{6, 498}, new int[]{6, 550}";
        if (content.contains(oldBenchRK)) {
            content = content.replace(oldBenchRK, newBenchRK);
            System.out.println("Step 2 (RK coordinates): SUCCESS");
        } else {
            System.out.println("Step 2 (RK coordinates): NOT FOUND");
        }

        // 3. Update hit testing in T()
        String oldT = "    public static int T(int i, int i2) {\n"
            + "        for (int i3 = 1; i3 <= 36; i3++) {\n"
            + "            int i4 = RK[i3][0];\n"
            + "            int i5 = RK[i3][1];\n"
            + "            if (i3 <= 25) {\n"
            + "                i4 += 200;\n"
            + "                i5 += 10;\n"
            + "            }\n"
            + "            if (i >= i4 && i <= i4 + 50 && i2 >= i5 && i2 <= i5 + 50) {\n"
            + "                return i3;\n"
            + "            }\n"
            + "        }\n"
            + "        return -1;\n"
            + "    }";

        String newT = "    public static int T(int i, int i2) {\n"
            + "        for (int i3 = 1; i3 <= 36; i3++) {\n"
            + "            int i4 = RK[i3][0];\n"
            + "            int i5 = RK[i3][1];\n"
            + "            if (i3 <= 25) {\n"
            + "                i4 += 200;\n"
            + "                i5 += 10;\n"
            + "                if (i >= i4 && i <= i4 + 50 && i2 >= i5 && i2 <= i5 + 50) {\n"
            + "                    return i3;\n"
            + "                }\n"
            + "            } else {\n"
            + "                if (i >= 0 && i <= 210 && i2 >= i5 && i2 <= i5 + 48) {\n"
            + "                    return i3;\n"
            + "                }\n"
            + "            }\n"
            + "        }\n"
            + "        return -1;\n"
            + "    }";

        if (content.contains(oldT)) {
            content = content.replace(oldT, newT);
            System.out.println("Step 3 (T hit-test): SUCCESS");
        } else {
            // Check with \r\n line breaks
            String oldTCrlf = oldT.replace("\n", "\r\n");
            String newTCrlf = newT.replace("\n", "\r\n");
            if (content.contains(oldTCrlf)) {
                content = content.replace(oldTCrlf, newTCrlf);
                System.out.println("Step 3 (T hit-test CRLF): SUCCESS");
            } else {
                System.out.println("Step 3 (T hit-test): NOT FOUND");
            }
        }

        // 4. Update method a(...) loop
        String oldLoopStart = "        BufferedImage bufferedImage = new BufferedImage(i4, i5, 2);\n"
            + "        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();\n"
            + "        int i6 = 1;\n"
            + "        int i7 = 25;\n"
            + "        if (i3 == 1 || i3 == 3) {\n"
            + "            i6 = 26;\n"
            + "            i7 = 36;\n"
            + "        }\n"
            + "        for (int i8 = i6; i8 <= i7; i8++) {\n"
            + "            if (((ao) arrayList.get(i8)).vf() != null && i8 != i) {";

        String newLoopStart = "        BufferedImage bufferedImage = new BufferedImage(i4, i5, 2);\n"
            + "        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();\n"
            + "        graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);\n"
            + "        graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);\n"
            + "        if (i3 == 3) {\n"
            + "            graphics2DCreateGraphics.setFont(new Font(\"Segoe UI\", Font.BOLD, 11));\n"
            + "            graphics2DCreateGraphics.setColor(new Color(148, 163, 184));\n"
            + "            graphics2DCreateGraphics.drawString(\"BANCO DE RESERVAS\", 10, 20);\n"
            + "        }\n"
            + "        int i6 = 1;\n"
            + "        int i7 = 25;\n"
            + "        if (i3 == 1 || i3 == 3) {\n"
            + "            i6 = 26;\n"
            + "            i7 = 36;\n"
            + "        }\n"
            + "        for (int i8 = i6; i8 <= i7; i8++) {\n"
            + "            if (((ao) arrayList.get(i8)).vf() != null && i8 != i) {\n"
            + "                if (i3 == 3) {\n"
            + "                    if (((ao) arrayList.get(i8)).x() != null) {\n"
            + "                        mod.brasfoot.EaFcUiHelper.drawBenchPlayerRow(graphics2DCreateGraphics, ((ao) arrayList.get(i8)).x(), i8, RK[i8][0], RK[i8][1], 188, 44);\n"
            + "                    } else {\n"
            + "                        graphics2DCreateGraphics.setColor(new Color(18, 24, 38, 120));\n"
            + "                        graphics2DCreateGraphics.fillRoundRect(RK[i8][0], RK[i8][1], 188, 44, 6, 6);\n"
            + "                    }\n"
            + "                    continue;\n"
            + "                }";

        if (content.contains(oldLoopStart)) {
            content = content.replace(oldLoopStart, newLoopStart);
            System.out.println("Step 4 (loop start): SUCCESS");
        } else {
            String oldLoopStartCrlf = oldLoopStart.replace("\n", "\r\n");
            String newLoopStartCrlf = newLoopStart.replace("\n", "\r\n");
            if (content.contains(oldLoopStartCrlf)) {
                content = content.replace(oldLoopStartCrlf, newLoopStartCrlf);
                System.out.println("Step 4 (loop start CRLF): SUCCESS");
            } else {
                System.out.println("Step 4 (loop start): NOT FOUND");
            }
        }

        Files.write(aoFile.toPath(), content.getBytes("UTF-8"));
        System.out.println("ALL PATCHES PROCESSED!");
    }
}
