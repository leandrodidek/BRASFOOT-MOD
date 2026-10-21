package mod.brasfoot;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LiveMatchPreviewRenderer {

    // Dimensões do Mod Widescreen
    public static final int W = 1740;
    public static final int H = 880;

    // Design Tokens EA FC
    public static final Color BG_DEEP = new Color(11, 15, 25);
    public static final Color PANEL_DARK = new Color(18, 24, 38);
    public static final Color CARD_BG = new Color(24, 33, 52);
    public static final Color BORDER_SLATE = new Color(45, 60, 88);
    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(180, 195, 215);
    public static final Color EMERALD = new Color(16, 185, 129);
    public static final Color BLUE_ACCENT = new Color(59, 130, 246);
    public static final Color GOLD_ACCENT = new Color(245, 158, 11);
    public static final Color RED_ACCENT = new Color(239, 68, 68);

    public static void main(String[] args) {
        try {
            renderMockup();
            System.out.println("Preview generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renderMockup() throws Exception {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        EaFcUiHelper.applyHighQualityRendering(g2);

        // Fundo Global Deep
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, W, H);

        // ====================================================================
        // 1. TOP HEADER: BROADCAST SCOREBUG & MATCH BAR (0, 0, 1740, 110)
        // ====================================================================
        drawTopScorebug(g2, 20, 12, W - 40, 96);

        // ====================================================================
        // 2. COLUNA ESQUERDA: ESTATÍSTICAS AO VIVO & FEED (20, 120, 440, 740)
        // ====================================================================
        drawStatsAndFeedPanel(g2, 20, 120, 440, 740);

        // ====================================================================
        // 3. COLUNA CENTRAL: CAMPO TÁTICO & MOMENTO DO JOGO (480, 120, 680, 740)
        // ====================================================================
        drawCenterPitchPanel(g2, 480, 120, 680, 740);

        // ====================================================================
        // 4. COLUNA DIREITA: TABELA AO VIVO & OUTROS JOGOS (1180, 120, 540, 740)
        // ====================================================================
        drawRightDynamicPanel(g2, 1180, 120, 540, 740);

        g2.dispose();

        // Salva imagem no projeto e copia para artefatos
        File outFile = new File("C:/Brasfoot22-23/projeto/live_match_concept_preview.png");
        ImageIO.write(img, "png", outFile);

        String artifactPath = "C:/Users/Leandro/.gemini/antigravity-ide/brain/6f6ea2db-1942-4a48-aed3-5d00adb4970a/live_match_concept_preview.png";
        ImageIO.write(img, "png", new File(artifactPath));
    }

    private static void drawTopScorebug(Graphics2D g2, int x, int y, int w, int h) {
        // Card de fundo
        RoundRectangle2D.Float card = new RoundRectangle2D.Float(x, y, w, h, 16, 16);
        g2.setColor(PANEL_DARK);
        g2.fill(card);
        g2.setColor(BORDER_SLATE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(card);

        // Placar Central Box
        int cx = x + w / 2;
        int scoreBoxW = 280;
        int scoreBoxH = 54;
        int sbx = cx - scoreBoxW / 2;
        int sby = y + 12;

        RoundRectangle2D.Float sBox = new RoundRectangle2D.Float(sbx, sby, scoreBoxW, scoreBoxH, 12, 12);
        GradientPaint gp = new GradientPaint(sbx, sby, new Color(14, 20, 32), sbx, sby + scoreBoxH, new Color(24, 33, 52));
        g2.setPaint(gp);
        g2.fill(sBox);
        g2.setColor(new Color(59, 130, 246, 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(sBox);

        // Placar Texto
        g2.setFont(new Font("Impact", Font.PLAIN, 36));
        g2.setColor(TEXT_WHITE);
        String scoreStr = "2   -   1";
        FontMetrics fmScore = g2.getFontMetrics();
        g2.drawString(scoreStr, cx - fmScore.stringWidth(scoreStr) / 2, sby + 40);

        // Time Mandante (Esquerda do Placar)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(TEXT_WHITE);
        String homeName = "CORINTHIANS";
        FontMetrics fmHome = g2.getFontMetrics();
        g2.drawString(homeName, sbx - 24 - fmHome.stringWidth(homeName), sby + 37);

        // Escudo Home Mock
        drawMockCrest(g2, sbx - 24 - fmHome.stringWidth(homeName) - 52, sby + 3, 44, 44, BLUE_ACCENT, "COR");

        // Autores dos Gols Mandante (Abaixo do nome)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(160, 185, 215));
        String homeScorers = "⚽ Yuri Alberto 23', 58'";
        FontMetrics fmHS = g2.getFontMetrics();
        g2.drawString(homeScorers, sbx - 24 - fmHS.stringWidth(homeScorers), sby + 60);

        // Time Visitante (Direita do Placar)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(TEXT_WHITE);
        String awayName = "FLAMENGO";
        g2.drawString(awayName, sbx + scoreBoxW + 74, sby + 37);

        // Escudo Away Mock
        drawMockCrest(g2, sbx + scoreBoxW + 20, sby + 3, 44, 44, RED_ACCENT, "FLA");

        // Autores dos Gols Visitante
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(160, 185, 215));
        g2.drawString("⚽ Pedro 41'", sbx + scoreBoxW + 74, sby + 60);

        // Cronômetro Dinâmico de 90 Minutos (Centro inferior)
        int barW = 320;
        int barH = 8;
        int barX = cx - barW / 2;
        int barY = sby + scoreBoxH + 12;

        // Fundo da barra
        g2.setColor(new Color(30, 42, 65));
        g2.fillRoundRect(barX, barY, barW, barH, 4, 4);

        // Progresso proporcional a 90' (ex: 68' = ~75%)
        int curMin = 68;
        int progW = (int) (barW * (curMin / 90.0));
        GradientPaint pGrad = new GradientPaint(barX, barY, EMERALD, barX + progW, barY, new Color(52, 211, 153));
        g2.setPaint(pGrad);
        g2.fillRoundRect(barX, barY, progW, barH, 4, 4);

        // Badge do Minuto
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(EMERALD);
        String timeStr = "68' 2º TEMPO";
        FontMetrics fmT = g2.getFontMetrics();
        g2.drawString(timeStr, cx - fmT.stringWidth(timeStr) / 2, barY + 20);

        // Botões de Ação na Direita do Header (Pausar / Velocidade)
        drawActionBadge(g2, x + w - 160, y + 26, 140, 40, "PAUSAR JOGO", new Color(245, 158, 11));
    }

    private static void drawStatsAndFeedPanel(Graphics2D g2, int x, int y, int w, int h) {
        RoundRectangle2D.Float card = new RoundRectangle2D.Float(x, y, w, h, 16, 16);
        g2.setColor(PANEL_DARK);
        g2.fill(card);
        g2.setColor(BORDER_SLATE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(card);

        // Título do Card
        drawSectionHeader(g2, x + 16, y + 16, w - 32, "ESTATÍSTICAS AO VIVO", BLUE_ACCENT);

        int sy = y + 54;
        int rowH = 46;

        drawStatBar(g2, x + 16, sy + 0 * rowH, w - 32, "Posse de Bola", "58%", "42%", 58, 42, BLUE_ACCENT, RED_ACCENT);
        drawStatBar(g2, x + 16, sy + 1 * rowH, w - 32, "Finalizações", "14", "9", 14, 9, BLUE_ACCENT, RED_ACCENT);
        drawStatBar(g2, x + 16, sy + 2 * rowH, w - 32, "Chutes no Gol", "7", "4", 7, 4, BLUE_ACCENT, RED_ACCENT);
        drawStatBar(g2, x + 16, sy + 3 * rowH, w - 32, "Escanteios", "6", "3", 6, 3, BLUE_ACCENT, RED_ACCENT);
        drawStatBar(g2, x + 16, sy + 4 * rowH, w - 32, "Faltas Cometidas", "11", "15", 11, 15, BLUE_ACCENT, RED_ACCENT);
        drawStatBar(g2, x + 16, sy + 5 * rowH, w - 32, "Cartões Amarelos", "1", "3", 1, 3, GOLD_ACCENT, GOLD_ACCENT);
        drawStatBar(g2, x + 16, sy + 6 * rowH, w - 32, "Desarmes", "22", "18", 22, 18, BLUE_ACCENT, RED_ACCENT);

        // Divisor e Feed de Eventos
        int fy = sy + 7 * rowH + 10;
        g2.setColor(new Color(45, 60, 88, 120));
        g2.drawLine(x + 16, fy, x + w - 16, fy);

        drawSectionHeader(g2, x + 16, fy + 12, w - 32, "LINHA DO TEMPO DA PARTIDA", EMERALD);

        int ey = fy + 50;
        drawFeedItem(g2, x + 16, ey + 0 * 48, w - 32, "68'", "🔄 Substituição: Entra Romero, Sai Mosquito", new Color(59, 130, 246));
        drawFeedItem(g2, x + 16, ey + 1 * 48, w - 32, "58'", "⚽ GOL! Yuri Alberto finaliza cruzamento no canto", new Color(16, 185, 129));
        drawFeedItem(g2, x + 16, ey + 2 * 48, w - 32, "52'", "🟨 Cartão Amarelo para Gerson (falta dura)", new Color(245, 158, 11));
        drawFeedItem(g2, x + 16, ey + 3 * 48, w - 32, "45'", "⏱ Fim do Primeiro Tempo (1 - 1)", new Color(148, 163, 184));
        drawFeedItem(g2, x + 16, ey + 4 * 48, w - 32, "41'", "⚽ GOL! Pedro empata de cabeça", new Color(239, 68, 68));
        drawFeedItem(g2, x + 16, ey + 5 * 48, w - 32, "23'", "⚽ GOL! Yuri Alberto abre o placar", new Color(16, 185, 129));
    }

    private static void drawCenterPitchPanel(Graphics2D g2, int x, int y, int w, int h) {
        RoundRectangle2D.Float card = new RoundRectangle2D.Float(x, y, w, h, 16, 16);
        g2.setColor(PANEL_DARK);
        g2.fill(card);
        g2.setColor(BORDER_SLATE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(card);

        // Header
        drawSectionHeader(g2, x + 16, y + 16, w - 32, "MOMENTO DO JOGO & FORMAÇÃO TÁTICA", GOLD_ACCENT);

        // Mini Gramado Estilizado Central (cantos arredondados, bordas sutis)
        int pw = w - 40;
        int ph = 520;
        int px = x + 20;
        int py = y + 54;

        RoundRectangle2D.Float pitchBox = new RoundRectangle2D.Float(px, py, pw, ph, 14, 14);
        GradientPaint pitchGrad = new GradientPaint(px, py, new Color(18, 55, 28), px, py + ph, new Color(24, 75, 38));
        g2.setPaint(pitchGrad);
        g2.fill(pitchBox);

        // Linhas do campo modernas em branco sutil
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(pitchBox);
        // Linha do meio
        g2.drawLine(px, py + ph / 2, px + pw, py + ph / 2);
        // Círculo central
        g2.drawOval(px + pw / 2 - 50, py + ph / 2 - 50, 100, 100);
        g2.fillOval(px + pw / 2 - 4, py + ph / 2 - 4, 8, 8);
        // Áreas
        g2.drawRect(px + pw / 2 - 110, py, 220, 80);
        g2.drawRect(px + pw / 2 - 110, py + ph - 80, 220, 80);

        // Desenha jogadores em campo (camisas miniaturas ou círculos luminosos)
        drawPitchPlayer(g2, px + 80, py + 120, "10", "R. Augusto", BLUE_ACCENT);
        drawPitchPlayer(g2, px + pw / 2, py + 100, "9", "Y. Alberto", BLUE_ACCENT);
        drawPitchPlayer(g2, px + pw - 80, py + 120, "11", "Romero", BLUE_ACCENT);
        drawPitchPlayer(g2, px + pw / 2, py + 40, "12", "Cássio", BLUE_ACCENT);

        drawPitchPlayer(g2, px + pw / 2, py + ph - 110, "9", "Pedro", RED_ACCENT);
        drawPitchPlayer(g2, px + 100, py + ph - 130, "14", "Arrascaeta", RED_ACCENT);
        drawPitchPlayer(g2, px + pw - 100, py + ph - 130, "7", "L. Araújo", RED_ACCENT);

        // Indicador de Posse Ativa no Gramado (Glow do setor de ataque)
        g2.setColor(new Color(59, 130, 246, 35));
        g2.fillRoundRect(px + 40, py + 60, pw - 80, 160, 20, 20);

        // Banner de Destaque Tático na base do painel central
        int by = py + ph + 16;
        RoundRectangle2D.Float bCard = new RoundRectangle2D.Float(px, by, pw, 120, 12, 12);
        g2.setColor(CARD_BG);
        g2.fill(bCard);
        g2.setColor(BORDER_SLATE);
        g2.draw(bCard);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(TEXT_WHITE);
        g2.drawString("ANÁLISE DE MOMENTO: PRESSÃO OFENSIVA", px + 16, by + 26);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(TEXT_MUTED);
        g2.drawString("O Corinthians mantém maior volume no campo adversário nos últimos 10 minutos.", px + 16, by + 48);
        g2.drawString("Última finalização perigosa aos 64' com chute defendido pelo goleiro.", px + 16, by + 68);
        g2.drawString("Formações: 4-3-3 (Ofensivo) vs 4-2-3-1 (Equilibrado)", px + 16, by + 94);
    }

    private static void drawRightDynamicPanel(Graphics2D g2, int x, int y, int w, int h) {
        RoundRectangle2D.Float card = new RoundRectangle2D.Float(x, y, w, h, 16, 16);
        g2.setColor(PANEL_DARK);
        g2.fill(card);
        g2.setColor(BORDER_SLATE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(card);

        // Seletor de Abas Estilo EA FC HUD
        int tabW = (w - 32) / 2;
        int tabH = 36;
        int tx = x + 16;
        int ty = y + 16;

        // Aba 1: Tabela ao Vivo (Ativa)
        RoundRectangle2D.Float tab1 = new RoundRectangle2D.Float(tx, ty, tabW, tabH, 8, 8);
        g2.setColor(BLUE_ACCENT);
        g2.fill(tab1);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(TEXT_WHITE);
        String t1Title = "🏆 TABELA AO VIVO";
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString(t1Title, tx + (tabW - fm1.stringWidth(t1Title)) / 2, ty + 23);

        // Aba 2: Outros Jogos
        RoundRectangle2D.Float tab2 = new RoundRectangle2D.Float(tx + tabW + 8, ty, tabW, tabH, 8, 8);
        g2.setColor(CARD_BG);
        g2.fill(tab2);
        g2.setColor(BORDER_SLATE);
        g2.draw(tab2);
        g2.setColor(TEXT_MUTED);
        String t2Title = "⚽ OUTROS JOGOS (RODADA)";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(t2Title, tx + tabW + 8 + (tabW - fm2.stringWidth(t2Title)) / 2, ty + 23);

        // Mostra a Tabela ao Vivo (Parte Superior)
        int tableY = ty + tabH + 16;
        drawLiveTablePreview(g2, x + 16, tableY, w - 32, 340);

        // Mostra Cards Limpos dos Outros Jogos da Rodada (Parte Inferior)
        int matchesY = tableY + 355;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(TEXT_WHITE);
        g2.drawString("OUTROS JOGOS DA RODADA (AO VIVO):", x + 16, matchesY);

        int my = matchesY + 12;
        drawCleanMatchCard(g2, x + 16, my + 0 * 50, (w - 38) / 2, "PAL", "1 - 1", "SAO", "72'", false);
        drawCleanMatchCard(g2, x + 16 + (w - 38) / 2 + 6, my + 0 * 50, (w - 38) / 2, "GRE", "0 - 2", "INT", "65'", false);
        drawCleanMatchCard(g2, x + 16, my + 1 * 50, (w - 38) / 2, "BOT", "3 - 0", "VAS", "FIM", true);
        drawCleanMatchCard(g2, x + 16 + (w - 38) / 2 + 6, my + 1 * 50, (w - 38) / 2, "CAM", "2 - 1", "CRU", "80'", false);
        drawCleanMatchCard(g2, x + 16, my + 2 * 50, (w - 38) / 2, "FLU", "0 - 0", "SAN", "61'", false);
        drawCleanMatchCard(g2, x + 16 + (w - 38) / 2 + 6, my + 2 * 50, (w - 38) / 2, "FOR", "1 - 0", "CAP", "FIM", true);
    }

    private static void drawLiveTablePreview(Graphics2D g2, int x, int y, int w, int h) {
        RoundRectangle2D.Float box = new RoundRectangle2D.Float(x, y, w, h, 12, 12);
        g2.setColor(CARD_BG);
        g2.fill(box);
        g2.setColor(BORDER_SLATE);
        g2.draw(box);

        // Header das Colunas
        g2.setColor(new Color(16, 22, 35));
        g2.fillRoundRect(x + 2, y + 2, w - 4, 28, 10, 10);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(148, 163, 184));
        g2.drawString("POS", x + 10, y + 19);
        g2.drawString("CLUBE", x + 44, y + 19);
        g2.drawString("PTS", x + w - 180, y + 19);
        g2.drawString("J", x + w - 145, y + 19);
        g2.drawString("V", x + w - 115, y + 19);
        g2.drawString("SG", x + w - 85, y + 19);
        g2.drawString("VAR", x + w - 45, y + 19);

        // Linhas de Classificação Mock
        String[][] teams = {
            {"1", "Botafogo", "61", "28", "19", "+28", "▲ +3"},
            {"2", "Palmeiras", "57", "28", "17", "+24", "▼ -2"},
            {"3", "Corinthians (Você)", "55", "28", "16", "+18", "▲ +3"},
            {"4", "Flamengo", "54", "28", "16", "+16", "▼ -3"},
            {"5", "Internacional", "49", "28", "14", "+11", "▲ +3"},
            {"6", "São Paulo", "48", "28", "13", "+9", "― 0"},
            {"7", "Atlético-MG", "44", "28", "12", "+4", "▲ +3"},
            {"8", "Cruzeiro", "41", "28", "11", "+2", "▼ -3"},
            {"9", "Bahia", "40", "28", "11", "0", "― 0"},
            {"10", "Vasco", "38", "28", "10", "-4", "▼ -3"}
        };

        int ry = y + 34;
        for (int i = 0; i < teams.length; i++) {
            boolean isUser = (i == 2);
            if (isUser) {
                // Destaque com brilho na linha do time do jogador
                g2.setColor(new Color(59, 130, 246, 60));
                g2.fillRoundRect(x + 4, ry - 3, w - 8, 26, 6, 6);
                g2.setColor(BLUE_ACCENT);
                g2.drawRoundRect(x + 4, ry - 3, w - 8, 26, 6, 6);
            } else if (i % 2 == 1) {
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillRect(x + 4, ry - 3, w - 8, 26);
            }

            // Indicador de G4 (Verde)
            if (i < 4) {
                g2.setColor(EMERALD);
                g2.fillRoundRect(x + 6, ry + 2, 3, 16, 2, 2);
            }

            g2.setFont(new Font("Segoe UI", isUser ? Font.BOLD : Font.PLAIN, 11));
            g2.setColor(isUser ? Color.WHITE : TEXT_WHITE);
            g2.drawString(teams[i][0], x + 14, ry + 15);
            g2.drawString(teams[i][1], x + 44, ry + 15);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(isUser ? EMERALD : TEXT_WHITE);
            g2.drawString(teams[i][2], x + w - 180, ry + 15);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_MUTED);
            g2.drawString(teams[i][3], x + w - 145, ry + 15);
            g2.drawString(teams[i][4], x + w - 115, ry + 15);
            g2.drawString(teams[i][5], x + w - 85, ry + 15);

            boolean posUp = teams[i][6].startsWith("▲");
            g2.setColor(posUp ? EMERALD : (teams[i][6].startsWith("▼") ? RED_ACCENT : TEXT_MUTED));
            g2.drawString(teams[i][6], x + w - 45, ry + 15);

            ry += 29;
        }
    }

    private static void drawCleanMatchCard(Graphics2D g2, int x, int y, int w, String hTeam, String score, String aTeam, String time, boolean fim) {
        RoundRectangle2D.Float rc = new RoundRectangle2D.Float(x, y, w, 44, 8, 8);
        g2.setColor(CARD_BG);
        g2.fill(rc);
        g2.setColor(BORDER_SLATE);
        g2.draw(rc);

        // Mini Escudo Home
        drawMockCrest(g2, x + 8, y + 10, 24, 24, BLUE_ACCENT, hTeam.substring(0, 1));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(TEXT_WHITE);
        g2.drawString(hTeam, x + 36, y + 26);

        // Placar Central
        g2.setFont(new Font("Impact", Font.PLAIN, 14));
        g2.setColor(fim ? new Color(180, 195, 215) : EMERALD);
        FontMetrics fmS = g2.getFontMetrics();
        g2.drawString(score, x + w / 2 - fmS.stringWidth(score) / 2, y + 26);

        // Time Away
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(TEXT_WHITE);
        g2.drawString(aTeam, x + w - 36 - 28, y + 26);
        drawMockCrest(g2, x + w - 32, y + 10, 24, 24, RED_ACCENT, aTeam.substring(0, 1));

        // Badge de Status/Minuto
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(fim ? new Color(148, 163, 184) : EMERALD);
        FontMetrics fmT = g2.getFontMetrics();
        g2.drawString(time, x + w / 2 - fmT.stringWidth(time) / 2, y + 38);
    }

    private static void drawStatBar(Graphics2D g2, int x, int y, int w, String title, String valH, String valA, int numH, int numA, Color colH, Color colA) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(TEXT_MUTED);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, x + (w - fm.stringWidth(title)) / 2, y + 13);

        g2.setColor(colH);
        g2.drawString(valH, x + 8, y + 13);

        g2.setColor(colA);
        FontMetrics fmA = g2.getFontMetrics();
        g2.drawString(valA, x + w - 8 - fmA.stringWidth(valA), y + 13);

        int barH = 6;
        int barY = y + 20;
        int halfW = (w - 24) / 2;

        // Barra Home (cresce para a esquerda a partir do centro)
        int total = Math.max(1, numH + numA);
        int hW = (int) (halfW * ((double) numH / total));
        g2.setColor(new Color(30, 42, 65));
        g2.fillRoundRect(x + 8, barY, halfW, barH, 3, 3);
        g2.setColor(colH);
        g2.fillRoundRect(x + 8 + (halfW - hW), barY, hW, barH, 3, 3);

        // Barra Away (cresce para a direita a partir do centro)
        int aW = (int) (halfW * ((double) numA / total));
        g2.setColor(new Color(30, 42, 65));
        g2.fillRoundRect(x + 16 + halfW, barY, halfW, barH, 3, 3);
        g2.setColor(colA);
        g2.fillRoundRect(x + 16 + halfW, barY, aW, barH, 3, 3);
    }

    private static void drawFeedItem(Graphics2D g2, int x, int y, int w, String min, String desc, Color badgeCol) {
        RoundRectangle2D.Float bg = new RoundRectangle2D.Float(x, y, w, 40, 8, 8);
        g2.setColor(CARD_BG);
        g2.fill(bg);
        g2.setColor(new Color(45, 60, 88, 80));
        g2.draw(bg);

        // Pill do minuto
        g2.setColor(badgeCol);
        g2.fillRoundRect(x + 8, y + 10, 36, 20, 6, 6);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(Color.WHITE);
        FontMetrics fmM = g2.getFontMetrics();
        g2.drawString(min, x + 8 + (36 - fmM.stringWidth(min)) / 2, y + 24);

        // Texto
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(TEXT_WHITE);
        g2.drawString(desc, x + 52, y + 24);
    }

    private static void drawPitchPlayer(Graphics2D g2, int x, int y, String num, String name, Color col) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillOval(x - 14, y - 14, 28, 28);
        g2.setColor(col);
        g2.fillOval(x - 12, y - 12, 24, 24);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x - 12, y - 12, 24, 24);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fmN = g2.getFontMetrics();
        g2.drawString(num, x - fmN.stringWidth(num) / 2, y + 4);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(Color.WHITE);
        FontMetrics fmNm = g2.getFontMetrics();
        g2.drawString(name, x - fmNm.stringWidth(name) / 2, y + 22);
    }

    private static void drawSectionHeader(Graphics2D g2, int x, int y, int w, String title, Color pillCol) {
        g2.setColor(pillCol);
        g2.fillRoundRect(x, y, 4, 18, 2, 2);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(TEXT_WHITE);
        g2.drawString(title, x + 12, y + 14);
    }

    private static void drawActionBadge(Graphics2D g2, int x, int y, int w, int h, String text, Color col) {
        RoundRectangle2D.Float r = new RoundRectangle2D.Float(x, y, w, h, 8, 8);
        g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 35));
        g2.fill(r);
        g2.setColor(col);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(r);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (w - fm.stringWidth(text)) / 2, y + (h - fm.getHeight()) / 2 + fm.getAscent());
    }

    private static void drawMockCrest(Graphics2D g2, int x, int y, int w, int h, Color col, String label) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillOval(x + 2, y + 2, w, h);
        g2.setColor(col);
        g2.fillOval(x, y, w, h);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(x, y, w, h);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + h / 2 + 4);
    }
}
