package mod.brasfoot;

public class SimulateVarRng {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        SIMULACAO DE TESTES DO NOVO SISTEMA TACTICAL VAR RNG (10.000 LANCES)     ");
        System.out.println("================================================================================\n");

        // 1. CENARIO A: CONTRA-ATAQUE (Ataque) vs RETRANCA 5-4-1 (Defesa)
        simularCenario("CENARIO A: Contra-Ataque vs Retranca 5-4-1 (Linha de 5 Zagueiros)",
            VarManager.ESTILO_CONTRA_ATAQUE, VarManager.MARCACAO_PESADA, 2, 75, 70);

        // 2. CENARIO B: ATAQUE TOTAL + DEFESA COM MARCACAO MUITO PESADA
        simularCenario("CENARIO B: Ataque Total vs Defesa com Marcacao Muito Pesada (Muita Disputa)",
            VarManager.ESTILO_ATAQUE_TOTAL, VarManager.MARCACAO_MUITO_PESADA, 5, 80, 65);

        // 3. CENARIO C: JOGO LIMPO EQUILIBRADO (Equilibrado vs Marcacao Leve, 4-4-2)
        simularCenario("CENARIO C: Jogo Limpo e Equilibrado (Equilibrado vs Marcacao Leve, 4-4-2)",
            VarManager.ESTILO_EQUILIBRADO, VarManager.MARCACAO_LEVE, 5, 85, 85);

        // 4. CENARIO D: FIM DE JOGO DESGASTADO (Energia 40% em ambos, Marcacao Muito Pesada)
        simularCenario("CENARIO D: Fim de Jogo com Fadiga (Energia 40%, Defesa Desesperada)",
            VarManager.ESTILO_CONTRA_ATAQUE, VarManager.MARCACAO_MUITO_PESADA, 2, 40, 38);

        // 5. TESTE DE EXPULSOES (10.000 lances de cartao vermelho por tipo de marcacao)
        simularExpulsoes();
    }

    private static void simularCenario(String titulo, int estiloAtq, int marcDef, int formDef, int nrgAtq, int nrgDef) {
        int totalGols = 10000;
        int chamouVar = 0;
        int golsLegais = 0;
        int golsAnuladosImp = 0;
        int golsAnuladosFalta = 0;

        for (int i = 0; i < totalGols; i++) {
            boolean foiAoMonitor = VarManager.deveChamarVarMonitor(VarDialog.EVENT_GOL, estiloAtq, marcDef, formDef, nrgAtq, nrgDef);
            if (foiAoMonitor) {
                chamouVar++;
                int desfecho = VarManager.sortearDesfechoGol(estiloAtq, marcDef, formDef, nrgAtq);
                if (desfecho == VarDialog.RESULT_GOL_LEGAL) {
                    golsLegais++;
                } else if (desfecho == VarDialog.RESULT_GOL_ANULADO_IMP) {
                    golsAnuladosImp++;
                } else if (desfecho == VarDialog.RESULT_GOL_ANULADO_FALTA) {
                    golsAnuladosFalta++;
                }
            } else {
                golsLegais++; // Gols sem chamada ao monitor sao validados direto
            }
        }

        int totalAnulados = golsAnuladosImp + golsAnuladosFalta;
        double pctChamouVar = (chamouVar * 100.0) / totalGols;
        double pctAnuladosDoTotal = (totalAnulados * 100.0) / totalGols;
        double pctConfirmadosDoVar = chamouVar > 0 ? (golsLegais - (totalGols - chamouVar)) * 100.0 / chamouVar : 0;
        double pctAnuladosDoVar = chamouVar > 0 ? (totalAnulados * 100.0) / chamouVar : 0;

        System.out.println("--- " + titulo + " ---");
        System.out.printf("  Total de Gols na Amostra: %,d\n", totalGols);
        System.out.printf("  Chamadas ao Monitor do VAR: %,d (%.1f%% dos lances de gol)\n", chamouVar, pctChamouVar);
        System.out.printf("  Decisoes no Monitor: %.1f%% Validados | %.1f%% Anulados\n", pctConfirmadosDoVar, pctAnuladosDoVar);
        System.out.printf("  Impacto no Placar: %,d Gols Validados (%.1f%%) vs %,d Gols Anulados (%.1f%%)\n",
            (totalGols - totalAnulados), ((totalGols - totalAnulados) * 100.0 / totalGols), totalAnulados, pctAnuladosDoTotal);
        if (totalAnulados > 0) {
            System.out.printf("  Motivo das Anulacoes: %.1f%% Impedimento | %.1f%% Falta de Ataque\n",
                (golsAnuladosImp * 100.0 / totalAnulados), (golsAnuladosFalta * 100.0 / totalAnulados));
        }
        System.out.println();
    }

    private static void simularExpulsoes() {
        System.out.println("================================================================================");
        System.out.println("               SIMULACAO DE EXPULSOES E REVERSOES PELO VAR (10.000 LANCES)       ");
        System.out.println("================================================================================\n");

        int[] marcacoes = {VarManager.MARCACAO_LEVE, VarManager.MARCACAO_PESADA, VarManager.MARCACAO_MUITO_PESADA};
        String[] nomes = {"Marcacao Leve (Jogo Limpo)", "Marcacao Pesada (Padrao)", "Marcacao Muito Pesada (Dura)"};

        for (int m = 0; m < marcacoes.length; m++) {
            int total = 10000;
            int chamouVar = 0;
            int vermelhosMantidos = 0;
            int vermelhosRevertidos = 0;

            for (int i = 0; i < total; i++) {
                boolean foiAoMonitor = VarManager.deveChamarVarMonitor(VarDialog.EVENT_VERMELHO, 0, marcacoes[m], 5, 75, 70);
                if (foiAoMonitor) {
                    chamouVar++;
                    int desfecho = VarManager.sortearDesfechoVermelho(marcacoes[m], 70);
                    if (desfecho == VarDialog.RESULT_VERMELHO_MANTIDO) {
                        vermelhosMantidos++;
                    } else {
                        vermelhosRevertidos++;
                    }
                } else {
                    vermelhosMantidos++; // Mantem expulsao nativa direta se o VAR nao interveio
                }
            }

            double pctChamouVar = (chamouVar * 100.0) / total;
            double pctRevertidoNoVar = chamouVar > 0 ? (vermelhosRevertidos * 100.0) / chamouVar : 0;
            double pctMantidoNoVar = chamouVar > 0 ? (vermelhosMantidos - (total - chamouVar)) * 100.0 / chamouVar : 0;

            System.out.println("--- Tipo: " + nomes[m] + " ---");
            System.out.printf("  Revisoes no Monitor: %,d (%.1f%% dos lances de vermelho)\n", chamouVar, pctChamouVar);
            System.out.printf("  Decisao na Cabine do VAR: %.1f%% Manteve Vermelho | %.1f%% Reverteu para Amarelo\n",
                pctMantidoNoVar, pctRevertidoNoVar);
            System.out.printf("  Resultado Geral: %,d Jogadores Expulsos (%.1f%%) | %,d Salvos com Amarelo (%.1f%%)\n\n",
                (total - vermelhosRevertidos), ((total - vermelhosRevertidos) * 100.0 / total),
                vermelhosRevertidos, (vermelhosRevertidos * 100.0 / total));
        }
    }
}
