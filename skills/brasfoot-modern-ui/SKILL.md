---
name: brasfoot-modern-ui
description: Use when building, restyling, or updating UI screens in Brasfoot with the EA FC / Console Dark HUD design system.
metadata:
  category: ui-design-system
  triggers: brasfoot ui, tela treinador, estilo ea fc, modernizar tela, java swing hud, broadcast dark theme
---

# 🎮 Brasfoot Modern UI Design System (EA FC & Console Dark HUD)

Guia de engenharia e diretrizes completas de design para modernizar interfaces do Brasfoot (Java Swing e Java2D Graphics2D), baseado na estética de transmissão televisiva e consoles modernos (EA FC, PES/eFootball e Football Manager Dark Mode).

---

## 🧭 1. Filosofia e Princípios de Design

1. **Console Dark HUD**:
   * O tema deve ser predominantemente escuro (tons profundos de azul-marinho e cinza-azulado), eliminando completamente as cores cinzas opacas e barras brancas do visual legado do Windows 98/XP.
   * Cria alto contraste com o verde dos gramados, escudos e cores de camisas dos clubes.

2. **Hierarquia e Legibilidade em Resoluções Densas**:
   * O Brasfoot opera originalmente em telas compactas (1024x768). As janelas modernizadas operam na proporção widescreen de console (ex: **1580x756**).
   * Informações críticas (Posição, Lado, OVR/Força e Energia) recebem elementos geométricos próprios (pills, chips e barras com cantos arredondados), impedindo confusão visual.

3. **Eliminação de Elementos Nativos Obsoletos**:
   * Nenhuma janela modal deve conter a barra de título padrão do Windows com botões 'X' brancos/cinzas. Modais devem ser caixas flutuantes integradas ao próprio canvas do jogo.

---

## 🎨 2. Tokens de Design (Cores, Tipografia e Bordas)

### 2.1 Paleta Cromática Global (RGB & Hexadecimal)

| Token | Hex | RGB | Utilização |
| :--- | :--- | :--- | :--- |
| `BG_DEEP` | `#0b0f19` | `rgb(11, 15, 25)` | Fundo da janela principal e telas de fundo |
| `PANEL_DARK` | `#121826` | `rgb(18, 24, 38)` | Containers principais, colunas e áreas de lista |
| `CARD_BG` | `#182134` | `rgb(24, 33, 52)` | Cartões de jogadores, caixas de status e cards elevados |
| `BORDER_SLATE` | `#2d3c58` | `rgb(45, 60, 88)` | Linhas divisórias sutis e contornos de cartões |
| `BORDER_ACTIVE` | `#3b82f6` | `rgb(59, 130, 246)` | Borda de destaque / seleção / foco |
| `TEXT_WHITE` | `#ffffff` | `rgb(255, 255, 255)` | Títulos principais, valores de força e destaque |
| `TEXT_BRIGHT` | `#e1ebf8` | `rgb(225, 235, 248)` | Nomes de atletas e rótulos de alta prioridade |
| `TEXT_MUTED` | `#b4c3d7` | `rgb(180, 195, 215)` | Informações secundárias e dados estatísticos |

### 2.2 Cores por Posição dos Atletas (Chips de Função)

Cada setor do campo possui uma cor de identificação imediata com borda de realce em tom mais claro:

| Posição | Lado/Sigla | Cor do Fundo (Hex / RGB) | Cor da Borda (Hex / RGB) |
| :--- | :--- | :--- | :--- |
| **Goleiro** | `G` | `#d97706` (`217, 119, 6`) | `#f59e0b` (`245, 158, 11`) |
| **Laterais** | `LD`, `LE` | `#0284c7` (`2, 132, 199`) | `#38bdf8` (`56, 189, 248`) |
| **Zagueiros** | `ZD`, `ZE`, `Z` | `#2563eb` (`37, 99, 235`) | `#60a5fa` (`96, 165, 250`) |
| **Meio-Campo** | `VOL`, `M`, `MD`, `ME` | `#059669` (`5, 150, 105`) | `#34d399` (`52, 211, 153`) |
| **Atacantes** | `AD`, `AE`, `A` | `#dc2626` (`220, 38, 38`) | `#f87171` (`248, 113, 113`) |
| **Improvisado** | Qualquer | `#facc15` (Alerta Amarelo Ouro) | `#fef08a` |

### 2.3 Tipografia Mandatória

Para garantir nitidez perfeita no renderizador nativo do Java2D:
* **Família Padrão**: `Verdana` (primária para cartões/nomes/OVR) e `Segoe UI` (para headers, modais e textos de transmissão).
* **Tamanhos e Pesos**:
  * Nome do Jogador: `new Font("Verdana", Font.BOLD, 10)`
  * Número do OVR (Força): `new Font("Verdana", Font.BOLD, 10)`
  * Chip da Posição: `new Font("Verdana", Font.BOLD, 9)`
  * Títulos de Seção: `new Font("Verdana", Font.BOLD, 11)`
  * Textos de Transmissão (VAR/Placar): `new Font("Segoe UI", Font.BOLD, 14)` a `18`

---

## 🧩 3. Componentes Estruturais

### 3.1 Player Shirt Card (Card no Campo de Jogo)

O card sobreposto na camisa do jogador possui **4 linhas verticais organizadas**, desenhadas em um canvas transparente de 150x54 px centralizado (`cx ≈ 74`):

```
       [====== BARRA DE ENERGIA ======]           -> Linha 1 (h=3px, w=48px, r=2px)
            [ POS | OVR ]                         -> Linha 2 (Pill unificado h=14px, r=4px)
[+]    [     Nome do Atleta      ]                -> Linha 3 (Card escuro h=14px) + Cruz de Lesão à esquerda
            (C) (F) (E) (9)                       -> Linha 4 (Badges de Funções centralizadas)
```

1. **Linha 1 - Barra de Energia**:
   * Fundo escuro: `#0f172a` com cantos arredondados de 2px.
   * Barra colorida: Verde (`#22c55e`) para $\ge 70$, Âmbar (`#f59e0b`) para $45 \dots 69$, Vermelho (`#ef4444`) para $< 45$.
2. **Linha 2 - Pill de OVR e Posição**:
   * Container arredondado (fundo `#0f172a`, borda cinza ou amarela de alerta se improvisado).
   * Chip da Posição: Fundo sólido na cor do setor (ex: Ciano para LD, Azul para ZD) com sigla em branco.
   * OVR: Número em branco puro (ou amarelo `#facc15` se houver penalidade de improvisação).
3. **Linha 3 - Nome do Jogador e Contusão**:
   * Card arredondado (r=4px) com preenchimento `#0f172a` e sombra suave em preto (`rgba(0,0,0,180)`) em `textX + 1, textY + 1`.
   * **Cruz Vermelha de Contusão**: Se o jogador estiver machucado (`fP() == true`), desenha o ícone oficial 13x13px colado **ao lado esquerdo do card do nome**, alinhado ao centro vertical. Não invade a barra de energia nem o pill de OVR.
4. **Linha 4 - Badges Táticas**:
   * Ícones circulares de 12x12px centralizados abaixo do nome: Capitão (`C`), Batedor de Faltas (`F`), Escanteios (`E`) e Falso 9 (`9`).

### 3.2 Linha de Reservas (Banco de Substituições)

* **Altura ultra-compacta**: `h = 26px`.
* **Espaçamento vertical**: 9px de folga entre linhas para evitar sobreposição ao rolar a lista.
* Badges de função alinhadas verticalmente no canto esquerdo da linha do atleta.

### 3.3 Botões Modernos EA FC

* **Botão de Ação Primária ("Jogar Partida" / "Confirmar")**:
  * Gradiente de fundo: `#10b981` (Esmeralda Superior) para `#059669` (Esmeralda Inferior).
  * Texto branco com cantos arredondados (`r = 8px`).
  * Efeito Hover: Clareamento dinâmico (`#34d399`).
* **Botão Secundário ("Táticas Salvas" / "Voltar")**:
  * Fundo escuro com borda fosca (`#1e293b`), cantos arredondados (`r = 6px`).
  * Hover com borda destacada em Azul Elétrico (`#3b82f6`).

### 3.4 Janelas Modais Internas (Popups Modernos)

* **Sem Barra Nativa**: Usar `JDialog.setUndecorated(true)` ou painéis internos com `JLayeredPane`.
* **Borda Glow Broadcast**: Borda composta com 1px externo iluminado e cantos arredondados.
* **Header Temático**: Escudo do clube à esquerda, título em caixa alta com subtítulo explicativo e botão fechar com hover suave.

---

## 💻 4. Snippets de Código Reutilizáveis (Java Graphics2D)

### 4.1 Configuração Obrigatória do Graphics2D

Sempre aplicar antes de renderizar qualquer elemento:

```java
public static void applyHighQualityRendering(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
}
```

### 4.2 Renderização da Borda Arredondada EA FC

```java
public static void drawEaFcBorder(Graphics2D g, int x, int y, int width, int height, int radius, Color border, Color fill) {
    if (fill != null) {
        g.setColor(fill);
        g.fillRoundRect(x, y, width, height, radius, radius);
    }
    if (border != null) {
        g.setColor(border);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}
```

### 4.3 Desenho do Pill de Posição + OVR

```java
public static void drawPositionOvrPill(Graphics2D g, int cx, int y, String posLabel, int ovr, Color chipBg, Color chipBorder, boolean isImprovised) {
    Font fontChip = new Font("Verdana", Font.BOLD, 9);
    Font fontOvr = new Font("Verdana", Font.BOLD, 10);
    
    g.setFont(fontChip);
    FontMetrics fmChip = g.getFontMetrics(fontChip);
    int chipTxtW = fmChip.stringWidth(posLabel);
    int posBadgeW = Math.max(chipTxtW + 8, 22);

    g.setFont(fontOvr);
    FontMetrics fmOvr = g.getFontMetrics(fontOvr);
    String ovrStr = String.valueOf(ovr);
    int ovrW = fmOvr.stringWidth(ovrStr);

    int gap = 5;
    int rightPad = 7;
    int pillW = posBadgeW + gap + ovrW + rightPad;
    int pillH = 14;
    int pillX = cx - (pillW / 2);

    Color pillBg = new Color(15, 23, 42, 245);
    Color pillBorder = isImprovised ? new Color(250, 204, 21, 230) : new Color(71, 85, 105, 220);
    Color ovrColor = isImprovised ? new Color(250, 204, 21) : Color.WHITE;

    // Fundo do pill
    g.setColor(pillBg);
    g.fillRoundRect(pillX, y, pillW, pillH, 4, 4);
    g.setColor(pillBorder);
    g.drawRoundRect(pillX, y, pillW, pillH, 4, 4);

    // Chip da posição interno
    int chipX = pillX + 1;
    int chipY = y + 1;
    int chipH = pillH - 2;
    g.setColor(chipBg);
    g.fillRoundRect(chipX, chipY, posBadgeW, chipH, 3, 3);
    g.setColor(chipBorder);
    g.drawRoundRect(chipX, chipY, posBadgeW, chipH, 3, 3);

    g.setFont(fontChip);
    g.setColor(Color.WHITE);
    int chipTxtX = chipX + (posBadgeW - chipTxtW) / 2;
    int chipTxtY = chipY + (chipH - fmChip.getHeight()) / 2 + fmChip.getAscent();
    g.drawString(posLabel, chipTxtX, chipTxtY);

    // Texto do OVR
    int ovrX = chipX + posBadgeW + gap;
    int ovrY = y + (pillH - fmOvr.getHeight()) / 2 + fmOvr.getAscent();
    g.setFont(fontOvr);
    g.setColor(ovrColor);
    g.drawString(ovrStr, ovrX, ovrY);
}
```

---

## 📋 5. Guia de Aplicação em Outras Telas do Jogo

Para estender a identidade visual do pré-jogo para qualquer outra área do Brasfoot:

1. **Tela Durante a Partida (`a/cD`)**:
   * Substituir o placar cru por painel estilo broadcast televisivo com tempo decorrido em pill azul (`#0284c7`), escudos iluminados e placar centralizado em destaque.
   * Aplicar os mesmos pills de posição e energia quando o jogador clicar em substituições em tempo real.
2. **Tela de Escalação Geral / Elenco (`a/bz`)**:
   * Utilizar a paleta de cores por setor nos nomes e posições da tabela principal.
   * Empregar linhas com efeito zebra sutil (`#121826` e `#182134`).
3. **Janelas de Pós-Jogo e Estatísticas (`a/eL`, `b/aD`)**:
   * Substituir as barras de posse de bola e finalizações por medidores horizontais modernos com gradientes coloridos e cantos arredondados.
4. **Telas Financeiras e Transferências**:
   * Valores monetários em verde esmeralda (`#10b981`), déficits em carmesim (`#dc2626`).
   * Cards escuros para cada proposta recebida.

---

## ✅ 6. Checklist de Validação Visual

Antes de liberar qualquer tela com este design:
- [ ] O fundo utiliza os tons profundos de console (`#0b0f19` ou `#121826`)?
- [ ] Todas as bordas utilizam cantos arredondados (`roundRect`) com no mínimo 4px de raio?
- [ ] Os textos utilizam `Verdana` ou `Segoe UI` com `RenderingHints` antialiasing ativos?
- [ ] Há contraste suficiente entre texto e fundo (relação mínima 4.5:1)?
- [ ] Não há sobreposição de componentes ao redimensionar ou rolar?
- [ ] Se houver ícone de contusão, ele está posicionado à esquerda do nome do jogador sem cobrir atributos?
- [ ] As modais e janelas pop-up estão sem a barra de título padrão do Windows?
