package mod.brasfoot;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Painel de Estatísticas Comparativas em Tempo Real para a Tela de Treinador Durante a Partida (cD).
 * Desenha barras de progresso horizontais modernas no padrão Console EA FC Dark HUD:
 * - Posse de Bola (% vs %)
 * - Finalizações (Total, No Gol e Para Fora)
 * - Desarmes
 * - Passes Errados
 * - Faltas
 */
public class InMatchStatsPanel extends JPanel {

    // Cores do Design System EA FC
    public static final Color CARD_BG = new Color(18, 24, 38);
    public static final Color BORDER_SLATE = new Color(45, 60, 88);
    public static final Color BAR_BG = new Color(24, 33, 52);
    public static final Color HOME_ACCENT = new Color(59, 130, 246);  // Azul Royal Mandante
    public static final Color AWAY_ACCENT = new Color(245, 158, 11);  // Âmbar/Ouro Visitante
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(180, 195, 215);

    // Tipografia nítida
    private static final Font FONT_VAL = new Font("Verdana", Font.BOLD, 11);
    private static final Font FONT_LBL = new Font("Verdana", Font.BOLD, 10);
    private static final Font FONT_SUB = new Font("Verdana", Font.PLAIN, 9);

    // Dados das estatísticas [Mandante, Visitante]
    private int homePossession = 50;
    private int awayPossession = 50;

    private int homeShots = 0;
    private int awayShots = 0;
    private int homeShotsOnTarget = 0;
    private int awayShotsOnTarget = 0;
    private int homeShotsOffTarget = 0;
    private int awayShotsOffTarget = 0;

    private int homeTackles = 0;
    private int awayTackles = 0;

    private int homeWrongPasses = 0;
    private int awayWrongPasses = 0;

    private int homeFouls = 0;
    private int awayFouls = 0;

    public InMatchStatsPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(440, 190));
    }

    /**
     * Atualiza os dados brutos a partir do objeto best.I da partida.
     */
    public void updateStats(best.I match) {
        if (match == null) return;
        try {
            // Posse de bola
            int[] pos = match.hz();
            if (pos != null && pos.length >= 2) {
                homePossession = Math.max(0, pos[0]);
                awayPossession = Math.max(0, pos[1]);
                if (homePossession == 0 && awayPossession == 0) {
                    homePossession = 50;
                    awayPossession = 50;
                }
            }

            // Finalizações
            int[] shots = match.hB();
            if (shots != null && shots.length >= 2) {
                homeShots = Math.max(0, shots[0]);
                awayShots = Math.max(0, shots[1]);
            }

            // No gol
            int[] onTarget = match.hC();
            if (onTarget != null && onTarget.length >= 2) {
                homeShotsOnTarget = Math.max(0, onTarget[0]);
                awayShotsOnTarget = Math.max(0, onTarget[1]);
            }

            // Para fora
            int[] offTarget = match.hD();
            if (offTarget != null && offTarget.length >= 2) {
                homeShotsOffTarget = Math.max(0, offTarget[0]);
                awayShotsOffTarget = Math.max(0, offTarget[1]);
            }

            // Desarmes
            int[] tackles = match.hY();
            if (tackles != null && tackles.length >= 2) {
                homeTackles = Math.max(0, tackles[0]);
                awayTackles = Math.max(0, tackles[1]);
            }

            // Passes errados
            int[] passes = match.hZ();
            if (passes != null && passes.length >= 2) {
                homeWrongPasses = Math.max(0, passes[0]);
                awayWrongPasses = Math.max(0, passes[1]);
            }

            // Faltas
            int[] fouls = match.ia();
            if (fouls != null && fouls.length >= 2) {
                homeFouls = Math.max(0, fouls[0]);
                awayFouls = Math.max(0, fouls[1]);
            }

            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        EaFcUiHelper.applyHighQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Card Container com cantos arredondados
        g2.setColor(CARD_BG);
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        g2.setColor(BORDER_SLATE);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

        // Header de Título da Seção
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(TEXT_MUTED);
        String title = "ESTATÍSTICAS DA PARTIDA";
        FontMetrics fmT = g2.getFontMetrics();
        g2.drawString(title, (w - fmT.stringWidth(title)) / 2, 17);

        // Linhas de Estatísticas (5 métricas)
        int startY = 32;
        int rowH = 30;

        // 1. Posse de Bola
        drawStatRow(g2, 16, startY, w - 32, "POSSE DE BOLA",
                homePossession + "%", awayPossession + "%",
                homePossession, awayPossession, true);

        // 2. Finalizações (Total + no gol/fora)
        String shotsHomeStr = homeShots + " (" + homeShotsOnTarget + "/" + homeShotsOffTarget + ")";
        String shotsAwayStr = awayShots + " (" + awayShotsOnTarget + "/" + awayShotsOffTarget + ")";
        drawStatRow(g2, 16, startY + rowH, w - 32, "FINALIZAÇÕES (NO GOL)",
                shotsHomeStr, shotsAwayStr,
                homeShots, awayShots, false);

        // 3. Desarmes
        drawStatRow(g2, 16, startY + rowH * 2, w - 32, "DESARMES",
                String.valueOf(homeTackles), String.valueOf(awayTackles),
                homeTackles, awayTackles, false);

        // 4. Passes Errados
        drawStatRow(g2, 16, startY + rowH * 3, w - 32, "PASSES ERRADOS",
                String.valueOf(homeWrongPasses), String.valueOf(awayWrongPasses),
                homeWrongPasses, awayWrongPasses, false);

        // 5. Faltas Cometidas
        drawStatRow(g2, 16, startY + rowH * 4, w - 32, "FALTAS COMETIDAS",
                String.valueOf(homeFouls), String.valueOf(awayFouls),
                homeFouls, awayFouls, false);

        g2.dispose();
    }

    private void drawStatRow(
            Graphics2D g2, int x, int y, int width, String label,
            String valHome, String valAway, int numHome, int numAway, boolean isPercentage
    ) {
        // Texto Mandante (Esquerda)
        g2.setFont(FONT_VAL);
        g2.setColor(TEXT_WHITE);
        g2.drawString(valHome, x, y + 10);

        // Rótulo Central (Meio)
        g2.setFont(FONT_LBL);
        g2.setColor(TEXT_MUTED);
        FontMetrics fmLbl = g2.getFontMetrics(FONT_LBL);
        int lblW = fmLbl.stringWidth(label);
        g2.drawString(label, x + (width - lblW) / 2, y + 10);

        // Texto Visitante (Direita)
        g2.setFont(FONT_VAL);
        g2.setColor(TEXT_WHITE);
        FontMetrics fmAway = g2.getFontMetrics(FONT_VAL);
        int awayW = fmAway.stringWidth(valAway);
        g2.drawString(valAway, x + width - awayW, y + 10);

        // Barra de Progresso Dupla (Abaixo dos valores)
        int barY = y + 15;
        int barH = 5;
        int barR = 3;

        // Fundo total da barra
        g2.setColor(BAR_BG);
        g2.fillRoundRect(x, barY, width, barH, barR, barR);

        // Proporção de preenchimento
        double total = numHome + numAway;
        int homeW;
        if (total <= 0) {
            homeW = width / 2;
        } else {
            homeW = (int) Math.round((numHome / total) * width);
            homeW = Math.max(barH, Math.min(width - barH, homeW));
        }

        // Lado Mandante (Azul)
        g2.setColor(HOME_ACCENT);
        g2.fillRoundRect(x, barY, homeW, barH, barR, barR);

        // Lado Visitante (Ouro)
        g2.setColor(AWAY_ACCENT);
        g2.fillRoundRect(x + homeW + 1, barY, Math.max(0, width - homeW - 1), barH, barR, barR);
    }
}
