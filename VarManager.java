package mod.brasfoot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Random;

public class VarManager {
    private static final Random rnd = new Random();
    private static boolean varAutomatico = true;
    private static final File CONFIG_FILE = new File("mod_config.properties");

    static {
        loadConfig();
    }

    public static boolean isVarAutomatico() {
        return varAutomatico;
    }

    public static void setVarAutomatico(boolean auto) {
        varAutomatico = auto;
        saveConfig();
    }

    private static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            varAutomatico = true;
            return;
        }
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(fis);
            String val = props.getProperty("var_automatico", "true");
            varAutomatico = !"false".equalsIgnoreCase(val);
        } catch (Exception e) {
            varAutomatico = true;
        }
    }

    private static void saveConfig() {
        try {
            Properties props = new Properties();
            if (CONFIG_FILE.exists()) {
                try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                    props.load(fis);
                } catch (Exception ignored) {}
            }
            props.setProperty("var_automatico", String.valueOf(varAutomatico));
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Brasfoot 22-23 Mod Configurations");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Constantes de Estilo (best.aq.rC)
    public static final int ESTILO_EQUILIBRADO = 0;
    public static final int ESTILO_ATAQUE_TOTAL = 1;
    public static final int ESTILO_CONTRA_ATAQUE = 2;

    // Constantes de Marcacao (best.aq.rE)
    public static final int MARCACAO_LEVE = 0;
    public static final int MARCACAO_PESADA = 1;
    public static final int MARCACAO_MUITO_PESADA = 2;

    // Constantes de Foco de Ataque (best.aq.rD)
    public static final int FOCO_MISTO = 0;
    public static final int FOCO_MEIO = 1;
    public static final int FOCO_LATERAIS = 2;

    /**
     * RNG 1: Determina se o VAR é acionado para o lance.
     * Retorna TRUE se o lance for duvidoso e for para a tela do VAR, ou FALSE se o gol/lance for direto e normal.
     * REDUZIDO EM 50% PARA TORNAR O VAR MAIS RARO.
     */
    public static boolean deveChamarVarMonitor(int tipoEvento, int estiloAtaque, int marcacaoDefesa, int formacaoDefesa, int energiaAtaque, int energiaDefesa) {
        if (tipoEvento == VarDialog.EVENT_GOL) {
            int chance = 12; // Base: reduzido em 50% (era 25% -> agora ~12%, 1 em cada 8 gols)

            // Contra-ataque veloz gera jogadas no limite da linha de impedimento
            if (estiloAtaque == ESTILO_CONTRA_ATAQUE) {
                chance += 5; // era 10% -> 5%
            } else if (estiloAtaque == ESTILO_ATAQUE_TOTAL) {
                chance += 3; // era 5% -> 3%
            }

            // Defesa com 5 zagueiros (linhas de retranca: 5-4-1, 5-3-2)
            if (formacaoDefesa == 2 || formacaoDefesa == 3) {
                chance += 4; // era 8% -> 4%
            }

            // Marcação pesada da defesa causa disputas físicas na área
            if (marcacaoDefesa == MARCACAO_MUITO_PESADA) {
                chance += 4; // era 7% -> 4%
            } else if (marcacaoDefesa == MARCACAO_PESADA) {
                chance += 2; // era 3% -> 2%
            }

            // Cansaço físico (<55%) aumenta erros de posicionamento e lances duvidosos
            if (energiaDefesa < 55 || energiaAtaque < 55) {
                chance += 3; // era 5% -> 3%
            }

            // Trava de segurança entre 7% e 22% (reduzido em 50%)
            if (chance < 7) chance = 7;
            if (chance > 22) chance = 22;

            return rnd.nextInt(100) < chance;
        } else if (tipoEvento == VarDialog.EVENT_VERMELHO) {
            int chance = 15; // Base para revisão de expulsão: reduzido em 50% (era 30% -> agora 15%)
            if (marcacaoDefesa == MARCACAO_LEVE) {
                chance += 8; // era 15% -> 8%
            } else if (marcacaoDefesa == MARCACAO_MUITO_PESADA) {
                chance -= 5; // era 10% -> 5%
            }
            if (energiaDefesa < 50) {
                chance += 3; // era 5% -> 3%
            }

            if (chance < 8) chance = 8;
            if (chance > 25) chance = 25;

            return rnd.nextInt(100) < chance;
        }

        return false;
    }

    /**
     * RNG 2: Quando o VAR foi chamado na tela, sorteia a DECISÃO do lance de GOL.
     * Inclui +15% de fator favorável ao time que joga em casa (pressão da torcida).
     */
    public static int sortearDesfechoGol(int estiloAtaque, int marcacaoDefesa, int formacaoDefesa, int energiaAtaque, boolean isTimeDaCasa) {
        // Se a arbitragem chamou o VAR na tela, há uma dúvida real (50% base de anulação)
        int chanceAnulacao = 50; // Base neutra: 50% anula, 50% confirma

        // FATOR CASA (+15% favorável ao mandante):
        if (isTimeDaCasa) {
            // O time da casa marcou o gol -> Decisão favorável ao mandante é CONFIRMAR o gol (-15% na chance de anulação)
            chanceAnulacao -= 15; // 35% de chance de anular (65% de confirmar)
        } else {
            // O visitante marcou o gol -> Decisão favorável ao mandante é ANULAR o gol (+15% na chance de anulação)
            chanceAnulacao += 15; // 65% de chance de anular (35% de confirmar)
        }

        // Contra-ataque tem mais impedimento milimétrico
        if (estiloAtaque == ESTILO_CONTRA_ATAQUE) {
            chanceAnulacao += 10;
        }

        // Defesa com 5 zagueiros faz linha de impedimento eficiente
        if (formacaoDefesa == 2 || formacaoDefesa == 3) {
            chanceAnulacao += 8;
        }

        // Marcação pesada do adversário gera faltas na origem da jogada
        if (marcacaoDefesa == MARCACAO_MUITO_PESADA) {
            chanceAnulacao += 10;
        }

        // Atacante cansado se precipita e cai mais em impedimento
        if (energiaAtaque < 50) {
            chanceAnulacao += 5;
        }

        // Trava entre 15% e 85%
        if (chanceAnulacao < 15) chanceAnulacao = 15;
        if (chanceAnulacao > 85) chanceAnulacao = 85;

        int roll = rnd.nextInt(100);
        if (roll >= chanceAnulacao) {
            return VarDialog.RESULT_GOL_LEGAL; // Gol confirmado!
        }

        // Se anulou: define se o motivo foi impedimento ou falta de ataque
        int chanceImpedimento = 55;
        if (estiloAtaque == ESTILO_CONTRA_ATAQUE) {
            chanceImpedimento = 80; // No contra-ataque a imensa maioria é impedimento
        } else if (marcacaoDefesa == MARCACAO_MUITO_PESADA) {
            chanceImpedimento = 30; // Com marcação pesada, a maioria é falta de ataque
        }

        if (rnd.nextInt(100) < chanceImpedimento) {
            return VarDialog.RESULT_GOL_ANULADO_IMP;
        } else {
            return VarDialog.RESULT_GOL_ANULADO_FALTA;
        }
    }

    public static int sortearDesfechoGol(int estiloAtaque, int marcacaoDefesa, int formacaoDefesa, int energiaAtaque) {
        return sortearDesfechoGol(estiloAtaque, marcacaoDefesa, formacaoDefesa, energiaAtaque, true);
    }

    /**
     * RNG 3: Quando o VAR foi chamado na tela, sorteia a DECISÃO do CARTÃO VERMELHO.
     * Inclui +15% de fator favorável ao time que joga em casa.
     */
    public static int sortearDesfechoVermelho(int marcacaoInfrator, int energiaInfrator, boolean isTimeDaCasa) {
        int chanceReverter = 45; // Base: 45% reverte para amarelo, 55% mantém vermelho

        // FATOR CASA (+15% favorável ao mandante):
        if (isTimeDaCasa) {
            // O infrator é do time da casa -> Decisão favorável é REVERTER para cartão amarelo (+15%)
            chanceReverter += 15; // 60% chance de salvar o atleta do mandante
        } else {
            // O infrator é do time visitante -> Decisão favorável ao mandante é MANTER o vermelho (-15%)
            chanceReverter -= 15; // 30% chance de reverter (70% de manter o visitante com 1 a menos)
        }

        if (marcacaoInfrator == MARCACAO_LEVE) {
            chanceReverter += 15; // Time disciplinado
        } else if (marcacaoInfrator == MARCACAO_PESADA) {
            chanceReverter -= 10;
        } else if (marcacaoInfrator == MARCACAO_MUITO_PESADA) {
            chanceReverter -= 25; // Time violento: quase certeza de manter a expulsão
        }

        if (energiaInfrator < 45 && marcacaoInfrator != MARCACAO_MUITO_PESADA) {
            chanceReverter += 10;
        }

        if (chanceReverter < 10) chanceReverter = 10;
        if (chanceReverter > 85) chanceReverter = 85;

        if (rnd.nextInt(100) < chanceReverter) {
            return VarDialog.RESULT_VERMELHO_REVERTIDO;
        } else {
            return VarDialog.RESULT_VERMELHO_MANTIDO;
        }
    }

    public static int sortearDesfechoVermelho(int marcacaoInfrator, int energiaInfrator) {
        return sortearDesfechoVermelho(marcacaoInfrator, energiaInfrator, true);
    }
}
