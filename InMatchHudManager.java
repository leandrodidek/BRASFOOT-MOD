package mod.brasfoot;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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

    private static JPanel oppPreviewPanel = null;
    private static JPanel oppPitchComp = null;
    private static JLabel oppBenchLabel = null;
    private static Image currentPitchBg = null;
    private static Image currentOppPlayersImg = null;

    private static JLabel lblHomeHeader = null;
    private static JLabel lblAwayHeader = null;
    private static JPanel oppInfoCard = null;
    private static JLabel lblOppCoach = null;
    private static JLabel lblOppFormation = null;

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
            // Remove a textura verde escura de madeira do banco do usuário
            if (dqBench != null) {
                dqBench.setIcon(null);
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
            if (subsLbl != null) {
                subsLbl.setFont(new Font("Verdana", Font.BOLD, 12));
                subsLbl.setForeground(EMERALD);
                subsLbl.setBounds(25, 696, 280, 22);
            }

            if (coachLbl != null) {
                coachLbl.setFont(new Font("Verdana", Font.BOLD, 11));
                coachLbl.setForeground(TEXT_MUTED);
                coachLbl.setBounds(330, 696, 310, 22);
            }

            styleLabel(lblEstilo, "Estilo de jogo:");
            styleLabel(lblMarcacao, "Marcação:");
            styleLabel(lblAtaque, "Concentrar ataques:");

            EaFcUiHelper.styleModernComboBox(cbEstilo);
            EaFcUiHelper.styleModernComboBox(cbMarcacao);
            EaFcUiHelper.styleModernComboBox(cbAtaque);

            int comboY = 746;
            int lblY = 726;
            int colW = 195;

            if (lblEstilo != null) lblEstilo.setBounds(25, lblY, colW, 18);
            if (cbEstilo != null) cbEstilo.setBounds(25, comboY, colW, 30);

            if (lblMarcacao != null) lblMarcacao.setBounds(230, lblY, colW, 18);
            if (cbMarcacao != null) cbMarcacao.setBounds(230, comboY, colW, 30);

            if (lblAtaque != null) lblAtaque.setBounds(435, lblY, 205, 18);
            if (cbAtaque != null) cbAtaque.setBounds(435, comboY, 205, 30);

            // =========================================================
            // 6. HUB ANALÍTICO CENTRAL (x: 650, w: 440)
            // =========================================================

            // 6.1 Placar Broadcast TV (y: 10, h: 95)
            if (scoreboardCard == null) {
                scoreboardCard = buildScoreboardCard(cDObj, match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            } else {
                updateScoreboardValues(match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            }
            scoreboardCard.setBounds(650, 10, 440, 95);
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

            // 6.5 Botão de Ação "VOLTAR AO JOGO" (y: 792, h: 46)
            if (btnVoltar != null) {
                btnVoltar.setBounds(670, 792, 400, 46);
                EaFcUiHelper.styleActionPillButton(btnVoltar, "< VOLTAR AO JOGO", true);
            }

            // 7. Painel de Resumo Tático do Adversário (abaixo do campo adversário)
            if (oppInfoCard == null) {
                oppInfoCard = buildOpponentInfoCard(match, formOppLbl);
            } else {
                updateOpponentInfo(match, formOppLbl);
            }
            oppInfoCard.setBounds(1100, 696, 620, 80);
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

            JLabel dsPitch = (JLabel) getFieldValue(cDObj, "Ds");
            JLabel dqBench = (JLabel) getFieldValue(cDObj, "Dq");
            if (dqBench != null) {
                dqBench.setIcon(null);
                dqBench.setOpaque(true);
                dqBench.setBackground(BG_DEEP);
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
     * Constrói o Card do Placar estilo TV Broadcast EA FC.
     */
    private static JPanel buildScoreboardCard(
            Object cDObj, best.I match,
            JLabel sHome, JLabel sAway, JLabel mTime, JLabel mPeriod,
            JLabel stadium, JLabel att
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

        // Escudos dos times
        ImageIcon crestH = (match != null && match.hc() != null) ? match.hc().x(36, 36) : null;
        ImageIcon crestA = (match != null && match.hd() != null) ? match.hd().x(36, 36) : null;

        JLabel lblCrestH = new JLabel(crestH);
        lblCrestH.setBounds(40, 14, 36, 36);
        card.add(lblCrestH);

        JLabel lblCrestA = new JLabel(crestA);
        lblCrestA.setBounds(364, 14, 36, 36);
        card.add(lblCrestA);

        // Placares (Dw e Dx) perfeitamente centralizados
        if (sHome != null) {
            sHome.setFont(new Font("Segoe UI", Font.BOLD, 30));
            sHome.setForeground(TEXT_WHITE);
            sHome.setHorizontalAlignment(SwingConstants.RIGHT);
            sHome.setBounds(95, 10, 65, 42);
            card.add(sHome);
        }

        JLabel lblX = new JLabel("x");
        lblX.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblX.setForeground(TEXT_MUTED);
        lblX.setHorizontalAlignment(SwingConstants.CENTER);
        lblX.setBounds(205, 16, 30, 30);
        card.add(lblX);

        if (sAway != null) {
            sAway.setFont(new Font("Segoe UI", Font.BOLD, 30));
            sAway.setForeground(TEXT_WHITE);
            sAway.setHorizontalAlignment(SwingConstants.LEFT);
            sAway.setBounds(280, 10, 65, 42);
            card.add(sAway);
        }

        // Tempo Decorrido & Período (46' • 1º)
        String tText = (mTime != null && mTime.getText() != null) ? mTime.getText() : "0'";
        String pText = (mPeriod != null && mPeriod.getText() != null) ? mPeriod.getText() : "1º";
        lblScoreTimePeriod = new JLabel(tText + "  •  " + pText);
        lblScoreTimePeriod.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblScoreTimePeriod.setForeground(EMERALD);
        lblScoreTimePeriod.setHorizontalAlignment(SwingConstants.CENTER);
        lblScoreTimePeriod.setBounds(120, 52, 200, 18);
        card.add(lblScoreTimePeriod);

        // Estádio e Público / Torcedores Integrados na Base
        String stadText = (stadium != null && stadium.getText() != null) ? stadium.getText() : "Estádio";
        String attText = (att != null && att.getText() != null) ? att.getText() : "";
        String fullInfo = stadText;
        if (!attText.isEmpty()) {
            fullInfo += "   •   " + attText + " torcedores";
        }
        lblScoreStadiumAtt = new JLabel(fullInfo);
        lblScoreStadiumAtt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblScoreStadiumAtt.setForeground(TEXT_MUTED);
        lblScoreStadiumAtt.setHorizontalAlignment(SwingConstants.CENTER);
        lblScoreStadiumAtt.setBounds(10, 72, 420, 18);
        card.add(lblScoreStadiumAtt);

        return card;
    }

    private static void updateScoreboardValues(
            best.I match,
            JLabel sHome, JLabel sAway, JLabel mTime, JLabel mPeriod,
            JLabel stadium, JLabel att
    ) {
        if (scoreboardCard == null) return;
        try {
            if (lblScoreTimePeriod != null) {
                String tText = (mTime != null && mTime.getText() != null) ? mTime.getText() : "0'";
                String pText = (mPeriod != null && mPeriod.getText() != null) ? mPeriod.getText() : "1º";
                lblScoreTimePeriod.setText(tText + "  •  " + pText);
            }
            if (lblScoreStadiumAtt != null) {
                String stadText = (stadium != null && stadium.getText() != null) ? stadium.getText() : "Estádio";
                String attText = (att != null && att.getText() != null) ? att.getText() : "";
                String fullInfo = stadText;
                if (!attText.isEmpty()) {
                    fullInfo += "   •   " + attText + " torcedores";
                }
                lblScoreStadiumAtt.setText(fullInfo);
            }
        } catch (Exception ignored) {}
        scoreboardCard.repaint();
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

        final TableCellRenderer orig = table.getDefaultRenderer(best.A.class);
        table.setDefaultRenderer(best.A.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = (orig != null) 
                        ? orig.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col) 
                        : new DefaultTableCellRenderer().getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);

                c.setBackground((row % 2 == 0) ? PANEL_DARK : new Color(22, 30, 48));
                c.setForeground(TEXT_WHITE);
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                }
                return c;
            }
        });
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

    private static JPanel buildOpponentInfoCard(best.I match, JLabel formLbl) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(PANEL_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_SLATE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        String coach = "Adversário";
        try {
            if (match != null && match.hd() != null && match.hd().ka() != null) {
                coach = match.hd().ka().dS();
            }
        } catch (Exception ignored) {}

        String formation = (formLbl != null && formLbl.getText() != null) ? formLbl.getText() : "4-4-2";

        JLabel lblTitle = new JLabel("COMANDO TÉCNICO & ESTRATÉGIA DO ADVERSÁRIO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setBounds(15, 10, 400, 20);
        card.add(lblTitle);

        lblOppCoach = new JLabel("Técnico: " + coach);
        lblOppCoach.setFont(new Font("Verdana", Font.BOLD, 12));
        lblOppCoach.setForeground(TEXT_WHITE);
        lblOppCoach.setBounds(15, 34, 300, 20);
        card.add(lblOppCoach);

        lblOppFormation = new JLabel("Esquema Tático: " + formation);
        lblOppFormation.setFont(new Font("Verdana", Font.PLAIN, 11));
        lblOppFormation.setForeground(GOLD_ACCENT);
        lblOppFormation.setBounds(330, 34, 250, 20);
        card.add(lblOppFormation);

        return card;
    }

    private static void updateOpponentInfo(best.I match, JLabel formLbl) {
        if (oppInfoCard == null) return;
        try {
            String coach = "Adversário";
            if (match != null && match.hd() != null && match.hd().ka() != null) {
                coach = match.hd().ka().dS();
            }
            if (lblOppCoach != null) {
                lblOppCoach.setText("Técnico: " + coach);
            }
            if (lblOppFormation != null && formLbl != null && formLbl.getText() != null) {
                lblOppFormation.setText("Esquema Tático: " + formLbl.getText());
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
