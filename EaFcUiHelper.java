package mod.brasfoot;

import java.awt.*;
import javax.swing.*;

public class EaFcUiHelper {

    // Paleta EA Sports FC 2027
    public static final Color BG_DARK = new Color(9, 13, 22);          // #090D16 Deep Obsidian
    public static final Color PANEL_DARK = new Color(15, 23, 42);       // #0F172A Midnight Glass
    public static final Color CARD_BG = new Color(20, 28, 46);          // #141C2E Elevated Surface
    public static final Color BORDER_COLOR = new Color(30, 41, 59);     // #1E293B Subtle Border
    public static final Color TEXT_WHITE = new Color(248, 250, 252);    // #F8FAFC
    public static final Color TEXT_MUTED = new Color(148, 163, 184);    // #94A3B8
    public static final Color EMERALD = new Color(16, 185, 129);        // #10B981 Action Glow
    public static final Color CYAN = new Color(6, 182, 212);            // #06B6D4 Accent

    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Aplica a estilizacao EA FC 27 na Tela de Escalacao (C0053bz) em 1024x748.
     */
    public static void applyPreMatchLayout(
            JPanel root,
            JLayeredPane bp,
            JLabel pitchLabel,
            JPanel grassPanel,
            JPanel tacticsPanel,
            JLabel benchHeader,
            JLabel availHeader,
            JCheckBox junioresCheck,
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
        try {
            // 1. Cores de Fundo (Obsidian Glass)
            root.setBackground(BG_DARK);
            bp.setBackground(BG_DARK);
            bp.setOpaque(true);

            // 2. Painel Inferior do Campo (Condicao do Gramado / Adversario)
            grassPanel.setBackground(PANEL_DARK);
            grassPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

            // 3. Cabecalho: Banco de Reservas (X = 435, Y = 0, W = 580, H = 22)
            benchHeader.setBackground(CARD_BG);
            benchHeader.setForeground(TEXT_WHITE);
            benchHeader.setFont(FONT_BOLD);
            benchHeader.setText("   \u26BD BANCO DE RESERVAS");
            benchHeader.setOpaque(true);
            benchHeader.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, EMERALD));

            // 4. Cabecalho: Jogadores Disponiveis (X = 435, Y = 185, W = 580, H = 22)
            availHeader.setBackground(CARD_BG);
            availHeader.setForeground(TEXT_WHITE);
            availHeader.setFont(FONT_BOLD);
            availHeader.setText("   \uD83D\uDCCB JOGADORES DISPON\u00CDVEIS");
            availHeader.setOpaque(true);
            availHeader.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, CYAN));

            // Checkbox Ver Juniores
            junioresCheck.setBackground(CARD_BG);
            junioresCheck.setForeground(TEXT_MUTED);
            junioresCheck.setFont(FONT_REGULAR);
            junioresCheck.setOpaque(false);

            // 5. Painel Tatico Inferior
            tacticsPanel.setBackground(PANEL_DARK);
            tacticsPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

            lblAuto.setFont(FONT_SMALL);
            lblAuto.setForeground(TEXT_MUTED);
            cbAuto.setBackground(CARD_BG);
            cbAuto.setForeground(TEXT_WHITE);

            lblEstilo.setFont(FONT_SMALL);
            lblEstilo.setForeground(TEXT_MUTED);
            cbEstilo.setBackground(CARD_BG);
            cbEstilo.setForeground(TEXT_WHITE);

            lblMarcacao.setFont(FONT_SMALL);
            lblMarcacao.setForeground(TEXT_MUTED);
            cbMarcacao.setBackground(CARD_BG);
            cbMarcacao.setForeground(TEXT_WHITE);

            lblAtaque.setFont(FONT_SMALL);
            lblAtaque.setForeground(TEXT_MUTED);
            cbAtaque.setBackground(CARD_BG);
            cbAtaque.setForeground(TEXT_WHITE);

            // Botoes
            btnTaticas.setBackground(CARD_BG);
            btnTaticas.setForeground(TEXT_WHITE);
            btnTaticas.setFont(FONT_BOLD);
            btnTaticas.setFocusPainted(false);
            btnTaticas.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

            btnVoltar.setBackground(CARD_BG);
            btnVoltar.setForeground(TEXT_WHITE);
            btnVoltar.setFont(FONT_BOLD);
            btnVoltar.setFocusPainted(false);
            btnVoltar.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

            // Botao Jogar com destaque Neon Emerald
            btnJogar.setBackground(EMERALD);
            btnJogar.setForeground(Color.BLACK);
            btnJogar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnJogar.setFocusPainted(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aplica a estilizacao EA FC 27 na Tela do Treinador Durante a Partida (cD).
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
            root.setBackground(BG_DARK);
            df.setBackground(BG_DARK);
            df.setOpaque(true);

            teamHome.setFont(new Font("Segoe UI", Font.BOLD, 13));
            teamHome.setForeground(TEXT_WHITE);

            scoreHome.setFont(new Font("Segoe UI", Font.BOLD, 18));
            scoreHome.setForeground(TEXT_WHITE);

            teamAway.setFont(new Font("Segoe UI", Font.BOLD, 13));
            teamAway.setForeground(TEXT_WHITE);

            scoreAway.setFont(new Font("Segoe UI", Font.BOLD, 18));
            scoreAway.setForeground(TEXT_WHITE);

            matchTime.setFont(new Font("Segoe UI", Font.BOLD, 18));
            matchTime.setForeground(EMERALD);

            btnVoltarJogo.setBackground(EMERALD);
            btnVoltarJogo.setForeground(Color.BLACK);
            btnVoltarJogo.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnVoltarJogo.setFocusPainted(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
