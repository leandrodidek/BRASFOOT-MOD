# Brasfoot 2022-2023 - Mod EA Sports FC 2027 & VAR System

Repositório com o código-fonte, scripts de injeção bytecode e utilitários do mod de Brasfoot 2022-2023.

---

## 📌 Funcionalidades Principais (v1.0.0)

1. **Sistema de VAR Realista & Raro**:
   - Frequência reduzida em 50% para evitar interrupções excessivas na partida (~1 a cada 8 gols).
   - Diálogos interativos de revisão com animação gráfica de TV (Gol Legal, Gol Anulado por Impedimento, Gol Anulado por Falta).
   - Revisão de Cartões Vermelhos com possibilidade de reversão para Amarelo.
   - **Fator Casa (+15%)**: Pressão da torcida influencia as decisões da arbitragem de vídeo a favor do mandante.
   - Opção nas configurações do jogo para ligar/desligar o VAR automático.

2. **Evento Raro: Expulsão por Gesto Obsceno**:
   - 1% de chance de ocorrer em partidas tensas (com mais de 10 faltas somadas entre os dois times).
   - Intervenção do VAR com tipificação específica no diálogo e súmula.

3. **Substituições Livres (Estilo EA FC / PES)**:
   - Trocas flexíveis no banco de reservas sem travas por posição.
   - Sistema de mapeamento preciso que sincroniza posições nativas e slots ao voltar ao jogo.

4. **Interface EA Sports FC 2027**:
   - Design moderno na tela de escalação (`a.bz`) e pausa/substituições (`a.cD`).
   - Cores Midnight Obsidian, acentos Emerald Neon e Cyan, e fontes Segoe UI.

---

## 🛠️ Scripts de Build e Execução

- `compile_helpers.ps1`: Compila as classes Java do mod para `C:\Brasfoot22-23\projeto\build_mod`.
- `pack_all.ps1`: Injeta todas as classes e recursos modificados em `C:\Brasfoot22-23\bf22-23_modded.jar` e gera o executável `bf22-23_modded.exe`.
- `run_verify.ps1`: Executa a suíte de testes de integridade de classes e simulações de RNG.

---

## 📂 Estrutura de Arquivos

- `ModBridge.java`: Ponte de interceptação de eventos de partida e sincronização de placar/estatísticas.
- `VarManager.java`: Regras de negócio, probabilidades e motor de RNG do VAR e eventos raros.
- `VarDialog.java`: Interface gráfica do monitor do VAR (estilo transmissão de TV).
- `VarOptionsHelper.java`: Integração da opção de VAR na tela de opções do Brasfoot.
- `SubstitutionManager.java`: Gerenciador de substituições livres e sincronização com o motor do jogo.
- `EaFcUiHelper.java`: Estilização das telas no tema EA FC 27.
- `EnhancedJarPacker.java`: Utilitário de empacotamento JAR e substituição cirúrgica de classes.
- `base_patches/`: Classes bytecode originais modificadas (`a/cD`, `a/dV`, `components/ao`, etc.).
