# 🎮 Brasfoot Modern UI - Design System & Diretrizes de Interface (EA FC)

Documentação completa de design system, arquitetura de componentes, paleta de cores, tipografia e diretrizes de implementação do Mod Visual do Brasfoot 2022-2023.

> [!TIP]
> A skill oficial pronta para uso por agentes de IA encontra-se no caminho [`skills/brasfoot-modern-ui/SKILL.md`](skills/brasfoot-modern-ui/SKILL.md).

---

## 🎨 1. Tokens de Cores Globais

### Superfícies e Painéis
* **Fundo Global (`BG_DEEP`)**: `#0b0f19` / `rgb(11, 15, 25)`
* **Container e Colunas (`PANEL_DARK`)**: `#121826` / `rgb(18, 24, 38)`
* **Cartões e Superfícies Elevadas (`CARD_BG`)**: `#182134` / `rgb(24, 33, 52)`
* **Borda Sutil Divisória (`BORDER_SLATE`)**: `#2d3c58` / `rgb(45, 60, 88)`
* **Borda Ativa / Foco (`BORDER_ACTIVE`)**: `#3b82f6` / `rgb(59, 130, 246)`

### Tipografia e Contraste
* **Texto Branco Puro (`TEXT_WHITE`)**: `#ffffff` (força real, números e títulos)
* **Texto Primário Suave (`TEXT_BRIGHT`)**: `#e1ebf8` (nomes de atletas)
* **Texto Secundário (`TEXT_MUTED`)**: `#b4c3d7` (dados de apoio e posições)

### Cores por Posição em Campo (Chips de Função)
* **Goleiro (G)**: Fundo `#d97706` / Borda `#f59e0b` (Âmbar/Laranja)
* **Laterais (LD, LE)**: Fundo `#0284c7` / Borda `#38bdf8` (Ciano)
* **Zagueiros (ZD, ZE, Z)**: Fundo `#2563eb` / Borda `#60a5fa` (Azul Royal)
* **Meio-Campo (VOL, M, MD, ME)**: Fundo `#059669` / Borda `#34d399` (Verde Esmeralda)
* **Atacantes (AD, AE, A)**: Fundo `#dc2626` / Borda `#f87171` (Carmesim/Vermelho)
* **Alerta de Improvisação**: Fundo `#facc15` / Borda `#fef08a` (Amarelo Ouro)

---

## 📐 2. Grid e Proporções da Tela do Treinador (Pré-Jogo)

* **Resolução Total**: **1580 x 756 px** (proporção widescreen 21:9 / 16:9 de console)
* **Grid de 3 Colunas**:
  1. **Coluna 1 (Esquerda - Campo HD)**: `434 x 650 px` com o gramado em alta definição e 11 jogadores posicionados dinamicamente.
  2. **Coluna 2 (Centro - Banco e Métricas)**: `490 px` de largura. Contém o Banco de Reservas expandido (14 atletas em linhas de 26px de altura, espaçamento de 9px), Radar Tático e estatísticas globais do elenco.
  3. **Coluna 3 (Direita - Tática e Adversário)**: Painel tático moderno (Estilo, Formação, Marcação, Foco), escalação adversária e botões inferiores de ação.

---

## 🎴 3. Anatomia do Card do Jogador (Player Shirt Card)

Renderizado dinamicamente em uma área transparente centralizada de **150 x 54 px**:
1. **Linha 1 (Energia / Estamina)**: Barra horizontal de 48 x 3 px com cantos arredondados (r=2px). Verde $\ge 70$, Âmbar $45 \dots 69$, Vermelho $< 45$.
2. **Linha 2 (Pill de Posição & OVR)**: Container escuro de 14px de altura contendo o chip colorido da posição à esquerda e a força real do jogador à direita.
3. **Linha 3 (Nome do Jogador & Cruz de Lesão)**: Card arredondado (r=4px) escuro com sombra suave. Se machucado, a **cruz vermelha 13x13px** é renderizada colada ao lado esquerdo do card do nome, perfeitamente alinhada.
4. **Linha 4 (Badges Táticas)**: Mini-ícones circulares (C, F, E, 9) centralizados abaixo do nome.

---

## 🚀 4. Como Reutilizar em Outras Telas

Para aplicar esta mesma identidade visual em outras áreas do Brasfoot (durante a partida em `a/cD`, telas de pós-jogo, finanças ou transferências), basta carregar a skill `brasfoot-modern-ui` ou consultar os snippets em [`skills/brasfoot-modern-ui/SKILL.md`](skills/brasfoot-modern-ui/SKILL.md).
