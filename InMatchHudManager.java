package mod.brasfoot;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Gerenciador Mestre da Tela de Treinador Durante a Partida (cD).
 * Refatoração Widescreen Full HD (1740 x 880) - EA FC Console HUD:
 * - Dois campos simultâneos (Mandante à esquerda, Visitante à direita)
 * - Ambos os campos utilizando o mesmo gramado selecionado no pré-jogo
 * - Fundos dos bancos de reservas na cor pura da interface (sem textura verde escura)
 * - Placar estilo Broadcast TV com Estádio e Público integrados
 * - Radar Tático Duplo Comparativo Sobreposto (sem info interna no centro e sem barras laterais)
 * - Botão "< VOLTAR AO JOGO" centralizado e harmonioso
 */
public class InMatchHudManager {

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

    // Componentes Customizados Integrados
    private static InMatchStatsPanel statsPanel = null;
    private static InMatchDualRadarPanel radarPanel = null;
    private static JPanel scoreboardCard = null;
    private static JLabel lblScoreTimePeriod = null;
    private static JLabel lblScoreStadiumAtt = null;
    private static JLabel lblScoreNameH = null;
    private static JLabel lblScoreNameA = null;
    private static JLabel lblScoreCrestH = null;
    private static JLabel lblScoreCrestA = null;

    private static JPanel oppPreviewPanel = null;
    private static JPanel oppPitchComp = null;
    private static JLabel oppBenchLabel = null;
    private static Image currentPitchBg = null;
    private static Image currentOppPlayersImg = null;

    private static JLabel lblHomeHeader = null;
    private static JLabel lblAwayHeader = null;
    private static JPanel userTacticsCard = null;
    private static JPanel oppInfoCard = null;
    private static JLabel lblOppCoach = null;
    private static JLabel lblOppFormation = null;
    private static JLabel lblOppEstilo = null;
    private static JLabel lblOppMarcacao = null;
    private static JLabel lblOppSubs = null;

    /**
     * Configura todo o layout widescreen da tela de treinador durante a partida.
     */
    public static void setupHud(Object cDObj) {
        if (cDObj == null) return;
        try {
            JPanel root = (JPanel) cDObj;

            // 1. Redimensiona o JDialog pai
            JDialog dialog = (JDialog) getFieldValue(cDObj, "ub");
            if (dialog != null) {
                dialog.setSize(WIN_WIDTH, WIN_HEIGHT);
                dialog.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
                dialog.setLocationRelativeTo(null);
            }

            root.setBackground(BG_DEEP);
            root.setLayout(null);
            root.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));

            // Reseta referências cacheadas da sessão anterior para evitar reutilizar componentes de diálogo destruído
            userTacticsCard = null;
            oppInfoCard = null;
            scoreboardCard = null;
            statsPanel = null;
            radarPanel = null;
            oppPreviewPanel = null;
            oppPitchComp = null;
            oppBenchLabel = null;
            lblHomeHeader = null;
            lblAwayHeader = null;
            lblOppCoach = null;
            lblOppFormation = null;
            lblOppEstilo = null;
            lblOppMarcacao = null;
            lblOppSubs = null;

            // Componentes nativos de cD
            components.v matchComp = (components.v) getFieldValue(cDObj, "Db");
            best.I match = (matchComp != null) ? matchComp.tR() : null;

            JLayeredPane df = (JLayeredPane) getFieldValue(cDObj, "DF");
            JLabel dsPitch = (JLabel) getFieldValue(cDObj, "Ds");
            JLabel dqBench = (JLabel) getFieldValue(cDObj, "Dq");

            JLabel scoreHome = (JLabel) getFieldValue(cDObj, "Dw");
            JLabel scoreAway = (JLabel) getFieldValue(cDObj, "Dx");
            JLabel matchTime = (JLabel) getFieldValue(cDObj, "Dv");   // Dv é o tempo real (ex: 46')
            JLabel matchPeriod = (JLabel) getFieldValue(cDObj, "DE"); // DE é o período real (ex: 1º)

            JLabel stadiumLbl = (JLabel) getFieldValue(cDObj, "Cg"); // Cg é o estádio real
            JLabel attLbl = (JLabel) getFieldValue(cDObj, "Dy");     // Dy é o público/torcedores real
            JLabel revLbl = (JLabel) getFieldValue(cDObj, "xI");     // posse de bola antiga
            JLabel formOppLbl = (JLabel) getFieldValue(cDObj, "DC"); // Formação adversária

            JButton btnVoltar = (JButton) getFieldValue(cDObj, "Dm");
            JComboBox cbEstilo = (JComboBox) getFieldValue(cDObj, "Do");
            JComboBox cbMarcacao = (JComboBox) getFieldValue(cDObj, "Dp");
            JComboBox cbAtaque = (JComboBox) getFieldValue(cDObj, "Dn");

            JLabel lblEstilo = (JLabel) getFieldValue(cDObj, "vy");
            JLabel lblMarcacao = (JLabel) getFieldValue(cDObj, "vz");
            JLabel lblAtaque = (JLabel) getFieldValue(cDObj, "vA");

            JLabel subsLbl = (JLabel) getFieldValue(cDObj, "DA");
            JLabel coachLbl = (JLabel) getFieldValue(cDObj, "DD");

            JScrollPane scrollEvents = (JScrollPane) getFieldValue(cDObj, "ut");
            JTable tableEvents = (JTable) getFieldValue(cDObj, "DG");

            int activeTeam = getIntField(cDObj, "Di", 1); // 1 = Casa, 2 = Fora

            // Sincroniza imediatamente o texto de subsLbl com as substituições REAIS do time HUMANO
            int userTeamIndex = (match != null && match.hd() != null && match.hd().jZ() != null && match.hd().jZ().booleanValue()) ? 1 : 0;
            if (subsLbl != null && match != null) {
                int remaining = match.aR(userTeamIndex);
                subsLbl.setText("Substituições restantes: " + remaining);
            }

            // Esconde componentes nativos antigos de fundo ou redundantes
            hideComponent((Component) getFieldValue(cDObj, "vd"));
            hideComponent((Component) getFieldValue(cDObj, "we"));
            hideComponent((Component) getFieldValue(cDObj, "zO")); // banner branco antigo
            hideComponent((Component) getFieldValue(cDObj, "zP")); // banner branco antigo
            hideComponent((Component) getFieldValue(cDObj, "Dl")); // label antiga
            hideComponent((Component) getFieldValue(cDObj, "ug")); // fundo semi antigo
            hideComponent((Component) getFieldValue(cDObj, "Dt")); // jLabel2 antigo
            hideComponent((Component) getFieldValue(cDObj, "Du")); // jLabel2 antigo
            hideComponent((Component) getFieldValue(cDObj, "Dr")); // jLabel2 antigo
            hideComponent((Component) getFieldValue(cDObj, "Dz")); // renda label
            hideComponent(revLbl);

            // =========================================================
            // 2. CABEÇALHOS DOS TIMES (MANDANTE & VISITANTE)
            // =========================================================
            String homeName = (match != null && match.hc() != null) ? match.hc().getNome() : "MANDANTE";
            String awayName = (match != null && match.hd() != null) ? match.hd().getNome() : "VISITANTE";
            ImageIcon homeCrest = (match != null && match.hc() != null) ? match.hc().x(24, 24) : null;
            ImageIcon awayCrest = (match != null && match.hd() != null) ? match.hd().x(24, 24) : null;

            if (lblHomeHeader == null) {
                lblHomeHeader = new JLabel();
                lblHomeHeader.setFont(new Font("Verdana", Font.BOLD, 13));
                lblHomeHeader.setForeground(TEXT_WHITE);
            }
            lblHomeHeader.setText("  " + homeName.toUpperCase());
            lblHomeHeader.setIcon(homeCrest);
            lblHomeHeader.setBounds(20, 8, 620, 28);
            if (!isChildOf(root, lblHomeHeader)) root.add(lblHomeHeader);

            if (lblAwayHeader == null) {
                lblAwayHeader = new JLabel();
                lblAwayHeader.setFont(new Font("Verdana", Font.BOLD, 13));
                lblAwayHeader.setForeground(TEXT_WHITE);
                lblAwayHeader.setHorizontalAlignment(SwingConstants.RIGHT);
            }
            lblAwayHeader.setText(awayName.toUpperCase() + "  ");
            lblAwayHeader.setIcon(awayCrest);
            lblAwayHeader.setHorizontalTextPosition(SwingConstants.LEFT);
            lblAwayHeader.setBounds(1100, 8, 620, 28);
            if (!isChildOf(root, lblAwayHeader)) root.add(lblAwayHeader);

            // =========================================================
            // 3. BANCO E CAMPO DO USUÁRIO
            // =========================================================
            // Remove a textura verde escura de madeira do banco do usuário e limpa texto residual
            if (dqBench != null) {
                dqBench.setIcon(null);
                dqBench.setText("");
                dqBench.setOpaque(true);
                dqBench.setBackground(BG_DEEP);
            }

            // =========================================================
            // 4. CAMPO DO ADVERSÁRIO (COM GRAMADO IDÊNTICO AO DO USUÁRIO)
            // =========================================================
            if (oppPreviewPanel == null) {
                oppPreviewPanel = new JPanel(null);
                oppPreviewPanel.setOpaque(false);

                // Banco de reservas do adversário (fundo puro da interface)
                oppBenchLabel = new JLabel();
                oppBenchLabel.setText("");
                oppBenchLabel.setBounds(0, 0, 210, 650);
                oppBenchLabel.setOpaque(true);
                oppBenchLabel.setBackground(BG_DEEP);
                oppPreviewPanel.add(oppBenchLabel);

                // Campo do adversário: pinta a grama igual ao do usuário + cards dos atletas por cima
                oppPitchComp = new JPanel(null) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        EaFcUiHelper.applyHighQualityRendering(g2);
                        if (currentPitchBg != null) {
                            g2.drawImage(currentPitchBg, 0, 0, getWidth(), getHeight(), null);
                        } else {
                            g2.setColor(new Color(24, 75, 35));
                            g2.fillRect(0, 0, getWidth(), getHeight());
                        }
                        if (currentOppPlayersImg != null) {
                            g2.drawImage(currentOppPlayersImg, 0, 0, null);
                        }
                        g2.dispose();
                    }
                };
                oppPitchComp.setBounds(200, 0, 420, 650);
                oppPitchComp.setOpaque(false);
                oppPreviewPanel.add(oppPitchComp);
            }

            // Pega o gramado oficial de dsPitch
            if (dsPitch != null && dsPitch.getIcon() instanceof ImageIcon) {
                currentPitchBg = ((ImageIcon) dsPitch.getIcon()).getImage();
            }

            if (activeTeam == 1) {
                if (df != null) df.setBounds(20, 38, 620, 650);
                oppPreviewPanel.setBounds(1100, 38, 620, 650);
            } else {
                if (df != null) df.setBounds(1100, 38, 620, 650);
                oppPreviewPanel.setBounds(20, 38, 620, 650);
            }

            if (!isChildOf(root, oppPreviewPanel)) {
                root.add(oppPreviewPanel);
            }

            refreshOpponentPitch(cDObj, activeTeam);

            // =========================================================
            // 5. CONTROLES TÁTICOS (ABAIXO DO CAMPO DO MANDANTE/USUÁRIO)
            // =========================================================
            if (userTacticsCard == null) {
                userTacticsCard = buildUserTacticsCard(subsLbl, coachLbl, lblEstilo, cbEstilo, lblMarcacao, cbMarcacao, lblAtaque, cbAtaque);
            }
            int cardUserX = (activeTeam == 1) ? 20 : 1100;
            userTacticsCard.setBounds(cardUserX, 696, 620, 165);
            if (!isChildOf(root, userTacticsCard)) {
                root.add(userTacticsCard);
            }

            // =========================================================
            // 6. HUB ANALÍTICO CENTRAL (x: 650, w: 440)
            // =========================================================

            // 6.1 Placar Broadcast TV Estilo Real (y: 10, h: 96)
            if (scoreboardCard == null) {
                scoreboardCard = buildScoreboardCard(cDObj, match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            } else {
                updateScoreboardValues(match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            }
            scoreboardCard.setBounds(650, 10, 440, 96);
            if (!isChildOf(root, scoreboardCard)) {
                root.add(scoreboardCard);
            }

            // 6.2 Log de Eventos da Partida (y: 112, h: 180)
            if (scrollEvents != null) {
                scrollEvents.setBounds(650, 112, 440, 180);
                styleEventsTable(scrollEvents, tableEvents);
            }

            // 6.3 Estatísticas Comparativas em Barras (y: 298, h: 180)
            if (statsPanel == null) {
                statsPanel = new InMatchStatsPanel();
            }
            statsPanel.setBounds(650, 298, 440, 180);
            if (match != null) statsPanel.updateStats(match);
            if (!isChildOf(root, statsPanel)) {
                root.add(statsPanel);
            }

            // 6.4 Radar Tático Hexagonal Comparativo Sobreposto (y: 485, h: 295)
            if (radarPanel == null) {
                radarPanel = new InMatchDualRadarPanel();
            }
            radarPanel.setBounds(650, 485, 440, 295);
            updateRadarStats(cDObj, match);
            if (!isChildOf(root, radarPanel)) {
                root.add(radarPanel);
            }

            // 6.5 Botão de Ação "VOLTAR AO JOGO" (y: 794, h: 52)
            if (btnVoltar != null) {
                btnVoltar.setBounds(670, 794, 400, 52);
                EaFcUiHelper.styleActionPillButton(btnVoltar, "< VOLTAR AO JOGO", true);
            }

            // 7. Painel de Resumo Tático do Adversário (abaixo do campo adversário - y: 696, h: 165)
            if (oppInfoCard == null) {
                oppInfoCard = buildOpponentInfoCard(match, formOppLbl);
            } else {
                updateOpponentInfo(match, formOppLbl);
            }
            int cardOppX = (activeTeam == 1) ? 1100 : 20;
            oppInfoCard.setBounds(cardOppX, 696, 620, 165);
            if (!isChildOf(root, oppInfoCard)) {
                root.add(oppInfoCard);
            }

            root.revalidate();
            root.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza os dados quando uma substituição ocorre ou o usuário clica em trocar time.
     */
    public static void onTeamRefreshed(Object cDObj) {
        if (cDObj == null) return;
        try {
            int activeTeam = getIntField(cDObj, "Di", 1);
            JLayeredPane df = (JLayeredPane) getFieldValue(cDObj, "DF");

            if (df != null && oppPreviewPanel != null) {
                if (activeTeam == 1) {
                    df.setBounds(20, 38, 620, 650);
                    oppPreviewPanel.setBounds(1100, 38, 620, 650);
                } else {
                    df.setBounds(1100, 38, 620, 650);
                    oppPreviewPanel.setBounds(20, 38, 620, 650);
                }
            }

            if (userTacticsCard != null && oppInfoCard != null) {
                if (activeTeam == 1) {
                    userTacticsCard.setBounds(20, 696, 620, 165);
                    oppInfoCard.setBounds(1100, 696, 620, 165);
                } else {
                    userTacticsCard.setBounds(1100, 696, 620, 165);
                    oppInfoCard.setBounds(20, 696, 620, 165);
                }
            }

            JLabel dsPitch = (JLabel) getFieldValue(cDObj, "Ds");
            JLabel dqBench = (JLabel) getFieldValue(cDObj, "Dq");
            if (dqBench != null) {
                dqBench.setIcon(null);
                dqBench.setText("");
                dqBench.setOpaque(true);
                dqBench.setBackground(BG_DEEP);
            }
            if (oppBenchLabel != null) {
                oppBenchLabel.setText("");
            }

            if (dsPitch != null && dsPitch.getIcon() instanceof ImageIcon) {
                currentPitchBg = ((ImageIcon) dsPitch.getIcon()).getImage();
            }

            refreshOpponentPitch(cDObj, activeTeam);

            components.v matchComp = (components.v) getFieldValue(cDObj, "Db");
            best.I match = (matchComp != null) ? matchComp.tR() : null;

            if (statsPanel != null && match != null) {
                statsPanel.updateStats(match);
            }
            if (radarPanel != null && match != null) {
                updateRadarStats(cDObj, match);
            }

            JLabel subsLbl = (JLabel) getFieldValue(cDObj, "DA");
            if (subsLbl != null && match != null) {
                int userTeamIndex = (match.hd() != null && match.hd().jZ() != null && match.hd().jZ().booleanValue()) ? 1 : 0;
                int remaining = match.aR(userTeamIndex);
                subsLbl.setText("Substituições restantes: " + remaining);
                subsLbl.repaint();
            }

            JLabel scoreHome = (JLabel) getFieldValue(cDObj, "Dw");
            JLabel scoreAway = (JLabel) getFieldValue(cDObj, "Dx");
            JLabel matchTime = (JLabel) getFieldValue(cDObj, "Dv");
            JLabel matchPeriod = (JLabel) getFieldValue(cDObj, "DE");
            JLabel stadiumLbl = (JLabel) getFieldValue(cDObj, "Cg");
            JLabel attLbl = (JLabel) getFieldValue(cDObj, "Dy");
            JLabel formOppLbl = (JLabel) getFieldValue(cDObj, "DC");

            updateScoreboardValues(match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            updateOpponentInfo(match, formOppLbl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Alterna suavemente entre Mandante e Visitante chamando o método a/cD.a(this, teamId).
     */
    public static void switchActiveTeam(Object cDObj) {
        try {
            int cur = getIntField(cDObj, "Di", 1);
            int next = (cur == 1) ? 2 : 1;
            Method switchM = cDObj.getClass().getDeclaredMethod("a", cDObj.getClass(), int.class);
            switchM.setAccessible(true);
            switchM.invoke(null, cDObj, next);
            onTeamRefreshed(cDObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Renderiza o campo do adversário usando o gerador gráfico oficial de components.ao.
     */
    public static void refreshOpponentPitch(Object cDObj, int activeTeam) {
        try {
            components.v matchComp = (components.v) getFieldValue(cDObj, "Db");
            if (matchComp == null) return;
            best.I match = matchComp.tR();
            if (match == null) return;

            int oppIndex = (activeTeam == 1) ? 2 : 1;
            ArrayList<?> list = (oppIndex == 2) 
                    ? (ArrayList<?>) getFieldValue(cDObj, "Dc") 
                    : (ArrayList<?>) getFieldValue(cDObj, "AE");
            ImageIcon jersey = (oppIndex == 2) 
                    ? (ImageIcon) getFieldValue(cDObj, "Dd") 
                    : (ImageIcon) getFieldValue(cDObj, "AL");
            best.ah tactics = (oppIndex == 2) ? match.hd() : match.hc();

            if (list != null && !list.isEmpty()) {
                components.ao firstAo = (components.ao) list.get(0);
                if (firstAo != null) {
                    ImageIcon pitchIcon = firstAo.a(list, -1, jersey, false, oppIndex, tactics, 2);
                    ImageIcon benchIcon = firstAo.a(list, -1, jersey, false, oppIndex, tactics, 3);
                    if (pitchIcon != null) {
                        currentOppPlayersImg = pitchIcon.getImage();
                    }
                    if (oppBenchLabel != null) {
                        oppBenchLabel.setIcon(benchIcon);
                    }
                    if (oppPitchComp != null) {
                        oppPitchComp.repaint();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constrói o Card do Placar estilo TV Broadcast EA FC (Premier League / La Liga).
     * Exibe nomes completos dos times (sem abreviação), escudos, caixas de gols translúcidas e cronômetro.
     */
    // Variáveis de estado do placar estilo Premier League
    private static best.I currentMatch = null;
    private static JLabel currentSHome = null;
    private static JLabel currentSAway = null;
    private static JLabel currentMTime = null;
    private static JLabel currentMPeriod = null;
    private static JLabel currentStadium = null;
    private static JLabel currentAtt = null;

    private static boolean isLightColor(Color c) {
        if (c == null) return false;
        double luminance = (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255.0;
        return luminance > 0.65;
    }

    private static boolean isNearWhite(Color c) {
        if (c == null) return false;
        return c.getRed() > 220 && c.getGreen() > 220 && c.getBlue() > 220;
    }

    private static Color getVibrantAccent(Color prim, Color sec) {
        if (sec != null && !isNearWhite(sec) && !isSimilarColor(prim, sec)) {
            return sec;
        }
        if (isLightColor(prim)) {
            return new Color(220, 38, 38); // Vermelho vibrante
        } else {
            return new Color(250, 204, 21); // Ouro / Amarelo elétrico Premier League
        }
    }

    private static boolean isSimilarColor(Color c1, Color c2) {
        if (c1 == null || c2 == null) return false;
        int dr = Math.abs(c1.getRed() - c2.getRed());
        int dg = Math.abs(c1.getGreen() - c2.getGreen());
        int db = Math.abs(c1.getBlue() - c2.getBlue());
        return (dr + dg + db) < 80;
    }

    private static JPanel buildScoreboardCard(
            Object cDObj, best.I match,
            JLabel sHome, JLabel sAway, JLabel mTime, JLabel mPeriod,
            JLabel stadium, JLabel att
    ) {
        currentMatch = match;
        currentSHome = sHome;
        currentSAway = sAway;
        currentMTime = mTime;
        currentMPeriod = mPeriod;
        currentStadium = stadium;
        currentAtt = att;

        // Oculta componentes nativos brutos para evitar sobreposições feias
        hideComponent(sHome);
        hideComponent(sAway);
        hideComponent(mTime);
        hideComponent(mPeriod);
        hideComponent(stadium);
        hideComponent(att);

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);

                int w = getWidth();
                int h = getHeight();

                // Fundo escuro do card geral
                g2.setColor(PANEL_DARK);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                g2.setColor(BORDER_SLATE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

                best.I m = currentMatch;

                // Nomes dos clubes completos
                String nameH = (m != null && m.hc() != null) ? m.hc().getNome() : "MANDANTE";
                String nameA = (m != null && m.hd() != null) ? m.hd().getNome() : "VISITANTE";

                // Cores dos clubes
                Color colH_prim = (m != null && m.hc() != null && m.hc().kB() != null) ? m.hc().kB() : new Color(22, 22, 24);
                Color colH_sec = (m != null && m.hc() != null && m.hc().kC() != null) ? m.hc().kC() : Color.WHITE;

                Color colA_prim = (m != null && m.hd() != null && m.hd().kB() != null) ? m.hd().kB() : new Color(218, 41, 28);
                Color colA_sec = (m != null && m.hd() != null && m.hd().kC() != null) ? m.hd().kC() : Color.WHITE;

                if (isSimilarColor(colH_prim, colA_prim) && m != null && m.hd() != null && m.hd().kC() != null) {
                    colA_prim = m.hd().kC();
                }

                // Escudos oficiais
                ImageIcon crestH = (m != null && m.hc() != null) ? m.hc().x(22, 22) : null;
                ImageIcon crestA = (m != null && m.hd() != null) ? m.hd().x(22, 22) : null;

                // Placar
                String scoreH = (currentSHome != null && currentSHome.getText() != null) ? currentSHome.getText().trim() : "0";
                String scoreA = (currentSAway != null && currentSAway.getText() != null) ? currentSAway.getText().trim() : "0";

                // Tempo e Período (1T ou 2T)
                String periodRaw = (currentMPeriod != null && currentMPeriod.getText() != null) ? currentMPeriod.getText() : "1º";
                String periodStr = "1T";
                if (periodRaw.contains("2")) {
                    periodStr = "2T";
                } else if (periodRaw.toLowerCase().contains("pr") || periodRaw.toLowerCase().contains("pro")) {
                    periodStr = "PR";
                } else if (periodRaw.toLowerCase().contains("pen")) {
                    periodStr = "PEN";
                } else if (periodRaw.toLowerCase().contains("int")) {
                    periodStr = "INT";
                } else if (periodRaw.toLowerCase().contains("fim")) {
                    periodStr = "FIM";
                }

                String timeRaw = (currentMTime != null && currentMTime.getText() != null) ? currentMTime.getText().trim() : "0'";
                String timeStr = timeRaw;
                if (timeStr.endsWith("'")) {
                    String minOnly = timeStr.substring(0, timeStr.length() - 1);
                    if (!minOnly.contains("+")) {
                        try {
                            int minVal = Integer.parseInt(minOnly);
                            timeStr = String.format("%02d:00", minVal);
                        } catch (Exception ignored) {}
                    }
                }

                // Geometria do Scorebug estilo Premier League
                int bugW = w - 24;
                if (bugW > 416) bugW = 416;
                int bugH = 38;
                int bugX = (w - bugW) / 2;
                int bugY = 12;

                int midX = bugX + bugW / 2;

                // Determina cores dos raios (Lightning Bolts)
                Color boltColH = getVibrantAccent(colH_prim, colH_sec);
                Color boltColA = getVibrantAccent(colA_prim, colA_sec);

                // 1. Fundo Central Branco do Placar (com protrusão de 3px no topo)
                int boltW = 18; // Raio mais grosso e pronunciado no padrão Premier League
                int halfScoreSpan = 52;
                int cutA = midX + halfScoreSpan - 6;
                int cutH = midX - halfScoreSpan - 6 - boltW;

                int scoreY = bugY - 3;
                int scoreBoxH = (bugY + bugH) - scoreY; // Projeta 3px acima da barra

                GeneralPath pathScore = new GeneralPath();
                pathScore.moveTo(cutH + 4 + boltW, scoreY);
                pathScore.lineTo(cutA + 4, scoreY);
                pathScore.lineTo(cutA + 16, bugY + bugH / 2);
                pathScore.lineTo(cutA + 5, bugY + bugH / 2 + 2);
                pathScore.lineTo(cutA + 14, bugY + bugH);
                pathScore.lineTo(cutH + 14 + boltW, bugY + bugH);
                pathScore.lineTo(cutH + 5 + boltW, bugY + bugH / 2 + 2);
                pathScore.lineTo(cutH + 16 + boltW, bugY + bugH / 2);
                pathScore.closePath();

                g2.setColor(Color.WHITE);
                g2.fill(pathScore);

                // 2. Traçado do Raio Mandante com DEGRADÊ de fora para dentro (secundária -> primária)
                GeneralPath boltH = new GeneralPath();
                boltH.moveTo(cutH + 4, bugY);
                boltH.lineTo(cutH + 16, bugY + bugH / 2);
                boltH.lineTo(cutH + 5, bugY + bugH / 2 + 2);
                boltH.lineTo(cutH + 14, bugY + bugH);
                boltH.lineTo(cutH + 14 + boltW, bugY + bugH);
                boltH.lineTo(cutH + 5 + boltW, bugY + bugH / 2 + 2);
                boltH.lineTo(cutH + 16 + boltW, bugY + bugH / 2);
                boltH.lineTo(cutH + 4 + boltW, bugY);
                boltH.closePath();

                GradientPaint gpBoltH = new GradientPaint(cutH + 4, bugY, boltColH, cutH + 16 + boltW, bugY, colH_prim);

                // Bloco Mandante: Cantos SUPERIORES arredondados, INFERIORES RETOS (90°)
                GeneralPath pathH = new GeneralPath();
                pathH.moveTo(bugX + 7, bugY);
                pathH.lineTo(cutH + 4, bugY);
                pathH.lineTo(cutH + 16, bugY + bugH / 2);
                pathH.lineTo(cutH + 5, bugY + bugH / 2 + 2);
                pathH.lineTo(cutH + 14, bugY + bugH);
                pathH.lineTo(bugX, bugY + bugH); // Canto inferior reto!
                pathH.lineTo(bugX, bugY + 7);
                pathH.quadTo(bugX, bugY, bugX + 7, bugY); // Canto superior arredondado!
                pathH.closePath();

                g2.setColor(colH_prim);
                g2.fill(pathH);

                g2.setPaint(gpBoltH);
                g2.fill(boltH);

                g2.setColor(new Color(255, 255, 255, 50));
                g2.draw(pathH);

                // 3. Traçado do Raio Visitante com DEGRADÊ de fora para dentro (secundária -> primária)
                int awayEndX = bugX + bugW;

                GeneralPath boltA = new GeneralPath();
                boltA.moveTo(cutA + 4, bugY);
                boltA.lineTo(cutA + 16, bugY + bugH / 2);
                boltA.lineTo(cutA + 5, bugY + bugH / 2 + 2);
                boltA.lineTo(cutA + 14, bugY + bugH);
                boltA.lineTo(cutA + 14 + boltW, bugY + bugH);
                boltA.lineTo(cutA + 5 + boltW, bugY + bugH / 2 + 2);
                boltA.lineTo(cutA + 16 + boltW, bugY + bugH / 2);
                boltA.lineTo(cutA + 4 + boltW, bugY);
                boltA.closePath();

                Color awayEdgeCol = isNearWhite(colA_prim) ? new Color(203, 213, 225) : colA_prim;
                GradientPaint gpBoltA = new GradientPaint(cutA + 16 + boltW, bugY, boltColA, cutA + 4, bugY, awayEdgeCol);

                // Bloco Visitante: Cantos SUPERIORES arredondados, INFERIORES RETOS (90°)
                GeneralPath pathA = new GeneralPath();
                pathA.moveTo(cutA + 4 + boltW, bugY);
                pathA.lineTo(awayEndX - 7, bugY);
                pathA.quadTo(awayEndX, bugY, awayEndX, bugY + 7); // Canto superior arredondado!
                pathA.lineTo(awayEndX, bugY + bugH); // Canto inferior reto!
                pathA.lineTo(cutA + 14 + boltW, bugY + bugH);
                pathA.lineTo(cutA + 5 + boltW, bugY + bugH / 2 + 2);
                pathA.lineTo(cutA + 16 + boltW, bugY + bugH / 2);
                pathA.closePath();

                Color drawColA = isNearWhite(colA_prim) ? new Color(248, 250, 252) : colA_prim;
                g2.setColor(drawColA);
                g2.fill(pathA);

                g2.setPaint(gpBoltA);
                g2.fill(boltA);

                if (isNearWhite(colA_prim)) {
                    g2.setColor(new Color(203, 213, 225));
                    g2.draw(pathA);
                } else {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.draw(pathA);
                }

                // 4. Conteúdo Mandante: Escudo + NOME COMPLETO (UPPERCASE)
                String upperNameH = nameH.toUpperCase();
                int crestSize = 22;
                int crestHY = bugY + (bugH - crestSize) / 2;
                if (crestH != null && crestH.getImage() != null) {
                    g2.drawImage(crestH.getImage(), bugX + 8, crestHY, crestSize, crestSize, null);
                }

                Color txtColH = isLightColor(colH_prim) ? new Color(15, 23, 42) : Color.WHITE;
                int maxNameW = (cutH + 4) - (bugX + 36) - 4;
                int fontSizeH = 13;
                Font fontH = new Font("Segoe UI", Font.BOLD, fontSizeH);
                while (fontSizeH > 9 && g2.getFontMetrics(fontH).stringWidth(upperNameH) > maxNameW) {
                    fontSizeH--;
                    fontH = new Font("Segoe UI", Font.BOLD, fontSizeH);
                }
                g2.setFont(fontH);
                g2.setColor(txtColH);
                FontMetrics fmH = g2.getFontMetrics();
                g2.drawString(upperNameH, bugX + 36, bugY + (bugH - fmH.getHeight()) / 2 + fmH.getAscent());

                // 5. Conteúdo Visitante: NOME COMPLETO (UPPERCASE) + Escudo
                String upperNameA = nameA.toUpperCase();
                int crestAY = bugY + (bugH - crestSize) / 2;
                if (crestA != null && crestA.getImage() != null) {
                    g2.drawImage(crestA.getImage(), awayEndX - 30, crestAY, crestSize, crestSize, null);
                }

                Color txtColA = isLightColor(drawColA) ? new Color(15, 23, 42) : Color.WHITE;
                int maxNameAW = (awayEndX - 36) - (cutA + 16 + boltW) - 4;
                int fontSizeA = 13;
                Font fontA = new Font("Segoe UI", Font.BOLD, fontSizeA);
                while (fontSizeA > 9 && g2.getFontMetrics(fontA).stringWidth(upperNameA) > maxNameAW) {
                    fontSizeA--;
                    fontA = new Font("Segoe UI", Font.BOLD, fontSizeA);
                }
                g2.setFont(fontA);
                g2.setColor(txtColA);
                FontMetrics fmA = g2.getFontMetrics();
                int aNameW = fmA.stringWidth(upperNameA);
                int textAX = (awayEndX - 36) - aNameW - 4;
                g2.drawString(upperNameA, textAX, bugY + (bugH - fmA.getHeight()) / 2 + fmA.getAscent());

                // 6. Placar Central: Números de gols maiores (28pt Bold), passando ligeiramente da barra
                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fmScore = g2.getFontMetrics();

                int scoreHW = fmScore.stringWidth(scoreH);
                int scoreAW = fmScore.stringWidth(scoreA);

                int scoreNumY = scoreY + (scoreBoxH - fmScore.getHeight()) / 2 + fmScore.getAscent() + 1;
                g2.drawString(scoreH, midX - 25 - (scoreHW / 2), scoreNumY);
                g2.drawString(scoreA, midX + 25 - (scoreAW / 2), scoreNumY);

                // 7. Círculo Central com "1T" ou "2T" (com borda dourada elegante)
                int badgeD = 24;
                int badgeX = midX - badgeD / 2;
                int badgeY = scoreY + (scoreBoxH - badgeD) / 2;

                Color plPurple = new Color(56, 0, 60); // Roxo Premier League (#38003c)
                g2.setColor(plPurple);
                g2.fillOval(badgeX, badgeY, badgeD, badgeD);
                g2.setColor(new Color(250, 204, 21)); // Borda dourada
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(badgeX, badgeY, badgeD, badgeD);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fmP = g2.getFontMetrics();
                int pw = fmP.stringWidth(periodStr);
                g2.drawString(periodStr, midX - pw / 2, badgeY + (badgeD - fmP.getHeight()) / 2 + fmP.getAscent());

                // 8. Card do Tempo: RETO nas laterais (sem caimento!) com cantos inferiores arredondados
                int tabW = 86;
                int tabH = 19;
                int tabX = midX - tabW / 2;
                int tabY = bugY + bugH;

                GeneralPath pathTab = new GeneralPath();
                pathTab.moveTo(tabX, tabY);
                pathTab.lineTo(tabX + tabW, tabY); // Topo reto
                pathTab.lineTo(tabX + tabW, tabY + tabH - 6); // Lateral reta (sem caimento!)
                pathTab.quadTo(tabX + tabW, tabY + tabH, tabX + tabW - 6, tabY + tabH); // Canto inferior direito arredondado
                pathTab.lineTo(tabX + 6, tabY + tabH);
                pathTab.quadTo(tabX, tabY + tabH, tabX, tabY + tabH - 6); // Canto inferior esquerdo arredondado
                pathTab.closePath();

                g2.setColor(plPurple);
                g2.fill(pathTab);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.draw(pathTab);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fmT = g2.getFontMetrics();
                int tw = fmT.stringWidth(timeStr);
                g2.drawString(timeStr, midX - tw / 2, tabY + (tabH - fmT.getHeight()) / 2 + fmT.getAscent());
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(pathTab);

                // 9. Informações de Estádio e Torcedores abaixo
                String stadText = (currentStadium != null && currentStadium.getText() != null) ? currentStadium.getText().trim() : "Estádio";
                String attRaw = (currentAtt != null && currentAtt.getText() != null) ? currentAtt.getText().trim() : "";
                String attFormatted = attRaw;
                if (!attRaw.isEmpty()) {
                    try {
                        long num = Long.parseLong(attRaw.replaceAll("\\D", ""));
                        attFormatted = String.format(Locale.GERMAN, "%,d", num);
                    } catch (Exception ignored) {}
                }

                g2.setColor(new Color(180, 195, 215));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                FontMetrics fmInfo = g2.getFontMetrics();
                String infoStr = stadText + (attFormatted.isEmpty() ? "" : "   •   " + attFormatted + " torcedores");
                int iw = fmInfo.stringWidth(infoStr);
                g2.drawString(infoStr, (w - iw) / 2, 85);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private static void updateScoreboardValues(
            best.I match,
            JLabel sHome, JLabel sAway, JLabel mTime, JLabel mPeriod,
            JLabel stadium, JLabel att
    ) {
        currentMatch = match;
        currentSHome = sHome;
        currentSAway = sAway;
        currentMTime = mTime;
        currentMPeriod = mPeriod;
        currentStadium = stadium;
        currentAtt = att;

        if (scoreboardCard != null) {
            scoreboardCard.repaint();
        }
    }

    /**
     * Estiliza o JTable e o JScrollPane de eventos para o visual moderno EA FC Dark.
     */
    private static void styleEventsTable(JScrollPane sp, JTable table) {
        if (sp == null || table == null) return;

        sp.setOpaque(false);
        sp.getViewport().setBackground(PANEL_DARK);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_SLATE, 1));

        table.setBackground(PANEL_DARK);
        table.setForeground(TEXT_WHITE);
        table.setGridColor(new Color(24, 33, 52));
        table.setRowHeight(24);
        table.setFont(new Font("Verdana", Font.PLAIN, 11));
        table.setShowGrid(false);
        table.setTableHeader(null);

        table.setRowHeight(26);

        table.setDefaultRenderer(best.A.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setOpaque(true);
                cell.setBackground((row % 2 == 0) ? PANEL_DARK : new Color(22, 30, 48));

                if (!(val instanceof best.A)) {
                    return cell;
                }

                best.A ev = (best.A) val;
                if (!ev.isDone()) {
                    return cell;
                }

                int type = ev.b();

                // 1. Linhas de Seção/Cabeçalho da tabela de eventos (type > 90)
                if (type > 90) {
                    cell.setBackground(new Color(24, 33, 52));
                    if (col == 1) {
                        String title;
                        if (type == 91) title = "⚽  GOLS DA PARTIDA";
                        else if (type == 92) title = "🟨  CARTÕES & ADVERTÊNCIAS";
                        else if (type == 95) title = "🩹  CONTUSÕES";
                        else if (type == 96) title = "🔄  SUBSTITUIÇÕES";
                        else title = "ACONTECIMENTOS";

                        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        lbl.setForeground(GOLD_ACCENT);
                        cell.add(lbl, BorderLayout.CENTER);
                    }
                    return cell;
                }

                // 2. Linhas de Eventos (type <= 90)
                int teamSide = ev.et(); // 0 = Mandante (esquerda), 1 = Visitante (direita)

                // Renderiza o badge/ícone moderno no lado correto
                if ((col == 0 && teamSide == 0) || (col == 2 && teamSide == 1)) {
                    cell.add(createModernEventBadge(ev, type), BorderLayout.CENTER);
                } else if (col == 1) {
                    // Coluna central: Texto do evento (limpo, moderno, com alinhamento correto)
                    JLabel lbl = new JLabel(ev.er());
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setForeground(TEXT_WHITE);
                    lbl.setHorizontalAlignment(teamSide == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                    cell.add(lbl, BorderLayout.CENTER);
                }

                return cell;
            }
        });
    }

    private static JComponent createModernEventBadge(final best.A ev, final int type) {
        return new JComponent() {
            {
                setPreferredSize(new Dimension(24, 22));
                setOpaque(false);
            }
            @Override
            protected void paintComponent(Graphics g1) {
                super.paintComponent(g1);
                Graphics2D g = (Graphics2D) g1.create();
                EaFcUiHelper.applyHighQualityRendering(g);

                int w = getWidth();
                int h = getHeight();

                if (type == 1) { // GOL
                    boolean isOwnGoal = (ev.el() == 2);
                    if (isOwnGoal) {
                        g.setColor(new Color(220, 38, 38));
                        g.fillRoundRect(1, 4, w - 2, h - 8, 4, 4);
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Verdana", Font.BOLD, 8));
                        FontMetrics fm = g.getFontMetrics();
                        int tw = fm.stringWidth("GC");
                        g.drawString("GC", (w - tw) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
                    } else {
                        g.setColor(new Color(16, 185, 129));
                        g.fillRoundRect(1, 4, w - 2, h - 8, 4, 4);
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Verdana", Font.BOLD, 8));
                        FontMetrics fm = g.getFontMetrics();
                        int tw = fm.stringWidth("GOL");
                        g.drawString("GOL", (w - tw) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
                    }
                } else if (type == 7) { // Gol contra direto
                    g.setColor(new Color(220, 38, 38));
                    g.fillRoundRect(1, 4, w - 2, h - 8, 4, 4);
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Verdana", Font.BOLD, 8));
                    FontMetrics fm = g.getFontMetrics();
                    int tw = fm.stringWidth("GC");
                    g.drawString("GC", (w - tw) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
                } else if (type == 2) { // Cartão Amarelo
                    int cw = 11, ch = 15;
                    int cx = (w - cw) / 2, cy = (h - ch) / 2;
                    g.setColor(new Color(234, 179, 8));
                    g.fillRoundRect(cx, cy, cw, ch, 3, 3);
                    g.setColor(new Color(202, 138, 4));
                    g.drawRoundRect(cx, cy, cw, ch, 3, 3);
                } else if (type == 3) { // 2º Amarelo -> Vermelho
                    int cw = 10, ch = 14;
                    g.setColor(new Color(234, 179, 8));
                    g.fillRoundRect(w / 2 - 7, h / 2 - 6, cw, ch, 2, 2);
                    g.setColor(new Color(239, 68, 68));
                    g.fillRoundRect(w / 2 - 2, h / 2 - 4, cw, ch, 2, 2);
                } else if (type == 4) { // Cartão Vermelho Direto
                    int cw = 11, ch = 15;
                    int cx = (w - cw) / 2, cy = (h - ch) / 2;
                    g.setColor(new Color(239, 68, 68));
                    g.fillRoundRect(cx, cy, cw, ch, 3, 3);
                    g.setColor(new Color(185, 28, 28));
                    g.drawRoundRect(cx, cy, cw, ch, 3, 3);
                } else if (type == 5) { // Contusão (Cruz vermelha)
                    int sz = 15;
                    int cx = (w - sz) / 2, cy = (h - sz) / 2;
                    g.setColor(new Color(239, 68, 68, 45));
                    g.fillRoundRect(cx, cy, sz, sz, 4, 4);
                    g.setColor(new Color(239, 68, 68));
                    g.drawRoundRect(cx, cy, sz, sz, 4, 4);
                    g.fillRect(w / 2 - 1, cy + 3, 2, 9);
                    g.fillRect(cx + 3, h / 2 - 1, 9, 2);
                } else if (type == 6) { // Substituição
                    g.setColor(new Color(30, 41, 59));
                    g.fillRoundRect(1, 4, w - 2, h - 8, 4, 4);
                    g.setColor(new Color(56, 189, 248));
                    g.drawRoundRect(1, 4, w - 2, h - 8, 4, 4);
                    g.setColor(new Color(56, 189, 248));
                    g.setFont(new Font("Verdana", Font.BOLD, 8));
                    FontMetrics fm = g.getFontMetrics();
                    int tw = fm.stringWidth("SUB");
                    g.drawString("SUB", (w - tw) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
                }

                g.dispose();
            }
        };
    }

    /**
     * Atualiza os atributos de ambos os times no Radar Tático Hexagonal Sobreposto.
     */
    private static void updateRadarStats(Object cDObj, best.I match) {
        if (radarPanel == null || match == null) return;
        try {
            ArrayList<?> listH = (ArrayList<?>) getFieldValue(cDObj, "AE");
            ArrayList<?> listA = (ArrayList<?>) getFieldValue(cDObj, "Dc");

            List<best.F> startersH = extractStarters(listH);
            List<best.F> startersA = extractStarters(listA);

            List<Integer> slotsH = extractSlots(listH);
            List<Integer> slotsA = extractSlots(listA);

            int homeOvr = (startersH.isEmpty()) ? 75 : EaFcUiHelper.calculateOverall(startersH, slotsH);
            int awayOvr = (startersA.isEmpty()) ? 75 : EaFcUiHelper.calculateOverall(startersA, slotsA);

            int[] homeAttrs = (startersH.isEmpty()) ? new int[]{75,75,75,75,75,75} : EaFcUiHelper.calculateTeamAttributes(startersH, slotsH);
            int[] awayAttrs = (startersA.isEmpty()) ? new int[]{75,75,75,75,75,75} : EaFcUiHelper.calculateTeamAttributes(startersA, slotsA);

            String nameH = (match.hc() != null) ? match.hc().getNome() : "MANDANTE";
            String nameA = (match.hd() != null) ? match.hd().getNome() : "VISITANTE";
            ImageIcon crestH = (match.hc() != null) ? match.hc().x(24, 24) : null;
            ImageIcon crestA = (match.hd() != null) ? match.hd().x(24, 24) : null;

            radarPanel.updateStats(
                    nameH, crestH, homeOvr, homeAttrs,
                    nameA, crestA, awayOvr, awayAttrs
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<best.F> extractStarters(ArrayList<?> list) {
        List<best.F> starters = new ArrayList<best.F>();
        if (list == null) return starters;
        for (int i = 1; i <= 25 && i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj instanceof components.ao) {
                best.F player = ((components.ao) obj).x();
                if (player != null) starters.add(player);
            }
        }
        return starters;
    }

    private static List<Integer> extractSlots(ArrayList<?> list) {
        List<Integer> slots = new ArrayList<Integer>();
        if (list == null) return slots;
        for (int i = 1; i <= 25 && i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj instanceof components.ao) {
                best.F player = ((components.ao) obj).x();
                if (player != null) slots.add(i);
            }
        }
        return slots;
    }

    private static JPanel buildUserTacticsCard(
            JLabel subsLbl, JLabel coachLbl,
            JLabel lblEstilo, JComboBox<?> cbEstilo,
            JLabel lblMarcacao, JComboBox<?> cbMarcacao,
            JLabel lblAtaque, JComboBox<?> cbAtaque
    ) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(PANEL_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_SLATE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel lblTitle = new JLabel("SUAS INSTRUÇÕES TÁTICAS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setBounds(15, 12, 250, 20);
        card.add(lblTitle);

        if (subsLbl != null) {
            subsLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            subsLbl.setForeground(EMERALD);
            subsLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            subsLbl.setBounds(430, 10, 175, 24);
            card.add(subsLbl);
        }

        if (coachLbl != null) {
            coachLbl.setFont(new Font("Verdana", Font.BOLD, 12));
            coachLbl.setForeground(TEXT_WHITE);
            coachLbl.setBounds(15, 38, 350, 20);
            card.add(coachLbl);
        }

        styleLabel(lblEstilo, "Estilo de jogo:");
        styleLabel(lblMarcacao, "Marcação:");
        styleLabel(lblAtaque, "Concentrar ataques:");

        EaFcUiHelper.styleModernComboBox(cbEstilo);
        EaFcUiHelper.styleModernComboBox(cbMarcacao);
        EaFcUiHelper.styleModernComboBox(cbAtaque);

        if (lblEstilo != null) { lblEstilo.setBounds(15, 66, 185, 16); card.add(lblEstilo); }
        if (cbEstilo != null) { cbEstilo.setBounds(15, 86, 185, 32); card.add(cbEstilo); }

        if (lblMarcacao != null) { lblMarcacao.setBounds(215, 66, 185, 16); card.add(lblMarcacao); }
        if (cbMarcacao != null) { cbMarcacao.setBounds(215, 86, 185, 32); card.add(cbMarcacao); }

        if (lblAtaque != null) { lblAtaque.setBounds(415, 66, 190, 16); card.add(lblAtaque); }
        if (cbAtaque != null) { cbAtaque.setBounds(415, 86, 190, 32); card.add(cbAtaque); }

        return card;
    }

    private static JPanel createOppStatBox(String title, JLabel lblValue) {
        JPanel box = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_SLATE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        box.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblT.setForeground(TEXT_MUTED);
        lblT.setBounds(10, 6, 170, 16);
        box.add(lblT);

        if (lblValue != null) {
            lblValue.setFont(new Font("Verdana", Font.BOLD, 11));
            lblValue.setForeground(TEXT_WHITE);
            lblValue.setBounds(10, 24, 170, 24);
            box.add(lblValue);
        }

        return box;
    }

    private static JPanel buildOpponentInfoCard(best.I match, JLabel formLbl) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(PANEL_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_SLATE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        int userTeamIndex = (match != null && match.hd() != null && match.hd().jZ() != null && match.hd().jZ().booleanValue()) ? 1 : 0;
        best.ah oppTeam = (userTeamIndex == 0) ? (match != null ? match.hd() : null) : (match != null ? match.hc() : null);

        String coach = "Adversário";
        try {
            if (oppTeam != null && oppTeam.ka() != null) {
                coach = oppTeam.ka().dS();
            }
        } catch (Exception ignored) {}

        String formation = (formLbl != null && formLbl.getText() != null) ? formLbl.getText() : "4-4-2";

        JLabel lblTitle = new JLabel("COMANDO TÉCNICO & ESTRATÉGIA DO ADVERSÁRIO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setBounds(15, 12, 350, 20);
        card.add(lblTitle);

        // Esquema Tático posicionado no topo direito dentro do card
        lblOppFormation = new JLabel("ESQUEMA: " + formation);
        lblOppFormation.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblOppFormation.setForeground(GOLD_ACCENT);
        lblOppFormation.setHorizontalAlignment(SwingConstants.CENTER);
        lblOppFormation.setBounds(440, 10, 165, 24);
        lblOppFormation.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD_ACCENT, 1, true),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        card.add(lblOppFormation);

        lblOppCoach = new JLabel("Técnico: " + coach);
        lblOppCoach.setFont(new Font("Verdana", Font.BOLD, 12));
        lblOppCoach.setForeground(TEXT_WHITE);
        lblOppCoach.setBounds(15, 38, 400, 20);
        card.add(lblOppCoach);

        String initialEstilo = "Equilibrado";
        String initialMarcacao = "Pesada";
        if (oppTeam != null && oppTeam.kj() != null) {
            if (oppTeam.kj().length > 1 && oppTeam.kj()[1] >= 0 && oppTeam.kj()[1] < best.aq.rC.length) {
                initialEstilo = best.aq.rC[oppTeam.kj()[1]];
            }
            if (oppTeam.kj().length > 2 && oppTeam.kj()[2] >= 0 && oppTeam.kj()[2] < best.aq.rE.length) {
                initialMarcacao = best.aq.rE[oppTeam.kj()[2]];
            }
        }

        lblOppEstilo = new JLabel(initialEstilo);
        lblOppMarcacao = new JLabel(initialMarcacao);
        lblOppSubs = new JLabel("0 de 5 realizadas");

        // 3 caixas de status tático com nomes REAIS do Brasfoot
        JPanel box1 = createOppStatBox("ESTILO DE JOGO", lblOppEstilo);
        box1.setBounds(15, 66, 185, 56);
        card.add(box1);

        JPanel box2 = createOppStatBox("MARCAÇÃO", lblOppMarcacao);
        box2.setBounds(215, 66, 185, 56);
        card.add(box2);

        JPanel box3 = createOppStatBox("SUBSTITUIÇÕES", lblOppSubs);
        box3.setBounds(415, 66, 190, 56);
        card.add(box3);

        return card;
    }

    private static void updateOpponentInfo(best.I match, JLabel formLbl) {
        if (oppInfoCard == null) return;
        try {
            int userTeamIndex = (match != null && match.hd() != null && match.hd().jZ() != null && match.hd().jZ().booleanValue()) ? 1 : 0;
            best.ah oppTeam = (userTeamIndex == 0) ? (match != null ? match.hd() : null) : (match != null ? match.hc() : null);
            int oppTeamIndex = (userTeamIndex == 0) ? 1 : 0;

            String coach = "Adversário";
            if (oppTeam != null && oppTeam.ka() != null) {
                coach = oppTeam.ka().dS();
            }
            if (lblOppCoach != null) {
                lblOppCoach.setText("Técnico: " + coach);
            }
            if (lblOppFormation != null && formLbl != null && formLbl.getText() != null) {
                lblOppFormation.setText("ESQUEMA: " + formLbl.getText());
            }
            if (lblOppEstilo != null && oppTeam != null && oppTeam.kj() != null && oppTeam.kj().length > 1) {
                int idx = oppTeam.kj()[1];
                String estilo = (idx >= 0 && idx < best.aq.rC.length) ? best.aq.rC[idx] : "Equilibrado";
                lblOppEstilo.setText(estilo);
            }
            if (lblOppMarcacao != null && oppTeam != null && oppTeam.kj() != null && oppTeam.kj().length > 2) {
                int idx = oppTeam.kj()[2];
                String marc = (idx >= 0 && idx < best.aq.rE.length) ? best.aq.rE[idx] : "Pesada";
                lblOppMarcacao.setText(marc);
            }
            if (lblOppSubs != null && match != null) {
                int oppRemaining = match.aR(oppTeamIndex);
                int oppUsed = Math.max(0, 5 - oppRemaining);
                lblOppSubs.setText(oppUsed + " de 5 realizadas");
            }
        } catch (Exception ignored) {}
        oppInfoCard.repaint();
    }

    private static void styleLabel(JLabel lbl, String text) {
        if (lbl == null) return;
        lbl.setText(text);
        lbl.setFont(new Font("Verdana", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
    }

    private static void hideComponent(Component c) {
        if (c != null) c.setVisible(false);
    }

    private static boolean isChildOf(Container parent, Component child) {
        for (Component c : parent.getComponents()) {
            if (c == child) return true;
        }
        return false;
    }

    private static Object getFieldValue(Object target, String fieldName) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            return null;
        }
    }

    private static int getIntField(Object target, String fieldName, int fallback) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.getInt(target);
        } catch (Exception e) {
            return fallback;
        }
    }
}
