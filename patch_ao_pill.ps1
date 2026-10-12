$aoFile = "C:\Brasfoot22-23\projeto\src_full\sources\components\ao.java"
$content = [System.IO.File]::ReadAllText($aoFile)

# 1. Constantes de layout do banco e disponiveis (1024x748 nativo perfeito)
$oldConst = "    private static int RD = 630;
    private static int RE = 250;
    private static int RF = 790;
    private static int RG = 460;"

$newConst = "    private static int RD = 435;
    private static int RE = best.aq.aer;
    private static int RF = 580;
    private static int RG = 410;"

# 2. RJ e RK calibrados milimetricamente
$oldRJ = "    private static int[][] RJ = {new int[]{-1, -1}, new int[]{275, 620}, new int[]{50, 470}, new int[]{170, 520}, new int[]{275, 520}, new int[]{380, 520}, new int[]{170, 430}, new int[]{275, 430}, new int[]{380, 430}, new int[]{500, 470}, new int[]{50, 290}, new int[]{170, 330}, new int[]{275, 330}, new int[]{380, 330}, new int[]{170, 220}, new int[]{275, 220}, new int[]{380, 220}, new int[]{500, 290}, new int[]{50, 90}, new int[]{170, 120}, new int[]{275, 120}, new int[]{380, 120}, new int[]{170, 40}, new int[]{275, 35}, new int[]{380, 40}, new int[]{500, 90}, new int[]{650, 48}, new int[]{775, 48}, new int[]{900, 48}, new int[]{1025, 48}, new int[]{1150, 48}, new int[]{1275, 48}, new int[]{710, 128}, new int[]{835, 128}, new int[]{960, 128}, new int[]{1085, 128}, new int[]{1210, 128}};"

$newRJ = "    private static int[][] RJ = {new int[]{-1, -1}, new int[]{190, 565}, new int[]{35, 425}, new int[]{100, 485}, new int[]{190, 485}, new int[]{285, 485}, new int[]{100, 405}, new int[]{190, 405}, new int[]{285, 405}, new int[]{345, 425}, new int[]{35, 265}, new int[]{100, 315}, new int[]{190, 315}, new int[]{285, 315}, new int[]{100, 205}, new int[]{190, 205}, new int[]{285, 205}, new int[]{345, 265}, new int[]{35, 55}, new int[]{100, Opcodes.DREM}, new int[]{190, Opcodes.DREM}, new int[]{285, Opcodes.DREM}, new int[]{100, 35}, new int[]{190, 30}, new int[]{285, 35}, new int[]{345, 55}, new int[]{470, 30}, new int[]{558, 30}, new int[]{655, 30}, new int[]{755, 30}, new int[]{855, 30}, new int[]{950, 30}, new int[]{470, 105}, new int[]{558, 105}, new int[]{655, 105}, new int[]{755, 105}, new int[]{855, 105}};"

$oldRK = "    private static int[][] RK = {new int[]{-1, -1}, new int[]{275, 620}, new int[]{50, 470}, new int[]{170, 520}, new int[]{275, 520}, new int[]{380, 520}, new int[]{170, 430}, new int[]{275, 430}, new int[]{380, 430}, new int[]{500, 470}, new int[]{50, 290}, new int[]{170, 330}, new int[]{275, 330}, new int[]{380, 330}, new int[]{170, 220}, new int[]{275, 220}, new int[]{380, 220}, new int[]{500, 290}, new int[]{50, 90}, new int[]{170, 120}, new int[]{275, 120}, new int[]{380, 120}, new int[]{170, 40}, new int[]{275, 35}, new int[]{380, 40}, new int[]{500, 90}, new int[]{25, 40}, new int[]{135, 40}, new int[]{25, 150}, new int[]{135, 150}, new int[]{25, 260}, new int[]{135, 260}, new int[]{25, 370}, new int[]{135, 370}, new int[]{25, 480}, new int[]{135, 480}, new int[]{80, 580}};"

$newRK = "    private static int[][] RK = {new int[]{-1, -1}, new int[]{190, 565}, new int[]{35, 425}, new int[]{100, 485}, new int[]{190, 485}, new int[]{285, 485}, new int[]{100, 405}, new int[]{190, 405}, new int[]{285, 405}, new int[]{345, 425}, new int[]{35, 270}, new int[]{100, 315}, new int[]{190, 315}, new int[]{285, 315}, new int[]{100, 205}, new int[]{190, 205}, new int[]{285, 205}, new int[]{345, 270}, new int[]{35, 55}, new int[]{100, Opcodes.DREM}, new int[]{190, Opcodes.DREM}, new int[]{285, Opcodes.DREM}, new int[]{100, 35}, new int[]{190, 30}, new int[]{285, 35}, new int[]{345, 55}, new int[]{20, 25}, new int[]{Opcodes.DREM, 35}, new int[]{20, Opcodes.LUSHR}, new int[]{Opcodes.DREM, 135}, new int[]{20, best.aq.aer}, new int[]{Opcodes.DREM, best.aq.aeb}, new int[]{20, 305}, new int[]{Opcodes.DREM, 315}, new int[]{20, 400}, new int[]{Opcodes.DREM, 410}, new int[]{20, 495}};"

# 3. T() offset
$oldT = "            if (i3 <= 25) {
                i4 += 245;
                i5 += 10;
            }"

$newT = "            if (i3 <= 25) {
                i4 += 200;
                i5 += 10;
            }"

# 4. V() e W()
$oldV = "    public static boolean V(int i, int i2) {
        if (i < 0 || i > 620 || i2 < 0 || i2 > 720) {
            return i >= 630 && i <= 1430 && i2 >= 15 && i2 <= 200;
        }
        return true;
    }

    public static boolean W(int i, int i2) {
        if (i < 245 || i > 850 || i2 < 0 || i2 > 700) {
            return i >= 0 && i <= 245 && i2 >= 0 && i2 <= 700;
        }
        return true;
    }"

$newV = "    public static boolean V(int i, int i2) {
        if (i < 0 || i > 434 || i2 < 0 || i2 > 640) {
            return i >= 440 && i <= 1020 && i2 >= 20 && i2 <= 180;
        }
        return true;
    }

    public static boolean W(int i, int i2) {
        if (i < 200 || i > 640 || i2 < 0 || i2 > 650) {
            return i >= 10 && i <= 200 && i2 >= 20 && i2 <= 650;
        }
        return true;
    }"

# 5. Canvas sizes
$oldSizes = "        int i4 = 600;
        int i5 = 700;
        if (i3 == 1) {
            i4 = 790;
            i5 = 160;
        }
        if (i3 == 3) {
            i4 = 240;
            i5 = 700;
        }"

$newSizes = "        int i4 = 480;
        int i5 = 679;
        if (i3 == 1) {
            i4 = 580;
            i5 = 160;
        }
        if (i3 == 3) {
            i4 = 200;
            i5 = 650;
        }"

$oldOffset = "                if (i3 == 1) {
                    i9 = RJ[i8][0] - 630;
                    i10 = RJ[i8][1] - 48;
                }"

$newOffset = "                if (i3 == 1) {
                    i9 = RJ[i8][0] - 450;
                    i10 = RJ[i8][1] - 30;
                }"

# 6. Available players wrap (6 por linha no 1024)
$oldAvailWrap = "                i3 += 108;
                i2++;
                if (i2 == 7) {
                    i2 = 0;
                    i3 = 20;
                    i4 += 88;
                }"

$newAvailWrap = "                i3 += 100;
                i2++;
                if (i2 == 6) {
                    i2 = 0;
                    i3 = 20;
                    i4 += 88;
                }"

# 7. Posicionamento do card relativo a camisa em drawImage:
# i9 - 55, i10 + 34  ->  i9 - 33, i10 + 31 (card w=110, centro = 55)
$oldDrawCard1 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, `"`"), i9 - 55, i10 + 34, (ImageObserver) null);"
$newDrawCard1 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, `"`"), i9 - 33, i10 + 31, (ImageObserver) null);"

$oldDrawCard2 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 2, i5, `"`"), i3 - 53, i4 + 42, (ImageObserver) null);"
$newDrawCard2 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 2, i5, `"`"), i3 - 33, i4 + 31, (ImageObserver) null);"

$oldDrawCard3 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 3, i5, ((ao) arrayList.get(i5)).fg() != null ? ((ao) arrayList.get(i5)).fg().getNome() : `"`"), i6 - 50, i7 + 47, (ImageObserver) null);"
$newDrawCard3 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 3, i5, ((ao) arrayList.get(i5)).fg() != null ? ((ao) arrayList.get(i5)).fg().getNome() : `"`"), i6 - 33, i7 + 31, (ImageObserver) null);"

$content = $content.Replace($oldConst, $newConst)
$content = $content.Replace($oldRJ, $newRJ)
$content = $content.Replace($oldRK, $newRK)
$content = $content.Replace($oldT, $newT)
$content = $content.Replace($oldV, $newV)
$content = $content.Replace($oldSizes, $newSizes)
$content = $content.Replace($oldOffset, $newOffset)
$content = $content.Replace($oldAvailWrap, $newAvailWrap)
$content = $content.Replace($oldDrawCard1, $newDrawCard1)
$content = $content.Replace($oldDrawCard2, $newDrawCard2)
$content = $content.Replace($oldDrawCard3, $newDrawCard3)

[System.IO.File]::WriteAllText($aoFile, $content)
Write-Host "SUCCESS: ao.java restored and updated with EA FC pill cards!"
