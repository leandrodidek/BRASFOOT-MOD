package mod.brasfoot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import javax.swing.*;

public class SubstitutionManager {

    // Guarda os titulares iniciais da sessao de pausa SEPARADAMENTE por time:
    // teamIndex 0 (Mandante, AE) e teamIndex 1 (Visitante, Dc)
    private static final Map<Integer, best.F> initialStartersMandante = new LinkedHashMap<>();
    private static final Map<Integer, best.F> initialStartersVisitante = new LinkedHashMap<>();
    private static boolean sessionActive = false;

    /**
     * Chamado ao abrir a tela cD (inicio da pausa).
     * Grava os titulares iniciais de AMBOS os times separadamente para evitar qualquer
     * cruzamento indevido caso o usuario alterne a visualizacao para o adversario!
     */
    public static void onPauseOpen(Object cDInstance) {
        initialStartersMandante.clear();
        initialStartersVisitante.clear();
        sessionActive = true;
        if (cDInstance == null) return;

        try {
            Class<?> clazz = cDInstance.getClass();

            // Grava titulares iniciais do MANDANTE (AE)
            Field aeField = clazz.getDeclaredField("AE");
            aeField.setAccessible(true);
            ArrayList<?> listAE = (ArrayList<?>) aeField.get(cDInstance);
            recordStarters(listAE, initialStartersMandante);

            // Grava titulares iniciais do VISITANTE (Dc)
            Field dcField = clazz.getDeclaredField("Dc");
            dcField.setAccessible(true);
            ArrayList<?> listDc = (ArrayList<?>) dcField.get(cDInstance);
            recordStarters(listDc, initialStartersVisitante);

            System.out.println("[SubstitutionManager] Session started. Initial starters recorded -> Mandante: " 
                + initialStartersMandante.size() + ", Visitante: " + initialStartersVisitante.size());

            // Atualiza imediatamente o contador DA com as substituições reais restantes
            try {
                Field diField = clazz.getDeclaredField("Di");
                diField.setAccessible(true);
                int di = diField.getInt(cDInstance);
                int teamIndex = di - 1;

                Field dbField = clazz.getDeclaredField("Db");
                dbField.setAccessible(true);
                components.v vObj = (components.v) dbField.get(cDInstance);
                best.I matchEngine = vObj.tR();

                Field daField = clazz.getDeclaredField("DA");
                daField.setAccessible(true);
                JLabel lblDA = (JLabel) daField.get(cDInstance);
                if (lblDA != null && matchEngine != null) {
                    int remaining = matchEngine.aR(teamIndex);
                    lblDA.setText("Substituições restantes: " + remaining);
                }
            } catch (Exception ignored) {}

            // Garante que o HUD widescreen atualize com ambos os times já carregados na abertura
            try {
                InMatchHudManager.onTeamRefreshed(cDInstance);
            } catch (Exception ignored) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void recordStarters(ArrayList<?> list, Map<Integer, best.F> targetMap) {
        if (list == null) return;
        for (int i = 1; i <= 25; i++) {
            if (i < list.size()) {
                Object ao = list.get(i);
                if (ao instanceof components.ao) {
                    components.ao aoComp = (components.ao) ao;
                    best.F player = aoComp.x();
                    if (player != null) {
                        targetMap.put(i, player);
                        player.as(i); // Garante que o slot do titular esta perfeitamente sincronizado
                    }
                }
            }
        }
    }

    /**
     * Intercepta K(i, i2) dentro de cD.H(i, i2) para fazer a troca livre estilo EA FC / PES!
     */
    public static void handleSwap(Object cDInstance, int i, int i2) {
        if (cDInstance == null) return;

        try {
            Class<?> clazz = cDInstance.getClass();

            // Pega o time ativo (Di: 1 = Mandante, 2 = Visitante)
            Field diField = clazz.getDeclaredField("Di");
            diField.setAccessible(true);
            int di = diField.getInt(cDInstance);
            int teamIndex = di - 1; // 0 ou 1

            // Pega components.v (Db) e motor da partida
            Field dbField = clazz.getDeclaredField("Db");
            dbField.setAccessible(true);
            components.v vObj = (components.v) dbField.get(cDInstance);
            best.I matchEngine = vObj.tR();

            // SEGURANÇA MÁXIMA: Só permite substituição se o time ativo for controlado por HUMANO!
            best.ah activeTeam = (teamIndex == 0) ? matchEngine.hc() : matchEngine.hd();
            if (activeTeam == null || activeTeam.jZ() == null || !activeTeam.jZ().booleanValue()) {
                System.out.println("[SubstitutionManager] Troca bloqueada: o time ativo (" + teamIndex + ") e controlado pela CPU!");
                return;
            }

            // Pega a lista de jogadores correta (AE para time 0, Dc para time 1)
            Field listField = (teamIndex == 1) ? clazz.getDeclaredField("Dc") : clazz.getDeclaredField("AE");
            listField.setAccessible(true);
            ArrayList<?> list = (ArrayList<?>) listField.get(cDInstance);

            Map<Integer, best.F> initialStarters = (teamIndex == 1) ? initialStartersVisitante : initialStartersMandante;
            if (initialStarters.isEmpty()) {
                recordStarters(list, initialStarters);
            }

            // Pega o icone da camisa (AL para time 0, Dd para time 1)
            Field iconField = (teamIndex == 1) ? clazz.getDeclaredField("Dd") : clazz.getDeclaredField("AL");
            iconField.setAccessible(true);
            ImageIcon shirtIcon = (ImageIcon) iconField.get(cDInstance);

            int totalAllowed = matchEngine.aR(teamIndex);

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
            int currentNetSubs = calculateNetSubsAfterSwap(list, fieldSlot, benchSlot, pBench, pField, initialStarters);

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

            try {
                InMatchHudManager.onTeamRefreshed(cDInstance);
            } catch (Exception ignored) {}

            // 5. Redesenha a tela
            if (cDInstance instanceof JComponent) {
                ((JComponent) cDInstance).repaint();
            }

            System.out.println("[SubstitutionManager] Free swap (Team " + teamIndex + "): " + pField.getNome() + " <-> " + pBench.getNome() 
                + " (Substituicoes pendentes: " + currentNetSubs + "/" + totalAllowed + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int calculateNetSubsAfterSwap(ArrayList<?> list, int fSlot, int bSlot, best.F simField, best.F simBench,
                                                Map<Integer, best.F> initialStarters) {
        Set<best.F> simulatedStarters = new HashSet<>();
        for (int s = 1; s <= 25; s++) {
            if (s < list.size()) {
                components.ao ao = (components.ao) list.get(s);
                if (ao != null) {
                    if (s == fSlot) {
                        simulatedStarters.add(simField);
                    } else if (ao.x() != null) {
                        simulatedStarters.add(ao.x());
                    }
                }
            }
        }

        int changed = 0;
        for (best.F originalPlayer : initialStarters.values()) {
            boolean found = false;
            for (best.F sim : simulatedStarters) {
                if (sim == originalPlayer || (sim != null && originalPlayer != null && sim.equals(originalPlayer))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                changed++;
            }
        }
        return changed;
    }

    /**
     * Chamado quando o jogador clica em "Voltar ao jogo" (em cD.pb).
     * Efetiva apenas as substituicoes reais no motor do jogo com mapeamento estrito de posicoes,
     * avaliando Mandante e Visitante SEPARADAMENTE e apenas para clubes controlados por humanos!
     */
    public static void onResumeGame(Object cDInstance) {
        if (!sessionActive || cDInstance == null) return;
        sessionActive = false;

        try {
            Class<?> clazz = cDInstance.getClass();

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

            // 1. Processa o MANDANTE (Time 0) se for controlado por humano
            if (matchEngine.hc() != null && matchEngine.hc().jZ() != null && matchEngine.hc().jZ().booleanValue()) {
                Field aeField = clazz.getDeclaredField("AE");
                aeField.setAccessible(true);
                ArrayList<?> listAE = (ArrayList<?>) aeField.get(cDInstance);
                commitTeamSubstitutions(0, listAE, initialStartersMandante, matchEngine, minute, ed, cDInstance);
            }

            // 2. Processa o VISITANTE (Time 1) se for controlado por humano
            if (matchEngine.hd() != null && matchEngine.hd().jZ() != null && matchEngine.hd().jZ().booleanValue()) {
                Field dcField = clazz.getDeclaredField("Dc");
                dcField.setAccessible(true);
                ArrayList<?> listDc = (ArrayList<?>) dcField.get(cDInstance);
                commitTeamSubstitutions(1, listDc, initialStartersVisitante, matchEngine, minute, ed, cDInstance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            initialStartersMandante.clear();
            initialStartersVisitante.clear();
        }
    }

    private static void commitTeamSubstitutions(int teamIndex, ArrayList<?> list, Map<Integer, best.F> initialStarters,
                                                best.I matchEngine, int minute, int ed, Object cDInstance) {
        if (list == null || initialStarters == null || initialStarters.isEmpty()) return;

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

        // Identifica titulares originais que saíram do campo
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

        // Validação: garante que pIn pertença ao clube ativo
        best.ah activeTeam = (teamIndex == 0) ? matchEngine.hc() : matchEngine.hd();
        Iterator<best.F> inIt = playersIn.iterator();
        while (inIt.hasNext()) {
            best.F pIn = inIt.next();
            boolean validIn = false;
            if (pIn != null) {
                best.ah pTeam = pIn.fg();
                if (pTeam == null || pTeam == activeTeam || (activeTeam != null && pTeam.equals(activeTeam))) {
                    validIn = true;
                } else if (activeTeam != null && activeTeam.kc() != null && activeTeam.kc().contains(pIn)) {
                    validIn = true;
                }
            }
            if (!validIn) {
                System.out.println("[SubstitutionManager] REJEITADO jogador que nao pertence ao elenco do time " + teamIndex + ": " + (pIn != null ? pIn.getNome() : "null"));
                inIt.remove();
            }
        }

        int subsToCommit = Math.min(playersOut.size(), playersIn.size());
        int subsRemaining = matchEngine.aR(teamIndex);
        int committed = 0;

        // Pareamento inteligente de saída e entrada para manter a posição exata
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

            // Define o slot temporário do pOut como targetSlot antes de chamar o motor
            pOut.as(targetSlot);

            // Executa no motor nativo do Brasfoot
            best.A subEvent = matchEngine.a(teamIndex, pOut, pIn, minute, ed, -1);
            if (subEvent != null) {
                subEvent.p(true);
            }

            // Garante que pIn tenha o slot de campo exato
            pIn.as(targetSlot);
            pOut.as(0); // pOut fica no banco com slot 0

            committed++;
            System.out.println("[SubstitutionManager] COMMITTED SUB (Team " + teamIndex + "): SAI " + pOut.getNome() 
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
            try {
                Field tableField = cDInstance.getClass().getDeclaredField("DG");
                tableField.setAccessible(true);
                JTable table = (JTable) tableField.get(cDInstance);
                if (table != null) {
                    table.addNotify();
                }
            } catch (Exception ignored) {}
            System.out.println("[SubstitutionManager] Resuming match (Team " + teamIndex + "): " + committed + " real substitution(s) committed.");
        }
    }
}
