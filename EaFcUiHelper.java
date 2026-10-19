package mod.brasfoot;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Utilitário de Modernização e Design System da Interface de Escalação e Partida.
 * - Estilo EA Sports FC (Console HUD Moderno com Cantos Arredondados)
 * - Renderização de camisas: 3 linhas verticais perfeitamente centralizadas (Barra de Energia, OVR & POS, Nome)
 * - Altura ultra-compacta de 26px para garantir folga vertical de 9px no banco de reservas
 * - Badges com lado explícito para todas as posições (G, LD, LE, ZD, ZE, MD, ME, AD, AE)
 * - Ícones de função exclusivos na lateral esquerda (1º Capitão, 2º Pênalti, 3º Faltas) dinâmicos de cima para baixo
 * - Escudo oficial dos dois clubes no cabeçalho dos cartões de Radar
 * - Ícones vetoriais modernos nos botões inferiores (Táticas Salvas, Voltar, Jogar Partida sem '>>')
 * - Acesso livre e funcional à Base / Juniores (Sub-20) com recarregamento nativo instantâneo
 */
public class EaFcUiHelper {

    // Paleta Console Dark HUD EA FC
    public static final Color BG_DARK = new Color(11, 15, 25);
    public static final Color BG_DEEP = BG_DARK;
    public static final Color PANEL_DARK = new Color(18, 24, 38);
    public static final Color CARD_BG = new Color(24, 33, 52);
    public static final Color BORDER_COLOR = new Color(45, 60, 88);
    public static final Color BORDER_SLATE = BORDER_COLOR;
    public static final Color BORDER_ACTIVE = new Color(59, 130, 246);
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(180, 195, 215);
    public static final Color TEXT_BRIGHT = new Color(225, 235, 248);
    public static final Color EMERALD = new Color(16, 185, 129);
    public static final Color CYAN = new Color(14, 165, 233);
    public static final Color AMBER = new Color(245, 158, 11);

    public static void applyHighQualityRendering(Graphics2D g) {
        if (g == null) return;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static final Font FONT_TITLE = new Font("Verdana", Font.BOLD, 11);
    public static final Font FONT_BOLD = new Font("Verdana", Font.BOLD, 11);
    public static final Font FONT_REGULAR = new Font("Verdana", Font.PLAIN, 11);
    public static final Font FONT_LABEL = new Font("Verdana", Font.BOLD, 10);

    private static Image injuryIcon = null;

    public static Image getInjuryIcon() {
        if (injuryIcon == null) {
            try {
                java.net.URL url = EaFcUiHelper.class.getResource("/aeicons/ect.png");
                if (url == null) {
                    url = EaFcUiHelper.class.getResource("/aicons/icontusao.png");
                }
                if (url != null) {
                    injuryIcon = new javax.swing.ImageIcon(url).getImage();
                } else {
                    java.io.File f = new java.io.File("C:/Brasfoot22-23/aeicons/ect.png");
                    if (f.exists()) {
                        injuryIcon = javax.imageio.ImageIO.read(f);
                    }
                }
            } catch (Throwable ignored) {}
        }
        return injuryIcon;
    }

    // Mapeamento nativo dos slots do campo (0..25) para detecção resiliente de improvisação
    public static final int[] SLOT_EXPECTED_POS = {
        -1, // 0
         0, // 1: Goleiro (G)
         1, // 2: Lateral (L)
         2, // 3: Zagueiro (Z)
         2, // 4: Zagueiro (Z)
         2, // 5: Zagueiro (Z)
         2, // 6: Zagueiro (Z)
         2, // 7: Zagueiro (Z)
         2, // 8: Zagueiro (Z)
         1, // 9: Lateral (L)
         1, // 10: Lateral / Meia (L/M)
         3, // 11: Meia (M)
         3, // 12: Meia (M)
         3, // 13: Meia (M)
         3, // 14: Meia (M)
         3, // 15: Meia (M)
         3, // 16: Meia (M)
         1, // 17: Lateral / Meia (L/M)
         4, // 18: Atacante (A)
         4, // 19: Atacante (A)
         4, // 20: Atacante (A)
         4, // 21: Atacante (A)
         4, // 22: Atacante (A)
         4, // 23: Atacante (A)
         4, // 24: Atacante (A)
         4  // 25: Atacante (A)
    };

    // Instância única do painel de radar integrado
    private static TacticalRadarPanel embeddedRadarPanel = null;
    private static JPanel rootRef = null;
    private static JCheckBox junioresCheckRef = null;

    /**
     * Borda com cantos arredondados para painéis e containers do Swing.
     */
    public static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        private final int thickness;

        public RoundedBorder(int radius, Color color) {
            this(radius, color, 1);
        }

        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    /**
     * Renderiza uma linha do banco de reservas no formato de lista vertical moderna:
     * - Badge de posição (G, L, Z, M, A) com fundo colorido e borda acentuada
     * - Nome do atleta (com cruz vermelha se lesionado)
     * - Força (OVR) alinhada à direita em branco bold
     * - Barra de energia proporcional que começa no início da badge e vai até o fim da área do nome
     */
    public static void drawBenchPlayerRow(Graphics2D g, best.F f2, int slot, int x, int y, int w, int h) {
        if (f2 == null) return;

        // Container da linha com cantos arredondados e fundo sutil
        g.setColor(new Color(18, 24, 38, 160));
        g.fillRoundRect(x, y, w, h, 6, 6);
        g.setColor(new Color(45, 60, 88, 90));
        g.drawRoundRect(x, y, w, h, 6, 6);

        int forca = f2.fi();
        int energia = f2.fp();
        if (energia < 0) energia = 0;
        if (energia > 100) energia = 100;

        String posLabel = PositionPenaltyHelper.getDisplayLabel(f2, slot);

        Color chipBg;
        Color chipBorder;
        if (posLabel.startsWith("G")) {
            chipBg = new Color(217, 119, 6);       // Âmbar Goleiro
            chipBorder = new Color(245, 158, 11);
        } else if (posLabel.startsWith("L")) {
            chipBg = new Color(2, 132, 199);        // Ciano Lateral
            chipBorder = new Color(56, 189, 248);
        } else if (posLabel.startsWith("Z")) {
            chipBg = new Color(37, 99, 235);        // Azul Royal Zagueiro
            chipBorder = new Color(96, 165, 250);
        } else if (posLabel.startsWith("M") || posLabel.equals("VOL")) {
            chipBg = new Color(5, 150, 105);        // Esmeralda Meio-campo
            chipBorder = new Color(52, 211, 153);
        } else {
            chipBg = new Color(220, 38, 38);        // Vermelho Carmesim Atacante
            chipBorder = new Color(248, 113, 113);
        }

        // 1. Badge da Posição oficial
        int badgeX = x + 8;
        int badgeY = y + 7;
        int badgeW = 28;
        int badgeH = 17;

        g.setColor(chipBg);
        g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 4, 4);
        g.setColor(chipBorder);
        g.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 4, 4);

        Font fontBadge = new Font("Verdana", Font.BOLD, 9);
        g.setFont(fontBadge);
        g.setColor(Color.WHITE);
        FontMetrics fmB = g.getFontMetrics(fontBadge);
        int bw = fmB.stringWidth(posLabel);
        g.drawString(posLabel, badgeX + (badgeW - bw) / 2, badgeY + (badgeH - fmB.getHeight()) / 2 + fmB.getAscent());

        // 2. Força / OVR à direita
        Font fontOvr = new Font("Segoe UI", Font.BOLD, 14);
        g.setFont(fontOvr);
        FontMetrics fmO = g.getFontMetrics(fontOvr);
        String ovrStr = String.valueOf(forca);
        int ovrW = fmO.stringWidth(ovrStr);
        int ovrX = x + w - ovrW - 8;
        int ovrY = badgeY + (badgeH - fmO.getHeight()) / 2 + fmO.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(ovrStr, ovrX, ovrY);

        // 3. Nome do Jogador (com ícone de contusão se machucado)
        boolean isInjured = false;
        try { isInjured = f2.fP(); } catch (Throwable ignored) {}

        int nameX = badgeX + badgeW + 8;
        if (isInjured) {
            Image icon = getInjuryIcon();
            if (icon != null) {
                int iconSize = 13;
                int iconY = badgeY + (badgeH - iconSize) / 2;
                g.drawImage(icon, nameX, iconY, iconSize, iconSize, null);
                nameX += iconSize + 4;
            }
        }

        Font fontName = new Font("Segoe UI", Font.BOLD, 12);
        g.setFont(fontName);
        FontMetrics fmN = g.getFontMetrics(fontName);
        String nome = f2.getNome();
        if (nome == null) nome = "";
        String displayName = nome;
        int maxNameW = (ovrX - 8) - nameX;
        while (displayName.length() > 3 && fmN.stringWidth(displayName) > maxNameW) {
            displayName = displayName.substring(0, displayName.length() - 1);
        }
        if (displayName.length() < nome.length()) {
            displayName = displayName.substring(0, Math.max(1, displayName.length() - 1)) + ".";
        }

        int nameY = badgeY + (badgeH - fmN.getHeight()) / 2 + fmN.getAscent();
        g.setColor(new Color(248, 250, 252));
        g.drawString(displayName, nameX, nameY);

        // 4. Barra de Energia (começa no início da badge até o fim da área do nome)
        int barX = badgeX;
        int barEndX = ovrX - 8; // Fim da área do nome
        int barW = barEndX - barX;
        int barH = 3;
        int barY = badgeY + badgeH + 6;

        g.setColor(new Color(15, 23, 42, 220));
        g.fillRoundRect(barX, barY, barW, barH, 2, 2);

        Color staminaColor;
        if (energia >= 70) {
            staminaColor = new Color(34, 197, 94);   // Verde Elétrico (#22c55e)
        } else if (energia >= 45) {
            staminaColor = new Color(245, 158, 11);  // Âmbar (#f59e0b)
        } else {
            staminaColor = new Color(239, 68, 68);   // Vermelho Carmesim (#ef4444)
        }

        int fillW = (int) Math.round((energia / 100.0) * barW);
        if (fillW > barW) fillW = barW;
        if (fillW > 0) {
            g.setColor(staminaColor);
            g.fillRoundRect(barX, barY, fillW, barH, 2, 2);
        }
    }

    /**
     * Renderiza o cartão de jogador sob a camisa no campo, banco de reservas e elenco.
     * Estrutura visual premium inspirada no EA Sports FC (Console HUD):
     * 1. Barra de energia (stamina) arredondada com brilho (h=3px)
     * 2. Pill moderno com OVR & Posição + Lado (chip colorido e borda acentuada) (h=14px)
     * 3. Nome do jogador em card escuro arredondado com sombra para máxima nitidez (h=14px)
     * 4. Badges de funções táticas (Capitão, Faltas, Escanteios, Falso 9) centralizadas ABAIXO do nome (h=12px)
     */
    public static Image renderPlayerShirtCard(best.F f2, int i, int i2, String str) {
        if (f2 == null) return null;

        int w = 150;
        int h = 54;
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Centro horizontal rigorosamente alinhado com o centro das camisas desenhadas em components.ao (cx=77 no campo)
        int cx = (i == 2) ? 75 : 77;

        int forca = f2.fi(); // Força real do jogador (10-99)
        int energia = f2.fp(); // Energia/stamina (0-100)
        if (energia < 0) energia = 0;
        if (energia > 100) energia = 100;

        int pos = f2.getPosicao(); // Brasfoot: 0=G, 1=L, 2=Z, 3=M, 4=A
        int lado = f2.getLado();   // Brasfoot: 0=D, 1=E

        // Detecção de Jogador Fora de Posição (Improvisado no campo: slot 1..11)
        int posEsperada = -1;
        if (i2 >= 1 && i2 <= 11) {
            try {
                if (best.aq.sE != null && i2 < best.aq.sE.length && best.aq.sE[i2] != null) {
                    posEsperada = best.aq.sE[i2][0];
                }
            } catch (Throwable ignored) {}
            if (posEsperada < 0 && i2 < SLOT_EXPECTED_POS.length) {
                posEsperada = SLOT_EXPECTED_POS[i2];
            }
            if (i2 == 10 || i2 == 17) {
                posEsperada = 3; // Meia
            }
        }

        // Cálculo de penalidade de improvisação e lateralidade via PositionPenaltyHelper
        int penalty = (i2 >= 1 && i2 <= 25) ? PositionPenaltyHelper.calculatePenalty(f2, i2) : 0;
        boolean isImprovised = (penalty > 0);
        int displayForca = isImprovised ? Math.max(10, forca - penalty) : forca;
        String posLabel = PositionPenaltyHelper.getDisplayLabel(f2, i2);

        Color chipBg;
        Color chipBorder;
        if (posLabel.startsWith("G")) {
            chipBg = new Color(217, 119, 6);       // Âmbar Goleiro
            chipBorder = new Color(245, 158, 11);
        } else if (posLabel.startsWith("L")) {
            chipBg = new Color(2, 132, 199);        // Ciano Lateral
            chipBorder = new Color(56, 189, 248);
        } else if (posLabel.startsWith("Z")) {
            chipBg = new Color(37, 99, 235);        // Azul Royal Zagueiro
            chipBorder = new Color(96, 165, 250);
        } else if (posLabel.startsWith("M") || posLabel.equals("VOL")) {
            chipBg = new Color(5, 150, 105);        // Esmeralda Meio-campo
            chipBorder = new Color(52, 211, 153);
        } else {
            chipBg = new Color(220, 38, 38);        // Vermelho Carmesim Atacante
            chipBorder = new Color(248, 113, 113);
        }

        // Se improvisado: OVR e detalhes em amarelo vibrante (#facc15) de alerta
        Color ovrColor = isImprovised ? new Color(250, 204, 21) : Color.WHITE;
        Color pillBorderColor = isImprovised ? new Color(250, 204, 21, 230) : new Color(71, 85, 105, 220);
        Color chipBorderColor = isImprovised ? new Color(250, 204, 21) : chipBorder;

        String nome = f2.getNome();
        if (nome == null) nome = "";
        if (nome.length() > 12) {
            nome = nome.substring(0, 10) + ".";
        }

        // ==========================================================
        // LINHA 1: PILL MODERNO DE OVR & POSIÇÃO (VERDANA LEGÍVEL)
        // ==========================================================
        Font fontChip = new Font("Verdana", Font.BOLD, 9);
        Font fontForca = new Font("Verdana", Font.BOLD, 10);

        g.setFont(fontChip);
        FontMetrics fmChip = g.getFontMetrics(fontChip);
        int chipTxtW = fmChip.stringWidth(posLabel);
        int posBadgeW = Math.max(chipTxtW + 8, 22);

        g.setFont(fontForca);
        FontMetrics fmForca = g.getFontMetrics(fontForca);
        String forcaStr = String.valueOf(displayForca);
        int forcaW = fmForca.stringWidth(forcaStr);

        int gap = 5;      // Espaçamento nítido e equilibrado
        int rightPad = 7; // Padding à direita do número
        int pillW = posBadgeW + gap + forcaW + rightPad;
        int pillH = 14;
        int pillX = cx - (pillW / 2);
        int pillY = 1;

        // Fundo escuro do Pill com cantos arredondados e borda sutil
        g.setColor(new Color(15, 23, 42, 245));
        g.fillRoundRect(pillX, pillY, pillW, pillH, 4, 4);
        g.setColor(pillBorderColor);
        g.drawRoundRect(pillX, pillY, pillW, pillH, 4, 4);

        // Chip da Posição interno
        int chipX = pillX + 1;
        int chipY = pillY + 1;
        int chipH = pillH - 2;
        g.setColor(chipBg);
        g.fillRoundRect(chipX, chipY, posBadgeW, chipH, 3, 3);
        g.setColor(chipBorderColor);
        g.drawRoundRect(chipX, chipY, posBadgeW, chipH, 3, 3);

        g.setFont(fontChip);
        g.setColor(Color.WHITE);
        int chipTxtX = chipX + (posBadgeW - chipTxtW) / 2;
        int chipTxtY = chipY + (chipH - fmChip.getHeight()) / 2 + fmChip.getAscent();
        g.drawString(posLabel, chipTxtX, chipTxtY);

        // Número OVR com kerning limpo
        int ovrX = chipX + posBadgeW + gap;
        int ovrY = pillY + (pillH - fmForca.getHeight()) / 2 + fmForca.getAscent();
        g.setFont(fontForca);
        g.setColor(ovrColor);
        g.drawString(forcaStr, ovrX, ovrY);

        // ==========================================================
        // LINHA 2: BARRA DE ENERGIA ARREDONDADA COM BRILHO (ABAIXO DO CARD DE POSIÇÃO)
        // ==========================================================
        int barW = 48;
        int barH = 3;
        int barX = cx - (barW / 2);
        int barY = pillY + pillH + 2;

        g.setColor(new Color(15, 23, 42, 240));
        g.fillRoundRect(barX, barY, barW, barH, 2, 2);

        Color staminaColor;
        if (energia >= 70) {
            staminaColor = new Color(34, 197, 94);   // Verde Elétrico (#22c55e)
        } else if (energia >= 45) {
            staminaColor = new Color(245, 158, 11);  // Âmbar (#f59e0b)
        } else {
            staminaColor = new Color(239, 68, 68);   // Vermelho Carmesim (#ef4444)
        }

        int fillW = (int) Math.round((energia / 100.0) * barW);
        if (fillW > barW) fillW = barW;
        if (fillW > 0) {
            g.setColor(staminaColor);
            g.fillRoundRect(barX, barY, fillW, barH, 2, 2);
        }

        // ==========================================================
        // LINHA 3: CARD ARREDONDADO DO NOME (CENTRALIZADO COM SOMBRA)
        // ==========================================================
        Font fontNome = new Font("Verdana", Font.BOLD, 10);
        g.setFont(fontNome);
        FontMetrics fmNome = g.getFontMetrics(fontNome);
        int nameW = fmNome.stringWidth(nome);
        int cardNameW = Math.max(nameW + 12, 54);
        int cardNameH = 14;
        int cardNameX = cx - (cardNameW / 2);
        int cardNameY = barY + barH + 2;

        g.setColor(new Color(15, 23, 42, 245));
        g.fillRoundRect(cardNameX, cardNameY, cardNameW, cardNameH, 4, 4);
        g.setColor(new Color(71, 85, 105, 220));
        g.drawRoundRect(cardNameX, cardNameY, cardNameW, cardNameH, 4, 4);

        int textX = cardNameX + ((cardNameW - nameW) / 2);
        int textY = cardNameY + (cardNameH - fmNome.getHeight()) / 2 + fmNome.getAscent();
        // Sombra suave para contraste sobre o gramado
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(nome, textX + 1, textY + 1);
        g.setColor(new Color(248, 250, 252));
        g.drawString(nome, textX, textY);

        // Ícone de contusão (cruz vermelha) ao lado esquerdo do nome do jogador
        boolean isMachucado = false;
        try {
            isMachucado = f2.fP();
        } catch (Throwable ignored) {}

        if (isMachucado) {
            Image icon = getInjuryIcon();
            if (icon != null) {
                int iconSize = 13;
                int iconX = cardNameX - iconSize - 2;
                int iconY = cardNameY + (cardNameH - iconSize) / 2;
                g.drawImage(icon, iconX, iconY, iconSize, iconSize, null);
            }
        }

        // ==========================================================
        // LINHA 4: BADGES DE FUNÇÕES TÁTICAS ABAIXO DO NOME (CENTRALIZADAS)
        // Capitão (C), Faltas (F), Escanteios (E), Falso 9 (9)
        // ==========================================================
        best.ah team = null;
        try { team = f2.fg(); } catch (Throwable ignored) {}
        List<String> roles = new ArrayList<String>();
        if (team != null) {
            best.F cap = null;
            best.F fts = null;
            best.F esc = null;
            best.F f9 = null;
            try { cap = team.ke(); } catch (Throwable ignored) {}
            try { fts = team.kd(); } catch (Throwable ignored) {}
            try { esc = team.lq(); } catch (Throwable ignored) {}
            try { f9  = team.lr(); } catch (Throwable ignored) {}

            if (cap == null) {
                try {
                    Field f = team.getClass().getDeclaredField("nh");
                    f.setAccessible(true);
                    cap = (best.F) f.get(team);
                } catch (Throwable ignored) {}
            }
            if (fts == null) {
                try {
                    Field f = team.getClass().getDeclaredField("ni");
                    f.setAccessible(true);
                    fts = (best.F) f.get(team);
                } catch (Throwable ignored) {}
            }
            if (esc == null) {
                try {
                    Field f = team.getClass().getDeclaredField("nj");
                    f.setAccessible(true);
                    esc = (best.F) f.get(team);
                } catch (Throwable ignored) {}
            }
            if (f9 == null) {
                try {
                    Field f = team.getClass().getDeclaredField("nk");
                    f.setAccessible(true);
                    f9 = (best.F) f.get(team);
                } catch (Throwable ignored) {}
            }

            if (cap != null && cap == f2) roles.add("C");
            if (fts != null && fts == f2) roles.add("F");
            if (esc != null && esc == f2) roles.add("E");
            if (f9 != null && f9 == f2) roles.add("9");
        }

        if (!roles.isEmpty()) {
            int badgeW = 14;
            int badgeH = 12;
            int badgeGap = 3;
            int totalRolesW = roles.size() * badgeW + (roles.size() - 1) * badgeGap;
            int rx = cx - (totalRolesW / 2);
            int ry = cardNameY + cardNameH + 2;

            Font fontRole = new Font("Verdana", Font.BOLD, 9);
            g.setFont(fontRole);
            FontMetrics fmRole = g.getFontMetrics(fontRole);

            for (String r : roles) {
                Color rBg, rBorder;
                if ("C".equals(r)) {
                    rBg = new Color(217, 119, 6);       // Dourado Capitão
                    rBorder = new Color(245, 158, 11);
                } else if ("F".equals(r)) {
                    rBg = new Color(37, 99, 235);       // Azul Royal Faltas
                    rBorder = new Color(56, 189, 248);
                } else if ("E".equals(r)) {
                    rBg = new Color(124, 58, 237);      // Violeta Escanteios
                    rBorder = new Color(168, 85, 247);
                } else if ("9".equals(r)) {
                    rBg = new Color(225, 29, 72);       // Ruby Carmesim Falso 9
                    rBorder = new Color(244, 63, 94);
                } else {
                    rBg = new Color(5, 150, 105);
                    rBorder = new Color(52, 211, 153);
                }

                // Sombra do badge
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRoundRect(rx, ry + 1, badgeW, badgeH, 3, 3);

                // Fundo do badge
                g.setColor(rBg);
                g.fillRoundRect(rx, ry, badgeW, badgeH, 3, 3);

                // Borda do badge
                g.setColor(rBorder);
                g.drawRoundRect(rx, ry, badgeW, badgeH, 3, 3);

                // Letra do badge
                g.setColor(Color.WHITE);
                int rtw = fmRole.stringWidth(r);
                int rtx = rx + (badgeW - rtw) / 2;
                int rty = ry + (badgeH - fmRole.getHeight()) / 2 + fmRole.getAscent();
                g.drawString(r, rtx, rty);

                rx += badgeW + badgeGap;
            }
        }

        g.dispose();
        return bufferedImage;
    }

    /**
     * Atualiza a posição da unidade de arrasto (Drag & Drop) centralizando a camisa
     * e o card completo sob o ponteiro do mouse em tempo real.
     */
    public static void updateDragLocation(JLabel ac, MouseEvent e) {
        if (ac == null || e == null) return;
        ac.setLocation(e.getX() - 75, e.getY() - 24);
    }

    /**
     * Cancela qualquer operação de arrasto ativa caso o usuário dê Alt+Tab, troque de janela
     * ou solte o mouse fora do jogo, restaurando imediatamente o atleta à sua posição original.
     */
    public static void cancelDrag(final JPanel root, final JLayeredPane bp) {
        if (root == null) return;
        try {
            Field fAC = null;
            Field fAR = null;
            Field fAS = null;
            Method mCS = null;
            Method mCT = null;
            Method mX = null;

            try {
                fAC = root.getClass().getDeclaredField("AC");
                fAC.setAccessible(true);
            } catch (Throwable ignored) {}

            try {
                fAR = root.getClass().getDeclaredField("AR");
                fAR.setAccessible(true);
            } catch (Throwable ignored) {}

            try {
                fAS = root.getClass().getDeclaredField("AS");
                fAS.setAccessible(true);
            } catch (Throwable ignored) {}

            try {
                mCS = root.getClass().getDeclaredMethod("cS", int.class);
                mCS.setAccessible(true);
            } catch (Throwable ignored) {}

            try {
                mCT = root.getClass().getDeclaredMethod("cT", int.class);
                mCT.setAccessible(true);
            } catch (Throwable ignored) {}

            try {
                mX = root.getClass().getDeclaredMethod("X", boolean.class);
                mX.setAccessible(true);
            } catch (Throwable ignored) {}

            boolean isDragging = false;
            if (fAR != null && fAR.getBoolean(root)) isDragging = true;
            if (fAS != null && fAS.getBoolean(root)) isDragging = true;
            if (fAC != null && fAC.get(root) != null) isDragging = true;

            if (isDragging) {
                if (mX != null) {
                    try { mX.invoke(root, false); } catch (Throwable ignored) {}
                }
                if (fAC != null) {
                    JLabel ac = (JLabel) fAC.get(root);
                    if (ac != null) {
                        ac.setVisible(false);
                        if (bp != null) bp.remove(ac);
                        fAC.set(root, null);
                    }
                }
                if (fAR != null && fAR.getBoolean(root)) {
                    if (mCS != null) {
                        try { mCS.invoke(root, -1); } catch (Throwable ignored) {}
                    }
                    fAR.setBoolean(root, false);
                }
                if (fAS != null && fAS.getBoolean(root)) {
                    if (mCT != null) {
                        try { mCT.invoke(root, -1); } catch (Throwable ignored) {}
                    }
                    fAS.setBoolean(root, false);
                }
                root.revalidate();
                root.repaint();
                if (bp != null) {
                    bp.revalidate();
                    bp.repaint();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Badges de funções táticas agora são renderizadas diretamente sob o nome do atleta
     * dentro de renderPlayerShirtCard, mantendo perfeito alinhamento centralizado.
     */
    public static void drawPlayerRoles(Graphics2D g2, best.F player, best.ah team, int shirtX, int shirtY) {
        // No-op: As badges são desenhadas diretamente dentro de renderPlayerShirtCard centralizadas abaixo do nome.
    }

    /**
     * Constrói o ícone composto completo do jogador para arrastar e soltar (Drag & Drop),
     * incluindo a camisa (45x45), subcard e as badges de funções táticas abaixo do nome.
     */
    public static ImageIcon createDragPlayerIcon(a.bz bz, int slot, best.F player) {
        ImageIcon shirt = (bz != null) ? bz.a(slot, player) : null;
        return createDragPlayerIcon(shirt, player);
    }

    public static ImageIcon createDragPlayerIcon(ImageIcon shirtIcon, best.F player) {
        int w = 150;
        int h = 102;
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. Camisa (45x45) centralizada horizontalmente no card
        int shirtX = (w - 45) / 2; // 52
        int shirtY = 2;
        if (shirtIcon != null && shirtIcon.getImage() != null) {
            g.drawImage(shirtIcon.getImage(), shirtX, shirtY, 45, 45, null);
        }

        // 2. Subcard completo sob a camisa (já inclui stamina + OVR/POS + nome + badges centralizadas abaixo)
        if (player != null) {
            Image card = renderPlayerShirtCard(player, 0, 0, "");
            if (card != null) {
                g.drawImage(card, 0, 48, null);
            }
        }

        g.dispose();
        return new ImageIcon(bi);
    }

    private static void drawRoleBadge(Graphics2D g, FontMetrics fm, String text,
                                      int x, int y, int w, int h, Color bg, Color border) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y + 1, w, h, 4, 4);

        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 4, 4);

        g.setColor(border);
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(x, y, w, h, 4, 4);

        g.setColor(Color.WHITE);
        int tw = fm.stringWidth(text);
        int tx = x + (w - tw) / 2;
        int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, tx, ty);
    }

    /**
     * Aplica o layout moderno da interface EA FC na Tela de Escalação (a.bz).
     */
    public static void applyPreMatchLayout(
            final JPanel root,
            final JLayeredPane bp,
            JLabel pitchLabel,
            JPanel grassPanel,
            JPanel tacticsPanel,
            final JLabel benchHeader,
            final JLabel availHeader,
            final JCheckBox junioresCheck,
            JButton btnJogar,
            JButton btnVoltar,
            JButton btnTaticas,
            JComboBox cbAuto,
            JComboBox cbEstilo,
            JComboBox cbMarcacao,
            JComboBox cbAtaque,
            JLabel lblAuto,
            JLabel lblEstilo,
            JLabel lblMarcacao,
            JLabel lblAtaque
    ) {
        rootRef = root;
        junioresCheckRef = junioresCheck;

        try {
            // 1. Fundo geral do painel e janela com borda arredondada na moldura
            root.setBackground(BG_DARK);
            root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                new RoundedBorder(16, BORDER_COLOR, 1)
            ));

            bp.setBackground(BG_DARK);
            bp.setOpaque(true);

            // Se possível, aplica acabamento e cantos arredondados na janela hospedeira
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Window win = SwingUtilities.getWindowAncestor(root);
                    if (win != null) {
                        win.setBackground(BG_DARK);
                        if (win instanceof JDialog) {
                            JDialog dlg = (JDialog) win;
                            dlg.getContentPane().setBackground(BG_DARK);
                            try {
                                dlg.setSize(1580, 756);
                                dlg.setPreferredSize(new Dimension(1580, 756));
                                dlg.setLocationRelativeTo(null);
                            } catch (Throwable ignored) {}
                            try {
                                if (dlg.isUndecorated()) {
                                    dlg.setShape(new RoundRectangle2D.Float(0, 0, dlg.getWidth(), dlg.getHeight(), 20, 20));
                                }
                            } catch (Throwable ignored) {}
                        } else if (win instanceof JFrame) {
                            ((JFrame) win).getContentPane().setBackground(BG_DARK);
                        }

                        // Proteção contra Alt+Tab / Perda de Foco durante o arrasto de jogadores
                        win.addWindowFocusListener(new WindowAdapter() {
                            @Override
                            public void windowLostFocus(WindowEvent e) {
                                cancelDrag(root, bp);
                            }
                        });
                        win.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowDeactivated(WindowEvent e) {
                                cancelDrag(root, bp);
                            }
                        });
                    }
                }
            });

            // 2. Painel inferior do campo com cantos arredondados limpos
            grassPanel.setBackground(PANEL_DARK);
            grassPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));

            for (Component c : grassPanel.getComponents()) {
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    String text = lbl.getText();
                    lbl.setFont(FONT_REGULAR);
                    if (text != null && (text.contains("Advers") || text.contains("Condi"))) {
                        lbl.setForeground(TEXT_MUTED);
                        lbl.setFont(FONT_BOLD);
                    } else {
                        lbl.setForeground(TEXT_WHITE);
                    }
                }
            }

            // 3. Cabeçalho: Banco de Reservas
            benchHeader.setBackground(CARD_BG);
            benchHeader.setForeground(TEXT_WHITE);
            benchHeader.setFont(FONT_TITLE);
            benchHeader.setText("   BANCO DE RESERVAS  \u2022  7 SUPLENTES");
            benchHeader.setOpaque(true);
            benchHeader.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));

            // 4. Cabeçalho: Jogadores Disponíveis & Juniores
            availHeader.setBackground(CARD_BG);
            availHeader.setForeground(TEXT_WHITE);
            availHeader.setFont(FONT_TITLE);
            availHeader.setText("   ELENCO DISPONIVEL (PROFISSIONAL)");
            availHeader.setOpaque(true);
            availHeader.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));

            // Acesso aos Juniores: apenas em amistosos (conforme jogo base AX == true)
            boolean isFriendly = false;
            try {
                Field axField = root.getClass().getDeclaredField("AX");
                axField.setAccessible(true);
                isFriendly = axField.getBoolean(root);
            } catch (Throwable ignored) {}

            junioresCheck.setVisible(isFriendly);
            junioresCheck.setEnabled(isFriendly);
            junioresCheck.setBackground(CARD_BG);
            junioresCheck.setForeground(TEXT_BRIGHT);
            junioresCheck.setFont(FONT_BOLD);
            junioresCheck.setText("Ver Juniores (Base Sub-20)");
            junioresCheck.setOpaque(false);
            junioresCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
            junioresCheck.setFocusPainted(false);

            if (isFriendly) {
                // Acoplamento nativo com o método oe() de recarga de atletas em amistosos
                junioresCheck.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            boolean isJunior = junioresCheck.isSelected();
                            Field ayField = root.getClass().getDeclaredField("AY");
                            ayField.setAccessible(true);
                            ayField.setBoolean(root, isJunior);

                            Method oeMethod = root.getClass().getMethod("oe");
                            oeMethod.invoke(root);

                            if (isJunior) {
                                availHeader.setText("   CATEGORIAS DE BASE (SUB-20)");
                                availHeader.setForeground(AMBER);
                            } else {
                                availHeader.setText("   ELENCO DISPONIVEL (PROFISSIONAL)");
                                availHeader.setForeground(TEXT_WHITE);
                            }

                            root.revalidate();
                            root.repaint();
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            // 5. Painel Tático Inferior com cantos arredondados
            tacticsPanel.setBackground(PANEL_DARK);
            tacticsPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            lblAuto.setFont(FONT_LABEL);
            lblAuto.setForeground(TEXT_MUTED);
            lblAuto.setText("AUTO / FORMACAO");

            lblEstilo.setFont(FONT_LABEL);
            lblEstilo.setForeground(TEXT_MUTED);
            lblEstilo.setText("ESTILO DE JOGO");

            lblMarcacao.setFont(FONT_LABEL);
            lblMarcacao.setForeground(TEXT_MUTED);
            lblMarcacao.setText("MARCACAO");

            lblAtaque.setFont(FONT_LABEL);
            lblAtaque.setForeground(TEXT_MUTED);
            lblAtaque.setText("POSTURA DE ATAQUE");

            // Dropdowns ComboBox modernos com cantos arredondados
            styleModernComboBox(cbAuto);
            styleModernComboBox(cbEstilo);
            styleModernComboBox(cbMarcacao);
            styleModernComboBox(cbAtaque);

            // Alinhamento simétrico e equidistante das 4 colunas superiores
            applyTacticsColumn(tacticsPanel, lblAuto, cbAuto, 12, 126);
            applyTacticsColumn(tacticsPanel, lblEstilo, cbEstilo, 148, 126);
            applyTacticsColumn(tacticsPanel, lblMarcacao, cbMarcacao, 284, 126);
            applyTacticsColumn(tacticsPanel, lblAtaque, cbAtaque, 420, 128);

            // ==========================================================
            // BOTÕES DE AÇÃO: ÍCONES VETORIAIS MODERNOS E EQUIDISTANTES
            // JOGAR PARTIDA sem as setas '>>'
            // ==========================================================
            styleActionPillButton(btnTaticas, "TATICAS SALVAS", false);
            styleActionPillButton(btnVoltar, "VOLTAR", false);
            styleActionPillButton(btnJogar, "JOGAR PARTIDA", true);

            int btnY = 58;
            int btnH = 34;
            int btnW = 168;

            int b1X = 12;
            int b2X = 12 + btnW + 16;  // 196
            int b3X = b2X + btnW + 16; // 380

            try {
                Class<?> constrClass = Class.forName("components.a");
                java.lang.reflect.Constructor<?> cConst = constrClass.getConstructor(int.class, int.class, int.class, int.class);

                tacticsPanel.add(btnTaticas, cConst.newInstance(b1X, btnY, btnW, btnH));
                tacticsPanel.add(btnVoltar, cConst.newInstance(b2X, btnY, btnW, btnH));
                tacticsPanel.add(btnJogar, cConst.newInstance(b3X, btnY, btnW, btnH));
            } catch (Exception ex) {
                btnTaticas.setBounds(b1X, btnY, btnW, btnH);
                btnVoltar.setBounds(b2X, btnY, btnW, btnH);
                btnJogar.setBounds(b3X, btnY, btnW, btnH);
            }

            // 6. Integração do Radar Duplo Comparativo
            installEmbeddedRadar(bp);

            // 7. Auto refresh contínuo
            setupAutoRefresh(root, bp, cbAuto, cbEstilo, cbMarcacao, cbAtaque);

            // 8. Primeiro cálculo de forças (executado de forma assíncrona para garantir renderização fluida)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        refreshTeamMetrics(root);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void applyTacticsColumn(JPanel panel, JLabel lbl, JComboBox cb, int x, int width) {
        try {
            Class<?> constrClass = Class.forName("components.a");
            java.lang.reflect.Constructor<?> cConst = constrClass.getConstructor(int.class, int.class, int.class, int.class);

            panel.add(lbl, cConst.newInstance(x, 8, width, 16));
            panel.add(cb, cConst.newInstance(x, 26, width, 26));
        } catch (Exception e) {
            lbl.setBounds(x, 8, width, 16);
            cb.setBounds(x, 26, width, 26);
        }
    }

    public static void styleModernComboBox(JComboBox cb) {
        if (cb == null) return;
        cb.setBackground(CARD_BG);
        cb.setForeground(TEXT_WHITE);
        cb.setFont(new Font("Verdana", Font.PLAIN, 11));
        cb.setFocusable(false);
        cb.setBorder(new RoundedBorder(8, BORDER_COLOR));

        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;

                        g2.setColor(new Color(180, 195, 215));
                        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                        g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
                        g2.dispose();
                    }
                };
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(20, 24));
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Limpo
            }
        });

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Verdana", Font.PLAIN, 11));
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    lbl.setBackground(new Color(37, 99, 235));
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(new Color(18, 24, 38));
                    lbl.setForeground(new Color(225, 235, 248));
                }
                return lbl;
            }
        });
    }

    /**
     * Estiliza botões de ação no padrão EA FC com ícones vetoriais modernos de alta definição.
     */
    public static void styleActionPillButton(final JButton btn, final String text, final boolean isPrimary) {
        if (btn == null) return;
        btn.setIcon(null);
        btn.setText(text);
        btn.setFont(new Font("Verdana", Font.BOLD, isPrimary ? 11 : 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                boolean hover = b.getModel().isRollover();
                boolean pressed = b.getModel().isPressed();

                int w = b.getWidth();
                int h = b.getHeight();
                int arc = 10;

                Color bg = isPrimary
                    ? (pressed ? new Color(5, 150, 105) : (hover ? new Color(52, 211, 153) : EMERALD))
                    : (pressed ? new Color(18, 24, 38) : (hover ? new Color(34, 46, 72) : CARD_BG));

                Color borderC = isPrimary
                    ? (hover ? Color.WHITE : new Color(110, 231, 183))
                    : (hover ? new Color(96, 165, 250) : BORDER_COLOR);

                // Fundo arredondado
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // Borda arredondada
                g2.setColor(borderC);
                g2.setStroke(new BasicStroke(isPrimary ? 1.5f : 1.1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                // Medição de Texto e Ícone Vetorial
                Font font = b.getFont();
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics(font);
                String txt = b.getText();
                int txtW = fm.stringWidth(txt);

                int iconW = 16;
                int gap = 8;
                int totalContentW = iconW + gap + txtW;
                int startX = (w - totalContentW) / 2;
                int iconY = (h - 16) / 2;

                Color iconColor = isPrimary ? Color.BLACK : (hover ? Color.WHITE : TEXT_BRIGHT);

                // RENDERIZAÇÃO DO ÍCONE VETORIAL MODERNO
                if (txt.contains("TATICAS")) {
                    // Ícone Vetorial: Prancheta Tática
                    g2.setColor(iconColor);
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawRoundRect(startX, iconY + 1, 14, 14, 3, 3);
                    g2.fillOval(startX + 3, iconY + 5, 2, 2);
                    g2.fillOval(startX + 7, iconY + 8, 2, 2);
                    g2.fillOval(startX + 10, iconY + 5, 2, 2);
                    g2.fillRect(startX + 4, iconY, 6, 2);
                } else if (txt.contains("VOLTAR")) {
                    // Ícone Vetorial: Chevron de Retorno elegante
                    g2.setColor(iconColor);
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(startX + 10, iconY + 3, startX + 4, iconY + 8);
                    g2.drawLine(startX + 4, iconY + 8, startX + 10, iconY + 13);
                } else if (txt.contains("JOGAR")) {
                    // Ícone Vetorial: Play Triangle limpo
                    g2.setColor(iconColor);
                    int[] xPts = {startX + 4, startX + 13, startX + 4};
                    int[] yPts = {iconY + 3, iconY + 8, iconY + 13};
                    g2.fillPolygon(xPts, yPts, 3);
                }

                // Texto do botão
                g2.setColor(isPrimary ? Color.BLACK : (hover ? Color.WHITE : TEXT_WHITE));
                int textX = startX + iconW + gap;
                int textY = ((h - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(txt, textX, textY);

                g2.dispose();
            }
        });
    }

    private static void installEmbeddedRadar(final JLayeredPane bp) {
        try {
            if (embeddedRadarPanel != null) {
                bp.remove(embeddedRadarPanel);
            }

            embeddedRadarPanel = new TacticalRadarPanel();
            Class<?> constrClass = Class.forName("components.a");
            java.lang.reflect.Constructor<?> cConst = constrClass.getConstructor(int.class, int.class, int.class, int.class);
            bp.add(embeddedRadarPanel, cConst.newInstance(1030, 8, 540, 672));
            bp.setLayer(embeddedRadarPanel, JLayeredPane.DEFAULT_LAYER);

        } catch (Exception e) {
            if (embeddedRadarPanel != null) {
                embeddedRadarPanel.setBounds(1030, 8, 540, 672);
                bp.add(embeddedRadarPanel);
            }
            e.printStackTrace();
        }
    }

    private static javax.swing.Timer liveTimer = null;

    private static void setupAutoRefresh(final JPanel root, final JLayeredPane bp, JComboBox... combos) {
        bp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        refreshTeamMetrics(root);
                    }
                });
            }
        });

        for (JComboBox cb : combos) {
            if (cb != null) {
                cb.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                refreshTeamMetrics(root);
                            }
                        });
                    }
                });
            }
        }

        if (liveTimer != null) {
            liveTimer.stop();
        }
        liveTimer = new javax.swing.Timer(250, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (root == null) {
                    if (liveTimer != null) liveTimer.stop();
                    return;
                }
                Window win = SwingUtilities.getWindowAncestor(root);
                if (win != null && !win.isDisplayable()) {
                    if (liveTimer != null) liveTimer.stop();
                    return;
                }

                if (root.isShowing()) {
                    refreshTeamMetrics(root);
                }
            }
        });
        liveTimer.start();
    }

    /**
     * Recalcula em tempo real a força dos jogadores que estão no gramado de ambos os clubes
     * levando em conta a força real (fi), energia (fp) e características (Cr1, Cr2).
     */
    public static void refreshTeamMetrics(JPanel root) {
        if (root == null) root = rootRef;
        if (root == null) return;

        try {
            // 1. NOSSO TIME (Varredura de todos os 25 slots táticos do campo)
            Field aeField = root.getClass().getDeclaredField("AE");
            aeField.setAccessible(true);
            ArrayList<?> list = (ArrayList<?>) aeField.get(root);
            if (list == null || list.isEmpty()) return;

            List<best.F> ourStarters = new ArrayList<best.F>();
            List<Integer> ourSlots = new ArrayList<Integer>();

            for (int slot = 1; slot <= 25 && slot < list.size(); slot++) {
                Object ao = list.get(slot);
                if (ao == null) continue;
                Method xM = ao.getClass().getMethod("x");
                best.F player = (best.F) xM.invoke(ao);
                if (player != null) {
                    ourStarters.add(player);
                    ourSlots.add(slot);
                }
            }

            String ourTeamName = "MEU TIME";
            best.ah ourTeamObj = null;
            ImageIcon ourCrest = null;
            try {
                Field zuField = root.getClass().getDeclaredField("zu");
                zuField.setAccessible(true);
                ourTeamObj = (best.ah) zuField.get(root);
                if (ourTeamObj != null) {
                    if (ourTeamObj.getNome() != null) ourTeamName = ourTeamObj.getNome();
                    ourCrest = ourTeamObj.x(24, 24);
                }
            } catch (Exception ignored) {}

            int ourOvr = calculateOverall(ourStarters, ourSlots);
            int[] ourAttrs = calculateTeamAttributes(ourStarters, ourSlots);

            // 2. ADVERSÁRIO
            boolean oppAvailable = false;
            String oppTeamName = "ADVERSÁRIO";
            ImageIcon oppCrest = null;
            int oppOvr = 75;
            int[] oppAttrs = {75, 75, 75, 75, 75, 75};

            try {
                Field bvField = root.getClass().getDeclaredField("bv");
                bvField.setAccessible(true);
                best.I match = (best.I) bvField.get(root);
                if (match != null) {
                    best.ah home = match.hc();
                    best.ah away = match.hd();
                    best.ah oppTeam = null;

                    if (ourTeamObj != null) {
                        if (ourTeamObj.equals(home)) oppTeam = away;
                        else oppTeam = home;
                    } else if (home != null) {
                        oppTeam = home;
                    }

                    if (oppTeam != null) {
                        oppAvailable = true;
                        if (oppTeam.getNome() != null) oppTeamName = oppTeam.getNome();
                        oppCrest = oppTeam.x(24, 24);

                        ArrayList<?> oppStartersList = oppTeam.kY();
                        List<best.F> oppStarters = new ArrayList<best.F>();
                        List<Integer> oppSlots = new ArrayList<Integer>();

                        if (oppStartersList != null && oppStartersList.size() >= 11) {
                            for (Object pObj : oppStartersList) {
                                if (pObj instanceof best.F) {
                                    best.F oppP = (best.F) pObj;
                                    oppStarters.add(oppP);
                                    oppSlots.add(PositionPenaltyHelper.getPlayerSlot(oppP));
                                }
                            }
                        } else {
                            ArrayList<best.F> oppAvail = new ArrayList<best.F>();
                            for (Object obj : oppTeam.kc()) {
                                if (obj instanceof best.F) oppAvail.add((best.F) obj);
                            }
                            int[] defaultOppFormation = {1, 20, 11, 13, 14, 16, 2, 9, 6, 4, 8};
                            for (int s : defaultOppFormation) {
                                best.F p = PositionPenaltyHelper.selectBestPlayerForSlot(oppAvail, s, false, false);
                                if (p != null) {
                                    oppAvail.remove(p);
                                    oppStarters.add(p);
                                    oppSlots.add(s);
                                }
                            }
                        }

                        if (!oppStarters.isEmpty()) {
                            oppOvr = calculateOverall(oppStarters, oppSlots);
                            oppAttrs = calculateTeamAttributes(oppStarters, oppSlots);
                        }
                    }
                }
            } catch (Exception e) {
                // Adversário não configurado
            }

            if (embeddedRadarPanel != null) {
                embeddedRadarPanel.updateAllStats(
                    ourTeamName, ourCrest, ourOvr, ourAttrs,
                    oppAvailable, oppTeamName, oppCrest, oppOvr, oppAttrs
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getPlayerEffectivePower(best.F p, int explicitSlot) {
        if (p == null) return 50;
        int power = p.fi();
        int slot = (explicitSlot >= 1 && explicitSlot <= 25) ? explicitSlot : PositionPenaltyHelper.getPlayerSlot(p);
        if (slot >= 1 && slot <= 25) {
            int pen = PositionPenaltyHelper.calculatePenalty(p, slot);
            power = Math.max(10, power - pen);
        }
        return power;
    }

    public static int calculateOverall(List<best.F> players, List<Integer> slots) {
        if (players == null || players.isEmpty()) return 70;
        int sum = 0;
        for (int i = 0; i < players.size(); i++) {
            best.F p = players.get(i);
            int slot = (slots != null && i < slots.size()) ? slots.get(i) : -1;
            sum += getPlayerEffectivePower(p, slot);
        }
        return Math.round((float) sum / players.size());
    }

    public static int[] calculateTeamAttributes(List<best.F> starters, List<Integer> slots) {
        if (starters == null || starters.isEmpty()) {
            return new int[]{75, 75, 75, 75, 75, 75};
        }

        int teamOvr = calculateOverall(starters, slots);

        int atkSum = 0, atkCount = 0;
        int midSum = 0, midCount = 0;
        int defSum = 0, defCount = 0;
        int energySum = 0;
        int ageSum = 0;

        int countArmacao = 0;
        int countCabeceio = 0;
        int countCruzamento = 0;
        int countDesarme = 0;
        int countDrible = 0;
        int countFinalizacao = 0;
        int countMarcacao = 0;
        int countPasse = 0;
        int countResistencia = 0;
        int countVelocidade = 0;

        for (int i = 0; i < starters.size(); i++) {
            best.F p = starters.get(i);
            int slot = (slots != null && i < slots.size()) ? slots.get(i) : -1;
            int power = getPlayerEffectivePower(p, slot);
            int pos = p.getPosicao();
            if (slot >= 1 && slot <= 25 && slot < PositionPenaltyHelper.SLOT_TARGET_POS.length) {
                int targetPos = PositionPenaltyHelper.SLOT_TARGET_POS[slot];
                if (targetPos >= 0) pos = targetPos;
            }
            energySum += p.fp();
            ageSum += p.getIdade();

            if (pos == 4) {
                atkSum += power;
                atkCount++;
            } else if (pos == 3) {
                midSum += power;
                midCount++;
            } else if (pos == 1 || pos == 2) {
                defSum += power;
                defCount++;
            }

            int cr1 = p.getCr1();
            int cr2 = p.getCr2();
            if (cr1 == 4 || cr2 == 4) countArmacao++;
            if (cr1 == 5 || cr2 == 5) countCabeceio++;
            if (cr1 == 6 || cr2 == 6) countCruzamento++;
            if (cr1 == 7 || cr2 == 7) countDesarme++;
            if (cr1 == 8 || cr2 == 8) countDrible++;
            if (cr1 == 9 || cr2 == 9) countFinalizacao++;
            if (cr1 == 10 || cr2 == 10) countMarcacao++;
            if (cr1 == 11 || cr2 == 11) countPasse++;
            if (cr1 == 12 || cr2 == 12) countResistencia++;
            if (cr1 == 13 || cr2 == 13) countVelocidade++;
        }

        int n = starters.size();
        float avgEnergy = (float) energySum / n;
        float avgAge = (float) ageSum / n;

        int baseAtk = atkCount > 0 ? Math.round((float) atkSum / atkCount) : teamOvr;
        int baseMid = midCount > 0 ? Math.round((float) midSum / midCount) : teamOvr;
        int baseDef = defCount > 0 ? Math.round((float) defSum / defCount) : teamOvr;

        // 1. ATAQUE (ATA)
        float rawAta = (baseAtk * 0.70f) + (baseMid * 0.30f);
        rawAta += (countFinalizacao * 3.5f) + (countCabeceio * 1.5f) + (countCruzamento * 1.0f) + (countVelocidade * 1.0f);
        int finalAta = clampStat(Math.round(rawAta));

        // 2. CRIAÇÃO (CRI)
        float rawCri = (baseMid * 0.75f) + (baseDef * 0.25f);
        rawCri += (countArmacao * 3.5f) + (countPasse * 3.5f) + (countDrible * 1.5f) + (countCruzamento * 1.5f);
        int finalCri = clampStat(Math.round(rawCri));

        // 3. DEFESA (DEF)
        float rawDef = (baseDef * 0.80f) + (baseMid * 0.20f);
        rawDef += (countDesarme * 3.5f) + (countMarcacao * 3.5f) + (countCabeceio * 1.5f) + (countResistencia * 1.0f);
        int finalDef = clampStat(Math.round(rawDef));

        // 4. FÍSICO (FIS)
        float rawFis = (avgEnergy * 0.50f) + (teamOvr * 0.50f);
        rawFis += (countResistencia * 4.0f) + (countMarcacao * 1.5f) + (countDesarme * 1.5f);
        int finalFis = clampStat(Math.round(rawFis));

        // 5. VELOCIDADE (VEL)
        float ageFactor = (28.0f - avgAge) * 1.2f;
        float rawVel = teamOvr + ageFactor;
        rawVel += (countVelocidade * 4.5f) + (countDrible * 2.5f);
        int finalVel = clampStat(Math.round(rawVel));

        // 6. TÉCNICA (TEC)
        float rawTec = (teamOvr * 0.60f) + (baseMid * 0.20f) + (baseAtk * 0.20f);
        rawTec += (countDrible * 3.0f) + (countPasse * 2.5f) + (countArmacao * 2.5f) + (countFinalizacao * 2.0f);
        int finalTec = clampStat(Math.round(rawTec));

        return new int[]{finalAta, finalCri, finalDef, finalFis, finalVel, finalTec};
    }

    private static int clampStat(int val) {
        return Math.max(25, Math.min(99, val));
    }

    /**
     * Pinta o fundo ultra escuro Console HUD EA FC eliminando o fundo antigo /img/f02.jpg.
     */
    public static void paintModernInMatchBackground(JPanel root, Graphics g) {
        if (root == null || g == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        applyHighQualityRendering(g2);
        int w = root.getWidth();
        int h = root.getHeight();
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, w, h);
        g2.setColor(BORDER_SLATE);
        g2.drawRoundRect(1, 1, w - 2, h - 2, 16, 16);
        g2.dispose();
    }

    /**
     * Aplica a estilização na Tela do Treinador Durante a Partida (cD).
     */
    public static void applyInMatchLayout(
            JPanel root,
            JLayeredPane df,
            JLabel dsPitch,
            JLabel dqBench,
            JLabel scoreHome,
            JLabel scoreAway,
            JLabel teamHome,
            JLabel teamAway,
            JLabel matchTime,
            JLabel matchPeriod,
            JLabel possession,
            JButton btnVoltarJogo,
            JComboBox cbEstilo,
            JComboBox cbMarcacao,
            JComboBox cbAtaque
    ) {
        try {
            InMatchHudManager.setupHud(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Chamado quando o time e atualizado ou ha substituicao.
     */
    public static void onInMatchTeamRefreshed(Object cD) {
        try {
            InMatchHudManager.onTeamRefreshed(cD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Estiliza botões do modal de diálogo com o visual moderno e cantos arredondados EA FC.
     */
    public static void styleModernDialogButton(JButton btn, final String text, final Color baseColor, final Color hoverColor) {
        if (btn == null) return;
        btn.setText(text);
        btn.setIcon(null);
        btn.setFont(new Font("Verdana", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                boolean hover = b.getModel().isRollover();
                boolean pressed = b.getModel().isPressed();

                int w = b.getWidth();
                int h = b.getHeight();
                int arc = 8;

                Color bg = pressed ? baseColor.darker() : (hover ? hoverColor : baseColor);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                g2.setColor(hover ? Color.WHITE : new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                Font font = b.getFont();
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics(font);
                String txt = b.getText();
                int txtW = fm.stringWidth(txt);
                int txtX = (w - txtW) / 2;
                int txtY = (h - fm.getHeight()) / 2 + fm.getAscent();

                g2.setColor(Color.WHITE);
                g2.drawString(txt, txtX, txtY);
                g2.dispose();
            }
        });
    }

    /**
     * Abre o modal de Táticas Salvas sem a barra de título padrão do Windows (undecorated),
     * atuando como uma janela interna integrada estilo EA FC.
     */
    public static void openTacticsDialog(JPanel bz) {
        if (bz == null) return;
        try {
            Field fUb = bz.getClass().getDeclaredField("ub");
            fUb.setAccessible(true);
            JDialog parent = (JDialog) fUb.get(bz);

            Field fZu = bz.getClass().getDeclaredField("zu");
            fZu.setAccessible(true);
            best.ah tacticsObj = (best.ah) fZu.get(bz);

            final JDialog dialog = new JDialog(parent);
            dialog.setUndecorated(true);
            dialog.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
            dialog.setModal(true);
            dialog.setResizable(false);

            java.lang.reflect.Constructor<?> constr = Class.forName("a.il")
                .getConstructor(Class.forName("a.bz"), JDialog.class, best.ah.class);
            JPanel panel = (JPanel) constr.newInstance(bz, dialog, tacticsObj);

            dialog.setContentPane(panel);
            dialog.setSize(400, 460);
            dialog.setPreferredSize(new Dimension(400, 460));
            dialog.setLocationRelativeTo(bz);
            dialog.setVisible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Redesenha e moderniza o diálogo de táticas salvas (a.il) com tema escuro EA FC,
     * barra de título interna com botão fechar moderno, suporte a arrastar com mouse,
     * cartões arredondados e sem cortes de layout.
     */
    public static void modernizeTacticsPanel(final JPanel panel) {
        if (panel == null) return;
        try {
            panel.setBackground(BG_DARK);

            Field fUb = panel.getClass().getDeclaredField("ub");
            fUb.setAccessible(true);
            final JDialog dialog = (JDialog) fUb.get(panel);

            Field fUg = panel.getClass().getDeclaredField("ug");
            fUg.setAccessible(true);
            JLabel lblCarregar = (JLabel) fUg.get(panel);

            Field fUh = panel.getClass().getDeclaredField("uh");
            fUh.setAccessible(true);
            JLabel lblSalvar = (JLabel) fUh.get(panel);

            Field fA = panel.getClass().getDeclaredField("a_");
            fA.setAccessible(true);
            JLabel lblNome = (JLabel) fA.get(panel);

            Field fBf = panel.getClass().getDeclaredField("Bf");
            fBf.setAccessible(true);
            JComboBox cbTaticas = (JComboBox) fBf.get(panel);

            Field fEj = panel.getClass().getDeclaredField("Ej");
            fEj.setAccessible(true);
            JTextField txtNome = (JTextField) fEj.get(panel);

            Field fVb = panel.getClass().getDeclaredField("vb");
            fVb.setAccessible(true);
            JButton btnCarregar = (JButton) fVb.get(panel);

            Field fVc = panel.getClass().getDeclaredField("vc");
            fVc.setAccessible(true);
            JButton btnApagar = (JButton) fVc.get(panel);

            Field fGp = panel.getClass().getDeclaredField("GP");
            fGp.setAccessible(true);
            JButton btnSalvar = (JButton) fGp.get(panel);

            panel.removeAll();
            panel.setLayout(new BorderLayout());
            panel.setBackground(BG_DARK);
            panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(14, new Color(45, 55, 80), 2),
                BorderFactory.createEmptyBorder(10, 14, 14, 14)
            ));

            // 1. Header Bar interna EA FC (substitui a barra branca do Windows)
            JPanel headerBar = new JPanel(new BorderLayout());
            headerBar.setOpaque(false);
            headerBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 10, 4));

            JLabel titleLabel = new JLabel("TÁTICAS SALVAS");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(Color.WHITE);

            final JButton btnClose = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean hover = getModel().isRollover();
                    g2.setColor(hover ? new Color(239, 68, 68) : new Color(160, 175, 205));
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int pad = 5;
                    g2.drawLine(pad, pad, getWidth() - pad, getHeight() - pad);
                    g2.drawLine(getWidth() - pad, pad, pad, getHeight() - pad);
                    g2.dispose();
                }
            };
            btnClose.setPreferredSize(new Dimension(20, 20));
            btnClose.setContentAreaFilled(false);
            btnClose.setBorderPainted(false);
            btnClose.setFocusPainted(false);
            btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (dialog != null) dialog.dispose();
                }
            });

            headerBar.add(titleLabel, BorderLayout.WEST);
            headerBar.add(btnClose, BorderLayout.EAST);

            // Suporte a arrastar a janela interna clicando e movendo pelo header
            final Point[] dragPoint = new Point[1];
            MouseAdapter dragAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragPoint[0] = e.getPoint();
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dialog != null && dragPoint[0] != null) {
                        Point curr = dialog.getLocation();
                        dialog.setLocation(curr.x + e.getX() - dragPoint[0].x, curr.y + e.getY() - dragPoint[0].y);
                    }
                }
            };
            headerBar.addMouseListener(dragAdapter);
            headerBar.addMouseMotionListener(dragAdapter);

            // Tecla ESC para fechar a janela
            panel.registerKeyboardAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (dialog != null) dialog.dispose();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            // 2. Card Carregar Tática
            JPanel cardLoad = new JPanel();
            cardLoad.setLayout(new BoxLayout(cardLoad, BoxLayout.Y_AXIS));
            cardLoad.setBackground(CARD_BG);
            cardLoad.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(30, 41, 59), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            lblCarregar.setText("CARREGAR TÁTICA");
            lblCarregar.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblCarregar.setForeground(new Color(200, 215, 240));
            lblCarregar.setOpaque(false);
            lblCarregar.setAlignmentX(Component.LEFT_ALIGNMENT);

            styleModernComboBox(cbTaticas);
            cbTaticas.setAlignmentX(Component.LEFT_ALIGNMENT);
            cbTaticas.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
            cbTaticas.setPreferredSize(new Dimension(320, 30));

            JPanel loadBtnRow = new JPanel(new GridLayout(1, 2, 10, 0));
            loadBtnRow.setOpaque(false);
            loadBtnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            loadBtnRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
            loadBtnRow.setPreferredSize(new Dimension(320, 32));

            styleModernDialogButton(btnCarregar, "Carregar", new Color(37, 99, 235), new Color(59, 130, 246));
            styleModernDialogButton(btnApagar, "Excluir", new Color(185, 28, 28), new Color(220, 38, 38));

            loadBtnRow.add(btnCarregar);
            loadBtnRow.add(btnApagar);

            cardLoad.add(lblCarregar);
            cardLoad.add(Box.createVerticalStrut(8));
            cardLoad.add(cbTaticas);
            cardLoad.add(Box.createVerticalStrut(10));
            cardLoad.add(loadBtnRow);

            // 3. Card Salvar Tática Atual
            JPanel cardSave = new JPanel();
            cardSave.setLayout(new BoxLayout(cardSave, BoxLayout.Y_AXIS));
            cardSave.setBackground(CARD_BG);
            cardSave.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(30, 41, 59), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            lblSalvar.setText("SALVAR TÁTICA ATUAL");
            lblSalvar.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblSalvar.setForeground(new Color(200, 215, 240));
            lblSalvar.setOpaque(false);
            lblSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel nameRow = new JPanel(new BorderLayout(8, 0));
            nameRow.setOpaque(false);
            nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
            nameRow.setPreferredSize(new Dimension(320, 30));

            lblNome.setText("Nome:");
            lblNome.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblNome.setForeground(new Color(226, 232, 240));
            lblNome.setOpaque(false);

            txtNome.setBackground(new Color(11, 15, 25));
            txtNome.setForeground(TEXT_WHITE);
            txtNome.setCaretColor(TEXT_WHITE);
            txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            txtNome.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(6, new Color(45, 55, 80), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));

            nameRow.add(lblNome, BorderLayout.WEST);
            nameRow.add(txtNome, BorderLayout.CENTER);

            styleModernDialogButton(btnSalvar, "Salvar Tática", new Color(5, 150, 105), new Color(16, 185, 129));
            btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnSalvar.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
            btnSalvar.setPreferredSize(new Dimension(320, 32));

            cardSave.add(lblSalvar);
            cardSave.add(Box.createVerticalStrut(8));
            cardSave.add(nameRow);
            cardSave.add(Box.createVerticalStrut(10));
            cardSave.add(btnSalvar);

            // Montagem
            panel.add(headerBar, BorderLayout.NORTH);

            JPanel centerContainer = new JPanel();
            centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
            centerContainer.setOpaque(false);
            centerContainer.add(cardLoad);
            centerContainer.add(Box.createVerticalStrut(12));
            centerContainer.add(cardSave);

            panel.add(centerContainer, BorderLayout.CENTER);

            if (dialog != null) {
                dialog.getContentPane().setBackground(BG_DARK);
                dialog.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                dialog.setSize(400, 460);
                dialog.setPreferredSize(new Dimension(400, 460));
                dialog.setResizable(false);
            }

            panel.revalidate();
            panel.repaint();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
