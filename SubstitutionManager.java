package mod.brasfoot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import javax.swing.*;

public class SubstitutionManager {

    // Guarda os titulares iniciais da sessao de pausa (slot 1..25 -> jogador best.F)
    private static final Map<Integer, best.F> initialStarters = new LinkedHashMap<>();
    private static boolean sessionActive = false;

    /**
     * Chamado ao abrir a tela cD (inicio da pausa).
     * Identifica dinamicamente se o usuario e mandante (Di == 1 -> AE) ou visitante (Di == 2 -> Dc).
     */
    public static void onPauseOpen(Object cDInstance) {
        initialStarters.clear();
        sessionActive = true;
        if (cDInstance == null) return;

        try {
            Class<?> clazz = cDInstance.getClass();

            // Pega Di (1 = Mandante, 2 = Visitante)
            Field diField = clazz.getDeclaredField("Di");
            diField.setAccessible(true);
            int di = diField.getInt(cDInstance);

            // Pega a lista correta de componentes de jogadores (AE ou Dc)
            Field listField = (di == 2) ? clazz.getDeclaredField("Dc") : clazz.getDeclaredField("AE");
            listField.setAccessible(true);
            ArrayList<?> list = (ArrayList<?>) listField.get(cDInstance);

            if (list == null) return;

            // Grava os 11 titulares iniciais com seus slots de campo (1..25)
            for (int i = 1; i <= 25; i++) {
                if (i < list.size()) {
                    Object ao = list.get(i);
                    if (ao instanceof components.ao) {
                        components.ao aoComp = (components.ao) ao;
                        best.F player = aoComp.x();
                        if (player != null) {
                            initialStarters.put(i, player);
                            player.as(i); // Garante que o slot do titular esta perfeitamente sincronizado
                        }
                    }
                }
            }
            System.out.println("[SubstitutionManager] Session started (Team Di=" + di + "). Initial starters recorded: " + initialStarters.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intercepta K(i, i2) dentro de cD.H(i, i2) para fazer a troca livre estilo EA FC / PES!
     */
    public static void handleSwap(Object cDInstance, int i, int i2) {
        if (cDInstance == null) return;

        try {
            Class<?> clazz = cDInstance.getClass();

            // Pega o time ativo (Di: 1 ou 2)
            Field diField = clazz.getDeclaredField("Di");
            diField.setAccessible(true);
            int di = diField.getInt(cDInstance);

            // Pega a lista de jogadores (AE para time 1, Dc para time 2)
            Field listField = (di == 2) ? clazz.getDeclaredField("Dc") : clazz.getDeclaredField("AE");
            listField.setAccessible(true);
            ArrayList<?> list = (ArrayList<?>) listField.get(cDInstance);

            // Pega o icone da camisa (AL para time 1, Dd para time 2)
            Field iconField = (di == 2) ? clazz.getDeclaredField("Dd") : clazz.getDeclaredField("AL");
            iconField.setAccessible(true);
            ImageIcon shirtIcon = (ImageIcon) iconField.get(cDInstance);

            // Pega components.v (Db) e motor da partida
            Field dbField = clazz.getDeclaredField("Db");
            dbField.setAccessible(true);
            components.v vObj = (components.v) dbField.get(cDInstance);
            best.I matchEngine = vObj.tR();
            int totalAllowed = matchEngine.aR(di - 1);

            // Identifica slot do campo (1..25) e slot do banco (>= 26)
            int fieldSlot = Math.min(i, i2);
            int benchSlot = Math.max(i, i2);

            if (fieldSlot < 1 || fieldSlot > 25 || benchSlot >= list.size() || benchSlot < 26) {
                return;
            }

            components.ao aoField = (components.ao) list.get(fieldSlot);
            components.ao aoBench = (components.ao) list.get(benchSlot);

            best.F pField = aoField.x();
            best.F pBench = aoBench.x();

            if (pField == null || pBench == null) {
                return;
            }

            // Simula a troca para verificar se o limite de substituicoes seria ultrapassado
            int currentNetSubs = calculateNetSubsAfterSwap(list, fieldSlot, benchSlot, pBench, pField);

            if (currentNetSubs > totalAllowed) {
                // Limite de substituicoes atingido!
                System.out.println("[SubstitutionManager] Troca bloqueada: limite de substituicoes (" + totalAllowed + ") excedido!");
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }

            // Realiza a troca livre:
            // 1. Jogador do banco vai para o campo no slot fieldSlot
            aoField.a(pBench);
            pBench.as(fieldSlot); // O novo jogador de campo assume o slot de campo (1..25)

            // 2. Jogador que saiu do campo vai para o banco
            aoBench.a(pField);
            aoBench.h(shirtIcon); // Mantem a camisa visivel no banco!
            pField.as(0); // IMPORTANTE: jogador no banco tem slot 0, NUNCA benchSlot (> 25)!

            // 3. Atualiza o contador de substituicoes restantes na tela (DA)
            try {
                Field daField = clazz.getDeclaredField("DA");
                daField.setAccessible(true);
                JLabel lblDA = (JLabel) daField.get(cDInstance);
                if (lblDA != null) {
                    int remaining = totalAllowed - currentNetSubs;
                    lblDA.setText("Substituições restantes: " + remaining);
                }
            } catch (Exception ignored) {}

            // 4. Atualiza a imagem grafica do banco de reservas (cS(-1))
            try {
                Method csMethod = clazz.getDeclaredMethod("cS", int.class);
                csMethod.setAccessible(true);
                csMethod.invoke(cDInstance, -1);
            } catch (Exception ignored) {}

            // 5. Redesenha a tela
            if (cDInstance instanceof JComponent) {
                ((JComponent) cDInstance).repaint();
            }

            System.out.println("[SubstitutionManager] Free swap: " + pField.getNome() + " <-> " + pBench.getNome() 
                + " (Substituicoes pendentes: " + currentNetSubs + "/" + totalAllowed + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int calculateNetSubsAfterSwap(ArrayList<?> list, int fSlot, int bSlot, best.F simField, best.F simBench) {
        Set<best.F> simulatedStarters = new HashSet<>();
        for (int s = 1; s <= 25; s++) {
            if (s < list.size()) {
                components.ao ao = (components.ao) list.get(s);
                if (ao != null) {
                    if (s == fSlot) {
                        simulatedStarters.add(simField);
                    } else {
                        if (ao.x() != null) {
                            simulatedStarters.add(ao.x());
                        }
                    }
                }
            }
        }

        int changed = 0;
        for (best.F originalPlayer : initialStarters.values()) {
            if (!simulatedStarters.contains(originalPlayer)) {
                changed++;
            }
        }
        return changed;
    }

    /**
     * Chamado quando o jogador clica em "Voltar ao jogo" (em cD.pb).
     * Efetiva apenas as substituicoes reais no motor do jogo com mapeamento estrito de posicoes!
     */
    public static void onResumeGame(Object cDInstance) {
        if (!sessionActive || cDInstance == null) return;
        sessionActive = false;

        try {
            Class<?> clazz = cDInstance.getClass();

            Field diField = clazz.getDeclaredField("Di");
            diField.setAccessible(true);
            int di = diField.getInt(cDInstance);

            Field listField = (di == 2) ? clazz.getDeclaredField("Dc") : clazz.getDeclaredField("AE");
            listField.setAccessible(true);
            ArrayList<?> list = (ArrayList<?>) listField.get(cDInstance);

            Field dbField = clazz.getDeclaredField("Db");
            dbField.setAccessible(true);
            components.v vObj = (components.v) dbField.get(cDInstance);
            best.I matchEngine = vObj.tR();

            Field minField = clazz.getDeclaredField("dB");
            minField.setAccessible(true);
            int minute = minField.getInt(cDInstance);

            Field edField = clazz.getDeclaredField("ed");
            edField.setAccessible(true);
            int ed = edField.getInt(cDInstance);

            // Mapeia os 11 jogadores atualmente em campo (slot -> jogador)
            Map<Integer, best.F> currentFieldPlayers = new LinkedHashMap<>();
            for (int i = 1; i <= 25; i++) {
                if (i < list.size()) {
                    components.ao aoComp = (components.ao) list.get(i);
                    if (aoComp != null && aoComp.x() != null) {
                        currentFieldPlayers.put(i, aoComp.x());
                    }
                }
            }

            // Identifica titulares originais que sairam do campo
            List<best.F> playersOut = new ArrayList<>();
            Map<best.F, Integer> initialSlotOfPlayerOut = new HashMap<>();
            for (Map.Entry<Integer, best.F> entry : initialStarters.entrySet()) {
                if (!currentFieldPlayers.containsValue(entry.getValue())) {
                    playersOut.add(entry.getValue());
                    initialSlotOfPlayerOut.put(entry.getValue(), entry.getKey());
                }
            }

            // Identifica novos jogadores que entraram no campo
            List<best.F> playersIn = new ArrayList<>();
            Map<best.F, Integer> currentSlotOfPlayerIn = new HashMap<>();
            for (Map.Entry<Integer, best.F> entry : currentFieldPlayers.entrySet()) {
                if (!initialStarters.containsValue(entry.getValue())) {
                    playersIn.add(entry.getValue());
                    currentSlotOfPlayerIn.put(entry.getValue(), entry.getKey());
                }
            }

            int subsToCommit = Math.min(playersOut.size(), playersIn.size());
            int subsRemaining = matchEngine.aR(di - 1);
            int committed = 0;

            // Pareamento inteligente de saida e entrada para manter a posicao exata
            for (int s = 0; s < subsToCommit && s < subsRemaining; s++) {
                best.F pIn = playersIn.get(s);
                int targetSlot = currentSlotOfPlayerIn.get(pIn);

                // Tenta achar um pOut que jogava naquele mesmo slot
                best.F pOut = null;
                for (best.F cand : playersOut) {
                    Integer origSlot = initialSlotOfPlayerOut.get(cand);
                    if (origSlot != null && origSlot == targetSlot) {
                        pOut = cand;
                        break;
                    }
                }
                if (pOut == null) {
                    pOut = playersOut.get(0);
                }
                playersOut.remove(pOut);

                // Define o slot temporario do pOut como targetSlot antes de chamar o motor
                pOut.as(targetSlot);

                // Executa no motor nativo do Brasfoot
                best.A subEvent = matchEngine.a(di - 1, pOut, pIn, minute, ed, -1);
                if (subEvent != null) {
                    subEvent.p(true);
                }

                // Garante que pIn tenha o slot de campo exato
                pIn.as(targetSlot);
                pOut.as(0); // pOut fica no banco com slot 0

                committed++;
                System.out.println("[SubstitutionManager] COMMITTED SUB: SAI " + pOut.getNome() 
                    + " -> ENTRA " + pIn.getNome() + " (Slot " + targetSlot + ")");
            }

            // =========================================================================
            // GARANTIA DE INTEGRIDADE: Sincroniza todos os slots dos 11 titulares em campo!
            // =========================================================================
            for (Map.Entry<Integer, best.F> entry : currentFieldPlayers.entrySet()) {
                entry.getValue().as(entry.getKey());
            }

            // Garante que os reservas tenham slot 0 (fora do campo)
            for (int b = 26; b < list.size(); b++) {
                components.ao aoB = (components.ao) list.get(b);
                if (aoB != null && aoB.x() != null) {
                    aoB.x().as(0);
                }
            }

            if (committed > 0) {
                Field tableField = clazz.getDeclaredField("DG");
                tableField.setAccessible(true);
                JTable table = (JTable) tableField.get(cDInstance);
                if (table != null) {
                    table.addNotify();
                }
            }

            System.out.println("[SubstitutionManager] Resuming match: " + committed + " real substitution(s) committed.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            initialStarters.clear();
        }
    }
}
