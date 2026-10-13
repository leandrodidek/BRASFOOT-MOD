package mod.brasfoot;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Painel Tático Comparativo Duplo estilo EA Sports FC (Console HUD Moderno)
 * - Design clean, refinado e livre de elementos genéricos ("AI Slop")
 * - Cantos arredondados padrão console (16px) em todos os cards, badges e barras
 * - Sem barras laterais coloridas artificiais
 * - Escudo oficial do clube renderizado no cabeçalho ao lado do nome do clube
 * - Badge de Overall moderno: rótulo "OVERALL" acima e valor numérico centralizado abaixo
 * - Radar hexagonal de 6 atributos ponderados sempre LIVE em tempo real com média do elenco
 * - Comparativo duplo: Nosso Time (topo) vs Adversário (base)
 */
public class TacticalRadarPanel extends JPanel {

    // Paleta Dark HUD Console EA FC
    public static final Color BG_DARK = new Color(11, 15, 25);
    public static final Color CARD_BG = new Color(18, 24, 38);
    public static final Color CARD_HEADER = new Color(24, 33, 52);
    public static final Color BORDER_COLOR = new Color(45, 60, 88);
    public static final Color BORDER_HIGHLIGHT = new Color(70, 95, 135);

    // Tipografia Nítida
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(180, 195, 215);
    public static final Color TEXT_BRIGHT = new Color(225, 235, 248);

    // Cores Principais
    public static final Color OUR_ACCENT = new Color(16, 185, 129);
    public static final Color OUR_POLY_FILL = new Color(16, 185, 129, 115);
    public static final Color OUR_POLY_BORDER = new Color(52, 211, 153);

    public static final Color OPP_ACCENT = new Color(249, 115, 22);
    public static final Color OPP_POLY_FILL = new Color(239, 68, 68, 110);
    public static final Color OPP_POLY_BORDER = new Color(251, 146, 60);

    // Cores dos 6 Atributos Táticos
    public static final Color COLOR_ATA = new Color(244, 63, 94);   // Rosa/Vermelho
    public static final Color COLOR_CRI = new Color(168, 85, 247);  // Roxo/Lilás
    public static final Color COLOR_DEF = new Color(59, 130, 246);   // Azul Royal
    public static final Color COLOR_FIS = new Color(16, 185, 129);  // Esmeralda
    public static final Color COLOR_VEL = new Color(14, 165, 233);  // Ciano
    public static final Color COLOR_TEC = new Color(234, 179, 8);   // Dourado Âmbar

    public static final String[] ATTR_NAMES = {"ATA", "CRI", "DEF", "FIS", "VEL", "TEC"};
    public static final String[] ATTR_FULL_NAMES = {"Ataque", "Criação", "Defesa", "Físico", "Velocidade", "Técnica"};
    public static final Color[] ATTR_COLORS = {COLOR_ATA, COLOR_CRI, COLOR_DEF, COLOR_FIS, COLOR_VEL, COLOR_TEC};

    // Dados do Nosso Time
    private String ourTeamName = "MEU TIME";
    private ImageIcon ourCrest = null;
    private int ourOvr = 75;
    private int[] ourStats = {75, 75, 75, 75, 75, 75};

    // Dados do Adversário
    private boolean hasOpponent = false;
    private String oppTeamName = "ADVERSÁRIO";
    private ImageIcon oppCrest = null;
    private int oppOvr = 75;
    private int[] oppStats = {75, 75, 75, 75, 75, 75};

    public TacticalRadarPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(540, 672));
    }

    public TacticalRadarPanel(boolean dummy) {
        this();
    }

    /**
     * Atualiza todas as estatísticas em tempo real (Sempre LIVE com médias do gramado).
     */
    public void updateAllStats(
            String ourName, ImageIcon ourIcon, int ourOvrVal, int[] ourAttrs,
            boolean oppAvailable, String oppName, ImageIcon oppIcon, int oppOvrVal, int[] oppAttrs
    ) {
        if (ourName != null && !ourName.isEmpty()) this.ourTeamName = ourName.toUpperCase();
        this.ourCrest = ourIcon;
        this.ourOvr = clamp(ourOvrVal);
        if (ourAttrs != null && ourAttrs.length == 6) {
            for (int i = 0; i < 6; i++) this.ourStats[i] = clamp(ourAttrs[i]);
        }

        this.hasOpponent = oppAvailable;
        if (oppName != null && !oppName.isEmpty()) this.oppTeamName = oppName.toUpperCase();
        this.oppCrest = oppIcon;
        this.oppOvr = clamp(oppOvrVal);
        if (oppAttrs != null && oppAttrs.length == 6) {
            for (int i = 0; i < 6; i++) this.oppStats[i] = clamp(oppAttrs[i]);
        }

        repaint();
    }

    /**
     * Versão de compatibilidade que aceita parâmetros antigos.
     */
    public void updateAllStats(
            boolean ourComplete, int starterCount, String incompleteReason,
            String ourName, int ourOvrVal, int ourBench, int[] ourAttrs,
            boolean oppAvailable, String oppName, int oppOvrVal, int oppBenchVal, int[] oppAttrs
    ) {
        updateAllStats(ourName, null, ourOvrVal, ourAttrs, oppAvailable, oppName, null, oppOvrVal, oppAttrs);
    }

    public void updateStats(int atk, int mid, int def, int gk, int phys, int pace, int ovr, int benchOvr, double avgAge, String formation, String teamName) {
        int[] attrs = new int[] {atk, mid, def, phys, pace, atk};
        updateAllStats(teamName, null, ovr, attrs, false, null, null, 75, new int[]{75, 75, 75, 75, 75, 75});
    }

    private static int clamp(int val) {
        return Math.max(20, Math.min(99, val));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int totalW = getWidth();
        int cardH = 328;

        // 1. CARD SUPERIOR: NOSSO TIME (SEMPRE LIVE)
        drawTeamCard(
            g2, 0, 0, totalW, cardH,
            ourTeamName, ourCrest, ourOvr, ourStats,
            OUR_ACCENT, OUR_POLY_FILL, OUR_POLY_BORDER
        );

        // 2. CARD INFERIOR: ADVERSÁRIO
        if (hasOpponent) {
            drawTeamCard(
                g2, 0, 336, totalW, cardH,
                oppTeamName, oppCrest, oppOvr, oppStats,
                OPP_ACCENT, OPP_POLY_FILL, OPP_POLY_BORDER
            );
        } else {
            drawEmptyOpponentCard(g2, 0, 336, totalW, cardH);
        }

        g2.dispose();
    }

    /**
     * Renderiza o cartão de um clube com visual refinado EA FC.
     * Cantos arredondados, escudo do time, sem barras laterais de IA.
     */
    private void drawTeamCard(
            Graphics2D g2, int x, int y, int w, int h,
            String teamName, ImageIcon crestIcon, int ovrVal, int[] stats,
            Color accentColor, Color polyFill, Color polyBorder
    ) {
        Shape oldClip = g2.getClip();
        int arc = 16;
        g2.clip(new RoundRectangle2D.Float(x, y, w, h, arc, arc));

        // Fundo principal do card
        g2.setColor(CARD_BG);
        g2.fillRect(x, y, w, h);

        // Faixa de cabeçalho limpa
        int headerH = 46;
        g2.setColor(CARD_HEADER);
        g2.fillRect(x, y, w, headerH);
        g2.setColor(BORDER_COLOR);
        g2.drawLine(x, y + headerH, x + w, y + headerH);

        // Escudo oficial do clube no cabeçalho
        int textX = x + 16;
        if (crestIcon != null && crestIcon.getImage() != null) {
            Image crestImg = crestIcon.getImage();
            g2.drawImage(crestImg, x + 16, y + 11, 24, 24, null);
            textX = x + 48;
        }

        // Nome do Clube em destaque direto
        g2.setFont(new Font("Verdana", Font.BOLD, 14));
        g2.setColor(TEXT_WHITE);
        String displayName = teamName;
        int maxLen = (crestIcon != null) ? 22 : 26;
        if (displayName.length() > maxLen) displayName = displayName.substring(0, maxLen - 2) + "...";
        g2.drawString(displayName, textX, y + 29);

        // Restaura clip
        g2.setClip(oldClip);

        // Borda externa arredondada de acabamento
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);

        // CONTEÚDO:
        // Lado Esquerdo: Radar Hexagonal (raio 66px para perfeito respiro das legendas)
        int radarCenterX = x + 120;
        int radarCenterY = y + 185;
        int radarRadius = 66;

        drawHexRadar(g2, radarCenterX, radarCenterY, radarRadius, stats, polyFill, polyBorder, ovrVal);
        drawAttributeBars(g2, x + 248, y + 56, w - 264, stats);
    }

    private void drawHexRadar(
            Graphics2D g2, int cx, int cy, int r, int[] stats,
            Color polyFill, Color polyBorder, int ovrVal
    ) {
        int n = 6;
        int[] steps = {33, 66, 100};
        for (int step : steps) {
            Polygon web = new Polygon();
            double ratio = step / 100.0;
            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
                int wx = (int) (cx + (r * ratio) * Math.cos(angle));
                int wy = (int) (cy + (r * ratio) * Math.sin(angle));
                web.addPoint(wx, wy);
            }
            g2.setColor(new Color(255, 255, 255, step == 100 ? 50 : 25));
            g2.setStroke(new BasicStroke(step == 100 ? 1.0f : 0.8f));
            g2.drawPolygon(web);
        }

        g2.setColor(new Color(255, 255, 255, 35));
        g2.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
            int ex = (int) (cx + r * Math.cos(angle));
            int ey = (int) (cy + r * Math.sin(angle));
            g2.drawLine(cx, cy, ex, ey);
        }

        Polygon dataPoly = new Polygon();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
            double norm = 0.20 + (Math.max(20, Math.min(99, stats[i])) / 99.0) * 0.80;
            int px = (int) (cx + (r * norm) * Math.cos(angle));
            int py = (int) (cy + (r * norm) * Math.sin(angle));
            dataPoly.addPoint(px, py);
            points[i] = new Point(px, py);
        }

        g2.setColor(polyFill);
        g2.fillPolygon(dataPoly);

        g2.setColor(polyBorder);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawPolygon(dataPoly);

        for (int i = 0; i < n; i++) {
            Point pt = points[i];
            g2.setColor(ATTR_COLORS[i]);
            g2.fillOval(pt.x - 3, pt.y - 3, 6, 6);
            g2.setColor(Color.WHITE);
            g2.fillOval(pt.x - 1, pt.y - 1, 2, 2);
        }

        g2.setFont(new Font("Verdana", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();

        // Pills dos 6 atributos posicionados com espaçamento sem sobreposição
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
            int dist = r + 20;
            int lx = (int) (cx + dist * Math.cos(angle));
            int ly = (int) (cy + dist * Math.sin(angle));

            String text = ATTR_NAMES[i] + " " + stats[i];
            int tw = fm.stringWidth(text);
            int drawX = lx - (tw / 2);
            int drawY = ly + 4;

            // Ajuste direcional fino para evitar proximidade excessiva
            if (i == 0) drawY -= 4; // Topo (ATA)
            if (i == 3) drawY += 4; // Base (FIS)
            if (i == 1 || i == 2) drawX += 4; // Direita (CRI, DEF)
            if (i == 4 || i == 5) drawX -= 4; // Esquerda (VEL, TEC)

            g2.setColor(new Color(11, 15, 25, 230));
            g2.fillRoundRect(drawX - 4, drawY - 11, tw + 8, 15, 6, 6);
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(0.9f));
            g2.drawRoundRect(drawX - 4, drawY - 11, tw + 8, 15, 6, 6);

            g2.setColor(ATTR_COLORS[i]);
            g2.drawString(text, drawX, drawY);
        }

        // OVR central sem círculo de fundo: Título "OVERALL" + Valor numérico em destaque
        Font fontOvrLbl = new Font("Verdana", Font.BOLD, 9);
        g2.setFont(fontOvrLbl);
        FontMetrics fmOvrLbl = g2.getFontMetrics(fontOvrLbl);
        String lblOvr = "OVERALL";
        g2.setColor(TEXT_MUTED);
        g2.drawString(lblOvr, cx - (fmOvrLbl.stringWidth(lblOvr) / 2), cy - 2);

        Font fontOvrVal = new Font("Verdana", Font.BOLD, 17);
        g2.setFont(fontOvrVal);
        FontMetrics fmOvrVal = g2.getFontMetrics(fontOvrVal);
        String ovrStr = String.valueOf(ovrVal);
        g2.setColor(TEXT_WHITE);
        g2.drawString(ovrStr, cx - (fmOvrVal.stringWidth(ovrStr) / 2), cy + 14);
    }

    private void drawAttributeBars(Graphics2D g2, int x, int y, int w, int[] stats) {
        int curY = y;
        int barH = 6;
        int rowH = 43;

        for (int i = 0; i < 6; i++) {
            int val = stats[i];
            Color col = ATTR_COLORS[i];

            g2.setFont(new Font("Verdana", Font.BOLD, 11));
            g2.setColor(TEXT_BRIGHT);
            g2.drawString(ATTR_FULL_NAMES[i], x, curY + 12);

            String valStr = String.valueOf(val);
            FontMetrics fm = g2.getFontMetrics();
            int vw = fm.stringWidth(valStr);
            g2.setColor(col);
            g2.drawString(valStr, x + w - vw, curY + 12);

            int barY = curY + 18;
            g2.setColor(new Color(11, 15, 25));
            g2.fillRoundRect(x, barY, w, barH, 6, 6);
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawRoundRect(x, barY, w, barH, 6, 6);

            int fillW = (int) (w * (Math.max(5, Math.min(99, val)) / 99.0));
            if (fillW > 0) {
                g2.setColor(col);
                g2.fillRoundRect(x, barY, fillW, barH, 6, 6);
            }

            curY += rowH;
        }
    }

    private void drawEmptyOpponentCard(Graphics2D g2, int x, int y, int w, int h) {
        Shape oldClip = g2.getClip();
        int arc = 16;
        g2.clip(new RoundRectangle2D.Float(x, y, w, h, arc, arc));

        g2.setColor(CARD_BG);
        g2.fillRect(x, y, w, h);

        g2.setClip(oldClip);
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);

        g2.setFont(new Font("Verdana", Font.BOLD, 12));
        g2.setColor(TEXT_MUTED);
        String msg = "AGUARDANDO DEFINIÇÃO DO ADVERSÁRIO";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, x + (w - fm.stringWidth(msg)) / 2, y + (h / 2));
    }
}
