package mod.brasfoot;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

/**
 * Gerenciador Mestre da Tela de Partida ao Vivo (a/dV).
 * Padrão EA FC Broadcast Match Center (1740 x 880 Widescreen Full HD):
 * 1. Top Scorebug de Transmissão:
 *    - Escudos e Nomes em destaque
 *    - Placar de LED Digital Centralizado
 *    - Autores dos gols com ícone ⚽ e minutos
 *    - Cronômetro Contínuo de 90 Minutos com barra de progresso suave
 *    - Botão estilizado para Pausar e abrir a tela de treinador (a/cD)
 * 2. Coluna Esquerda:
 *    - Estatísticas ao Vivo Comparativas (Posse, Finalizações, No Alvo, Faltas, Escanteios, Cartões, Desarmes)
 *    - Linha do Tempo / Feed de Eventos da Partida
 * 3. Coluna Central:
 *    - Mini-campo moderno com formações táticas ao vivo
 *    - Indicador do momento do jogo e setor de pressão
 * 4. Coluna Direita:
 *    - Tabela de Classificação ao Vivo (atualizada em tempo real a cada gol da rodada)
 *    - Cards de Placar Limpos dos outros jogos (apenas Escudos, Siglas, Placar e Minuto, sem estádio/público)
 */
public class LiveMatchHudManager {

    public static final int WIN_WIDTH = 1740;
    public static final int WIN_HEIGHT = 880;

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

    // Estado e referências
    private static Object currentDV = null;
    private static JPanel hudRoot = null;

    // Painéis Principais
    private static JPanel scorebugPanel = null;
    private static JPanel statsPanel = null;
    private static JPanel pitchPanel = null;
    private static JPanel rightPanel = null;

    // Componentes do Scorebug
    private static JLabel lblHomeName = null;
    private static JLabel lblAwayName = null;
    private static JLabel lblHomeCrest = null;
    private static JLabel lblAwayCrest = null;
    private static JLabel lblScore = null;
    private static JLabel lblHomeScorers = null;
    private static JLabel lblAwayScorers = null;
    private static JProgressBar pbMatchTime = null;
    private static JLabel lblMatchMinute = null;
    private static JButton btnPause = null;

    // Componentes de Estatísticas
    private static LiveStatsView statsView = null;
    private static JPanel timelinePanel = null;

    // Componentes da Coluna Direita
    private static boolean showTableTab = true;
    private static JPanel tablePanel = null;
    private static JPanel otherMatchesPanel = null;
    private static JButton btnTabTable = null;
    private static JButton btnTabMatches = null;

    // Cache de eventos para a timeline
    private static final List<String[]> eventFeed = new ArrayList<String[]>();
    private static int lastProcessedEventCount = -1;

    public static void setupHud(final JPanel dVPanel) {
        if (dVPanel == null) return;
        currentDV = dVPanel;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    buildHud(dVPanel);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    private static void buildHud(JPanel dVPanel) {
        dVPanel.setSize(WIN_WIDTH, WIN_HEIGHT);
        dVPanel.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
        dVPanel.setBackground(BG_DEEP);
        dVPanel.setLayout(null);

        // Oculta componentes legados nativos do Brasfoot
        hideLegacyComponent(dVPanel, "Hr"); // Painel do cabeçalho antigo
        hideLegacyComponent(dVPanel, "Hq"); // Painel de fundo antigo
        hideLegacyComponent(dVPanel, "ut"); // JScrollPane da tabela antiga de jogos
        hideLegacyComponent(dVPanel, "Hf"); // Barra de progresso antiga
        hideLegacyComponent(dVPanel, "uh"); // Label antiga de tempo

        if (hudRoot == null || hudRoot.getParent() != dVPanel) {
            hudRoot = new JPanel(null);
            hudRoot.setOpaque(false);
            hudRoot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT);
            dVPanel.add(hudRoot);
        } else {
            hudRoot.removeAll();
        }

        eventFeed.clear();
        lastProcessedEventCount = -1;

        // 1. Top Header: Scorebug de Transmissão (20, 12, 1700, 96)
        buildScorebug(hudRoot);

        // 2. Coluna Esquerda: Estatísticas e Feed (20, 120, 440, 740)
        buildLeftColumn(hudRoot);

        // 3. Coluna Central: Campo Tático & Momento (480, 120, 680, 740)
        buildCenterColumn(hudRoot);

        // 4. Coluna Direita: Tabela ao Vivo e Outros Jogos (1180, 120, 540, 740)
        buildRightColumn(hudRoot);

        refreshAllData();

        dVPanel.revalidate();
        dVPanel.repaint();
    }

    private static void buildScorebug(JPanel parent) {
        scorebugPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float card = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PANEL_DARK);
                g2.fill(card);
                g2.setColor(BORDER_SLATE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(card);
                g2.dispose();
            }
        };
        scorebugPanel.setOpaque(false);
        scorebugPanel.setBounds(20, 12, WIN_WIDTH - 40, 96);

        int cx = (WIN_WIDTH - 40) / 2;
        int sbw = 260;
        int sbh = 52;
        int sbx = cx - sbw / 2;
        int sby = 12;

        // Placar Central Box
        JPanel scoreBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float r = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                GradientPaint gp = new GradientPaint(0, 0, new Color(14, 20, 32), 0, getHeight(), new Color(24, 33, 52));
                g2.setPaint(gp);
                g2.fill(r);
                g2.setColor(new Color(59, 130, 246, 140));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(r);
                g2.dispose();
            }
        };
        scoreBox.setOpaque(false);
        scoreBox.setBounds(sbx, sby, sbw, sbh);

        lblScore = new JLabel("0   -   0", SwingConstants.CENTER);
        lblScore.setFont(new Font("Impact", Font.PLAIN, 36));
        lblScore.setForeground(TEXT_WHITE);
        scoreBox.add(lblScore, BorderLayout.CENTER);
        scorebugPanel.add(scoreBox);

        // Time Mandante
        lblHomeName = new JLabel("MANDANTE", SwingConstants.RIGHT);
        lblHomeName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHomeName.setForeground(TEXT_WHITE);
        lblHomeName.setBounds(sbx - 360, sby + 4, 300, 32);
        scorebugPanel.add(lblHomeName);

        lblHomeCrest = new JLabel("", SwingConstants.CENTER);
        lblHomeCrest.setBounds(sbx - 52, sby + 2, 44, 44);
        scorebugPanel.add(lblHomeCrest);

        lblHomeScorers = new JLabel("", SwingConstants.RIGHT);
        lblHomeScorers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHomeScorers.setForeground(new Color(160, 185, 215));
        lblHomeScorers.setBounds(sbx - 360, sby + 36, 350, 18);
        scorebugPanel.add(lblHomeScorers);

        // Time Visitante
        lblAwayCrest = new JLabel("", SwingConstants.CENTER);
        lblAwayCrest.setBounds(sbx + sbw + 8, sby + 2, 44, 44);
        scorebugPanel.add(lblAwayCrest);

        lblAwayName = new JLabel("VISITANTE", SwingConstants.LEFT);
        lblAwayName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAwayName.setForeground(TEXT_WHITE);
        lblAwayName.setBounds(sbx + sbw + 60, sby + 4, 300, 32);
        scorebugPanel.add(lblAwayName);

        lblAwayScorers = new JLabel("", SwingConstants.LEFT);
        lblAwayScorers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAwayScorers.setForeground(new Color(160, 185, 215));
        lblAwayScorers.setBounds(sbx + sbw + 8, sby + 36, 350, 18);
        scorebugPanel.add(lblAwayScorers);

        // Barra de Progresso Contínua de 90 Minutos
        int barW = 320;
        int barH = 7;
        int barX = cx - barW / 2;
        int barY = sby + sbh + 8;

        pbMatchTime = new JProgressBar(0, 90) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(30, 42, 65));
                g2.fillRoundRect(0, 0, w, h, 4, 4);

                double ratio = Math.min(1.0, (double) getValue() / Math.max(1, getMaximum()));
                int pw = (int) (w * ratio);
                if (pw > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, EMERALD, pw, 0, new Color(52, 211, 153));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, pw, h, 4, 4);
                }
                g2.dispose();
            }
        };
        pbMatchTime.setOpaque(false);
        pbMatchTime.setBorder(null);
        pbMatchTime.setBounds(barX, barY, barW, barH);
        scorebugPanel.add(pbMatchTime);

        lblMatchMinute = new JLabel("0' 1º TEMPO", SwingConstants.CENTER);
        lblMatchMinute.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMatchMinute.setForeground(EMERALD);
        lblMatchMinute.setBounds(cx - 150, barY + 10, 300, 16);
        scorebugPanel.add(lblMatchMinute);

        // Botão PAUSAR JOGO
        btnPause = new JButton("PAUSAR JOGO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                boolean hover = getModel().isRollover();
                RoundRectangle2D.Float r = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(hover ? new Color(245, 158, 11, 60) : new Color(245, 158, 11, 35));
                g2.fill(r);
                g2.setColor(GOLD_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(r);
                g2.setFont(getFont());
                g2.setColor(TEXT_WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        btnPause.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPause.setOpaque(false);
        btnPause.setContentAreaFilled(false);
        btnPause.setBorderPainted(false);
        btnPause.setFocusPainted(false);
        btnPause.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPause.setBounds(WIN_WIDTH - 40 - 160, 28, 140, 40);
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triggerPauseMatch();
            }
        });
        scorebugPanel.add(btnPause);

        parent.add(scorebugPanel);
    }

    private static void buildLeftColumn(JPanel parent) {
        statsPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float card = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PANEL_DARK);
                g2.fill(card);
                g2.setColor(BORDER_SLATE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(card);
                g2.dispose();
            }
        };
        statsPanel.setOpaque(false);
        statsPanel.setBounds(20, 120, 440, 740);

        // Header Estatísticas
        JLabel lblHeaderStats = createSectionHeader("ESTATÍSTICAS AO VIVO", BLUE_ACCENT);
        lblHeaderStats.setBounds(16, 16, 400, 24);
        statsPanel.add(lblHeaderStats);

        // Visualizador de Barras de Estatísticas
        statsView = new LiveStatsView();
        statsView.setBounds(16, 48, 408, 330);
        statsPanel.add(statsView);

        // Divisor
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(45, 60, 88, 120));
        sep.setBounds(16, 388, 408, 2);
        statsPanel.add(sep);

        // Header Linha do Tempo
        JLabel lblHeaderFeed = createSectionHeader("LINHA DO TEMPO DA PARTIDA", EMERALD);
        lblHeaderFeed.setBounds(16, 400, 400, 24);
        statsPanel.add(lblHeaderFeed);

        // Container de Linha do Tempo
        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setOpaque(false);

        JScrollPane spFeed = new JScrollPane(timelinePanel);
        spFeed.setOpaque(false);
        spFeed.getViewport().setOpaque(false);
        spFeed.setBorder(null);
        spFeed.setBounds(16, 432, 408, 290);
        spFeed.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        statsPanel.add(spFeed);

        parent.add(statsPanel);
    }

    private static void buildCenterColumn(JPanel parent) {
        pitchPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float card = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PANEL_DARK);
                g2.fill(card);
                g2.setColor(BORDER_SLATE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(card);

                // Gramado Estilizado Central
                int pw = getWidth() - 40;
                int ph = 520;
                int px = 20;
                int py = 54;

                RoundRectangle2D.Float pitchBox = new RoundRectangle2D.Float(px, py, pw, ph, 14, 14);
                GradientPaint pitchGrad = new GradientPaint(px, py, new Color(18, 55, 28), px, py + ph, new Color(24, 75, 38));
                g2.setPaint(pitchGrad);
                g2.fill(pitchBox);

                // Linhas do campo
                g2.setColor(new Color(255, 255, 255, 65));
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(pitchBox);
                g2.drawLine(px, py + ph / 2, px + pw, py + ph / 2);
                g2.drawOval(px + pw / 2 - 50, py + ph / 2 - 50, 100, 100);
                g2.fillOval(px + pw / 2 - 4, py + ph / 2 - 4, 8, 8);
                g2.drawRect(px + pw / 2 - 110, py, 220, 80);
                g2.drawRect(px + pw / 2 - 110, py + ph - 80, 220, 80);

                // Jogadores em campo (posicionados simbolicamente)
                drawPitchPlayer(g2, px + 80, py + 120, "10", BLUE_ACCENT);
                drawPitchPlayer(g2, px + pw / 2, py + 100, "9", BLUE_ACCENT);
                drawPitchPlayer(g2, px + pw - 80, py + 120, "11", BLUE_ACCENT);
                drawPitchPlayer(g2, px + pw / 2, py + 40, "1", BLUE_ACCENT);

                drawPitchPlayer(g2, px + pw / 2, py + ph - 110, "9", RED_ACCENT);
                drawPitchPlayer(g2, px + 100, py + ph - 130, "14", RED_ACCENT);
                drawPitchPlayer(g2, px + pw - 100, py + ph - 130, "7", RED_ACCENT);
                drawPitchPlayer(g2, px + pw / 2, py + ph - 40, "1", RED_ACCENT);

                // Card de Análise de Momento na base
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
                g2.drawString("Controle equilibrado da posse e movimentação tática no meio de campo.", px + 16, by + 50);
                g2.drawString("Disputa intensa nas intermediárias e transições velozes pelas alas.", px + 16, by + 72);
                g2.drawString("Formações: 4-3-3 vs 4-4-2", px + 16, by + 96);

                g2.dispose();
            }
        };
        pitchPanel.setOpaque(false);
        pitchPanel.setBounds(480, 120, 680, 740);

        JLabel lblHeaderPitch = createSectionHeader("MOMENTO DO JOGO & FORMAÇÃO TÁTICA", GOLD_ACCENT);
        lblHeaderPitch.setBounds(16, 16, 600, 24);
        pitchPanel.add(lblHeaderPitch);

        parent.add(pitchPanel);
    }

    private static void buildRightColumn(JPanel parent) {
        rightPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float card = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(PANEL_DARK);
                g2.fill(card);
                g2.setColor(BORDER_SLATE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(card);
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBounds(1180, 120, 540, 740);

        int tabW = (540 - 38) / 2;
        int tabH = 34;

        btnTabTable = createTabButton("🏆 TABELA AO VIVO", true);
        btnTabTable.setBounds(16, 16, tabW, tabH);
        btnTabTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTableTab = true;
                updateTabViews();
            }
        });
        rightPanel.add(btnTabTable);

        btnTabMatches = createTabButton("⚽ OUTROS JOGOS", false);
        btnTabMatches.setBounds(16 + tabW + 6, 16, tabW, tabH);
        btnTabMatches.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTableTab = false;
                updateTabViews();
            }
        });
        rightPanel.add(btnTabMatches);

        // Painel 1: Tabela ao Vivo (Metade superior no modo misto ou alternada)
        tablePanel = new LiveTablePanel();
        tablePanel.setBounds(16, 58, 508, 340);
        rightPanel.add(tablePanel);

        // Painel 2: Outros Jogos (Cards limpos)
        otherMatchesPanel = new LiveMatchesPanel();
        otherMatchesPanel.setBounds(16, 410, 508, 310);
        rightPanel.add(otherMatchesPanel);

        parent.add(rightPanel);
    }

    private static void updateTabViews() {
        if (btnTabTable != null) {
            btnTabTable.setBackground(showTableTab ? BLUE_ACCENT : CARD_BG);
            btnTabTable.setForeground(showTableTab ? Color.WHITE : TEXT_MUTED);
        }
        if (btnTabMatches != null) {
            btnTabMatches.setBackground(!showTableTab ? BLUE_ACCENT : CARD_BG);
            btnTabMatches.setForeground(!showTableTab ? Color.WHITE : TEXT_MUTED);
        }
        if (tablePanel != null && otherMatchesPanel != null) {
            if (showTableTab) {
                tablePanel.setBounds(16, 58, 508, 662);
                tablePanel.setVisible(true);
                otherMatchesPanel.setVisible(false);
            } else {
                otherMatchesPanel.setBounds(16, 58, 508, 662);
                tablePanel.setVisible(false);
                otherMatchesPanel.setVisible(true);
            }
            rightPanel.revalidate();
            rightPanel.repaint();
        }
    }

    public static void onTick(JPanel dVPanel) {
        if (currentDV == null) currentDV = dVPanel;
        refreshAllData();
    }

    public static void onMatchUpdated(JPanel dVPanel) {
        if (currentDV == null) currentDV = dVPanel;
        refreshAllData();
    }

    public static void onEventOrGoal(JPanel dVPanel) {
        if (currentDV == null) currentDV = dVPanel;
        refreshAllData();
    }

    private static void refreshAllData() {
        if (currentDV == null) return;
        try {
            int gw = getIntField(currentDV, "GW");
            ArrayList list = (ArrayList) getField(currentDV, "vK");
            if (list == null || gw < 0 || gw >= list.size()) return;

            components.v comp = (components.v) list.get(gw);
            if (comp == null || comp.tR() == null) return;
            best.I match = comp.tR();

            // 1. Atualiza Placar e Nomes
            if (lblHomeName != null && match.hc() != null) {
                lblHomeName.setText(match.hc().getNome().toUpperCase());
                if (lblHomeCrest != null) lblHomeCrest.setIcon(match.hc().x(44, 44));
            }
            if (lblAwayName != null && match.hd() != null) {
                lblAwayName.setText(match.hd().getNome().toUpperCase());
                if (lblAwayCrest != null) lblAwayCrest.setIcon(match.hd().x(44, 44));
            }

            int hG = match.hu();
            int aG = match.hw();
            if (lblScore != null) {
                lblScore.setText(hG + "   -   " + aG);
            }

            // Marcadores de Gols
            JLabel natHj = (JLabel) getField(currentDV, "Hj");
            JLabel natHk = (JLabel) getField(currentDV, "Hk");
            if (lblHomeScorers != null && natHj != null) {
                String s = natHj.getText();
                lblHomeScorers.setText(s != null && !s.trim().isEmpty() ? "⚽ " + s.trim() : "");
            }
            if (lblAwayScorers != null && natHk != null) {
                String s = natHk.getText();
                lblAwayScorers.setText(s != null && !s.trim().isEmpty() ? "⚽ " + s.trim() : "");
            }

            // Minuto e Período
            int curMin = getStaticIntField(currentDV.getClass(), "GT");
            int period = getStaticIntField(currentDV.getClass(), "dB");
            JLabel natUh = (JLabel) getField(currentDV, "uh");
            String periodText = (natUh != null && natUh.getText() != null) ? natUh.getText() : (period == 1 ? "1º TEMPO" : "2º TEMPO");

            if (pbMatchTime != null) {
                pbMatchTime.setValue(Math.min(90, curMin));
            }
            if (lblMatchMinute != null) {
                lblMatchMinute.setText(curMin + "' " + periodText.toUpperCase());
            }

            // 2. Atualiza Estatísticas
            if (statsView != null) {
                statsView.updateStats(match);
            }

            // 3. Atualiza Feed de Eventos
            updateFeed(match, curMin);

            // 4. Atualiza Tabela e Outros Jogos
            if (tablePanel instanceof LiveTablePanel) {
                ((LiveTablePanel) tablePanel).refreshTable();
            }
            if (otherMatchesPanel instanceof LiveMatchesPanel) {
                ((LiveMatchesPanel) otherMatchesPanel).refreshMatches();
            }

        } catch (Throwable t) {
            // Silencioso em updates rápidos
        }
    }

    private static void updateFeed(best.I match, int curMin) {
        if (timelinePanel == null || match == null) return;
        try {
            ArrayList goals = match.hn();
            int totalEvents = (goals != null ? goals.size() : 0);
            if (totalEvents != lastProcessedEventCount) {
                lastProcessedEventCount = totalEvents;
                timelinePanel.removeAll();

                // Reconstrói itens de feed mais recentes no topo
                if (goals != null) {
                    for (int i = goals.size() - 1; i >= 0; i--) {
                        Object gObj = goals.get(i);
                        timelinePanel.add(createFeedCard(curMin + "'", "⚽ " + String.valueOf(gObj), EMERALD));
                        timelinePanel.add(Box.createVerticalStrut(6));
                    }
                }
                timelinePanel.revalidate();
                timelinePanel.repaint();
            }
        } catch (Throwable ignored) {}
    }

    private static JPanel createFeedCard(String minute, String text, Color accent) {
        JPanel p = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float r = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(CARD_BG);
                g2.fill(r);
                g2.setColor(BORDER_SLATE);
                g2.draw(r);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(390, 36));
        p.setMaximumSize(new Dimension(410, 36));

        JLabel lblM = new JLabel(minute, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(accent);
                g2.fillRoundRect(2, 6, getWidth() - 4, getHeight() - 12, 6, 6);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + 1);
                g2.dispose();
            }
        };
        lblM.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblM.setPreferredSize(new Dimension(46, 36));
        p.add(lblM, BorderLayout.WEST);

        JLabel lblT = new JLabel(text);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblT.setForeground(TEXT_WHITE);
        p.add(lblT, BorderLayout.CENTER);

        return p;
    }

    private static void triggerPauseMatch() {
        if (currentDV == null) return;
        try {
            int gw = getIntField(currentDV, "GW");
            Method m = currentDV.getClass().getDeclaredMethod("e", int.class, boolean.class);
            m.setAccessible(true);
            m.invoke(currentDV, gw, false);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void hideLegacyComponent(JPanel panel, String fieldName) {
        try {
            Field f = panel.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Component c = (Component) f.get(panel);
            if (c != null) {
                c.setVisible(false);
                c.setBounds(0, 0, 0, 0);
            }
        } catch (Throwable ignored) {}
    }

    private static JLabel createSectionHeader(String title, Color pillCol) {
        JLabel l = new JLabel(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(pillCol);
                g2.fillRoundRect(0, 3, 4, getHeight() - 6, 2, 2);
                g2.setFont(getFont());
                g2.setColor(TEXT_WHITE);
                g2.drawString(getText(), 12, (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private static JButton createTabButton(final String text, final boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                RoundRectangle2D.Float r = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(getBackground());
                g2.fill(r);
                g2.setColor(BORDER_SLATE);
                g2.draw(r);
                g2.setFont(getFont());
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(active ? BLUE_ACCENT : CARD_BG);
        btn.setForeground(active ? Color.WHITE : TEXT_MUTED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static void drawPitchPlayer(Graphics2D g2, int x, int y, String num, Color col) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillOval(x - 14, y - 14, 28, 28);
        g2.setColor(col);
        g2.fillOval(x - 12, y - 12, 24, 24);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x - 12, y - 12, 24, 24);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(num, x - fm.stringWidth(num) / 2, y + 4);
    }

    private static Object getField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static int getIntField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private static int getStaticIntField(Class<?> cls, String name) {
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f.getInt(null);
        } catch (Throwable ignored) {
            return 0;
        }
    }

    // ========================================================================
    // PAINEL DE ESTATÍSTICAS COMPARATIVAS
    // ========================================================================
    private static class LiveStatsView extends JPanel {
        private int posH = 50, posA = 50;
        private int shotH = 0, shotA = 0;
        private int onH = 0, onA = 0;
        private int cornH = 0, cornA = 0;
        private int foulH = 0, foulA = 0;
        private int yelH = 0, yelA = 0;
        private int tklH = 0, tklA = 0;

        public LiveStatsView() {
            setOpaque(false);
        }

        public void updateStats(best.I match) {
            if (match == null) return;
            try {
                int[] p = match.hz();
                if (p != null && p.length >= 2) {
                    posH = p[0];
                    posA = p[1];
                }
                int[] s = match.hB();
                if (s != null && s.length >= 2) {
                    shotH = s[0];
                    shotA = s[1];
                }
                int[] on = match.hC();
                if (on != null && on.length >= 2) {
                    onH = on[0];
                    onA = on[1];
                }
                int[] t = match.hY();
                if (t != null && t.length >= 2) {
                    tklH = t[0];
                    tklA = t[1];
                }
                int[] f = match.ia();
                if (f != null && f.length >= 2) {
                    foulH = f[0];
                    foulA = f[1];
                }
                repaint();
            } catch (Throwable ignored) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            EaFcUiHelper.applyHighQualityRendering(g2);

            int w = getWidth();
            int rowH = 44;
            int sy = 0;

            drawStatBar(g2, 0, sy + 0 * rowH, w, "Posse de Bola", posH + "%", posA + "%", posH, posA, BLUE_ACCENT, RED_ACCENT);
            drawStatBar(g2, 0, sy + 1 * rowH, w, "Finalizações", String.valueOf(shotH), String.valueOf(shotA), shotH, shotA, BLUE_ACCENT, RED_ACCENT);
            drawStatBar(g2, 0, sy + 2 * rowH, w, "Chutes no Gol", String.valueOf(onH), String.valueOf(onA), onH, onA, BLUE_ACCENT, RED_ACCENT);
            drawStatBar(g2, 0, sy + 3 * rowH, w, "Faltas Cometidas", String.valueOf(foulH), String.valueOf(foulA), foulH, foulA, BLUE_ACCENT, RED_ACCENT);
            drawStatBar(g2, 0, sy + 4 * rowH, w, "Desarmes", String.valueOf(tklH), String.valueOf(tklA), tklH, tklA, BLUE_ACCENT, RED_ACCENT);

            g2.dispose();
        }

        private void drawStatBar(Graphics2D g2, int x, int y, int w, String title, String valH, String valA, int numH, int numA, Color colH, Color colA) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(TEXT_MUTED);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, x + (w - fm.stringWidth(title)) / 2, y + 13);

            g2.setColor(colH);
            g2.drawString(valH, x + 4, y + 13);

            g2.setColor(colA);
            FontMetrics fmA = g2.getFontMetrics();
            g2.drawString(valA, x + w - 4 - fmA.stringWidth(valA), y + 13);

            int barH = 5;
            int barY = y + 20;
            int halfW = (w - 20) / 2;

            int total = Math.max(1, numH + numA);
            int hW = (int) (halfW * ((double) numH / total));
            g2.setColor(new Color(30, 42, 65));
            g2.fillRoundRect(x + 4, barY, halfW, barH, 3, 3);
            g2.setColor(colH);
            g2.fillRoundRect(x + 4 + (halfW - hW), barY, hW, barH, 3, 3);

            int aW = (int) (halfW * ((double) numA / total));
            g2.setColor(new Color(30, 42, 65));
            g2.fillRoundRect(x + 12 + halfW, barY, halfW, barH, 3, 3);
            g2.setColor(colA);
            g2.fillRoundRect(x + 12 + halfW, barY, aW, barH, 3, 3);
        }
    }

    // ========================================================================
    // PAINEL DE TABELA DE CLASSIFICAÇÃO AO VIVO
    // ========================================================================
    private static class LiveTablePanel extends JPanel {
        public LiveTablePanel() {
            setOpaque(false);
        }

        public void refreshTable() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            EaFcUiHelper.applyHighQualityRendering(g2);

            int w = getWidth();
            int h = getHeight();

            RoundRectangle2D.Float box = new RoundRectangle2D.Float(0, 0, w, h, 12, 12);
            g2.setColor(CARD_BG);
            g2.fill(box);
            g2.setColor(BORDER_SLATE);
            g2.draw(box);

            // Cabeçalho das Colunas
            g2.setColor(new Color(16, 22, 35));
            g2.fillRoundRect(2, 2, w - 4, 26, 10, 10);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(new Color(148, 163, 184));
            g2.drawString("POS", 10, 18);
            g2.drawString("CLUBE", 44, 18);
            g2.drawString("PTS", w - 170, 18);
            g2.drawString("J", w - 135, 18);
            g2.drawString("V", w - 105, 18);
            g2.drawString("SG", w - 75, 18);

            // Obtém classificação
            try {
                best.at champ = (best.at) getStaticField(currentDV.getClass(), "Hb");
                if (champ != null) {
                    best.L[] rows = champ.mB();
                    if (rows != null && rows.length > 0) {
                        int ry = 34;
                        for (int i = 0; i < Math.min(18, rows.length); i++) {
                            best.L r = rows[i];
                            if (r == null) continue;

                            boolean isUser = (r.getNome() != null && r.getNome().contains("*"));
                            if (isUser) {
                                g2.setColor(new Color(59, 130, 246, 50));
                                g2.fillRoundRect(4, ry - 3, w - 8, 24, 6, 6);
                                g2.setColor(BLUE_ACCENT);
                                g2.drawRoundRect(4, ry - 3, w - 8, 24, 6, 6);
                            } else if (i % 2 == 1) {
                                g2.setColor(new Color(255, 255, 255, 5));
                                g2.fillRect(4, ry - 3, w - 8, 24);
                            }

                            if (i < 4) {
                                g2.setColor(EMERALD);
                                g2.fillRoundRect(6, ry + 2, 3, 14, 2, 2);
                            }

                            g2.setFont(new Font("Segoe UI", isUser ? Font.BOLD : Font.PLAIN, 11));
                            g2.setColor(isUser ? Color.WHITE : TEXT_WHITE);
                            g2.drawString(String.valueOf(i + 1), 14, ry + 14);

                            String tName = r.getNome();
                            if (tName != null) {
                                tName = tName.replace("*", "").trim();
                                if (tName.length() > 18) tName = tName.substring(0, 18);
                            }
                            g2.drawString(tName != null ? tName : "", 44, ry + 14);

                            ry += 26;
                        }
                    }
                }
            } catch (Throwable ignored) {}

            g2.dispose();
        }

        private Object getStaticField(Class<?> cls, String name) {
            try {
                Field f = cls.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(null);
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    // ========================================================================
    // PAINEL DE CARDS LIMPOS DOS OUTROS JOGOS DA RODADA
    // ========================================================================
    private static class LiveMatchesPanel extends JPanel {
        public LiveMatchesPanel() {
            setOpaque(false);
        }

        public void refreshMatches() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            EaFcUiHelper.applyHighQualityRendering(g2);

            int w = getWidth();
            try {
                ArrayList list = (ArrayList) getField(currentDV, "vK");
                int userGW = getIntField(currentDV, "GW");

                if (list != null) {
                    int cardW = (w - 12) / 2;
                    int cardH = 46;
                    int count = 0;

                    for (int i = 0; i < list.size(); i++) {
                        if (i == userGW) continue; // Pula o jogo do próprio usuário
                        components.v comp = (components.v) list.get(i);
                        if (comp == null || comp.tR() == null) continue;
                        best.I m = comp.tR();

                        int col = count % 2;
                        int row = count / 2;
                        int cx = col * (cardW + 10);
                        int cy = row * (cardH + 8);

                        drawSingleMatchCard(g2, cx, cy, cardW, cardH, m);
                        count++;
                        if (count >= 12) break;
                    }
                }
            } catch (Throwable ignored) {}

            g2.dispose();
        }

        private void drawSingleMatchCard(Graphics2D g2, int x, int y, int w, int h, best.I m) {
            RoundRectangle2D.Float rc = new RoundRectangle2D.Float(x, y, w, h, 8, 8);
            g2.setColor(CARD_BG);
            g2.fill(rc);
            g2.setColor(BORDER_SLATE);
            g2.draw(rc);

            String hN = (m.hc() != null) ? m.hc().jY() : "HOM";
            String aN = (m.hd() != null) ? m.hd().jY() : "AWA";
            if (hN == null || hN.isEmpty()) hN = (m.hc() != null) ? m.hc().getNome().substring(0, Math.min(3, m.hc().getNome().length())).toUpperCase() : "HOM";
            if (aN == null || aN.isEmpty()) aN = (m.hd() != null) ? m.hd().getNome().substring(0, Math.min(3, m.hd().getNome().length())).toUpperCase() : "AWA";

            // Escudo Home
            if (m.hc() != null) {
                ImageIcon c = m.hc().x(24, 24);
                if (c != null) c.paintIcon(null, g2, x + 6, y + 11);
            }
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(TEXT_WHITE);
            g2.drawString(hN, x + 34, y + 27);

            // Placar Central
            String sc = m.hu() + " - " + m.hw();
            g2.setFont(new Font("Impact", Font.PLAIN, 15));
            g2.setColor(EMERALD);
            FontMetrics fmS = g2.getFontMetrics();
            g2.drawString(sc, x + w / 2 - fmS.stringWidth(sc) / 2, y + 28);

            // Escudo e Nome Away
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(TEXT_WHITE);
            FontMetrics fmA = g2.getFontMetrics();
            g2.drawString(aN, x + w - 34 - fmA.stringWidth(aN), y + 27);
            if (m.hd() != null) {
                ImageIcon c = m.hd().x(24, 24);
                if (c != null) c.paintIcon(null, g2, x + w - 30, y + 11);
            }
        }
    }
}
