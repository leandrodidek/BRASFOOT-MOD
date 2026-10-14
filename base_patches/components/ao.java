package components;

import c.a;
import java.awt.RenderingHints;

import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.Opcodes;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;

/* JADX INFO: loaded from: bf22-23_extracted.jar:components/ao.class */
public class ao {
    private best.F U = null;
    private ImageIcon RC = null;
    private best.ah cg = null;
    private ImageIcon AH = new ImageIcon(getClass().getResource("/aicons/camisav.png"));
    private ImageIcon AJ = new ImageIcon(getClass().getResource("/aicons/camisat.png"));
    private ImageIcon AI = new ImageIcon(getClass().getResource("/aicons/camisag.png"));
    private static int RD = 435;
    private static int RE = best.aq.aer;
    private static int RF = 580;
    private static int RG = 410;
    private static ArrayList RH = new ArrayList();
    private static ArrayList RI = new ArrayList();
    private static int[][] RJ = {new int[]{-1, -1}, new int[]{190, 565}, new int[]{35, 425}, new int[]{100, 485}, new int[]{190, 485}, new int[]{285, 485}, new int[]{100, 405}, new int[]{190, 405}, new int[]{285, 405}, new int[]{345, 425}, new int[]{35, 265}, new int[]{100, 315}, new int[]{190, 315}, new int[]{285, 315}, new int[]{100, 205}, new int[]{190, 205}, new int[]{285, 205}, new int[]{345, 265}, new int[]{35, 55}, new int[]{100, Opcodes.DREM}, new int[]{190, Opcodes.DREM}, new int[]{285, Opcodes.DREM}, new int[]{100, 35}, new int[]{190, 30}, new int[]{285, 35}, new int[]{345, 55}, new int[]{470, 30}, new int[]{558, 30}, new int[]{655, 30}, new int[]{755, 30}, new int[]{855, 30}, new int[]{950, 30}, new int[]{470, 105}, new int[]{558, 105}, new int[]{655, 105}, new int[]{755, 105}, new int[]{855, 105}};
    private static int[][] RK = {new int[]{-1, -1}, new int[]{190, 565}, new int[]{35, 425}, new int[]{100, 485}, new int[]{190, 485}, new int[]{285, 485}, new int[]{100, 405}, new int[]{190, 405}, new int[]{285, 405}, new int[]{345, 425}, new int[]{35, 270}, new int[]{100, 315}, new int[]{190, 315}, new int[]{285, 315}, new int[]{100, 205}, new int[]{190, 205}, new int[]{285, 205}, new int[]{345, 270}, new int[]{35, 55}, new int[]{100, Opcodes.DREM}, new int[]{190, Opcodes.DREM}, new int[]{285, Opcodes.DREM}, new int[]{100, 35}, new int[]{190, 30}, new int[]{285, 35}, new int[]{345, 55}, new int[]{20, 25}, new int[]{Opcodes.DREM, 35}, new int[]{20, Opcodes.LUSHR}, new int[]{Opcodes.DREM, 135}, new int[]{20, best.aq.aer}, new int[]{Opcodes.DREM, best.aq.aeb}, new int[]{20, 305}, new int[]{Opcodes.DREM, 315}, new int[]{20, 400}, new int[]{Opcodes.DREM, 410}, new int[]{20, 495}};
    private static int[][] RL = {new int[]{-1, -1}, new int[]{200, 489}, new int[]{76, 376}, new int[]{135, 422}, new int[]{202, 421}, new int[]{273, 421}, new int[]{131, 356}, new int[]{202, 356}, new int[]{277, 356}, new int[]{328, 377}, new int[]{76, 243}, new int[]{131, 276}, new int[]{Opcodes.IFNONNULL, 276}, new int[]{275, 276}, new int[]{128, 188}, new int[]{201, 188}, new int[]{276, 188}, new int[]{323, 247}, new int[]{68, 62}, new int[]{129, 107}, new int[]{best.aq.aem, 107}, new int[]{276, 107}, new int[]{Opcodes.LAND, 37}, new int[]{200, 37}, new int[]{280, 37}, new int[]{331, 64}, new int[]{10, 480}, new int[]{10, 400}, new int[]{10, 320}, new int[]{10, 240}, new int[]{10, 160}, new int[]{10, 84}, new int[]{10, 1}};
    private static int[][] RM;

    public best.F x() {
        return this.U;
    }

    public void a(best.F f2) {
        this.U = f2;
    }

    public ImageIcon vf() {
        return this.RC;
    }

    public void h(ImageIcon imageIcon) {
        this.RC = imageIcon;
    }

    public static int S(int i, int i2) {
        for (int i3 = 1; i3 <= 36; i3++) {
            if (i >= RJ[i3][0] && i <= RJ[i3][0] + 60 && i2 >= RJ[i3][1] && i2 <= RJ[i3][1] + 60) {
                return i3;
            }
        }
        return -1;
    }

    public static int T(int i, int i2) {
        for (int i3 = 1; i3 <= 36; i3++) {
            int i4 = RK[i3][0];
            int i5 = RK[i3][1];
            if (i3 <= 25) {
                i4 += 200;
                i5 += 10;
            }
            if (i >= i4 && i <= i4 + 50 && i2 >= i5 && i2 <= i5 + 50) {
                return i3;
            }
        }
        return -1;
    }

    public static int U(int i, int i2) {
        for (int i3 = 0; i3 < RH.size(); i3++) {
            if (i >= ((Integer) RH.get(i3)).intValue() - 20 && i <= ((Integer) RH.get(i3)).intValue() + 70 && i2 >= ((Integer) RI.get(i3)).intValue() && i2 <= ((Integer) RI.get(i3)).intValue() + 80) {
                return i3 + 37;
            }
        }
        return -1;
    }

    public static boolean V(int i, int i2) {
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
    }

    public static boolean X(int i, int i2) {
        return i >= RD && i <= RD + RF && i2 >= 10 && i2 <= RE + RG;
    }

    public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon, boolean z, int i2, best.ah ahVar, int i3) {
        Image image = this.AH.getImage();
        Image image2 = imageIcon.getImage();
        Image image3 = new ImageIcon(getClass().getResource("/aeicons/ect.png")).getImage();
        Image image4 = new ImageIcon(getClass().getResource("/aicons/kicker.png")).getImage();
        Image image5 = new ImageIcon(getClass().getResource("/aicons/captain.png")).getImage();
        Image image6 = new ImageIcon(getClass().getResource("/aicons/escanteiosicone.png")).getImage();
        Image image7 = new ImageIcon(getClass().getResource("/aicons/falsonove.png")).getImage();
        int i4 = 480;
        int i5 = 679;
        if (i3 == 1) {
            i4 = 580;
            i5 = 160;
        }
        if (i3 == 3) {
            i4 = 200;
            i5 = 650;
        }
        if (z) {
            i4 = 390;
            i5 = 566;
        }
        BufferedImage bufferedImage = new BufferedImage(i4, i5, 2);
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        int i6 = 1;
        int i7 = 25;
        if (i3 == 1 || i3 == 3) {
            i6 = 26;
            i7 = 36;
        }
        for (int i8 = i6; i8 <= i7; i8++) {
            if (((ao) arrayList.get(i8)).vf() != null && i8 != i) {
                if (i8 == 1) {
                    image = this.AI.getImage();
                } else if (i8 <= 25) {
                    image = ((ao) arrayList.get(i8)).x() != null ? image2 : this.AH.getImage();
                } else if (i8 > 25) {
                    image = ((ao) arrayList.get(i8)).x() != null ? ((ao) arrayList.get(i8)).x().getPosicao() == 0 ? this.AI.getImage() : image2 : this.AH.getImage();
                }
                int i9 = RJ[i8][0];
                int i10 = RJ[i8][1];
                if (i3 == 1) {
                    i9 = RJ[i8][0] - 450;
                    i10 = RJ[i8][1] - 30;
                }
                if (i3 == 2) {
                    i9 = RJ[i8][0];
                    i10 = RJ[i8][1];
                }
                if (i3 == 3) {
                    i9 = RK[i8][0];
                    i10 = RK[i8][1];
                }
                if (z) {
                    i9 = RK[i8][0];
                    i10 = RK[i8][1];
                }
                graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2DCreateGraphics.drawImage(image, i9 - 6, i10 - 6, 52, 52, (ImageObserver) null);
                if (((ao) arrayList.get(i8)).x() != null) {
                    graphics2DCreateGraphics.setFont(new Font("Arial", 1, 11));
                    graphics2DCreateGraphics.setFont(graphics2DCreateGraphics.getFont().deriveFont(11.0f));
                    graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, ""), i9 - 33, i10 + 24, (ImageObserver) null);
                    if (((ao) arrayList.get(i8)).x().fP()) {
                        graphics2DCreateGraphics.drawImage(image3, i9 - 15, i10 + 30, (ImageObserver) null);
                    }
                    // Legacy shirt icons removed - using EA FC bottom badges
                    int i12 = 22;
                    int i13 = 20;
                    if (((ao) arrayList.get(i8)).x().fn() > 0) {
                        if (((ao) arrayList.get(i8)).x().fn() > 9) {
                            i12 = 19;
                            i13 = 20;
                        }
                        graphics2DCreateGraphics.setColor(Color.WHITE);
                        graphics2DCreateGraphics.drawString(Integer.toString(((ao) arrayList.get(i8)).x().fn()), i9 + i12, i10 + i13);
                    }
                }
            }
        }
        graphics2DCreateGraphics.dispose();
        return new ImageIcon(bufferedImage);
    }

    public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon) {
        this.AH.getImage();
        Image image = imageIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(RF, RG, 2);
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        RH.clear();
        RI.clear();
        int i2 = 0;
        int i3 = 20;
        int i4 = 0;
        for (int i5 = 37; i5 < arrayList.size(); i5++) {
            if (((ao) arrayList.get(i5)).vf() != null) {
                Image image2 = (((ao) arrayList.get(i5)).x() == null || ((ao) arrayList.get(i5)).x().getPosicao() != 0) ? image : this.AI.getImage();
                if (i5 != i) {
                    graphics2DCreateGraphics.drawImage(image2, i3, i4, 45, 45, (ImageObserver) null);
                }
                if (((ao) arrayList.get(i5)).x() != null) {
                    graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 2, i5, ""), i3 - 33, i4 + 24, (ImageObserver) null);
                    graphics2DCreateGraphics.setFont(new Font("Arial", 1, 11));
                    graphics2DCreateGraphics.setFont(graphics2DCreateGraphics.getFont().deriveFont(11.0f));
                    graphics2DCreateGraphics.setColor(Color.BLACK);
                    graphics2DCreateGraphics.drawString(best.aq.rI[((ao) arrayList.get(i5)).x().getPosicao()], i3, i4 + 39);
                    graphics2DCreateGraphics.setColor(Color.WHITE);
                    graphics2DCreateGraphics.drawString(best.aq.rI[((ao) arrayList.get(i5)).x().getPosicao()], i3, i4 + 38);
                    int i6 = 20;
                    int i7 = 20;
                    if (((ao) arrayList.get(i5)).x().fn() > 0) {
                        if (((ao) arrayList.get(i5)).x().fn() > 9) {
                            i6 = 17;
                            i7 = 20;
                        }
                        graphics2DCreateGraphics.setColor(Color.WHITE);
                        graphics2DCreateGraphics.drawString(Integer.toString(((ao) arrayList.get(i5)).x().fn()), i3 + i6, i4 + i7);
                    }
                }
                RH.add(Integer.valueOf(i3 + RD));
                RI.add(Integer.valueOf(i4 + RE));
                i3 += 100;
                i2++;
                if (i2 == 6) {
                    i2 = 0;
                    i3 = 20;
                    i4 += 88;
                }
            }
        }
        int[][] iArr = new int[RH.size()][2];
        for (int i8 = 0; i8 < RH.size(); i8++) {
            iArr[i8][0] = ((Integer) RH.get(i8)).intValue();
            iArr[i8][1] = ((Integer) RI.get(i8)).intValue();
        }
        RM = iArr;
        graphics2DCreateGraphics.dispose();
        return new ImageIcon(bufferedImage);
    }

    public static Image a(best.F f2, int i, int i2, String str) {
        if (f2 == null) {
            return null;
        }
        int w = 110;
        int h = 36;
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int forca = f2.fi();
        int energia = f2.fp();
        if (energia < 0) energia = 0;
        if (energia > 100) energia = 100;

        String nome = f2.getNome();
        if (nome == null) nome = "";
        if (nome.length() > 12) {
            nome = nome.substring(0, 11) + ".";
        }

        java.awt.Font fontForca = new java.awt.Font("Arial", java.awt.Font.BOLD, 12);
        java.awt.Font fontNome = new java.awt.Font("Arial", java.awt.Font.BOLD, 10);

        // --- LINHA 1: OVR + BARRA DE ESTAMINA (Sobre a barra inferior da camisa) ---
        g.setFont(fontForca);
        java.awt.FontMetrics fmForca = g.getFontMetrics(fontForca);
        String forcaStr = String.valueOf(forca);
        int forcaW = fmForca.stringWidth(forcaStr);

        int barW = 38;
        int barH = 4;
        int gap = 4;
        int row1W = forcaW + gap + barW;
        int startX = (w / 2) - (row1W / 2);

        // OVR com sombra escura de alto contraste
        g.setColor(new java.awt.Color(0, 0, 0, 240));
        g.drawString(forcaStr, startX + 1, 12);
        g.setColor(java.awt.Color.WHITE);
        g.drawString(forcaStr, startX, 11);

        // Barra de Estamina fina na barra inferior da camisa
        int barX = startX + forcaW + gap;
        int barY = 5;

        g.setColor(new java.awt.Color(15, 23, 42, 230)); // Dark obsidian track
        g.fillRoundRect(barX, barY, barW, barH, 2, 2);
        g.setColor(new java.awt.Color(0, 0, 0, 160));
        g.drawRoundRect(barX, barY, barW, barH, 2, 2);

        java.awt.Color staminaColor;
        if (energia >= 70) {
            staminaColor = new java.awt.Color(34, 197, 94); // Verde Neon
        } else if (energia >= 45) {
            staminaColor = new java.awt.Color(234, 179, 8); // Amarelo
        } else {
            staminaColor = new java.awt.Color(239, 68, 68); // Vermelho
        }

        int fillW = (int) Math.round((energia / 100.0) * barW);
        if (fillW > barW) fillW = barW;
        if (fillW > 0) {
            g.setColor(staminaColor);
            g.fillRoundRect(barX, barY, fillW, barH, 2, 2);
        }

        // --- LINHA 2: NOME EM PILULA ESCURA EA FC 27 ---
        g.setFont(fontNome);
        java.awt.FontMetrics fmNome = g.getFontMetrics(fontNome);
        int nameW = fmNome.stringWidth(nome);
        int pillW = nameW + 12;
        int pillH = 15;
        int pillX = (w / 2) - (pillW / 2);
        int pillY = 16;

        // Capsula escura (Dark Glass) com borda suave
        g.setColor(new java.awt.Color(15, 23, 42, 225));
        g.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
        g.setColor(new java.awt.Color(51, 65, 85, 180));
        g.drawRoundRect(pillX, pillY, pillW, pillH, 8, 8);

        // Texto do nome centralizado na pilula
        g.setColor(java.awt.Color.WHITE);
        g.drawString(nome, pillX + 6, pillY + 11);

        g.dispose();
        return bufferedImage;
    }

    public static void a(Graphics2D graphics2D, String str, int i, int i2, int i3, boolean z, int i4) {
        Dimension dimension = new Dimension(i4, 40);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(str, ((dimension.width - i2) / 2) + 1, i3 + 1);
        if (z) {
            graphics2D.setColor(Color.YELLOW);
        } else {
            graphics2D.setColor(Color.WHITE);
        }
        graphics2D.drawString(str, (dimension.width - i2) / 2, i3);
    }

    public void vg() {
        this.U = null;
        this.cg = null;
        this.RC = null;
    }

    public void ej(int i) {
        this.U = null;
        if (i <= 36) {
            this.RC = this.AH;
        }
    }

    public void vh() {
        this.RC = this.AH;
    }

    public static void a(int[][] iArr) {
        RM = iArr;
    }

    public static int vi() {
        return RD;
    }

    public static int vj() {
        return RE;
    }

    public static ImageIcon i(ImageIcon imageIcon) {
        Image image = imageIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(40, 40, 2);
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        graphics2DCreateGraphics.drawImage(image, 0, 0, 40, 40, (ImageObserver) null);
        graphics2DCreateGraphics.setComposite(AlphaComposite.Src);
        return new ImageIcon(bufferedImage);
    }

    public ImageIcon a(ArrayList arrayList, int i, ImageIcon imageIcon, boolean z, int i2, best.F f2) {
        Image image = this.AH.getImage();
        Image image2 = imageIcon.getImage();
        Image image3 = new ImageIcon(getClass().getResource("/aicons/bolaouro_s2.png")).getImage();
        int i3 = 480;
        int i4 = 679;
        if (z) {
            i3 = 390;
            i4 = 566;
        }
        BufferedImage bufferedImage = new BufferedImage(i3, i4, 2);
        Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();
        for (int i5 = 1; i5 <= 25; i5++) {
            if (((ao) arrayList.get(i5)).vf() != null && i5 != i) {
                if (i5 == 1) {
                    image = this.AI.getImage();
                } else if (i5 <= 25) {
                    image = ((ao) arrayList.get(i5)).x() != null ? ((ao) arrayList.get(i5)).vf().getImage() : this.AH.getImage();
                } else if (i5 > 25) {
                    image = ((ao) arrayList.get(i5)).x() != null ? ((ao) arrayList.get(i5)).x().getPosicao() == 0 ? this.AI.getImage() : image2 : this.AH.getImage();
                }
                int i6 = RJ[i5][0];
                int i7 = RJ[i5][1];
                if (z) {
                    i6 = RL[i5][0];
                    i7 = RL[i5][1];
                }
                graphics2DCreateGraphics.drawImage(image, i6, i7, (ImageObserver) null);
                if (((ao) arrayList.get(i5)).x() != null) {
                    graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 3, i5, ((ao) arrayList.get(i5)).fg() != null ? ((ao) arrayList.get(i5)).fg().getNome() : ""), i6 - 33, i7 + 24, (ImageObserver) null);
                    if (((ao) arrayList.get(i5)).x() == f2) {
                        graphics2DCreateGraphics.drawImage(image3, i6 - 20, i7 + 20, (ImageObserver) null);
                    }
                }
            }
        }
        graphics2DCreateGraphics.dispose();
        return new ImageIcon(bufferedImage);
    }

    public void n(best.ah ahVar) {
        this.cg = ahVar;
    }

    public best.ah fg() {
        return this.cg;
    }
}
