package mod.brasfoot;

import best.F;
import best.ah;
import components.ag;
import java.util.ArrayList;

/**
 * Motor de Penalidades por Improvisação de Posição & Lateralidade estilo EA FC
 * - Calcula a redução de força de jogadores fora de posição e com lateralidade invertida
 * - Atua em tempo real na interface (HUD EA FC) e durante as partidas para o Humano e para a CPU (Máquina)
 */
public class PositionPenaltyHelper {

    // Mapeamento de Posição Alvo por Slot (1..25):
    // 0: Goleiro (G)
    // 1: Lateral (L)
    // 2: Zagueiro (Z)
    // 3: Meio-campo (M)
    // 4: Atacante (A)
    public static final int[] SLOT_TARGET_POS = {
        -1, // 0
         0, // 1: Gol
         1, // 2: Lateral Esquerda (x=35, y=425)
         2, // 3: Zagueiro Esquerda (x=100, y=485)
         2, // 4: Zagueiro Centro (x=190, y=485)
         2, // 5: Zagueiro Direita (x=285, y=485)
         2, // 6: Volante/Zagueiro Esquerda (x=100, y=405)
         2, // 7: Volante/Zagueiro Centro (x=190, y=405)
         2, // 8: Volante/Zagueiro Direita (x=285, y=405)
         1, // 9: Lateral Direita (x=345, y=425)
         1, // 10: Ala Esquerdo (x=35, y=265)
         3, // 11: Meia Esquerda (x=100, y=315)
         3, // 12: Meia Centro (x=190, y=315)
         3, // 13: Meia Direita (x=285, y=315)
         3, // 14: Meia Ofensivo Esquerda (x=100, y=205)
         3, // 15: Meia Ofensivo Centro (x=190, y=205)
         3, // 16: Meia Ofensivo Direita (x=285, y=205)
         1, // 17: Ala Direito (x=345, y=265)
         4, // 18: Ponta Esquerda (x=35, y=55)
         4, // 19: Segundo Atacante Esquerda (x=100, y=115)
         4, // 20: Segundo Atacante Centro (x=190, y=115)
         4, // 21: Segundo Atacante Direita (x=285, y=115)
         4, // 22: Atacante Esquerda (x=100, y=35)
         4, // 23: Centroavante Centro (x=190, y=30)
         4, // 24: Atacante Direita (x=285, y=35)
         4  // 25: Ponta Direita (x=345, y=55)
    };

    // Mapeamento de Lado Alvo por Slot (1..25):
    // 0: Direita (D), 1: Esquerda (E), 2: Centro/Ambos (C)
    // No Brasfoot original: 0=Direito, 1=Esquerdo.
    // Na tela do campo: x pequeno (35, 100) = Esquerda (1); x central (190) = Centro (2); x grande (285, 345) = Direita (0).
    public static final int[] SLOT_TARGET_SIDE = {
        -1, // 0
         2, // 1: Gol (Centro)
         1, // 2: LE (Esquerda, x=35)
         1, // 3: ZE (Esquerda, x=100)
         2, // 4: ZAG (Centro, x=190)
         0, // 5: ZD (Direita, x=285)
         1, // 6: ZE/Volante (Esquerda, x=100)
         2, // 7: VOL (Centro, x=190)
         0, // 8: ZD/Volante (Direita, x=285)
         0, // 9: LD (Direita, x=345)
         1, // 10: Ala E (Esquerda, x=35)
         1, // 11: ME (Esquerda, x=100)
         2, // 12: MC (Centro, x=190)
         0, // 13: MD (Direita, x=285)
         1, // 14: ME (Esquerda, x=100)
         2, // 15: MEI (Centro, x=190)
         0, // 16: MD (Direita, x=285)
         0, // 17: Ala D (Direita, x=345)
         1, // 18: PE (Esquerda, x=35)
         1, // 19: AE (Esquerda, x=100)
         2, // 20: ATA (Centro, x=190)
         0, // 21: AD (Direita, x=285)
         1, // 22: AE (Esquerda, x=100)
         2, // 23: CA (Centro, x=190)
         0, // 24: AD (Direita, x=285)
         0  // 25: PD (Direita, x=345)
    };

    // Rótulos de exibição tática por slot (quando improvisado ou atuando na função)
    public static final String[] SLOT_DISPLAY_LABELS = {
        "",
        "G",   // 1
        "LE",  // 2
        "ZE",  // 3
        "ZAG", // 4
        "ZD",  // 5
        "ZE",  // 6
        "VOL", // 7
        "ZD",  // 8
        "LD",  // 9
        "LE",  // 10
        "ME",  // 11
        "MC",  // 12
        "MD",  // 13
        "ME",  // 14
        "MEI", // 15
        "MD",  // 16
        "LD",  // 17
        "PE",  // 18
        "AE",  // 19
        "ATA", // 20
        "AD",  // 21
        "AE",  // 22
        "CA",  // 23
        "AD",  // 24
        "PD"   // 25
    };

    /**
     * Calcula a penalidade total (Setor + Lateralidade) de um atleta escalado em determinado slot.
     */
    public static int calculatePenalty(F player, int slot) {
        if (player == null || slot < 1 || slot > 25) return 0;

        int origPos = player.getPosicao(); // 0=G, 1=L, 2=Z, 3=M, 4=A
        int origSide = player.getLado();   // 0=D, 1=E, 2=C/Ambos

        int targetPos = SLOT_TARGET_POS[slot];
        int targetSide = SLOT_TARGET_SIDE[slot];

        int sectorPenalty = 0;

        // 1. Goleiro vs Linha
        if (origPos == 0 && targetPos != 0) {
            sectorPenalty = 10; // Goleiro na linha: -10
        } else if (origPos != 0 && targetPos == 0) {
            sectorPenalty = 15; // Jogador de linha no gol: -15
        } else if (origPos != targetPos) {
            switch (origPos) {
                case 1: // Lateral de origem
                    if (targetPos == 2) sectorPenalty = 2; // de Zagueiro: -2
                    else if (targetPos == 3) sectorPenalty = 2; // de Meia: -2
                    else if (targetPos == 4) sectorPenalty = 4; // de Atacante/Ponta: -4
                    break;
                case 2: // Zagueiro de origem
                    if (targetPos == 1) sectorPenalty = 2; // de Lateral: -2
                    else if (targetPos == 3) sectorPenalty = 3; // de Meia/Volante: -3
                    else if (targetPos == 4) sectorPenalty = 5; // de Atacante: -5
                    break;
                case 3: // Meio-campo de origem
                    if (targetPos == 1) {
                        // Se atuando como Ala (slots 10 ou 17): penalidade leve de -1
                        if (slot == 10 || slot == 17) sectorPenalty = 1;
                        else sectorPenalty = 2; // de Lateral fixo: -2
                    } else if (targetPos == 2) {
                        sectorPenalty = 3; // Meia improvisado na zaga: -3
                    } else if (targetPos == 4) {
                        if (slot == 22 || slot == 23 || slot == 24) {
                            sectorPenalty = 3; // Centroavante fixo: -3
                        } else {
                            sectorPenalty = 2; // Segundo atacante / Ponta: -2
                        }
                    }
                    break;
                case 4: // Atacante de origem
                    if (targetPos == 3) sectorPenalty = 3; // de Meia: -3
                    else if (targetPos == 1) sectorPenalty = 4; // de Lateral: -4
                    else if (targetPos == 2) sectorPenalty = 5; // de Zagueiro: -5
                    break;
            }
        }

        // 2. Penalidade de Lado (Lateralidade invertida: Direita vs Esquerda)
        int sidePenalty = 0;
        if (targetSide != 2 && origSide != 2) {
            if ((origSide == 0 && targetSide == 1) || (origSide == 1 && targetSide == 0)) {
                sidePenalty = 2; // -2 por lado oposto
            }
        }

        return sectorPenalty + sidePenalty;
    }

    /**
     * Retorna a etiqueta de posição que deve ser exibida no card do atleta (ex: ZD, ATA, MC).
     */
    public static String getDisplayLabel(F player, int slot) {
        if (player == null) return "JOG";
        if (slot >= 1 && slot <= 25) {
            int penalty = calculatePenalty(player, slot);
            if (penalty > 0) {
                return SLOT_DISPLAY_LABELS[slot];
            }
        }
        // Posição nativa do atleta
        int pos = player.getPosicao();
        int lado = player.getLado();
        if (pos == 0) return "G";
        if (pos == 1) return (lado == 0 ? "LD" : (lado == 1 ? "LE" : "LAT"));
        if (pos == 2) return (lado == 0 ? "ZD" : (lado == 1 ? "ZE" : "ZAG"));
        if (pos == 3) return (lado == 0 ? "MD" : (lado == 1 ? "ME" : "MEI"));
        return (lado == 0 ? "AD" : (lado == 1 ? "AE" : "ATA"));
    }

    /**
     * Localiza o slot (1..25) de qualquer atleta dentro da formação tática ativa de seu time.
     * Utiliza o slot nativo armazenado no objeto do atleta (player.fT()) com altíssima velocidade.
     */
    public static int getPlayerSlot(F player) {
        if (player == null) return -1;
        try {
            int slot = player.fT();
            if (slot >= 1 && slot <= 25) {
                return slot;
            }
        } catch (Throwable ignored) {}
        return -1;
    }

    /**
     * Retorna a força efetiva de um jogador durante a partida, subtraindo penalidades de improvisação.
     * Utilizado pelo motor de simulação de partidas (c.b).
     */
    public static double applyMatchPenalty(double basePower, F player) {
        if (player == null) return basePower;
        try {
            int slot = getPlayerSlot(player);
            if (slot >= 1 && slot <= 25) {
                int penalty = calculatePenalty(player, slot);
                if (penalty > 0) {
                    return Math.max(5.0, basePower - (double) penalty);
                }
            }
        } catch (Throwable ignored) {}
        return basePower;
    }

    public static double applyMatchPenalty(F player, double basePower) {
        return applyMatchPenalty(basePower, player);
    }

    /**
     * Algoritmo de Seleção Tática Inteligente de Atletas (Substitui best.ah.a).
     * Seleciona o melhor atleta disponível para o slot tático especificado considerando:
     * 1. Prioridade absoluta para a posição e lado exatos (ou ambidestro)
     * 2. Se não houver especialista do lado, aceita a mesma posição com lado oposto
     * 3. Se não houver ninguém da posição, improvisa posições correlatas do mesmo lado
     * 4. Goleiro nunca vai para a linha (e vice-versa), salvo esgotamento total do elenco
     * 5. Para slots centrais (targetSide == 2), preserva atletas de lados escassos para slots de lado
     * 6. Dentro da melhor categoria de adequação, seleciona o atleta com maior força efetiva
     */
    public static F selectBestPlayerForSlot(ArrayList<F> available, int slot, boolean isBench, boolean disallowKeeper) {
        if (available == null || available.isEmpty()) return null;

        int targetPos = (slot >= 1 && slot <= 25) ? SLOT_TARGET_POS[slot] : -1;
        int targetSide = (slot >= 1 && slot <= 25) ? SLOT_TARGET_SIDE[slot] : 2;

        if (targetPos < 0) {
            try {
                if (best.aq.sE != null && slot >= 0 && slot < best.aq.sE.length && best.aq.sE[slot] != null) {
                    targetPos = best.aq.sE[slot][0];
                }
            } catch (Throwable ignored) {}
            if (targetPos < 0) targetPos = 3;
        }

        // 1. Caso Especial: Goleiro (slot == 1 ou targetPos == 0)
        if (targetPos == 0) {
            F bestKeeper = null;
            int maxPwr = -1;
            for (F p : available) {
                if (p != null && p.getPosicao() == 0) {
                    int pwr = p.fi();
                    if (pwr > maxPwr || (pwr == maxPwr && bestKeeper != null && p.fp() > bestKeeper.fp())) {
                        maxPwr = pwr;
                        bestKeeper = p;
                    }
                }
            }
            if (bestKeeper != null) return bestKeeper;

            if (disallowKeeper) return null;

            // Sem goleiro no elenco: improvisa melhor jogador de linha
            F bestLine = null;
            int maxLinePwr = -1;
            for (F p : available) {
                if (p != null) {
                    int eff = p.fi() - calculatePenalty(p, slot);
                    if (eff > maxLinePwr || (eff == maxLinePwr && bestLine != null && p.fp() > bestLine.fp())) {
                        maxLinePwr = eff;
                        bestLine = p;
                    }
                }
            }
            return bestLine;
        }

        // 2. Jogadores de Linha (targetPos > 0)
        // Conta a disponibilidade de lados para a posição exata (auxilia no balanceamento central)
        int countPosSide0 = 0; // Destros
        int countPosSide1 = 0; // Canhotos
        for (F p : available) {
            if (p != null && p.getPosicao() == targetPos) {
                if (p.getLado() == 0) countPosSide0++;
                else if (p.getLado() == 1) countPosSide1++;
            }
        }

        // Camada 1: Especialista exato (mesma posição, lado compatível)
        F bestExact = null;
        int maxExactPwr = -1;

        // Camada 2: Mesma posição, lado oposto
        F bestSamePosWrongSide = null;
        int maxSamePosPwr = -1;

        // Camada 3: Posição correlata, mesmo lado
        F bestRelated = null;
        int maxRelatedPwr = -1;

        // Camada 4: Posição correlata, lado oposto
        F bestRelatedWrongSide = null;
        int maxRelatedWrongSidePwr = -1;

        // Camada 5: Qualquer outro jogador de linha
        F bestAnyLine = null;
        int maxAnyLinePwr = -1;

        // Camada 6: Goleiro (último recurso se esgotado o elenco de linha)
        F bestKeeperFallback = null;
        int maxKeeperPwr = -1;

        for (F p : available) {
            if (p == null) continue;
            int origPos = p.getPosicao();
            int origSide = p.getLado(); // 0=D, 1=E, 2=C/Ambos

            if (origPos == 0) {
                if (!disallowKeeper && (p.fi() > maxKeeperPwr || (p.fi() == maxKeeperPwr && bestKeeperFallback != null && p.fp() > bestKeeperFallback.fp()))) {
                    maxKeeperPwr = p.fi();
                    bestKeeperFallback = p;
                }
                continue;
            }

            int penalty = calculatePenalty(p, slot);
            int effPwr = p.fi() - penalty;

            boolean sideMatches = (targetSide == 2 || origSide == 2 || origSide == targetSide);
            boolean isExactPos = (origPos == targetPos);

            if (isExactPos) {
                if (sideMatches) {
                    // Score para desempate inteligente em slots centrais (targetSide == 2):
                    // Se o slot é central, favorece gastar atleta do lado mais abundante
                    // para preservar o especialista escasso para os lados
                    int sideAbundanceScore = 0;
                    if (targetSide == 2) {
                        if (origSide == 2) sideAbundanceScore = 20; // Ambidestro/Centro natural é ideal
                        else if (origSide == 0) sideAbundanceScore = (countPosSide0 >= countPosSide1 ? 10 : 0);
                        else if (origSide == 1) sideAbundanceScore = (countPosSide1 >= countPosSide0 ? 10 : 0);
                    }

                    boolean isBetter = false;
                    if (bestExact == null) {
                        isBetter = true;
                    } else if (effPwr > maxExactPwr) {
                        isBetter = true;
                    } else if (effPwr == maxExactPwr) {
                        if (targetSide == 2) {
                            int currentScore = 0;
                            int bSide = bestExact.getLado();
                            if (bSide == 2) currentScore = 20;
                            else if (bSide == 0) currentScore = (countPosSide0 >= countPosSide1 ? 10 : 0);
                            else if (bSide == 1) currentScore = (countPosSide1 >= countPosSide0 ? 10 : 0);
                            if (sideAbundanceScore > currentScore) isBetter = true;
                            else if (sideAbundanceScore == currentScore && p.fp() > bestExact.fp()) isBetter = true;
                        } else if (p.fp() > bestExact.fp()) {
                            isBetter = true;
                        }
                    }

                    if (isBetter) {
                        maxExactPwr = effPwr;
                        bestExact = p;
                    }
                } else {
                    // Camada 2: Mesma posição, lado oposto
                    if (effPwr > maxSamePosPwr || (effPwr == maxSamePosPwr && bestSamePosWrongSide != null && p.fp() > bestSamePosWrongSide.fp())) {
                        maxSamePosPwr = effPwr;
                        bestSamePosWrongSide = p;
                    }
                }
            } else {
                // Posições correlatas
                boolean isRelated = false;
                if ((targetPos == 1 && (origPos == 2 || origPos == 3)) ||
                    (targetPos == 2 && (origPos == 1 || origPos == 3)) ||
                    (targetPos == 3 && (origPos == 1 || origPos == 4)) ||
                    (targetPos == 4 && origPos == 3)) {
                    isRelated = true;
                }

                if (isRelated) {
                    if (sideMatches) {
                        if (effPwr > maxRelatedPwr || (effPwr == maxRelatedPwr && bestRelated != null && p.fp() > bestRelated.fp())) {
                            maxRelatedPwr = effPwr;
                            bestRelated = p;
                        }
                    } else {
                        if (effPwr > maxRelatedWrongSidePwr || (effPwr == maxRelatedWrongSidePwr && bestRelatedWrongSide != null && p.fp() > bestRelatedWrongSide.fp())) {
                            maxRelatedWrongSidePwr = effPwr;
                            bestRelatedWrongSide = p;
                        }
                    }
                } else {
                    if (effPwr > maxAnyLinePwr || (effPwr == maxAnyLinePwr && bestAnyLine != null && p.fp() > bestAnyLine.fp())) {
                        maxAnyLinePwr = effPwr;
                        bestAnyLine = p;
                    }
                }
            }
        }

        if (bestExact != null) return bestExact;
        if (bestSamePosWrongSide != null) return bestSamePosWrongSide;
        if (bestRelated != null) return bestRelated;
        if (bestRelatedWrongSide != null) return bestRelatedWrongSide;
        if (bestAnyLine != null) return bestAnyLine;
        return bestKeeperFallback;
    }
}
