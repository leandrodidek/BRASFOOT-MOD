package mod.brasfoot;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Constructor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VarOptionsHelper {

    public static void injectVarOption(Object fbInstance, JPanel vdPanel) {
        if (vdPanel == null) return;

        try {
            // Label "VAR automático:"
            JLabel lblVar = new JLabel("VAR automático:");
            lblVar.setFont(new Font("Tahoma", Font.PLAIN, 12));
            lblVar.setHorizontalAlignment(SwingConstants.RIGHT);
            lblVar.setForeground(new Color(255, 255, 255));

            // ComboBox com "Sim" e "Não"
            String[] options = {"Sim", "Não"};
            JComboBox<String> cbVar = new JComboBox<>(options);
            cbVar.setFont(new Font("Tahoma", Font.PLAIN, 11));
            cbVar.setSelectedIndex(VarManager.isVarAutomatico() ? 0 : 1);

            cbVar.addActionListener(e -> {
                boolean auto = (cbVar.getSelectedIndex() == 0);
                VarManager.setVarAutomatico(auto);
                System.out.println("[VAR] Opcao alterada no menu Opcoes: VAR Automatico = " + auto);
            });

            // Layout customizado do Brasfoot: components.a(x, y, w, h)
            Class<?> aLayoutClass = Class.forName("components.a");
            Constructor<?> aConstr = aLayoutClass.getConstructor(int.class, int.class, int.class, int.class);

            // Posiciona em (x=20, y=510) que esta totalmente vago no painel esquerdo vd
            Object constrLbl = aConstr.newInstance(10, 510, 140, 24);
            Object constrCb = aConstr.newInstance(160, 508, 90, 26);

            vdPanel.add(lblVar, constrLbl);
            vdPanel.add(cbVar, constrCb);

            vdPanel.revalidate();
            vdPanel.repaint();
            System.out.println("[VarOptionsHelper] 'VAR automatico: Sim/Nao' injetado com sucesso no menu Opcoes!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
