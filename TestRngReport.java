import mod.brasfoot.VarManager;
import mod.brasfoot.VarDialog;

public class TestRngReport {
    public static void main(String[] args) {
        System.out.println("--- SIMULAÇÃO DE 10.000 LANCES DE GOL ---");
        
        // 1. Chance de chamar o VAR
        int chamouGolPadrao = 0;
        int chamouGolContraAtaque = 0;
        for (int i = 0; i < 10000; i++) {
            if (VarManager.deveChamarVarMonitor(VarDialog.EVENT_GOL, VarManager.ESTILO_EQUILIBRADO, 1, 5, 80, 80)) {
                chamouGolPadrao++;
            }
            if (VarManager.deveChamarVarMonitor(VarDialog.EVENT_GOL, VarManager.ESTILO_CONTRA_ATAQUE, 2, 2, 50, 50)) {
                chamouGolContraAtaque++;
            }
        }
        System.out.println("Chance de chamar VAR (Gol Equilibrado): " + (chamouGolPadrao / 100.0) + "%");
        System.out.println("Chance de chamar VAR (Contra-Ataque extremo): " + (chamouGolContraAtaque / 100.0) + "%");

        // 2. Decisão: Time da Casa vs Visitante
        int confirmouMandante = 0;
        int confirmouVisitante = 0;
        for (int i = 0; i < 10000; i++) {
            int resCasa = VarManager.sortearDesfechoGol(VarManager.ESTILO_EQUILIBRADO, 1, 5, 80, true);
            if (resCasa == VarDialog.RESULT_GOL_LEGAL) confirmouMandante++;

            int resFora = VarManager.sortearDesfechoGol(VarManager.ESTILO_EQUILIBRADO, 1, 5, 80, false);
            if (resFora == VarDialog.RESULT_GOL_LEGAL) confirmouVisitante++;
        }
        System.out.println("\n--- DECISÃO DO VAR COM FATOR CASA (+15%) ---");
        System.out.println("Gol do Time da Casa -> Confirmado: " + (confirmouMandante / 100.0) + "% | Anulado: " + ((10000 - confirmouMandante) / 100.0) + "%");
        System.out.println("Gol do Visitante     -> Confirmado: " + (confirmouVisitante / 100.0) + "% | Anulado: " + ((10000 - confirmouVisitante) / 100.0) + "%");
    }
}
