package mod.brasfoot;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Janela Modal / HUD de Análise Tática e Radar de Forças estilo EA Sports FC & PES.
 */
public class TacticalRadarDialog extends JDialog {

    private final TacticalRadarPanel radarPanel;
    private static TacticalRadarDialog activeInstance = null;

    public TacticalRadarDialog(Window parent) {
        super(parent, "EA SPORTS FC 27 \u2022 AN\u00C1LISE T\u00C1TICA DO ELENCO", ModalityType.MODELESS);
        activeInstance = this;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparência nos cantos
        setLayout(new BorderLayout());

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(EaFcUiHelper.BG_DARK);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EaFcUiHelper.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Barra Superior com Título e Botão Fechar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel titleLbl = new JLabel("\uD83D\uDCCA RELAT\u00D3RIO T\u00C1TICO EA FC 27");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(EaFcUiHelper.EMERALD);

        JButton btnClose = new JButton("\u2715");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setForeground(EaFcUiHelper.TEXT_MUTED);
        btnClose.setBackground(EaFcUiHelper.CARD_BG);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                activeInstance = null;
            }
        });

        topBar.add(titleLbl, BorderLayout.WEST);
        topBar.add(btnClose, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        radarPanel = new TacticalRadarPanel(false);
        container.add(radarPanel, BorderLayout.CENTER);

        // Barra Inferior com Dica
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBar.setOpaque(false);
        JLabel tipLbl = new JLabel("Pressione ESC para fechar \u2022 Atualiza\u00E7\u00E3o din\u00E2mica a cada troca de titular");
        tipLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tipLbl.setForeground(EaFcUiHelper.TEXT_MUTED);
        bottomBar.add(tipLbl);
        container.add(bottomBar, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(parent);

        // Fechar com tecla ESC
        container.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                activeInstance = null;
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Permitir arrastar a janela clicando na topBar
        final Point dragOffset = new Point();
        topBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragOffset.setLocation(e.getPoint());
            }
        });
        topBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - dragOffset.x, p.y + e.getY() - dragOffset.y);
            }
        });
    }

    public static TacticalRadarDialog getActiveInstance() {
        return activeInstance;
    }

    public void updateStats(int atk, int mid, int def, int gk, int phys, int pace, int ovr, int benchOvr, double avgAge, String formation, String teamName) {
        if (radarPanel != null) {
            radarPanel.updateStats(atk, mid, def, gk, phys, pace, ovr, benchOvr, avgAge, formation, teamName);
        }
    }
}
