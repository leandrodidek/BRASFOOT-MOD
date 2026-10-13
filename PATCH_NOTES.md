# Brasfoot 2022-2023 - Patch Notes do Mod EA FC & VAR

## Versão: Build 2026.10.13 - Atualização Geral de Estabilidade, IA, VAR e UI

---

### 1. Sistema do VAR (Árbitro de Vídeo Broadcast & Efeitos Sonoros)
* **Dinâmica Sonora Imersiva**:
  * **Intervenção com Apito**: No momento em que um lance duvidoso de gol vai para checagem do VAR, a comemoração inicial é imediatamente cortada pelo apito agudo do árbitro (`penalty.wav`), sinalizando a paralisação súbita da jogada.
  * **Gol do Usuário Confirmado**: O VAR reproduz o som de gol (`gol1.wav`) na etapa da decisão final. A execução nativa repetida pós-janela foi interceptada e silenciada, eliminando sons duplicados.
  * **Gol do Usuário Anulado**: Ao ter seu gol anulado pelo VAR, é reproduzido o efeito sonoro do adversário (`goladv.wav`), transmitindo a atmosfera de decepção e frustração do cancelamento.
  * **Gol do Adversário Confirmado**: Toca o áudio do adversário (`goladv.wav`), também com supressão de áudio duplicado ao fechar a janela.
  * **Gol do Adversário Cancelado**: Quando o VAR invalida um gol rival, toca instantaneamente o som de comemoração do usuário (`gol1.wav`), simulando a explosão de alívio e comemoração da sua torcida.
* **Gatilho de Expulsão Rara por Gesto Obsceno**:
  * Substituído o gatilho antigo baseado em faltas por contagem de cartões da partida.
  * O sorteio de 1% de expulsão direta por conduta anti-desportiva/gesto obsceno agora é condicionado a **partidas tensas com mais de 6 cartões** (`totalCartoes > 6`), somando amarelos e vermelhos de ambas as equipes.
* **Design Broadcast Premium**:
  * Integração da logomarca oficial do VAR (`img/var_logo.png` e badge vetorial).
  * Textos dinâmicos especificando time e tipo de lance (*"GOL do [Time] em revisão"*, *"Possível cartão vermelho para [Jogador]"*).
  * Painel com cantos arredondados, tipografia Segoe UI e esquema de cores escuro EA FC.

---

### 2. Interface Visual de Partida e Campo (Pitch & Player Cards)
* **Cruz Vermelha de Contusão Reposicionada**:
  * O ícone legado em coordenadas fixas (que ficava sobreposto na barra verde de energia) foi totalmente desativado.
  * Implementada renderização dinâmica em `EaFcUiHelper`: o ícone da cruz vermelha/hospitalar agora é desenhado **exclusivamente ao lado esquerdo da caixa com o nome do jogador**, perfeitamente alinhado verticalmente e acompanhando nomes curtos ou longos sem poluir a barra de estamina ou o pill de OVR.
* **Inversão e Calibração dos Gramados HD**:
  * `campo.png` (e miniatura `campo_diminuido.jpg`): Definido com o gramado tapete de excelência máxima, sendo o padrão principal para estádios de maior capacidade.
  * `campo2.png` (e miniatura `campo_diminuido2.jpg`): Definido com o gramado com marcas e textura regular/boa.

---

### 3. Inteligência Artificial de Escalação e Motor de Jogo
* **Seleção Inteligente da CPU (Smart Lineup & Improvisação)**:
  * Motor tático otimizado para que a máquina escale sempre seus melhores jogadores em suas respectivas posições nativas e lados de preferência (D/E).
  * Improvisação tratada estritamente como último recurso em caso de desfalques, lesões ou suspensões.
  * Cálculo dinâmico e calibrado de penalidade por improvisação via `PositionPenaltyHelper`.
* **Correção de Substituições Indevidas no 1º Minuto**:
  * Blindagem de sincronismo de índices em `SubstitutionManager` e `best.I`, impedindo substituições fantasmas ou remoções indevidas de goleiro nos minutos iniciais de jogo.

---

### 4. Janela de Táticas Salvas e Interface EA FC
* **Janela Interna sem Barra de Título Externa**:
  * Conversão para painel interno integrado à interface do jogo, eliminando bordas e barras brancas do sistema operacional.
  * Ajuste de altura e padding para visualização completa de todos os itens e botões sem cortes.
* **Badges Táticas e Radar Interativo**:
  * Badges de Capitão (C), Faltas (F), Escanteios (E) e Falso 9 (9) alinhadas abaixo do nome.
  * Painel de Radar Tático renderizado com polígono de atributos do elenco titular.

---

### 5. Estabilidade de Carregamento e Arquivos de Save
* **Prevenção de Tela Branca ao Carregar Saves**:
  * Tratamento de desserialização e reconstrução de ponteiros de equipe e atributos para saves gerados em versões anteriores e na build atual.
  * Compatibilidade estrita garantida com bytecode Java 8 (`--release 8`).
