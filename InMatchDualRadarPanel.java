package mod.brasfoot;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Radar Tático Comparativo Duplo Sobreposto estilo EA FC.
 * - Dois polígonos sobrepostos (Mandante e Visitante) no mesmo hexágono centralizado
 * - Sem barras laterais de atributos
 * - Sem círculo/informação interna no centro do hexágono (centro limpo)
 * - Badges com escudo, nome do time e valor do Overall posicionados nos lados superiores
 * - Legenda comparativa moderna na base
 */
public class InMatchDualRadarPanel extends JPanel {

    public static final Color PANEL_DARK = new Color(18, 24, 38);
    public static final Color CARD_BG = new Color(24, 33, 52);
    public static final Color BORDER_SLATE = new Color(45, 60, 88);
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(180, 195, 215);

    // Cores das duas equipes
    public static final Color HOME_COLOR = new Color(16, 185, 129);      // Esmeralda
    public static final Color HOME_FILL = new Color(16, 185, 129, 70);
    public static final Color AWAY_COLOR = new Color(245, 158, 11);      // Dourado/Laranja
    public static final Color AWAY_FILL = new Color(245, 158, 11, 70);

    // 6 Atributos Táticos
    public static final String[] ATTR_NAMES = {"ATA", "CRI", "DEF", "FIS", "VEL", "TEC"};
    public static final Color[] ATTR_COLORS = {
        new Color(244, 63, 94),   // ATA
        new Color(168, 85, 247),  // CRI
        new Color(59, 130, 246),   // DEF
        new Color(16, 185, 129),  // FIS
        new Color(14, 165, 233),  // VEL
        new Color(234, 179, 8)    // TEC
    };

    private String homeName = "MANDANTE";
    private ImageIcon homeCrest = null;
    private int homeOvr = 75;
    private int[] homeStats = {75, 75, 75, 75, 75, 75};

    private String awayName = "VISITANTE";
    private ImageIcon awayCrest = null;
    private int awayOvr = 75;
    private int[] awayStats = {75, 75, 75, 75, 75, 75};

    public InMatchDualRadarPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(440, 295));
    }

    public void updateStats(
            String hName, ImageIcon hCrest, int hOvr, int[] hStats,
            String aName, ImageIcon aCrest, int aOvr, int[] aStats
    ) {
        if (hName != null && !hName.isEmpty()) this.homeName = hName.toUpperCase();
        this.homeCrest = hCrest;
        this.homeOvr = clamp(hOvr);
        if (hStats != null && hStats.length == 6) {
            for (int i = 0; i < 6; i++) this.homeStats[i] = clamp(hStats[i]);
        }

        if (aName != null && !aName.isEmpty()) this.awayName = aName.toUpperCase();
        this.awayCrest = aCrest;
        this.awayOvr = clamp(aOvr);
        if (aStats != null && aStats.length == 6) {
            for (int i = 0; i < 6; i++) this.awayStats[i] = clamp(aStats[i]);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        EaFcUiHelper.applyHighQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // 1. Fundo do Card
        g2.setColor(PANEL_DARK);
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        g2.setColor(BORDER_SLATE);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

        // 2. Badges dos Times no Topo (Aos lados)
        drawTeamBadge(g2, 12, 10, 195, 38, homeName, homeCrest, homeOvr, HOME_COLOR, true);
        drawTeamBadge(g2, w - 207, 10, 195, 38, awayName, awayCrest, awayOvr, AWAY_COLOR, false);

        // 3. Radar Hexagonal Centralizado Sobreposto
        int cx = w / 2;
        int cy = 160;
        int r = 70;

        drawOverlaidHexRadar(g2, cx, cy, r);

        // 4. Legenda Comparativa na Base
        drawBottomLegend(g2, cx, h - 14);

        g2.dispose();
    }

    private void drawTeamBadge(
            Graphics2D g2, int bx, int by, int bw, int bh,
            String name, ImageIcon crest, int ovr, Color teamColor, boolean isLeft
    ) {
        // Fundo do mini badge
        g2.setColor(CARD_BG);
        g2.fillRoundRect(bx, by, bw, bh, 8, 8);
        g2.setColor(BORDER_SLATE);
        g2.drawRoundRect(bx, by, bw, bh, 8, 8);

        // Escudo
        int textX = bx + 8;
        if (crest != null && crest.getImage() != null) {
            g2.drawImage(crest.getImage(), bx + 6, by + 7, 24, 24, null);
            textX = bx + 36;
        }

        // Nome do Clube
        g2.setFont(new Font("Verdana", Font.BOLD, 11));
        g2.setColor(TEXT_WHITE);
        String shortName = name;
        if (shortName.length() > 11) {
            shortName = shortName.substring(0, 9) + "..";
        }
        g2.drawString(shortName, textX, by + 24);

        // Badge do Overall (ex: 78)
        int badgeW = 34;
        int badgeH = 24;
        int badgeX = bx + bw - badgeW - 6;
        int badgeY = by + 7;

        g2.setColor(new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue(), 35));
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 6, 6);
        g2.setColor(teamColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 6, 6);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        String ovrStr = String.valueOf(ovr);
        int ox = badgeX + (badgeW - fm.stringWidth(ovrStr)) / 2;
        int oy = badgeY + ((badgeH - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(ovrStr, ox, oy);
    }

    private void drawOverlaidHexRadar(Graphics2D g2, int cx, int cy, int r) {
        int n = 6;
        int[] steps = {33, 66, 100};

        // Grade teia de fundo
        for (int step : steps) {
            Polygon web = new Polygon();
            double ratio = step / 100.0;
            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
                int wx = (int) (cx + (r * ratio) * Math.cos(angle));
                int wy = (int) (cy + (r * ratio) * Math.sin(angle));
                web.addPoint(wx, wy);
            }
            g2.setColor(new Color(255, 255, 255, step == 100 ? 45 : 20));
            g2.setStroke(new BasicStroke(step == 100 ? 1.0f : 0.8f));
            g2.drawPolygon(web);
        }

        // Eixos radiais
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
            int ex = (int) (cx + r * Math.cos(angle));
            int ey = (int) (cy + r * Math.sin(angle));
            g2.drawLine(cx, cy, ex, ey);
        }

        // 1. Polígono do Mandante
        drawPolygonLayer(g2, cx, cy, r, homeStats, HOME_FILL, HOME_COLOR);

        // 2. Polígono do Visitante (Sobreposto!)
        drawPolygonLayer(g2, cx, cy, r, awayStats, AWAY_FILL, AWAY_COLOR);

        // Rótulos externos dos 6 atributos com valores comparativos
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
            int labelDist = r + 16;
            int lx = (int) (cx + labelDist * Math.cos(angle));
            int ly = (int) (cy + labelDist * Math.sin(angle));

            String attrName = ATTR_NAMES[i];
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(attrName);

            int drawX = lx - strW / 2;
            int drawY = ly + 4;

            g2.setColor(ATTR_COLORS[i]);
            g2.drawString(attrName, drawX, drawY);
        }
    }

    private void drawPolygonLayer(
            Graphics2D g2, int cx, int cy, int r,
            int[] stats, Color fillCol, Color borderCol
    ) {
        int n = 6;
        Polygon poly = new Polygon();
        Point[] pts = new Point[n];

        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2.0 + (i * 2.0 * Math.PI / n);
            double norm = 0.22 + (Math.max(20, Math.min(99, stats[i])) / 99.0) * 0.78;
            int px = (int) (cx + (r * norm) * Math.cos(angle));
            int py = (int) (cy + (r * norm) * Math.sin(angle));
            poly.addPoint(px, py);
            pts[i] = new Point(px, py);
        }

        // Preenchimento suave
        g2.setColor(fillCol);
        g2.fillPolygon(poly);

        // Contorno nítido
        g2.setColor(borderCol);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawPolygon(poly);

        // Vértices destacados
        for (int i = 0; i < n; i++) {
            Point p = pts[i];
            g2.setColor(borderCol);
            g2.fillOval(p.x - 3, p.y - 3, 6, 6);
            g2.setColor(Color.WHITE);
            g2.fillOval(p.x - 1, p.y - 1, 2, 2);
        }
    }

    private void drawBottomLegend(Graphics2D g2, int cx, int y) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));

        // Mandante
        String hTxt = homeName + " (" + homeOvr + ")";
        String aTxt = awayName + " (" + awayOvr + ")";
        FontMetrics fm = g2.getFontMetrics();

        int spacing = 36;
        int totalW = fm.stringWidth(hTxt) + 14 + spacing + fm.stringWidth(aTxt) + 14;
        int startX = cx - totalW / 2;

        // Ponto Mandante
        g2.setColor(HOME_COLOR);
        g2.fillOval(startX, y - 8, 8, 8);
        g2.setColor(TEXT_WHITE);
        g2.drawString(hTxt, startX + 12, y);

        // Ponto Visitante
        int nextX = startX + 12 + fm.stringWidth(hTxt) + spacing;
        g2.setColor(AWAY_COLOR);
        g2.fillOval(nextX, y - 8, 8, 8);
        g2.setColor(TEXT_WHITE);
        g2.drawString(aTxt, nextX + 12, y);
    }

    private int clamp(int v) {
        return Math.max(30, Math.min(99, v));
    }
}
