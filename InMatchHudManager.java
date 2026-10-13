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
 * Implementa a refatoração Widescreen Full HD (1740 x 880) do Design System EA FC:
 * - Dois campos simultâneos (Mandante à esquerda, Visitante à direita)
 * - Hub Central: Placar Broadcast -> Log de Eventos -> Stats em Barras -> Radar Tático -> Voltar ao Jogo
 * - Eliminação completa do fundo verde textura/madeira (/img/f02.jpg)
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
    private static TacticalRadarPanel radarPanel = null;
    private static JPanel scoreboardCard = null;
    private static JPanel oppPreviewPanel = null;
    private static JLabel oppPitchLabel = null;
    private static JLabel oppBenchLabel = null;

    private static JLabel lblHomeHeader = null;
    private static JLabel lblAwayHeader = null;
    private static JPanel oppInfoCard = null;

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
            JLabel matchTime = (JLabel) getFieldValue(cDObj, "zO");
            JLabel matchPeriod = (JLabel) getFieldValue(cDObj, "zP");

            JLabel stadiumLbl = (JLabel) getFieldValue(cDObj, "Dv");
            JLabel attLbl = (JLabel) getFieldValue(cDObj, "DE");
            JLabel revLbl = (JLabel) getFieldValue(cDObj, "xI");

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

            // Esconde labels nativas antigas de fundo ou irrelevantes
            hideComponent((Component) getFieldValue(cDObj, "vd"));
            hideComponent((Component) getFieldValue(cDObj, "we"));
            hideComponent(revLbl); // substituído pelo painel de stats

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
            lblHomeHeader.setText("  " + homeName.toUpperCase() + "  [MANDANTE]");
            lblHomeHeader.setIcon(homeCrest);
            lblHomeHeader.setBounds(20, 8, 620, 28);
            root.add(lblHomeHeader);

            if (lblAwayHeader == null) {
                lblAwayHeader = new JLabel();
                lblAwayHeader.setFont(new Font("Verdana", Font.BOLD, 13));
                lblAwayHeader.setForeground(TEXT_WHITE);
                lblAwayHeader.setHorizontalAlignment(SwingConstants.RIGHT);
            }
            lblAwayHeader.setText("[VISITANTE]  " + awayName.toUpperCase() + "  ");
            lblAwayHeader.setIcon(awayCrest);
            lblAwayHeader.setHorizontalTextPosition(SwingConstants.LEFT);
            lblAwayHeader.setBounds(1100, 8, 620, 28);
            root.add(lblAwayHeader);

            // =========================================================
            // 3. CAMPOS DE JOGO (DF INTERATIVO + PAINEL DO ADVERSÁRIO)
            // =========================================================
            // Configura o painel preview do adversário se ainda não criado
            if (oppPreviewPanel == null) {
                oppPreviewPanel = new JPanel(null);
                oppPreviewPanel.setOpaque(false);

                oppBenchLabel = new JLabel();
                oppBenchLabel.setBounds(0, 0, 210, 650);
                oppPreviewPanel.add(oppBenchLabel);

                oppPitchLabel = new JLabel();
                oppPitchLabel.setBounds(200, 0, 420, 650);
                oppPreviewPanel.add(oppPitchLabel);

                oppPreviewPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        switchActiveTeam(cDObj);
                    }
                });
            }

            if (activeTeam == 1) {
                // Usuário focado no Mandante: DF na esquerda, Adversário na direita
                if (df != null) df.setBounds(20, 38, 620, 650);
                oppPreviewPanel.setBounds(1100, 38, 620, 650);
            } else {
                // Usuário focado no Visitante: DF na direita, Adversário na esquerda
                if (df != null) df.setBounds(1100, 38, 620, 650);
                oppPreviewPanel.setBounds(20, 38, 620, 650);
            }

            if (!isChildOf(root, oppPreviewPanel)) {
                root.add(oppPreviewPanel);
            }

            refreshOpponentPitch(cDObj, activeTeam);

            // =========================================================
            // 4. CONTROLES TÁTICOS (ABAIXO DO CAMPO DO MANDANTE/USUÁRIO)
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
            // 5. HUB ANALÍTICO CENTRAL (x: 650, w: 440)
            // =========================================================

            // 5.1 Placar Broadcast TV (y: 10, h: 95)
            if (scoreboardCard == null) {
                scoreboardCard = buildScoreboardCard(cDObj, match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            } else {
                updateScoreboardValues(match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);
            }
            scoreboardCard.setBounds(650, 10, 440, 95);
            if (!isChildOf(root, scoreboardCard)) {
                root.add(scoreboardCard);
            }

            // 5.2 Log de Eventos da Partida (y: 112, h: 180)
            if (scrollEvents != null) {
                scrollEvents.setBounds(650, 112, 440, 180);
                styleEventsTable(scrollEvents, tableEvents);
            }

            // 5.3 Estatísticas Comparativas em Barras (y: 298, h: 190)
            if (statsPanel == null) {
                statsPanel = new InMatchStatsPanel();
            }
            statsPanel.setBounds(650, 298, 440, 190);
            if (match != null) statsPanel.updateStats(match);
            if (!isChildOf(root, statsPanel)) {
                root.add(statsPanel);
            }

            // 5.4 Radar Tático Hexagonal (y: 494, h: 290)
            if (radarPanel == null) {
                radarPanel = new TacticalRadarPanel();
            }
            radarPanel.setBounds(650, 494, 440, 290);
            updateRadarStats(cDObj, match);
            if (!isChildOf(root, radarPanel)) {
                root.add(radarPanel);
            }

            // 5.5 Botão de Ação "VOLTAR AO JOGO" (y: 794, h: 42)
            if (btnVoltar != null) {
                btnVoltar.setBounds(745, 794, 250, 42);
                EaFcUiHelper.styleActionPillButton(btnVoltar, "< VOLTAR AO JOGO", true);
            }

            // 6. Painel de Resumo do Adversário (abaixo do campo visitante)
            if (oppInfoCard == null) {
                oppInfoCard = buildOpponentInfoCard(match);
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
            JLabel matchTime = (JLabel) getFieldValue(cDObj, "zO");
            JLabel matchPeriod = (JLabel) getFieldValue(cDObj, "zP");
            JLabel stadiumLbl = (JLabel) getFieldValue(cDObj, "Dv");
            JLabel attLbl = (JLabel) getFieldValue(cDObj, "DE");

            updateScoreboardValues(match, scoreHome, scoreAway, matchTime, matchPeriod, stadiumLbl, attLbl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Alterna suavemente entre Mandante e Visitante chamando o método a/cD.a(this, teamId).
     */
    private static void switchActiveTeam(Object cDObj) {
        try {
            int cur = getIntField(cDObj, "Di", 1);
            int next = (cur == 1) ? 2 : 1;
            Method switchM = cDObj.getClass().getDeclaredMethod("a", cDObj.getClass(), int.class);
            switchM.setAccessible(true);
            switchM.invoke(null, cDObj, next);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Renderiza o campo do adversário usando o gerador gráfico oficial de components.ao.
     */
    private static void refreshOpponentPitch(Object cDObj, int activeTeam) {
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

            if (list != null && !list.isEmpty() && oppPitchLabel != null && oppBenchLabel != null) {
                components.ao firstAo = (components.ao) list.get(0);
                if (firstAo != null) {
                    ImageIcon pitchIcon = firstAo.a(list, -1, jersey, false, oppIndex, tactics, 2);
                    ImageIcon benchIcon = firstAo.a(list, -1, jersey, false, oppIndex, tactics, 3);
                    oppPitchLabel.setIcon(pitchIcon);
                    oppBenchLabel.setIcon(benchIcon);
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
        lblCrestH.setBounds(20, 20, 40, 40);
        card.add(lblCrestH);

        JLabel lblCrestA = new JLabel(crestA);
        lblCrestA.setBounds(380, 20, 40, 40);
        card.add(lblCrestA);

        // Placares (Dw e Dx)
        if (sHome != null) {
            sHome.setFont(new Font("Segoe UI", Font.BOLD, 28));
            sHome.setForeground(TEXT_WHITE);
            sHome.setHorizontalAlignment(SwingConstants.CENTER);
            sHome.setBounds(70, 18, 55, 42);
            card.add(sHome);
        }

        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblX.setForeground(TEXT_MUTED);
        lblX.setHorizontalAlignment(SwingConstants.CENTER);
        lblX.setBounds(130, 26, 20, 30);
        card.add(lblX);

        if (sAway != null) {
            sAway.setFont(new Font("Segoe UI", Font.BOLD, 28));
            sAway.setForeground(TEXT_WHITE);
            sAway.setHorizontalAlignment(SwingConstants.CENTER);
            sAway.setBounds(155, 18, 55, 42);
            card.add(sAway);
        }

        // Pill Central de Tempo Decorrido (38' 2ºT)
        JPanel timePill = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                EaFcUiHelper.applyHighQualityRendering(g2);
                g2.setColor(new Color(16, 185, 129, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
            }
        };
        timePill.setOpaque(false);
        timePill.setBounds(230, 24, 135, 30);

        if (mTime != null) {
            mTime.setFont(new Font("Verdana", Font.BOLD, 14));
            mTime.setForeground(Color.WHITE);
            timePill.add(mTime);
        }
        if (mPeriod != null) {
            mPeriod.setFont(new Font("Verdana", Font.BOLD, 12));
            mPeriod.setForeground(Color.WHITE);
            timePill.add(mPeriod);
        }
        card.add(timePill);

        // Estádio e Público / Renda (Rodapé do Card)
        String stadText = (stadium != null && stadium.getText() != null) ? stadium.getText() : "Estádio";
        String attText = (att != null && att.getText() != null) ? att.getText() : "";
        JLabel lblMatchInfo = new JLabel(stadText + "  •  " + attText);
        lblMatchInfo.setFont(new Font("Verdana", Font.PLAIN, 10));
        lblMatchInfo.setForeground(TEXT_MUTED);
        lblMatchInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblMatchInfo.setBounds(10, 68, 420, 20);
        card.add(lblMatchInfo);

        return card;
    }

    private static void updateScoreboardValues(
            best.I match,
            JLabel sHome, JLabel sAway, JLabel mTime, JLabel mPeriod,
            JLabel stadium, JLabel att
    ) {
        if (scoreboardCard == null) return;
        scoreboardCard.repaint();
    }

    /**
     * Estiliza o JTable e o JScrollPane de eventos para o visual moderno EA FC Dark
     * preservando os icones originais de bola, cartao, lesao e substituicao.
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
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col
            ) {
                Component c = (orig != null) 
                        ? orig.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col)
                        : new JLabel();
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(true);
                }
                c.setBackground(row % 2 == 0 ? PANEL_DARK : CARD_BG);
                c.setForeground(TEXT_WHITE);
                c.setFont(new Font("Verdana", Font.PLAIN, 11));
                return c;
            }
        });
    }

    /**
     * Atualiza o radar hexagonal tático comparativo com as médias ao vivo dos dois times.
     */
    private static void updateRadarStats(Object cDObj, best.I match) {
        if (radarPanel == null || match == null) return;
        try {
            ArrayList<?> listH = (ArrayList<?>) getFieldValue(cDObj, "AE");
            ArrayList<?> listA = (ArrayList<?>) getFieldValue(cDObj, "Dc");

            List<best.F> homeStarters = extractStarters(listH);
            List<Integer> homeSlots = extractSlots(listH);

            List<best.F> awayStarters = extractStarters(listA);
            List<Integer> awaySlots = extractSlots(listA);

            int homeOvr = EaFcUiHelper.calculateOverall(homeStarters, homeSlots);
            int[] homeAttrs = EaFcUiHelper.calculateTeamAttributes(homeStarters, homeSlots);

            int awayOvr = EaFcUiHelper.calculateOverall(awayStarters, awaySlots);
            int[] awayAttrs = EaFcUiHelper.calculateTeamAttributes(awayStarters, awaySlots);

            String nameH = (match.hc() != null) ? match.hc().getNome() : "CASA";
            String nameA = (match.hd() != null) ? match.hd().getNome() : "FORA";
            ImageIcon crestH = (match.hc() != null) ? match.hc().x(24, 24) : null;
            ImageIcon crestA = (match.hd() != null) ? match.hd().x(24, 24) : null;

            radarPanel.updateAllStats(
                    nameH, crestH, homeOvr, homeAttrs,
                    true, nameA, crestA, awayOvr, awayAttrs
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

    private static JPanel buildOpponentInfoCard(best.I match) {
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

        JLabel lblTitle = new JLabel("PLANO DE JOGO DO ADVERSÁRIO (IA)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setBounds(15, 8, 300, 20);
        card.add(lblTitle);

        JLabel lblCoach = new JLabel("Técnico: " + coach);
        lblCoach.setFont(new Font("Verdana", Font.BOLD, 11));
        lblCoach.setForeground(TEXT_WHITE);
        lblCoach.setBounds(15, 30, 250, 20);
        card.add(lblCoach);

        JLabel lblHint = new JLabel("💡 Clique no campo adversário para alternar foco ou inspecionar");
        lblHint.setFont(new Font("Verdana", Font.PLAIN, 10));
        lblHint.setForeground(new Color(148, 163, 184));
        lblHint.setBounds(15, 52, 450, 18);
        card.add(lblHint);

        return card;
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

    private static int getIntField(Object target, String fieldName, int def) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.getInt(target);
        } catch (Exception e) {
            return def;
        }
    }
}
