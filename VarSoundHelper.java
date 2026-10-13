package mod.brasfoot;

import java.io.File;
import javax.sound.sampled.*;

public class VarSoundHelper {

    // Flag de controle para evitar que o motor nativo (a.dV.a) toque som duplicado ao fechar o diálogo do VAR
    private static volatile boolean silenciarProximoGolNativo = false;

    public static boolean deveSilenciarSomGolNativo() {
        return silenciarProximoGolNativo;
    }

    public static void consumirSilenciamentoSomGolNativo() {
        silenciarProximoGolNativo = false;
    }

    public static void ativarSilenciamentoSomGolNativo() {
        silenciarProximoGolNativo = true;
    }

    private static boolean isSomHabilitado() {
        try {
            if (c.a.SR != null) {
                return c.a.SR.isUsaSons();
            }
        } catch (Throwable ignored) {}
        return true;
    }

    private static File getSoundFile(String filename) {
        File f = new File("sons/" + filename);
        if (f.exists()) return f;
        f = new File(System.getProperty("user.dir") + "/sons/" + filename);
        if (f.exists()) return f;
        f = new File("C:/Brasfoot22-23/sons/" + filename);
        if (f.exists()) return f;
        return null;
    }

    public static void tocarSomWav(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isSomHabilitado()) return;
                    File f = getSoundFile(filename);
                    if (f != null && f.exists()) {
                        AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                        Clip clip = AudioSystem.getClip();
                        clip.open(ais);
                        clip.start();
                    }
                } catch (Throwable ignored) {}
            }
        }).start();
    }

    /**
     * Toca o som de gol no início da checagem do VAR, interrompendo imediatamente com o apito
     * Se for time do usuário: toca gol1.wav + penalty.wav
     * Se for time adversário: toca goladv.wav + penalty.wav
     */
    public static void tocarSomInicioVar(final boolean isUserTeam) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Clip clipGol = null;
                try {
                    if (!isSomHabilitado()) return;

                    String somGol = isUserTeam ? "gol1.wav" : "goladv.wav";
                    File fGol = getSoundFile(somGol);
                    File fPen = getSoundFile("penalty.wav");

                    // 1. Inicia o som de explosão do Gol
                    if (fGol != null && fGol.exists()) {
                        AudioInputStream ais1 = AudioSystem.getAudioInputStream(fGol);
                        clipGol = AudioSystem.getClip();
                        clipGol.open(ais1);
                        clipGol.start();
                    }

                    // 2. Aguarda 220ms e entra imediatamente o apito de interrupção do árbitro
                    try {
                        Thread.sleep(220);
                    } catch (InterruptedException ignored) {}

                    // 3. Toca o apito (penalty.wav)
                    if (fPen != null && fPen.exists()) {
                        AudioInputStream ais2 = AudioSystem.getAudioInputStream(fPen);
                        Clip clipPen = AudioSystem.getClip();
                        clipPen.open(ais2);
                        clipPen.start();
                    }

                    // 4. Interrompe a comemoração do gol após o apito, simulando a parada súbita para checagem do VAR
                    try {
                        Thread.sleep(450);
                    } catch (InterruptedException ignored) {}

                    if (clipGol != null && clipGol.isRunning()) {
                        clipGol.stop();
                        clipGol.close();
                    }
                } catch (Throwable t) {
                    if (clipGol != null) {
                        try { clipGol.close(); } catch (Throwable ignored) {}
                    }
                }
            }
        }).start();
    }

    /**
     * Toca o áudio de desfecho final do VAR na Etapa 2:
     * - Gol confirmado do Usuário: toca gol1.wav (e silencia repetição nativa pós-diálogo)
     * - Gol confirmado do Adversário: toca goladv.wav (e silencia repetição nativa pós-diálogo)
     * - Gol do Adversário CANCELADO/ANULADO: toca gol1.wav para a torcida comemorar!
     * - Gol do Usuário CANCELADO: nenhum som de comemoração.
     */
    public static void tocarSomDecisaoFinal(boolean isUserTeam, boolean golConfirmado) {
        if (golConfirmado) {
            ativarSilenciamentoSomGolNativo();
            if (isUserTeam) {
                tocarSomWav("gol1.wav");
            } else {
                tocarSomWav("goladv.wav");
            }
        } else {
            // Gol anulado
            if (isUserTeam) {
                // Gol do usuário ANULADO: toca goladv.wav para passar a sensação negativa/frustração pelo gol cancelado
                tocarSomWav("goladv.wav");
            } else {
                // Anulação de gol do adversário: explosão de comemoração da torcida da casa/usuário!
                tocarSomWav("gol1.wav");
            }
        }
    }

    // Compatibilidade com chamadas legadas
    public static void tocarSomGolComInterrupcaoVar() {
        tocarSomInicioVar(true);
    }
}