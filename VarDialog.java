package mod.brasfoot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class VarDialog extends JPanel {

    public static final int EVENT_GOL = 1;
    public static final int EVENT_VERMELHO = 2;
    public static final int EVENT_PENALTI = 3;

    public static final int RESULT_GOL_LEGAL = 10;
    public static final int RESULT_GOL_ANULADO_IMP = 11;
    public static final int RESULT_GOL_ANULADO_FALTA = 12;

    public static final int RESULT_VERMELHO_MANTIDO = 20;
    public static final int RESULT_VERMELHO_REVERTIDO = 21;

    public static final int MOTIVO_IMPEDIMENTO = 1;
    public static final int MOTIVO_FALTA = 2;
    public static final int MOTIVO_CONDUTA = 3;

    // Sub-motivos para garantir coerencia absoluta entre Etapa 1 e Etapa 2
    public static final int SUB_IMPEDIMENTO = 101;
    public static final int SUB_FALTA_ORIGEM = 201;
    public static final int SUB_FALTA_MAO = 202;
    public static final int SUB_FALTA_GOLEIRO = 203;
    public static final int SUB_CONDUTA_VIOLENTA = 301;
    public static final int SUB_CONDUTA_OBSCENA = 302;

    private JDialog dialog;
    private JLabel lblStepPills;
    private JLabel lblMensagem;
    private JButton btnAcao;
    private Timer timer;
    private int step = 0;
    private int subMotivo;
    private String fraseStep0;
    private String tituloStep0;
    private String fraseFinal;
    private int resultadoFinal;
    private int tipoEvento;
    private int motivoChecagem;
    private boolean automatico;
    private String nomeTime;
    private String nomeJogador;
    private int minuto;
    private Runnable onComplete;

    // =========================================================================
    // FRASES DE IMPEDIMENTO (ETAPA 1 E ETAPA 2)
    // =========================================================================
    private static final String[] STEP0_IMP = {
        "Tracando linhas virtuais de impedimento no lance do gol de %s (%s)...",
        "Checando posicionamento da linha defensiva e condicao de %s no momento do passe...",
        "Avaliando sincronia do passe e projecao do atacante %s na conclusao...",
        "VAR analisa calibragem de linhas e posicao do penultimo defensor...",
        "Verificando se o atacante %s estava adiantado na hora do lancamento...",
        "Revisao minuciosa de impedimento em andamento na jogada de %s..."
    };

    private static final String[] LEGAL_IMP = {
        "Gol legal! O atacante estava em condicao perfeitamente regular na hora do passe.",
        "Tudo legal! O VAR confirmou as linhas virtuais e o gol e 100% valido. Bola ao centro!",
        "Lance checado e confirmado! Posicionamento milimetricamente correto do ataque.",
        "Gol confirmado! A tecnologia de linhas comprovou condicao legal do atacante.",
        "Gol validado! O atacante partiu de tras da linha da bola com precisao cirurgica.",
        "Decisao de campo mantida! Posicao de impedimento checada e totalmente descartada.",
        "Gol legitimo! O penultimo defensor dava condicao de jogo na origem da jogada.",
        "Confirmado com sucesso! Imagens atestam condicao legal do ataque por centimetros."
    };

    private static final String[] ANULADO_IMP = {
        "Gol anulado por impedimento! O ombro do atacante estava adiantado na hora do passe.",
        "Impedimento confirmado pelo VAR! Ponta da chuteira adiantada na origem do lancamento.",
        "Gol invalidado! O atacante se precipitou e estava meio corpo a frente da zaga.",
        "Impedimento assinalado! Tecnologia comprovou posicao irregular de poucos centimetros.",
        "Gol anulado! Atacante em posicao adiantada tirou proveito direto do passe em profundidade.",
        "Decisao corrigida: Impedimento! A linha virtual detectou o atacante a frente da defesa."
    };

    // =========================================================================
    // SUB-CENARIO 1: FALTA NA ORIGEM / DESARME NA JOGADA
    // =========================================================================
    private static final String[] STEP0_FALTA_ORIGEM = {
        "Checando possivel falta no desarme que originou a jogada de gol de %s (%s)...",
        "Revisando dividida e contato fisico na recuperacao de bola que gerou o gol...",
        "Avaliando se houve tranco desleal ou pisao no comeco da jogada de ataque...",
        "VAR analisa recuperacao de posse de bola e possivel falta de ataque..."
    };

    private static final String[] LEGAL_FALTA_ORIGEM = {
        "Gol confirmado! Dividida perfeitamente limpa na origem da jogada. Segue o jogo!",
        "Gol validado! Roubada de bola limpa, na bola, que originou o contragolpe fatal.",
        "Lance limpo! Arbitro de video nao constatou nenhuma infracao no desarme inicial.",
        "Decisao mantida: Gol legal! Disputa leal e sem falta na recuperacao da posse."
    };

    private static final String[] ANULADO_FALTA_ORIGEM = {
        "Gol anulado por falta! Empurrao claro nas costas do defensor na origem da jogada.",
        "Falta marcada no ataque! Pisao desleal no adversario detectado no inicio do lance.",
        "Decisao corrigida pelo VAR! Infracao clara no desarme que gerou a finalizacao.",
        "Gol anulado! Carga faltosa para roubar a bola na intermediaria antes do gol."
    };

    // =========================================================================
    // SUB-CENARIO 2: TOQUE DE MAO
    // =========================================================================
    private static final String[] STEP0_FALTA_MAO = {
        "Revisando toque de mao e possivel controle irregular no lance de %s (%s)...",
        "VAR checa angulos da camera para verificar toque de mao no dominio de %s...",
        "Avaliando se a bola tocou no braco do atacante antes da finalizacao...",
        "Verificando movimento do braco e possivel toque de mao no gol de %s..."
    };

    private static final String[] LEGAL_FALTA_MAO = {
        "Gol confirmado! Toque de mao descartado. A bola bateu no peito do jogador de forma legal.",
        "Gol validado! O VAR confirmou que o braco estava colado ao corpo e em posicao natural.",
        "Lance legal! Nao houve toque de mao, o dominio foi feito legitimamente com a coxa.",
        "Decisao mantida! Imagens comprovam que a bola bateu no ombro, sem irregularidade."
    };

    private static final String[] ANULADO_FALTA_MAO = {
        "Gol cancelado por toque de mao! Dominio com o braco revisado e punido no monitor.",
        "Gol anulado! Atacante ajeitou a trajetoria da bola com o braco antes de finalizar.",
        "Infracao confirmada pelo VAR! Toque de mao voluntario para dominar dentro da area.",
        "Gol invalidado! Movimento faltoso do braco para levar vantagem e marcar o gol."
    };

    // =========================================================================
    // SUB-CENARIO 3: FALTA NO GOLEIRO / DISPUTA NA PEQUENA AREA
    // =========================================================================
    private static final String[] STEP0_FALTA_GOLEIRO = {
        "Checando possivel carga faltosa sobre o goleiro na finalizacao de %s (%s)...",
        "VAR analisa choque com o goleiro e disputa pelo alto dentro da pequena area...",
        "Verificando se houve bloqueio faltoso contra o arqueiro no lance do gol..."
    };

    private static final String[] LEGAL_FALTA_GOLEIRO = {
        "Gol confirmado! O choque com o goleiro foi acidental apos a bola ja estar nas redes.",
        "Gol validado pelo VAR! Nao houve falta no goleiro, a disputa aerea foi totalmente leal.",
        "Lance legal! O arqueiro soltou a bola antes do contato, dividida normal de jogo.",
        "Decisao de campo mantida! Gol legitimo, sem carga faltosa dentro da pequena area."
    };

    private static final String[] ANULADO_FALTA_GOLEIRO = {
        "Gol anulado! Bloqueio faltoso e carga irregular contra o goleiro dentro da pequena area.",
        "Falta de ataque confirmada! Atacante calcou o goleiro no ar impedindo a defesa.",
        "Gol invalidado! Arbitro de video identificou tranco faltoso nas luvas do goleiro.",
        "Decisao corrigida: Falta no goleiro! Carga desmedida para tirar o arqueiro da jogada."
    };

    // =========================================================================
    // CONDUTA VIOLENTA / EXPULSAO
    // =========================================================================
    private static final String[] STEP0_CONDUTA = {
        "Revisando intensidade da entrada e possivel cartao vermelho de %s (%s)...",
        "Avaliando ponto de contato e forca desmedida no lance de %s...",
        "VAR checa imagens em camera lenta para definir possivel expulsao de %s...",
        "Verificando conduta violenta e risco a integridade fisica do adversario..."
    };

    private static final String[] VERMELHO_MANTIDO = {
        "Cartao vermelho confirmado! Entrada frontal com forca desmedida e travas na canela.",
        "Expulsao mantida! O arbitro confirmou agressao fora da disputa de bola. Direto pro vestiario!",
        "Cartao vermelho confirmado! Bloqueio intencional de gol claro com o braco na linha.",
        "Expulsao mantida pelo VAR! Carrinho violento e lateral atingindo o tornozelo do atleta.",
        "Decisao de campo mantida! Entrada temeraria colocando em risco a integridade fisica do rival."
    };

    private static final String[] VERMELHO_REVERTIDO = {
        "Expulsao cancelada! O VAR mostrou que ele recolheu a perna. Aplica apenas o cartao amarelo.",
        "Decisao revertida para amarelo! O jogador escorregou no gramado e nao teve forca excessiva.",
        "Cartao vermelho anulado! O arbitro corrigiu o exagero no monitor e aplicou somente o amarelo.",
        "Reversao confirmada! O juiz guarda o vermelho no bolso e aplica apenas advertencia com amarelo.",
        "Decisao revista pelo VAR! O atleta tocou na bola primeiro e nao visou atingir o adversario."
    };

    // =========================================================================
    // SUB-CENARIO 4: CONDUTA ANTI-DESPORTIVA (GESTO OBSCENO)
    // =========================================================================
    private static final String[] STEP0_OBSCENO = {
        "VAR analisa conduta anti-desportiva: flagrado possivel gesto obsceno de %s (%s)...",
        "Checando imagens de camera isolada: suposta provocacao ofensiva e gesto obsceno de %s...",
        "Clima tenso com mais de 10 faltas! VAR revisa gesto obsceno e ofensa grave de %s...",
        "Arbitro de video convocado para revisar conduta injuriosa e gesto obsceno de %s para a torcida..."
    };

    private static final String[] VERMELHO_OBSCENO = {
        "Cartao vermelho direto! Flagrado gesto obsceno acintoso em clima tenso com mais de 10 faltas. Expulsao confirmada!",
        "Expulsao confirmada pelo VAR! Gesto obsceno e provocacao desrespeitosa inaceitavel no gramado.",
        "Decisao de video: Vermelho direto! Conduta anti-desportiva gravissima com gestos obscenos.",
        "Direto para o vestiario! O VAR flagrou gesto obsceno em partida tensa. Jogador expulso!"
    };

    public VarDialog(JDialog dialog, int tipoLance, int resultado, int motivoChecagem, String nomeTime, String nomeJogador, int minuto, ImageIcon escudoTime, Runnable onComplete) {
        this.dialog = dialog;
        this.tipoEvento = tipoLance;
        this.resultadoFinal = resultado;
        this.motivoChecagem = motivoChecagem;
        this.automatico = VarManager.isVarAutomatico();
        this.nomeTime = (nomeTime != null && !nomeTime.isEmpty()) ? nomeTime : "Time";
        this.nomeJogador = (nomeJogador != null && !nomeJogador.isEmpty()) ? nomeJogador : "Jogador";
        this.minuto = minuto;
        this.onComplete = onComplete;

        // Determina o sub-cenario fixo para este lance garantindo 100% de coerencia entre as duas etapas
        definirSubCenario();

        // Configura mensagens
        configurarFrasesEtapa0();
        this.fraseFinal = sortearFraseDecisao();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 390));
        setMinimumSize(new Dimension(750, 390));
        setBackground(new Color(9, 13, 22)); // Deep Obsidian EA FC

        // =========================================================
        // BANNER SUPERIOR: BROADCAST PREMIUM EA FC / FOOTBALL MANAGER
        // =========================================================
        JPanel panelTopBanner = new JPanel(new BorderLayout(14, 0));
        panelTopBanner.setBackground(new Color(13, 21, 38)); // Slate Navy
        panelTopBanner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 41, 59)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Escudo do Clube
        if (escudoTime != null) {
            JLabel lblEscudo = new JLabel(escudoTime);
            lblEscudo.setPreferredSize(new Dimension(46, 46));
            panelTopBanner.add(lblEscudo, BorderLayout.WEST);
        }

        // Textos de Cabecalho: CLUBE, MINUTO E MOTIVO ESPECIFICO
        JPanel panelTextosTop = new JPanel(new GridLayout(2, 1, 0, 3));
        panelTextosTop.setOpaque(false);

        String minBadge = (minuto > 0) ? " [" + minuto + "']" : "";
        
        String linha1Html = "<html><span style='background:#0284c7; color:#ffffff; font-weight:bold; font-size:10px; padding:2px 7px; border-radius:3px;'>VAR REVIEW</span> &nbsp;"
            + "<span style='color:#f8fafc; font-weight:bold; font-size:16px; letter-spacing:0.5px;'>" + this.nomeTime.toUpperCase() + "</span> "
            + "<span style='background:#1e293b; color:#38bdf8; font-weight:bold; font-size:11px; padding:2px 6px; border:1px solid #334155; border-radius:3px;'>" + minBadge + "</span></html>";

        String textoMotivo;
        String corMotivoHex;
        if (this.motivoChecagem == MOTIVO_IMPEDIMENTO) {
            textoMotivo = "Checagem de Impedimento";
            corMotivoHex = "#facc15"; // Amarelo Dourado
        } else if (this.subMotivo == SUB_CONDUTA_OBSCENA) {
            textoMotivo = "Checagem: Gesto Obsceno";
            corMotivoHex = "#ef4444"; // Vermelho Alerta
        } else if (this.tipoEvento == EVENT_VERMELHO) {
            textoMotivo = "Checagem de Conduta Violenta";
            corMotivoHex = "#f87171"; // Vermelho Suave
        } else {
            if (this.subMotivo == SUB_FALTA_MAO) {
                textoMotivo = "Checagem de Toque de Mao no lance";
            } else if (this.subMotivo == SUB_FALTA_GOLEIRO) {
                textoMotivo = "Checagem de Falta no Goleiro";
            } else {
                textoMotivo = "Checagem de Falta na Origem do lance";
            }
            corMotivoHex = "#fb923c"; // Laranja Dourado
        }

        String linha2Html = "<html><span style='color:#94a3b8; font-size:12px; font-family:Segoe UI, sans-serif;'>"
            + "Jogador: <b style='color:#38bdf8; font-size:13px;'>" + this.nomeJogador + "</b> &nbsp;|&nbsp; "
            + "<b style='color:" + corMotivoHex + "; font-size:12px;'>" + textoMotivo + "</b>"
            + "</span></html>";

        JLabel lblLinha1 = new JLabel(linha1Html);
        JLabel lblLinha2 = new JLabel(linha2Html);

        panelTextosTop.add(lblLinha1);
        panelTextosTop.add(lblLinha2);
        panelTopBanner.add(panelTextosTop, BorderLayout.CENTER);

        add(panelTopBanner, BorderLayout.NORTH);

        // =========================================================
        // CENTRO: INDICADOR DE 2 ETAPAS + CARD DE TELEMETRIA DO VAR
        // =========================================================
        JPanel panelCenterWrapper = new JPanel(new BorderLayout(0, 12));
        panelCenterWrapper.setOpaque(false);
        panelCenterWrapper.setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

        this.lblStepPills = new JLabel("", SwingConstants.CENTER);
        panelCenterWrapper.add(this.lblStepPills, BorderLayout.NORTH);

        JPanel panelCard = new JPanel(new GridBagLayout());
        panelCard.setBackground(new Color(14, 22, 38)); // Dark Navy Telemetry
        panelCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 41, 59), 1),
            BorderFactory.createEmptyBorder(18, 15, 18, 15)
        ));

        this.lblMensagem = new JLabel("", SwingConstants.CENTER);
        panelCard.add(this.lblMensagem);
        panelCenterWrapper.add(panelCard, BorderLayout.CENTER);

        add(panelCenterWrapper, BorderLayout.CENTER);

        // =========================================================
        // INFERIOR: BOTAO DE ACAO CENTRALIZADO
        // =========================================================
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        panelBottom.setOpaque(false);

        this.btnAcao = new JButton("Ver Decisao >");
        this.btnAcao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        this.btnAcao.setBackground(new Color(16, 185, 129)); // Emerald Neon
        this.btnAcao.setForeground(new Color(2, 44, 34)); // Dark Green Contrast
        this.btnAcao.setFocusPainted(false);
        this.btnAcao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.btnAcao.setPreferredSize(new Dimension(210, 36));
        this.btnAcao.addActionListener(e -> avancarEtapa());
        panelBottom.add(this.btnAcao);

        add(panelBottom, BorderLayout.SOUTH);

        // Borda Obsidian externa
        atualizarBordaGlow(new Color(2, 132, 199)); // Cyan

        if (dialog != null) {
            dialog.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                        avancarEtapa();
                    }
                }
            });
        }

        setStep0();

        if (automatico) {
            iniciarAnimacaoAutomatica();
        }
    }

    private void definirSubCenario() {
        if (this.motivoChecagem == MOTIVO_IMPEDIMENTO) {
            this.subMotivo = SUB_IMPEDIMENTO;
        } else if (this.motivoChecagem == SUB_CONDUTA_OBSCENA) {
            this.subMotivo = SUB_CONDUTA_OBSCENA;
        } else if (this.tipoEvento == EVENT_VERMELHO) {
            this.subMotivo = SUB_CONDUTA_VIOLENTA;
        } else {
            // Sorteia o sub-motivo especifico da falta
            Random rnd = new Random();
            int r = rnd.nextInt(3);
            if (r == 0) {
                this.subMotivo = SUB_FALTA_ORIGEM;
            } else if (r == 1) {
                this.subMotivo = SUB_FALTA_MAO;
            } else {
                this.subMotivo = SUB_FALTA_GOLEIRO;
            }
        }
    }

    private void atualizarBordaGlow(Color cor) {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
    }

    private void configurarFrasesEtapa0() {
        Random rnd = new Random();
        if (this.subMotivo == SUB_IMPEDIMENTO) {
            this.tituloStep0 = "ANALISANDO IMPEDIMENTO NO MONITOR...";
            String template = STEP0_IMP[rnd.nextInt(STEP0_IMP.length)];
            this.fraseStep0 = String.format(template, this.nomeJogador, this.nomeTime);
        } else if (this.subMotivo == SUB_CONDUTA_OBSCENA) {
            this.tituloStep0 = "ANALISANDO CONDUTA ANTI-DESPORTIVA NO MONITOR...";
            String template = STEP0_OBSCENO[rnd.nextInt(STEP0_OBSCENO.length)];
            this.fraseStep0 = String.format(template, this.nomeJogador, this.nomeTime);
        } else if (this.subMotivo == SUB_CONDUTA_VIOLENTA) {
            this.tituloStep0 = "ANALISANDO CONDUTA VIOLENTA NO MONITOR...";
            String template = STEP0_CONDUTA[rnd.nextInt(STEP0_CONDUTA.length)];
            this.fraseStep0 = String.format(template, this.nomeJogador, this.nomeTime);
        } else if (this.subMotivo == SUB_FALTA_MAO) {
            this.tituloStep0 = "ANALISANDO POSSIVEL TOQUE DE MAO NO MONITOR...";
            String template = STEP0_FALTA_MAO[rnd.nextInt(STEP0_FALTA_MAO.length)];
            this.fraseStep0 = String.format(template, this.nomeJogador, this.nomeTime);
        } else if (this.subMotivo == SUB_FALTA_GOLEIRO) {
            this.tituloStep0 = "ANALISANDO CARGA SOBRE O GOLEIRO NO MONITOR...";
            String template = STEP0_FALTA_GOLEIRO[rnd.nextInt(STEP0_FALTA_GOLEIRO.length)];
            this.fraseStep0 = String.format(template, this.nomeJogador, this.nomeTime);
        } else {
            this.tituloStep0 = "ANALISANDO FALTA NA ORIGEM NO MONITOR...";
            String template = STEP0_FALTA_ORIGEM[rnd.nextInt(STEP0_FALTA_ORIGEM.length)];
            this.fraseStep0 = String.format(template, this.nomeJogador, this.nomeTime);
        }
    }

    private String sortearFraseDecisao() {
        Random rnd = new Random();
        if (this.resultadoFinal == RESULT_GOL_LEGAL) {
            if (this.subMotivo == SUB_IMPEDIMENTO) {
                return LEGAL_IMP[rnd.nextInt(LEGAL_IMP.length)];
            } else if (this.subMotivo == SUB_FALTA_MAO) {
                return LEGAL_FALTA_MAO[rnd.nextInt(LEGAL_FALTA_MAO.length)];
            } else if (this.subMotivo == SUB_FALTA_GOLEIRO) {
                return LEGAL_FALTA_GOLEIRO[rnd.nextInt(LEGAL_FALTA_GOLEIRO.length)];
            } else {
                return LEGAL_FALTA_ORIGEM[rnd.nextInt(LEGAL_FALTA_ORIGEM.length)];
            }
        } else if (this.resultadoFinal == RESULT_GOL_ANULADO_IMP) {
            return ANULADO_IMP[rnd.nextInt(ANULADO_IMP.length)];
        } else if (this.resultadoFinal == RESULT_GOL_ANULADO_FALTA) {
            if (this.subMotivo == SUB_FALTA_MAO) {
                return ANULADO_FALTA_MAO[rnd.nextInt(ANULADO_FALTA_MAO.length)];
            } else if (this.subMotivo == SUB_FALTA_GOLEIRO) {
                return ANULADO_FALTA_GOLEIRO[rnd.nextInt(ANULADO_FALTA_GOLEIRO.length)];
            } else {
                return ANULADO_FALTA_ORIGEM[rnd.nextInt(ANULADO_FALTA_ORIGEM.length)];
            }
        } else if (this.resultadoFinal == RESULT_VERMELHO_MANTIDO) {
            if (this.subMotivo == SUB_CONDUTA_OBSCENA) {
                return VERMELHO_OBSCENO[rnd.nextInt(VERMELHO_OBSCENO.length)];
            }
            return VERMELHO_MANTIDO[rnd.nextInt(VERMELHO_MANTIDO.length)];
        } else if (this.resultadoFinal == RESULT_VERMELHO_REVERTIDO) {
            return VERMELHO_REVERTIDO[rnd.nextInt(VERMELHO_REVERTIDO.length)];
        }
        return "Decisao confirmada apos analise minuciosa de video.";
    }

    /**
     * ETAPA 1 DE 2: CHECAGEM DO VAR EM ANDAMENTO
     */
    private void setStep0() {
        this.step = 0;
        atualizarPills(0);
        atualizarBordaGlow(new Color(2, 132, 199)); // Cyan

        this.lblMensagem.setText("<html><table width='670' cellpadding='0' cellspacing='0' border='0'>"
            + "<tr><td align='center' style='font-family:Segoe UI, sans-serif;'>"
            + "<div style='color:#38bdf8; font-size:17px; font-weight:bold; letter-spacing:1px; margin-bottom:12px;'>"
            + this.tituloStep0 + "</div>"
            + "<div style='color:#f8fafc; font-size:15px; font-weight:bold; line-height:1.45;'>"
            + this.fraseStep0 + "</div>"
            + "</td></tr></table></html>");

        this.btnAcao.setText("Ver Decisao >");
        this.btnAcao.setBackground(new Color(16, 185, 129));
        this.btnAcao.setForeground(new Color(2, 44, 34));
    }

    /**
     * ETAPA 2 DE 2: DECISAO FINAL CONFIRMADA
     */
    private void setStep1() {
        this.step = 1;
        atualizarPills(1);

        boolean legal = (this.resultadoFinal == RESULT_GOL_LEGAL || this.resultadoFinal == RESULT_VERMELHO_REVERTIDO);
        Color corGlow = legal ? new Color(16, 185, 129) : new Color(239, 68, 68);
        String corHex = legal ? "#10b981" : "#ef4444";

        atualizarBordaGlow(corGlow);

        String tituloDecisao;
        if (this.resultadoFinal == RESULT_GOL_LEGAL) {
            tituloDecisao = "GOL DO " + this.nomeTime.toUpperCase() + " CONFIRMADO!";
        } else if (this.resultadoFinal == RESULT_VERMELHO_REVERTIDO) {
            tituloDecisao = "DECISAO: REVERTIDO PARA CARTAO AMARELO!";
        } else if (this.subMotivo == SUB_CONDUTA_OBSCENA) {
            tituloDecisao = "EXPULSAO DIRETA: GESTO OBSCENO!";
        } else if (this.resultadoFinal == RESULT_VERMELHO_MANTIDO) {
            tituloDecisao = "DECISAO: CARTAO VERMELHO MANTIDO!";
        } else {
            tituloDecisao = "GOL DO " + this.nomeTime.toUpperCase() + " ANULADO!";
        }

        this.lblMensagem.setText("<html><table width='670' cellpadding='0' cellspacing='0' border='0'>"
            + "<tr><td align='center' style='font-family:Segoe UI, sans-serif;'>"
            + "<div style='color:" + corHex + "; font-size:20px; font-weight:bold; letter-spacing:0.5px; margin-bottom:12px;'>"
            + tituloDecisao + "</div>"
            + "<div style='color:#f8fafc; font-size:15px; font-weight:bold; line-height:1.45;'>"
            + this.fraseFinal + "</div>"
            + "</td></tr></table></html>");

        this.btnAcao.setText("Continuar Partida >");
        this.btnAcao.setBackground(new Color(16, 185, 129));
        this.btnAcao.setForeground(new Color(2, 44, 34));
        this.btnAcao.requestFocusInWindow();
    }

    private void atualizarPills(int etapaAtiva) {
        String c1 = (etapaAtiva == 0) ? "background:#0284c7; color:#ffffff; font-weight:bold;" : "background:#1e293b; color:#64748b;";
        String c2 = (etapaAtiva == 1) ? "background:#10b981; color:#022c22; font-weight:bold;" : "background:#1e293b; color:#64748b;";

        this.lblStepPills.setText("<html><center style='font-family:Segoe UI, sans-serif; font-size:11px;'>"
            + "<span style='" + c1 + " padding:4px 16px; border-radius:3px;'>1. CHECAGEM DO VAR</span> &nbsp; "
            + "<span style='color:#475569; font-size:13px;'>➔</span> &nbsp; "
            + "<span style='" + c2 + " padding:4px 16px; border-radius:3px;'>2. DECISAO FINAL</span>"
            + "</center></html>");
    }

    private void avancarEtapa() {
        if (timer != null) {
            timer.stop();
        }

        if (step == 0) {
            setStep1();
            if (automatico) {
                iniciarTimerEtapa(4500, this::fechar);
            }
        } else {
            fechar();
        }
    }

    private void fechar() {
        if (timer != null) {
            timer.stop();
        }
        if (dialog != null) {
            dialog.dispose();
        }
        if (onComplete != null) {
            onComplete.run();
        }
    }

    private void iniciarAnimacaoAutomatica() {
        iniciarTimerEtapa(2800, () -> {
            setStep1();
            iniciarTimerEtapa(4500, this::fechar);
        });
    }

    private void iniciarTimerEtapa(int delayMs, Runnable acao) {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(delayMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                acao.run();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void exibir(java.awt.Frame parent, int tipoLance, int resultado, int motivoChecagem, String nomeTime, String nomeJogador, int minuto, ImageIcon escudoTime, Runnable onComplete) {
        JDialog dlg = new JDialog(parent, true);
        dlg.setUndecorated(true);
        VarDialog panel = new VarDialog(dlg, tipoLance, resultado, motivoChecagem, nomeTime, nomeJogador, minuto, escudoTime, onComplete);
        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setSize(750, 390);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }
}
