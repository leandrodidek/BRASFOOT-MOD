package mod.brasfoot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;

public class ModBridge {

    // Controle de evento raro: Expulsão por Gesto Obsceno (>10 faltas no jogo, 1% de chance)
    private static int lastEvaluatedMatchHash = 0;
    private static boolean matchEvaluatedForObscene = false;
    private static boolean pendingObsceneExpulsion = false;

    // ==========================================
    // HOOK DO VAR E EXPULSÃO (a.dV)
    // ==========================================
    public static boolean handleEventHook(Object c0378v, Object match, Object eventA, int i2, Object dVInstance) {
        try {
            if (dVInstance == null || eventA == null || match == null) {
                return false;
            }

            // 1. FILTRO ESTRITO: Apenas a partida que o usuario esta jogando/assistindo na tela!
            Field gwField = dVInstance.getClass().getDeclaredField("GW");
            gwField.setAccessible(true);
            int gw = gwField.getInt(dVInstance);
            if (i2 != gw) {
                // Outro jogo da rodada simulado pela CPU: segue fluxo nativo direto em segundo plano!
                return false;
            }

            Method getEventCodeM = eventA.getClass().getMethod("b");
            int eventCode = ((Integer) getEventCodeM.invoke(eventA)).intValue();

            // Pega a janela principal do jogo
            Frame parentFrame = null;
            try {
                Field brField = dVInstance.getClass().getDeclaredField("Br");
                brField.setAccessible(true);
                parentFrame = (Frame) brField.get(dVInstance);
            } catch (Exception ignored) {}

            // Extrai os dados taticos e de energia dos dois times
            best.I matchEngine = (best.I) match;
            best.ah homeTeam = matchEngine.hc();
            best.ah awayTeam = matchEngine.hd();

            Method getTeamM = eventA.getClass().getMethod("cu");
            best.ah teamWhoDidEvent = (best.ah) getTeamM.invoke(eventA);
            best.ah teamOpponent = (teamWhoDidEvent == homeTeam) ? awayTeam : homeTeam;

            // Nome do clube, nome do jogador, minuto e escudo
            String nomeTime = (teamWhoDidEvent != null && teamWhoDidEvent.getNome() != null) ? teamWhoDidEvent.getNome() : "Time";
            String nomeJogador = "";
            try {
                Method getPlayerM = eventA.getClass().getMethod("eo");
                Object playerObj = getPlayerM.invoke(eventA);
                if (playerObj instanceof best.F) {
                    nomeJogador = ((best.F) playerObj).getNome();
                }
            } catch (Exception ignored) {}

            if (nomeJogador == null || nomeJogador.trim().isEmpty()) {
                try {
                    Method getPlayer2M = eventA.getClass().getMethod("ep");
                    Object playerObj2 = getPlayer2M.invoke(eventA);
                    if (playerObj2 instanceof best.F) {
                        nomeJogador = ((best.F) playerObj2).getNome();
                    }
                } catch (Exception ignored) {}
            }

            if (nomeJogador == null || nomeJogador.trim().isEmpty()) {
                try {
                    Method getErM = eventA.getClass().getMethod("er");
                    String strEr = (String) getErM.invoke(eventA);
                    if (strEr != null) {
                        String clean = strEr.replaceAll("<[^>]*>", "").trim();
                        // Remove traços ou minutos se houver
                        int dash = clean.indexOf("-");
                        if (dash >= 0 && dash + 1 < clean.length()) {
                            clean = clean.substring(dash + 1).trim();
                        }
                        int digit = -1;
                        for (int c = 0; c < clean.length(); c++) {
                            if (Character.isDigit(clean.charAt(c))) {
                                digit = c;
                                break;
                            }
                        }
                        if (digit > 0) {
                            clean = clean.substring(0, digit).trim();
                        }
                        if (!clean.isEmpty()) {
                            nomeJogador = clean;
                        }
                    }
                } catch (Exception ignored) {}
            }

            if (nomeJogador == null || nomeJogador.trim().isEmpty()) {
                nomeJogador = "Atacante";
            }

            int minuto = 0;
            try {
                // em() retorna o minuto exato da partida (dA)! (en() retornava apenas o tempo 1 ou 2)
                Method getMinM = eventA.getClass().getMethod("em");
                minuto = ((Integer) getMinM.invoke(eventA)).intValue();
            } catch (Exception ignored) {}

            if (minuto <= 0) {
                try {
                    // Fallback: pega o minuto em andamento no motor dV
                    Method qjMethod = dVInstance.getClass().getMethod("qj");
                    minuto = ((Integer) qjMethod.invoke(null)).intValue();
                } catch (Exception ignored) {}
            }

            ImageIcon escudoTime = null;
            try {
                if (teamWhoDidEvent != null) {
                    escudoTime = teamWhoDidEvent.x(44, 44);
                }
            } catch (Exception ignored) {}

            // Taticas do time do evento e do adversario:
            // kj()[0] = formacao, kj()[1] = estilo, kj()[2] = marcacao, kj()[3] = ataque
            int estiloAtq = 0;
            int marcDef = 1;
            int formDef = 5; // 4-4-2 padrao

            try {
                if (teamWhoDidEvent != null && teamWhoDidEvent.kj() != null) {
                    estiloAtq = teamWhoDidEvent.kj()[1];
                }
                if (teamOpponent != null && teamOpponent.kj() != null) {
                    marcDef = teamOpponent.kj()[2];
                    formDef = teamOpponent.kj()[0];
                }
            } catch (Exception ignored) {}

            // Energia media dos titulares
            int energiaAtq = calcularEnergiaMedia(matchEngine, teamWhoDidEvent == homeTeam);
            int energiaDef = calcularEnergiaMedia(matchEngine, teamOpponent == homeTeam);

            // ==========================================
            // VERIFICACAO DE CARTOES TOTAIS E EVENTO RARO (>6 CARTOES NO JOGO, 1% DE CHANCE)
            // ==========================================
            int currentMatchHash = System.identityHashCode(matchEngine);
            if (currentMatchHash != lastEvaluatedMatchHash) {
                lastEvaluatedMatchHash = currentMatchHash;
                matchEvaluatedForObscene = false;
                pendingObsceneExpulsion = false;
            }

            int totalCartoes = 0;
            try {
                // Percorre a lista de eventos da partida (best.I.hE) para somar amarelos e vermelhos de ambos os times
                ArrayList<?> eventsList = matchEngine.hE();
                if (eventsList != null) {
                    for (Object evObj : eventsList) {
                        if (evObj == null) continue;
                        try {
                            Method getCodeM = evObj.getClass().getMethod("et");
                            int cCode = (Integer) getCodeM.invoke(evObj);
                            // Código 2 = Amarelo, Código 3 = 2º Amarelo / Vermelho, Código 4 = Vermelho Direto
                            if (cCode == 2 || cCode == 3 || cCode == 4) {
                                totalCartoes++;
                            }
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}

            // Ativa o sorteio de 1% se a partida tiver mais de 6 cartões no jogo (somando amarelos e vermelhos de ambas as equipes)
            if (totalCartoes > 6 && !matchEvaluatedForObscene) {
                matchEvaluatedForObscene = true; // Sorteia apenas uma vez por partida elegível
                java.util.Random rndObsceno = new java.util.Random();
                if (rndObsceno.nextInt(100) == 0) { // 1% de chance rara
                    pendingObsceneExpulsion = true;
                    System.out.println("[VAR] EVENTO RARO SORTEADO! Partida tensa (" + totalCartoes + " cartoes). Expulsao por gesto obsceno ativada (1%)!");
                }
            }

            // Executa a expulsão por gesto obsceno em lances que não sejam gols ou expulsões já em curso
            if (pendingObsceneExpulsion && eventCode != 1 && eventCode != 3 && eventCode != 4) {
                pendingObsceneExpulsion = false;

                java.util.Random rndTeam = new java.util.Random();
                boolean pickHome = rndTeam.nextBoolean();
                ArrayList<?> activeList = pickHome ? matchEngine.hp() : matchEngine.hq();
                best.ah culpritTeam = pickHome ? homeTeam : awayTeam;

                if (activeList == null || activeList.isEmpty() || activeList.size() <= 7) {
                    pickHome = !pickHome;
                    activeList = pickHome ? matchEngine.hp() : matchEngine.hq();
                    culpritTeam = pickHome ? homeTeam : awayTeam;
                }

                if (activeList != null && !activeList.isEmpty() && activeList.size() > 7) {
                    int playerIdx = rndTeam.nextInt(activeList.size());
                    Object culpritPlayerObj = activeList.get(playerIdx);

                    if (culpritPlayerObj instanceof best.F) {
                        best.F culpritPlayer = (best.F) culpritPlayerObj;
                        String nomeInfrator = culpritPlayer.getNome();
                        String timeInfrator = (culpritTeam != null && culpritTeam.getNome() != null) ? culpritTeam.getNome() : "Time";
                        ImageIcon escudoInfrator = (culpritTeam != null) ? culpritTeam.x(44, 44) : null;

                        // 1. Exibe a interface broadcast do VAR para Gesto Obsceno
                        VarDialog.exibir(parentFrame, VarDialog.EVENT_VERMELHO, VarDialog.RESULT_VERMELHO_MANTIDO,
                            VarDialog.SUB_CONDUTA_OBSCENA, timeInfrator, nomeInfrator, minuto, escudoInfrator, null);

                        // 2. Converte o evento do jogo para Cartão Vermelho Direto nativo (código 4)
                        try {
                            Method setCodeM = eventA.getClass().getMethod("a", int.class);
                            setCodeM.invoke(eventA, 4);

                            Method setSubM = eventA.getClass().getMethod("R", int.class);
                            setSubM.invoke(eventA, 0);

                            Method setTeamM = eventA.getClass().getMethod("k", Class.forName("best.ah"));
                            setTeamM.invoke(eventA, culpritTeam);

                            Method setPlayerM = eventA.getClass().getMethod("f", Class.forName("best.F"));
                            setPlayerM.invoke(eventA, culpritPlayer);

                            Method setDoneM = eventA.getClass().getMethod("p", boolean.class);
                            setDoneM.invoke(eventA, false); // Garante processamento nativo em a.dV.a
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // 3. Registra nas estatísticas individuais da partida (components.s.tH)
                        try {
                            Method gBMethod = culpritPlayer.getClass().getMethod("gB");
                            Object statsS = gBMethod.invoke(culpritPlayer);
                            if (statsS != null) {
                                Method tHMethod = statsS.getClass().getMethod("tH");
                                tHMethod.invoke(statsS);
                            }
                        } catch (Exception ignored) {}

                        // 4. Remove o jogador expulso da escalação ativa de titulares (hp ou hq)
                        try {
                            activeList.remove(culpritPlayer);
                        } catch (Exception ignored) {}

                        // 5. Atualiza a tabela Hs
                        try {
                            Field hsField = dVInstance.getClass().getDeclaredField("Hs");
                            hsField.setAccessible(true);
                            JTable hsTable = (JTable) hsField.get(dVInstance);
                            if (hsTable != null) {
                                hsTable.addNotify();
                            }
                        } catch (Exception ignored) {}

                        System.out.println("[VAR] EXPULSAO APLICADA! " + nomeInfrator + " (" + timeInfrator + ") expulso por gesto obsceno! (" + totalCartoes + " cartoes no jogo).");
                        return false; // Permite ao Brasfoot tocar o apito de expulsão e abrir a tela tática
                    }
                }
            }

            // ==========================================
            // LANCE DE GOL (Código 1) - COMO NA VIDA REAL, TODO GOL É CHECADO!
            // ==========================================
            if (eventCode == 1) {
                // Se for cobranca de penalti (el == 3), NAO chama VAR de gol aqui!
                // O penalti ainda sera cobrado pelo jogador no minigame nativo do jogo.
                int subType = 0;
                try {
                    Method elMethod = eventA.getClass().getMethod("el");
                    subType = ((Integer) elMethod.invoke(eventA)).intValue();
                } catch (Exception ignored) {}

                if (subType == 3) {
                    return false;
                }

                // Checa se o lance vai para o VAR (RNG com base nas táticas e física)
                boolean precisaMonitor = VarManager.deveChamarVarMonitor(VarDialog.EVENT_GOL, estiloAtq, marcDef, formDef, energiaAtq, energiaDef);
                if (!precisaMonitor) {
                    // Lance normal e limpo: segue direto sem interrupção na tela!
                    return false;
                }

                // Sorteia a decisão do VAR quando o lance é duvidoso e vai para a tela (com +15% de fator casa)
                boolean isTimeDaCasa = (teamWhoDidEvent == homeTeam);
                int resultado = VarManager.sortearDesfechoGol(estiloAtq, marcDef, formDef, energiaAtq, isTimeDaCasa);

                // Identifica se a checagem especifica do lance e impedimento ou falta
                int motivoLance = VarDialog.MOTIVO_IMPEDIMENTO;
                if (resultado == VarDialog.RESULT_GOL_ANULADO_IMP) {
                    motivoLance = VarDialog.MOTIVO_IMPEDIMENTO;
                } else if (resultado == VarDialog.RESULT_GOL_ANULADO_FALTA) {
                    motivoLance = VarDialog.MOTIVO_FALTA;
                } else {
                    if (estiloAtq == VarManager.ESTILO_CONTRA_ATAQUE || formDef == 2 || formDef == 3) {
                        motivoLance = VarDialog.MOTIVO_IMPEDIMENTO;
                    } else {
                        motivoLance = VarDialog.MOTIVO_FALTA;
                    }
                }

                final int resFinal = resultado;
                final Object evA = eventA;

                boolean isUserTeam = false;
                try {
                    if (teamWhoDidEvent != null && teamWhoDidEvent.jZ() != null) {
                        isUserTeam = teamWhoDidEvent.jZ().booleanValue();
                    }
                } catch (Throwable ignored) {}

                VarDialog.exibir(parentFrame, VarDialog.EVENT_GOL, resultado, motivoLance, nomeTime, nomeJogador, minuto, escudoTime, isUserTeam, () -> {
                    if (resFinal != VarDialog.RESULT_GOL_LEGAL) {
                        try {
                            Method setCodeM = evA.getClass().getMethod("a", int.class);
                            setCodeM.invoke(evA, 0); // Anula o gol (vira lance neutro)
                            Method setDoneM = evA.getClass().getMethod("p", boolean.class);
                            setDoneM.invoke(evA, true); // Marca como processado
                        } catch (Exception ignored) {}
                    }
                });

                if (resultado != VarDialog.RESULT_GOL_LEGAL) {
                    int teamIdx = (teamWhoDidEvent == homeTeam) ? 0 : 1;

                    // 1. Remove o evento da lista de eventos da partida (best.I.fN / hE)
                    try {
                        Method hEMethod = matchEngine.getClass().getMethod("hE");
                        java.util.ArrayList fN = (java.util.ArrayList) hEMethod.invoke(matchEngine);
                        if (fN != null) {
                            fN.remove(eventA);
                        }
                    } catch (Exception ignored) {}

                    // 2. Anula o objeto do evento (código 0 = neutro, subtipo 0)
                    try {
                        Method setCodeM = eventA.getClass().getMethod("a", int.class);
                        setCodeM.invoke(eventA, 0);
                        Method setSubM = eventA.getClass().getMethod("R", int.class);
                        setSubM.invoke(eventA, 0);
                        Method setDoneM = eventA.getClass().getMethod("p", boolean.class);
                        setDoneM.invoke(eventA, true);
                    } catch (Exception ignored) {}

                    // 3. Recalcula formalmente os gols em best.I atraves do metodo nativo hF()
                    try {
                        Method hFMethod = matchEngine.getClass().getMethod("hF");
                        hFMethod.invoke(matchEngine);
                    } catch (Exception ignored) {}

                    // 4. Garante que os campos primarios de placar de best.I (fB e fC) estejam sincronizados
                    try {
                        Field fbField = matchEngine.getClass().getDeclaredField("fB");
                        Field fcField = matchEngine.getClass().getDeclaredField("fC");
                        fbField.setAccessible(true);
                        fcField.setAccessible(true);

                        // Sincroniza arrays de resultado do campeonato (hZ = fZ e ia = ga)
                        Method hZMethod = matchEngine.getClass().getMethod("hZ");
                        int[] hZ = (int[]) hZMethod.invoke(matchEngine);
                        if (hZ != null && hZ.length >= 2) {
                            hZ[0] = fbField.getInt(matchEngine);
                            hZ[1] = fcField.getInt(matchEngine);
                        }

                        Method iaMethod = matchEngine.getClass().getMethod("ia");
                        int[] ia = (int[]) iaMethod.invoke(matchEngine);
                        if (ia != null && ia.length >= 2) {
                            ia[0] = fbField.getInt(matchEngine);
                            ia[1] = fcField.getInt(matchEngine);
                        }

                        // Sincroniza o placar ao vivo de components.v (Pq e Pr)
                        Field pqField = c0378v.getClass().getDeclaredField("Pq");
                        Field prField = c0378v.getClass().getDeclaredField("Pr");
                        pqField.setAccessible(true);
                        prField.setAccessible(true);
                        pqField.setInt(c0378v, fbField.getInt(matchEngine));
                        prField.setInt(c0378v, fcField.getInt(matchEngine));
                    } catch (Exception ignored) {}

                    // 5. Decrementa e reverte estatísticas individuais do autor do gol e do assistente
                    // 5.1 Reverte o jogador que deu a assistência (ep()) -> remove assistência da partida, temporada e competição
                    try {
                        Method epMethod = eventA.getClass().getMethod("ep");
                        Object assistF = epMethod.invoke(eventA);
                        if (assistF != null) {
                            // A. Reverte assistência da partida em components.s (evita o ícone de chuteira no pós-jogo b.aD)
                            try {
                                Method gBMethod = assistF.getClass().getMethod("gB");
                                Object statsS = gBMethod.invoke(assistF);
                                if (statsS != null) {
                                    Field ccField = statsS.getClass().getDeclaredField("cc");
                                    ccField.setAccessible(true);
                                    int ast = ccField.getInt(statsS);
                                    if (ast > 0) {
                                        ccField.setInt(statsS, ast - 1);
                                    }
                                }
                            } catch (Exception ignored) {}

                            // B. Reverte assistência na temporada do clube (best.k)
                            try {
                                if (teamWhoDidEvent != null) {
                                    Method oMethod = assistF.getClass().getMethod("o", teamWhoDidEvent.getClass());
                                    Object kObj = oMethod.invoke(assistF, teamWhoDidEvent);
                                    if (kObj != null) {
                                        Method cDMethod = kObj.getClass().getMethod("cD");
                                        int kAst = ((Integer) cDMethod.invoke(kObj)).intValue();
                                        if (kAst > 0) {
                                            Method eMethod = kObj.getClass().getMethod("E", int.class);
                                            eMethod.invoke(kObj, kAst - 1);
                                        }
                                    }
                                }
                            } catch (Exception ignored) {}

                            // C. Reverte assistência na competição (best.H)
                            try {
                                Method hyMethod = matchEngine.getClass().getMethod("hy");
                                Object comp = hyMethod.invoke(matchEngine);
                                if (comp != null) {
                                    Method hMethod = assistF.getClass().getMethod("h", comp.getClass());
                                    Object hObj = hMethod.invoke(assistF, comp);
                                    if (hObj != null) {
                                        Method cDMethod = hObj.getClass().getMethod("cD");
                                        int hAst = ((Integer) cDMethod.invoke(hObj)).intValue();
                                        if (hAst > 0) {
                                            Method eMethod = hObj.getClass().getMethod("E", int.class);
                                            eMethod.invoke(hObj, hAst - 1);
                                        }
                                    }
                                }
                            } catch (Exception ignored) {}

                            // D. Limpa o assistente do evento
                            try {
                                Method gMethod = eventA.getClass().getMethod("g", Class.forName("best.F"));
                                gMethod.invoke(eventA, new Object[]{null});
                            } catch (Exception ignored) {}
                        }
                    } catch (Exception ignored) {}

                    // 5.2 Reverte o autor do gol (eo()) -> Pe (gols), V (no gol), Pd (finalizações)
                    try {
                        Method eoMethod = eventA.getClass().getMethod("eo");
                        Object playerF = eoMethod.invoke(eventA);
                        if (playerF != null) {
                            Method gBMethod = playerF.getClass().getMethod("gB");
                            Object statsS = gBMethod.invoke(playerF);
                            if (statsS != null) {
                                // Gols da partida (Pe)
                                try {
                                    Field peField = statsS.getClass().getDeclaredField("Pe");
                                    peField.setAccessible(true);
                                    int g = peField.getInt(statsS);
                                    if (g > 0) {
                                        peField.setInt(statsS, g - 1);
                                    }
                                } catch (Exception ignored) {}

                                // Finalizações no gol (V)
                                try {
                                    Field vField = statsS.getClass().getDeclaredField("V");
                                    vField.setAccessible(true);
                                    int v = vField.getInt(statsS);
                                    if (v > 0) {
                                        vField.setInt(statsS, v - 1);
                                    }
                                } catch (Exception ignored) {}

                                // Finalizações totais (Pd)
                                try {
                                    Field pdField = statsS.getClass().getDeclaredField("Pd");
                                    pdField.setAccessible(true);
                                    int pd = pdField.getInt(statsS);
                                    if (pd > 0) {
                                        pdField.setInt(statsS, pd - 1);
                                    }
                                } catch (Exception ignored) {}
                            }

                            // Limpa o autor do gol do evento
                            try {
                                Method fMethod = eventA.getClass().getMethod("f", Class.forName("best.F"));
                                fMethod.invoke(eventA, new Object[]{null});
                            } catch (Exception ignored) {}
                        }
                    } catch (Exception ignored) {}

                    // 5.3 Limpa a referência do último evento em components.v
                    try {
                        Method aMethod = c0378v.getClass().getMethod("a", Class.forName("best.A"));
                        aMethod.invoke(c0378v, new Object[]{null});
                    } catch (Exception ignored) {}

                    // 6. Atualiza os componentes visuais ao vivo
                    try {
                        Field hsField = dVInstance.getClass().getDeclaredField("Hs");
                        hsField.setAccessible(true);
                        JTable hsTable = (JTable) hsField.get(dVInstance);
                        if (hsTable != null) {
                            hsTable.addNotify();
                        }

                        Field hnField = dVInstance.getClass().getDeclaredField("Hn");
                        hnField.setAccessible(true);
                        JLabel lblHn = (JLabel) hnField.get(dVInstance);
                        if (lblHn != null) {
                            Method tTMethod = c0378v.getClass().getMethod("tT");
                            Method tUMethod = c0378v.getClass().getMethod("tU");
                            int tT = (Integer) tTMethod.invoke(c0378v);
                            int tU = (Integer) tUMethod.invoke(c0378v);
                            lblHn.setText(tT + " x " + tU);
                        }
                    } catch (Exception ignored) {}

                    System.out.println("[VAR] GOL DO " + nomeTime.toUpperCase() + " ANULADO! Placar 100% expurgado do motor (best.I, fN, hF, dV e pos-jogo).");
                    return true;
                }

                // GOL LEGAL: retorna false para o motor computar o gol no placar normalmente!
                System.out.println("[VAR] GOL DO " + nomeTime.toUpperCase() + " CONFIRMADO! Computando no placar...");
                return false;
            }

            // ==========================================
            // LANCE DE EXPULSÃO (Código 3 = 2º Amarelo, Código 4 = Vermelho Direto)
            // ==========================================
            else if (eventCode == 3 || eventCode == 4) {
                boolean jaTinhaAmarelo = (eventCode == 3);
                int marcInfrator = (teamWhoDidEvent != null && teamWhoDidEvent.kj() != null) ? teamWhoDidEvent.kj()[2] : 1;
                int energiaInfrator = energiaAtq;

                // Verifica se a expulsao chama o monitor do VAR
                if (VarManager.deveChamarVarMonitor(VarDialog.EVENT_VERMELHO, 0, marcInfrator, 0, 75, energiaInfrator)) {
                    boolean isTimeDaCasa = (teamWhoDidEvent == homeTeam);
                    int resultado = VarManager.sortearDesfechoVermelho(marcInfrator, energiaInfrator, isTimeDaCasa);

                    // Reversao para amarelo: apenas se nao tinha amarelo previo
                    if (resultado == VarDialog.RESULT_VERMELHO_REVERTIDO) {
                        if (!jaTinhaAmarelo) {
                            try {
                                Method setCodeM = eventA.getClass().getMethod("a", int.class);
                                setCodeM.invoke(eventA, 2); // Converte para cartao amarelo (2)
                                Method setDoneM = eventA.getClass().getMethod("p", boolean.class);
                                setDoneM.invoke(eventA, true);

                                Field hsField = dVInstance.getClass().getDeclaredField("Hs");
                                hsField.setAccessible(true);
                                JTable hsTable = (JTable) hsField.get(dVInstance);
                                if (hsTable != null) {
                                    hsTable.addNotify();
                                }
                            } catch (Exception ignored) {}

                            VarDialog.exibir(parentFrame, VarDialog.EVENT_VERMELHO, VarDialog.RESULT_VERMELHO_REVERTIDO, VarDialog.MOTIVO_CONDUTA, nomeTime, nomeJogador, minuto, escudoTime, null);
                            System.out.println("[VAR] EXPULSAO DE " + nomeJogador + " (" + nomeTime + ") REVERTIDA PARA AMARELO!");
                            return true; // Nao executa expulsao nativa
                        } else {
                            resultado = VarDialog.RESULT_VERMELHO_MANTIDO;
                        }
                    }

                    // Vermelho mantido pelo VAR
                    VarDialog.exibir(parentFrame, VarDialog.EVENT_VERMELHO, VarDialog.RESULT_VERMELHO_MANTIDO, VarDialog.MOTIVO_CONDUTA, nomeTime, nomeJogador, minuto, escudoTime, null);
                    System.out.println("[VAR] CARTAO VERMELHO PARA " + nomeJogador + " (" + nomeTime + ") MANTIDO!");
                    return false; // Executa expulsao nativa
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static int calcularEnergiaMedia(best.I matchEngine, boolean isHome) {
        try {
            ArrayList<?> list = isHome ? matchEngine.hp() : matchEngine.hq();
            if (list == null || list.isEmpty()) return 75;
            int total = 0;
            int count = 0;
            for (int i = 0; i < 11 && i < list.size(); i++) {
                Object p = list.get(i);
                if (p instanceof best.F) {
                    total += ((best.F) p).fp();
                    count++;
                }
            }
            return count > 0 ? (total / count) : 75;
        } catch (Exception e) {
            return 75;
        }
    }

    /**
     * Intercepta a chamada nativa a.dV.dk(int soundId) do motor de jogo.
     * Se o gol acabou de ser confirmado pelo VAR, silencia a execução nativa redundante
     * evitando que o som toque uma segunda vez logo após fechar o diálogo.
     */
    public static void playGameSound(Object dVInstance, int soundId) {
        if ((soundId == 2 || soundId == 3) && VarSoundHelper.deveSilenciarSomGolNativo()) {
            VarSoundHelper.consumirSilenciamentoSomGolNativo();
            return;
        }
        try {
            Method dkMethod = dVInstance.getClass().getDeclaredMethod("dk", int.class);
            dkMethod.setAccessible(true);
            dkMethod.invoke(dVInstance, soundId);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
