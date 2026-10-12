package mod.brasfoot;

import java.awt.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;

public class UndoHelper {
    private static Object currentCD = null;
    private static Object currentMatch = null;
    private static int initialSubCount = -1;
    private static int initialEventCount = -1;
    private static int teamIdx = -1;
    private static final Map<Object, Object> initialSlotPlayers = new HashMap<Object, Object>();
    private static Timer poller = null;

    public static void clear() {
        if (poller != null) {
            poller.stop();
            poller = null;
        }
        initialSlotPlayers.clear();
        currentCD = null;
        currentMatch = null;
        initialSubCount = -1;
        initialEventCount = -1;
        teamIdx = -1;
    }

    public static void snapshot(Object cD, Object db, Object lblCounter, Object campinho, JButton btnUndo) {
        clear();
        currentCD = cD;

        try {
            // Pega match (best.I)
            Method getMatchM = db.getClass().getMethod("tR");
            currentMatch = getMatchM.invoke(db);

            // Pega team index (Di - 1)
            Field diField = cD.getClass().getDeclaredField("Di");
            diField.setAccessible(true);
            int di = diField.getInt(cD);
            teamIdx = di - 1;

            // Pega sub count inicial
            Method aRM = currentMatch.getClass().getMethod("aR", int.class);
            initialSubCount = ((Integer) aRM.invoke(currentMatch, teamIdx)).intValue();

            // Pega tamanho inicial da lista de eventos
            Method hEM = currentMatch.getClass().getMethod("hE");
            ArrayList events = (ArrayList) hEM.invoke(currentMatch);
            initialEventCount = (events != null) ? events.size() : 0;

            // Pega lista de slots AE ou Dc
            Field listField = (di == 2) ? cD.getClass().getDeclaredField("Dc") : cD.getClass().getDeclaredField("AE");
            listField.setAccessible(true);
            ArrayList slotList = (ArrayList) listField.get(cD);

            if (slotList != null) {
                Method getPlayerM = null;
                for (Object slotComp : slotList) {
                    if (slotComp != null) {
                        if (getPlayerM == null) {
                            getPlayerM = slotComp.getClass().getMethod("x");
                        }
                        Object player = getPlayerM.invoke(slotComp);
                        initialSlotPlayers.put(slotComp, player);
                    }
                }
            }

            // Poller leve para habilitar o botao Desfazer assim que uma substituicao for realizada
            poller = new Timer(300, e -> {
                try {
                    if (currentMatch != null && teamIdx >= 0 && btnUndo != null) {
                        Method curARM = currentMatch.getClass().getMethod("aR", int.class);
                        int curSub = ((Integer) curARM.invoke(currentMatch, teamIdx)).intValue();
                        boolean hasChanged = (curSub < initialSubCount);
                        btnUndo.setEnabled(hasChanged);
                    }
                } catch (Exception ignored) {}
            });
            poller.setRepeats(true);
            poller.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean undo(JButton btnUndo, Object lblCounter, Object campinho) {
        if (currentMatch == null || currentCD == null) return false;

        try {
            // 1. Restaura jogadores originais nos slots
            Method setPlayerM = null;
            for (Map.Entry<Object, Object> entry : initialSlotPlayers.entrySet()) {
                Object slotComp = entry.getKey();
                Object origPlayer = entry.getValue();
                if (setPlayerM == null) {
                    setPlayerM = slotComp.getClass().getMethod("a", best.F.class);
                }
                setPlayerM.invoke(slotComp, origPlayer);
            }

            // 2. Restaura contador de substituicoes
            Field fRField = currentMatch.getClass().getDeclaredField("fR");
            fRField.setAccessible(true);
            int[] fR = (int[]) fRField.get(currentMatch);
            if (fR != null && teamIdx >= 0 && teamIdx < fR.length) {
                fR[teamIdx] = initialSubCount;
            }

            // 3. Remove eventos de substituicao adicionados nesta sessao de pausa
            Method hEM = currentMatch.getClass().getMethod("hE");
            ArrayList events = (ArrayList) hEM.invoke(currentMatch);
            if (events != null) {
                while (events.size() > initialEventCount) {
                    events.remove(events.size() - 1);
                }
            }

            // 4. Atualiza texto do label
            if (lblCounter instanceof JLabel) {
                ((JLabel) lblCounter).setText("Substituições restantes: " + initialSubCount);
            }

            // 5. Repinta campinho
            if (campinho instanceof Component) {
                ((Component) campinho).repaint();
            }

            if (btnUndo != null) {
                btnUndo.setEnabled(false);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
